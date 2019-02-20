/**
    FileSystem utilities of Nuke3D Editor
    Copyright (C) 2019 Anton "Vuvk" Shcherbatykh <vuvk69@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.vuvk.n3d.utils;

import com.vuvk.n3d.Const;
import com.vuvk.n3d.Global;
import com.vuvk.n3d.forms.FormMain;
import com.vuvk.n3d.forms.FormMaterialEditor;
import com.vuvk.n3d.forms.FormTextureEditor;
import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Texture;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class FileSystemUtils {  
    
    /** получить расширение файла
     * @param file Файл, расширение которого нужно узнать
     * @return строка, содержащая расширение файла без точки, в нижнем регистре
     */
    public static String getFileExtension(Path file) {  
        return getFileExtension(file.toFile());
    }
    
    /** получить расширение файла
     * @param file Файл, расширение которого нужно узнать
     * @return строка, содержащая расширение файла без точки, в нижнем регистре
     */
    public static String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
       
        return FilenameUtils.getExtension(file.toString()).toLowerCase();
    }
    
    /**
     * Получить путь до ресурса относительно папки resources (включительно)
     * @param path Путь, из которого необходимо собрать "путь проекта"
     * @return Путь в строковом представлении
     */
    public static String getProjectPath(Path path) {
        if (path == null) {
            return Const.RESOURCES_STRING;
        } else {
            String pathToRoot = "";
            Path checkPath = path;

            // собираем имена папок для перехода в путь от корня (resources)
            while (checkPath.compareTo(Global.RESOURCES_PATH) != 0) {
                if (Files.isDirectory(checkPath)) {
                    pathToRoot = checkPath.getFileName().toString() + "/" + pathToRoot;
                } else {
                    pathToRoot = checkPath.getFileName().toString();
                }
                checkPath = checkPath.getParent();
                if (checkPath == null) {
                    break;
                }
            }
            pathToRoot = Const.RESOURCES_STRING + pathToRoot;
            
            
            return pathToRoot;
        }
    }
    
    /**
     * Пробежаться по содержимому пути и добавить ссылки на файлы, если они нужного формата
     * @param path Путь, в котором нужно произвести поиск
     */
    static void addResourcesFromPath(Path path) {
        // списки путей для добавления
        final List<Path> texturesForAdd = new LinkedList<>();
        final List<Path> materialsForAdd = new LinkedList<>();
        
        if (path != null) {
            // это директория
            if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {   
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {                    
                            // по расширению файла определяем что это
                            switch (getFileExtension(file)) {
                                case Const.TEXTURE_FORMAT_EXT :
                                    texturesForAdd.add(file);
                                    break;
                                    
                                case Const.MATERIAL_FORMAT_EXT :
                                    materialsForAdd.add(file);
                                    break;
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.showException(ex);
                }
            // это файл
            } else {
                // по расширению файла определяем что это
                switch (getFileExtension(path)) {
                    case Const.TEXTURE_FORMAT_EXT :
                        texturesForAdd.add(path);
                        break;
                                    
                    case Const.MATERIAL_FORMAT_EXT :
                        materialsForAdd.add(path);
                        break;
                }
            }        
        }
        
        // добавляем всё, что нашли
        for (Path forAdd : texturesForAdd) {
            new Texture(forAdd);
        }
        for (Path forAdd : materialsForAdd) {
            new Material(forAdd);
        }
        
        texturesForAdd.clear();
        materialsForAdd.clear();
    }
    
    /**
     * Рекурсивно переименовать или перенести путь. Должны измениться пути у содержащихся внутри ресурсов 
     * @param src  путь, в котором должны смениться ссылки и пути ресурсов
     * @param dest новый путь (во что переименовывать)
     * @return true в случае успеха, false - возникла ошибка
     */
    public static boolean recursiveMoveFiles(Path src, Path dest) {        
        if (src  == null || !Files.exists(src) ||
            dest == null/* ||  Files.exists(dest)*/
           ) {
            return false;
        }
        
        // сохраняем имеющиеся ресурсы, чтобы перенести правильно их конфиги
        Material.saveAll();
        
        // списки путей для изменения (пара "старый путь" - "новый путь")
        final List<Pair<String, String>> texturesForRepath  = new LinkedList<>();
        final List<Pair<String, String>> materialsForRepath = new LinkedList<>(); 
        // список файлов на удаление перед заменой
        final List<Path> filesForRemove = new LinkedList();
        
        // существует?
        if (Files.exists(dest, LinkOption.NOFOLLOW_LINKS)) {         
            // нельзя вырезать себя в себя!
            if (src.compareTo(dest) == 0) {
                return false;
            // файл не тот же самый, но с таким именем уже существует
            } else {
                // последнее предупреждение!
                Boolean answer = MessageDialog.showConfirmationYesNoCancel("\"" + dest.toString() + "\"\nуже существует! Перезаписать?");
                // CANCEL or NO
                if (answer == null || !answer.booleanValue()) {
                    return false;
                }
                
                // если это файл, то сразу отметим его для предварительного удаления
                if (!Files.isDirectory(dest)) {
                    filesForRemove.add(dest);
                }
            }
        }

        // собираем старое и новое имя в строковом представлении
        final String oldName = getProjectPath(src);
        StringBuilder nameBuilder = new StringBuilder(getProjectPath(dest));
        // если папка, то добавить слэш, т.к. getProjectPath понятия не имеет 
        // что такое dest - папка или файл, если их не существует
        if (Files.isDirectory(src) && !Files.exists(dest)) {
            nameBuilder.append("/");
        }
        final String newName = nameBuilder.toString();

        // ищем в папке содержащиеся ресурсы и помечаем их для замены у них пути
        if (Files.isDirectory(src)) {
            try {
                Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {  
                        String oldPathString = getProjectPath(file);
                        String newPathString = oldPathString.replace(oldName, newName);
                        
                        // если файл уже существует, то сразу отметим его для предварительного удаления
                        Path newPath = Paths.get(newPathString);
                        if (Files.exists(newPath) && !Files.isDirectory(newPath)) {
                            filesForRemove.add(newPath);
                        }
                        
                        // по расширению файла определяем что это
                        switch (getFileExtension(file)) {
                            case Const.TEXTURE_FORMAT_EXT :                                
                                texturesForRepath.add(new ImmutablePair<String, String>(oldPathString, newPathString));
                                break;
                                
                            case Const.MATERIAL_FORMAT_EXT :
                                texturesForRepath.add(new ImmutablePair<String, String>(oldPathString, newPathString));
                                break;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
        } else {
            // по расширению файла определяем что это
            switch (getFileExtension(src)) {
                case Const.TEXTURE_FORMAT_EXT :
                    texturesForRepath.add(new ImmutablePair<String, String>(oldName, newName));
                    break;
                    
                case Const.MATERIAL_FORMAT_EXT :
                    materialsForRepath.add(new ImmutablePair<String, String>(oldName, newName));;
                    break;
            }
        }
        
        // если конечный путь существует, то заменяемые файлы необходимо предварительно удалить из проекта
        for (Path path : filesForRemove) {
            recursiveRemoveFiles(path);
        }
        
        // переносим путь
        try {
            Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);   
            /*if (Files.isDirectory(src)) {
                FileUtils.moveDirectory(src.toFile(), dest.toFile());
            } else {
                FileUtils.moveFile(src.toFile(), dest.toFile());
            } */           
        } catch (Exception ex) {
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }    
        
        // заменяем часть пути (или весь) с учетом нового имени папки или файла
        for (Pair<String, String> paths : texturesForRepath) {
            Texture txr = Texture.getByPath(paths.getLeft());            
            if (txr != null) {
                txr.setPath(paths.getRight());
            }
        }
        for (Pair<String, String> paths : materialsForRepath) {
            Material mat = Material.getByPath(paths.getLeft());
            if (mat != null) {
                mat.setPath(paths.getRight());
            }
        } 
        
        texturesForRepath.clear();
        materialsForRepath.clear();
        filesForRemove.clear();
            
        return true;
    }
    
    /**
     * Рекурсивное копирование всех файлов и папок (включая вложенные подпапки и файлы) в новый путь
     * @param src Путь, из которого нужно копировать, включая сам путь
     * @param to  Путь, в который нужно копировать src (ПАПКА, куда переносить файлы/папки из src)
     * @return true, если всё копировано, false - возникла ошибка
     */
    public static boolean recursiveCopyFiles(Path src, Path to) {
        if (src == null || to == null) {
            return false;
        }
        
        // сохраняем имеющиеся ресурсы, чтобы перенести правильно их конфиги
        Material.saveAll();
        
        // конечное имя
        Path dest = Paths.get(to.toString() + "/" + src.getFileName()); 
        
        // создаем целевую папку
        if (!Files.exists(to)) {
            try {
                Files.createDirectories(dest);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }
        }
                
        // существует?
        if (Files.exists(dest, LinkOption.NOFOLLOW_LINKS)) {         
            // нельзя копировать себя в себя, но можно поместить рядом копию!
            if (src.compareTo(dest) == 0) {
                Path newDest = dest;                
                while (Files.exists(newDest, LinkOption.NOFOLLOW_LINKS)) {
                    String newName = dest.getParent().toString() + File.separator + "copy of " + newDest.getFileName().toString();
                    newDest = Paths.get(newName);
                }
                dest = newDest;
            // файл не тот же самый, но с таким именем уже существует
            } else {
                // последнее предупреждение!
                Boolean answer = MessageDialog.showConfirmationYesNoCancel("\"" + dest.toString() + "\"\nуже существует! Перезаписать?");
                // CANCEL
                if (answer == null) {
                    return false;
                // NO
                } else if (!answer.booleanValue()) {
                    // решил переименовать
                    while (Files.exists(dest, LinkOption.NOFOLLOW_LINKS)) {
                        String newName = (String) MessageDialog.showInput("Введите новое имя для объекта\n\"" + dest.toString() + "\":", src.getFileName());
                        if (newName == null) {
                            return true;    // отмена?
                        } else {
                            dest = Paths.get(to.toString() + "/" + newName);
                        }
                    }       
                }       
            }
        }
                
        File fPath = src.toFile();
        File fDest = dest.toFile();

        // пишем!
        // директория?
        if (Files.isDirectory(src, LinkOption.NOFOLLOW_LINKS)) {
            try {
                FileUtils.copyDirectory(fPath, fDest);
            } catch (IOException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }
        // файл
        } else {
            try {
                FileUtils.copyFile(fPath, fDest);
            } catch (IOException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }
        }

        // добавляем ссылки на вновь созданные объекты
        addResourcesFromPath(dest);

        return true;
    }
            
    /**
     * Рекурсивное удаление всех файлов и папок (включая вложенные подпапки и файлы) в пути
     * @param path Путь, в котором будет удалено всё, включая сам путь
     * @return true, если удалены, false - возникла ошибка
     */
    public static boolean recursiveRemoveFiles(Path path) {   
        // списки путей для изменения
        final List<String> texturesForDelete = new LinkedList<>();
        final List<String> materialsForDelete = new LinkedList<>();
        
        // это директория
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.deleteIfExists(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {                    
                        // по расширению файла определяем что это
                        // для того, чтобы удалить файл из настроек проекта
                        switch (getFileExtension(file)) {
                            case Const.TEXTURE_FORMAT_EXT :
                                texturesForDelete.add(getProjectPath(file));
                                break;
                                
                            case Const.MATERIAL_FORMAT_EXT :
                                materialsForDelete.add(getProjectPath(file));
                                break;
                        }
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
                
                // Удаляем сам путь
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
        // это файл
        } else {
            // по расширению файла определяем что это
            // для того, чтобы удалить файл из настроек проекта
            switch (getFileExtension(path)) {
                case Const.TEXTURE_FORMAT_EXT :
                    texturesForDelete.add(getProjectPath(path));
                    break;
                                
                case Const.MATERIAL_FORMAT_EXT :
                    materialsForDelete.add(getProjectPath(path));
                    break;
            }
            
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
        }       
        
        // удаляем из проекта то, что нашли
        // текстуры
        for (String filePath : texturesForDelete) {
            for (Iterator it = Texture.TEXTURES.iterator(); it.hasNext(); ) {
                Texture txr = (Texture)it.next();
                if (txr.getPath().equals(filePath)) {
                    // закрыть окно с открытой удаляемой текстурой
                    if (FormMain.formTextureEditor != null && 
                        FormTextureEditor.selectedTexture != null &&
                        FormTextureEditor.selectedTexture.equals(txr)
                       ) {
                        FormMain.closeFormTextureEditor();
                    }
                    
                    it.remove();                    
                }
            }
        }
        // материалы
        for (String filePath : materialsForDelete) {
            for (Iterator it = Material.MATERIALS.iterator(); it.hasNext(); ) {
                Material mat = (Material)it.next();
                if (mat.getPath().equals(filePath)) {
                    // закрыть окно с открытой удаляемым материалом
                    if (FormMain.formMaterialEditor != null && 
                        FormMaterialEditor.selectedMaterial != null &&
                        FormMaterialEditor.selectedMaterial.equals(mat)
                       ) {
                        FormMain.closeFormMaterialEditor();
                    }
                    
                    it.remove();                    
                }
            }
        }
        
        texturesForDelete.clear();
        materialsForDelete.clear();
        
        // проверяем валидность материалов
        Material.checkAll();
 
        // всё успешно удалено?
        return !Files.exists(path);
    }
    
    
    
    private FileSystemUtils(){}
}
