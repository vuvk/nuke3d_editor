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
import com.vuvk.n3d.forms.FormSoundEditor;
import com.vuvk.n3d.forms.FormTextureEditor;
import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Sound;
import com.vuvk.n3d.resources.Texture;
import java.io.File;
import java.io.FilenameFilter;
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
import java.util.ArrayList;
import java.util.Arrays;
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
        final List<Path> texturesForAdd  = new LinkedList<>();
        final List<Path> materialsForAdd = new LinkedList<>();
        final List<Path> soundsForAdd    = new LinkedList<>();
        
        /**
         * класс для обработки обнаруженных файлов и папок    
         */
        class PathProcessor {
            /**
             * Обработать путь - добавить его в список добавления пути, если это известный ресурс
             * @param path Путь для проверки
             */
            public void process(Path path) {               
                String ext = getFileExtension(path);
                List list = null;

                // по расширению файла определяем что это
                switch (ext) {
                    case Const.TEXTURE_FORMAT_EXT  : list = texturesForAdd;  break;
                    case Const.MATERIAL_FORMAT_EXT : list = materialsForAdd; break;
                    case Const.SOUND_FORMAT_EXT    : list = soundsForAdd;    break;
                    
                    default:
                        break;
                }

                if (list != null) {
                    list.add(path);
                }
            }
        }
        PathProcessor pathProcessor = new PathProcessor();
        
        
        if (path != null) {
            // это директория
            if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {   
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {                    
                            pathProcessor.process(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.showException(ex);
                }
            // это файл
            } else {
                pathProcessor.process(path);
            }
        }
        
        // добавляем всё, что нашли
        for (Path forAdd : texturesForAdd) {
            if ((Texture.getByPath(forAdd)) == null) {
                new Texture(forAdd);
            }
        }
        for (Path forAdd : materialsForAdd) {
            if ((Material.getByPath(forAdd)) == null) {
                new Material(forAdd);
            }
        }
        for (Path forAdd : soundsForAdd) {
            if ((Sound.getByPath(forAdd)) == null) {
                new Sound(forAdd);
            }
        }
        
        texturesForAdd.clear();
        materialsForAdd.clear();
        soundsForAdd.clear();
    }
    
    /**
     * Рекурсивное копирование всех файлов и папок (включая вложенные подпапки и файлы) в новый путь
     * @param src  Путь, из которого нужно копировать, включая сам путь
     * @param dest Путь, в который нужно копировать src (ПАПКА, куда переносить файлы/папки из src)
     * @return true, если всё копировано, false - возникла ошибка
     */
    public static boolean copy(Path src, Path dest) {
        return repath(src, dest, false);
    }
    
    /**
     * Рекурсивно переименовать или перенести путь
     * @param src  путь, в котором должны смениться ссылки и пути ресурсов
     * @param dest новый путь (во что переименовывать)
     * @return true в случае успеха, false - возникла ошибка
     */
    public static boolean move(Path src, Path dest) {  
        return repath(src, dest, true);
    }
    
    /**
     * При выключенном флаге isCutMode - рекурсивное копирование всех файлов и папок (включая вложенные подпапки и файлы) в новый путь.
     * При включенном флаге isCutMode  - рекурсивно переименовать или перенести путь. Должны измениться пути у содержащихся внутри ресурсов. 
     * @param src  путь, в котором должны смениться ссылки и пути ресурсов
     * @param dest новый путь (во что переименовывать)
     * @param isCutMode Режим вырезания. Если true, то исходные файлы будут удалены, если false, то объекты просто будут скопированы
     * @return true в случае успеха, false - возникла ошибка
     */
    public static boolean repath(Path src, Path dest, boolean isCutMode) {        
        if (src  == null || !Files.exists(src) ||
            dest == null/* ||  Files.exists(dest)*/
           ) {
            return false;
        }
        
        // списки путей для изменения (пара "старый путь" - "новый путь")
        final List<Pair<String, String>> texturesForRepath  = new LinkedList<>();
        final List<Pair<String, String>> materialsForRepath = new LinkedList<>(); 
        final List<Pair<String, String>> soundsForRepath    = new LinkedList<>(); 
        
        // список переносимых путей (откуда - куда)
        final List<Pair<Path, Path>> pathsForMove = new LinkedList();        
        // найденные файлы на перезапись, которые пользователь запретил перезаписывать (пропустил)
        final List<Pair<Path, Path>> pathsForSkip = new LinkedList<>();
        
        // существует?
        if (Files.exists(dest, LinkOption.NOFOLLOW_LINKS)) {         
            // исходный и конечный пути совпадают?
            if (src.compareTo(dest) == 0) {
                // нельзя вырезать себя в себя!
                if (isCutMode) {
                    return false;
                // нельзя копировать себя в себя, но можно поместить рядом копию!
                } else {
                    Path newDest = dest;                
                    while (Files.exists(newDest, LinkOption.NOFOLLOW_LINKS)) {
                        String newName = dest.getParent().toString() + File.separator + "copy of " + newDest.getFileName().toString();
                        newDest = Paths.get(newName);
                    }
                    dest = newDest;
                }
            // папка не та же самая, но с таким именем уже существует
            } else if (Files.isDirectory(dest)) {
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
                            dest = Paths.get(dest.getParent().toString() + "/" + newName);
                        }
                    }
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
        
        /**
         * класс для обработки обнаруженных файлов и папок    
         */
        class PathProcessor {
            /**
             * Обработать путь - добавить его в список на перенос и в список обновления пути, если это известный ресурс
             * @param path Путь для проверки
             */
            public void process(Path path) {
                String oldPathString = getProjectPath(path);
                String newPathString = oldPathString.replace(oldName, newName);

                // если файл уже существует, то сразу отметим его для предварительного удаления
                Path oldPath = Paths.get(oldPathString);
                Path newPath = Paths.get(newPathString);
                
                if (!Files.isDirectory(oldPath)) {
                    // целевой файл существует? Переписать?
                    if (Files.exists(newPath)) {
                        if (!MessageDialog.showConfirmationYesNo("\"" + newPath.toString() + "\"\nуже существует! Перезаписать?")) {
                            pathsForSkip.add(new ImmutablePair<>(oldPath, newPath));
                            return;
                        }                        
                    }
                    
                    // в режиме переноса нужно обновить ссылки
                    if (isCutMode) {
                        String ext = getFileExtension(path);
                        List list = null;
                        
                        // по расширению файла определяем что это
                        switch (ext) {
                            case Const.TEXTURE_FORMAT_EXT  : list = texturesForRepath;  break;
                            case Const.MATERIAL_FORMAT_EXT : list = materialsForRepath; break;
                            case Const.SOUND_FORMAT_EXT    : list = soundsForRepath;    break;  
                            
                            default:
                                break;
                        }
                        
                        if (list != null) {
                            list.add(new ImmutablePair<String, String>(oldPathString, newPathString));
                        }
                    }
                }
                
                // если не существует или дано добро на перезапись, то отмечаем путь
                pathsForMove.add(new ImmutablePair<>(oldPath, newPath));
            }
        }
        PathProcessor pathProcessor = new PathProcessor();

        // ищем в папке содержащиеся ресурсы и помечаем их для замены у них пути
        if (Files.isDirectory(src)) {
            try {
                Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {  
                        pathProcessor.process(file);
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        pathProcessor.process(dir);                        
                        return FileVisitResult.CONTINUE;                        
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }
        } else {            
            pathProcessor.process(src);
        }
        
        // переносим путь
        // если конечный путь существует, то заменяемые файлы необходимо предварительно удалить из проекта
        for (Pair<Path, Path> paths : pathsForMove) {
            Path from = paths.getLeft();
            Path to   = paths.getRight();
            
            // если конечный путь существует
            if (Files.exists(to)) {
                // и это не директория, то удалить из проекта
                if (!Files.isDirectory(to)) {
                    if (!remove(to)) {
                        return false;
                    }
                }                   
            } 

            // копируем/переносим файл?
            if (!Files.isDirectory(from)) {
                try {
                    Files.createDirectories(to.getParent());
                    if (isCutMode) {
                        Files.move(from, to);
                    } else {
                        Files.copy(from, to);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.showException(ex);
                    return false;
                }    
            // папку
            } else {
                try {
                    Files.createDirectories(to);
                } catch (IOException ex) {
                    Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.showException(ex);
                    return false;
                }                        
            }
        }
                
        // если это режим переноса, то
        // заменяем часть пути (или весь) с учетом нового имени папки или файла
        if (isCutMode) {
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
            for (Pair<String, String> paths : soundsForRepath) {
                Sound snd = Sound.getByPath(paths.getLeft());
                if (snd != null) {
                    snd.setPath(paths.getRight());
                }
            } 
            
            // если исходный путь был папкой и нет пропущенных файлов, то смело удалить её
            if (Files.exists(src)      && 
                Files.isDirectory(src) && 
                pathsForSkip.isEmpty()
               ) {
                if (!remove(src)) {
                    return false;
                }
            }
        } else {
            // добавляем ссылки на вновь созданные объекты
            addResourcesFromPath(dest);
        }
        
        texturesForRepath.clear();
        materialsForRepath.clear();
        soundsForRepath.clear();
        pathsForMove.clear();

        return true;
    }
                
    /**
     * Рекурсивное удаление всех файлов и папок (включая вложенные подпапки и файлы) в пути
     * @param path Путь, в котором будет удалено всё, включая сам путь
     * @return true, если удалены, false - возникла ошибка
     */
    public static boolean remove(Path path) {   
        // списки путей для изменения
        final List<String> texturesForDelete  = new LinkedList<>();
        final List<String> materialsForDelete = new LinkedList<>();
        final List<String> soundsForDelete    = new LinkedList<>();
        
        /**
         * класс для обработки обнаруженных файлов и папок    
         */
        class PathProcessor {
            /**
             * Обработать путь - добавить его в список удаления пути, если это известный ресурс
             * @param path Путь для проверки
             */
            public void process(Path path) {                
                String ext = getFileExtension(path);
                List list = null;

                // по расширению файла определяем что это
                switch (ext) {
                    case Const.TEXTURE_FORMAT_EXT  : list = texturesForDelete;  break;
                    case Const.MATERIAL_FORMAT_EXT : list = materialsForDelete; break;
                    case Const.SOUND_FORMAT_EXT    : list = soundsForDelete;    break;
                    
                    default:
                        break;
                }

                if (list != null) {
                    list.add(getProjectPath(path));
                }
                
                try {
                    Files.delete(path);
                } catch (IOException ex) {
                    Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.showException(ex);
                }
            }
        }
        PathProcessor pathProcessor = new PathProcessor();
        
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
                        pathProcessor.process(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
                
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
        // это файл
        } else {   
            pathProcessor.process(path);
        }       
        
        // удаляем из проекта то, что нашли
        // текстуры
        for (String filePath : texturesForDelete) {
            Texture txr = Texture.getByPath(filePath);
            if (txr != null) {
                // закрыть окно с открытой удаляемой текстурой
                if (FormMain.formTextureEditor != null && 
                    FormTextureEditor.selectedTexture != null &&
                    FormTextureEditor.selectedTexture.equals(txr)
                   ) {
                    FormMain.closeFormTextureEditor();
                }
                
                Texture.TEXTURES.remove(txr);
            }
        }
        // материалы
        for (String filePath : materialsForDelete) {
            Material mat = Material.getByPath(filePath);
            if (mat != null) {
                // закрыть окно с открытой удаляемым материалом
                if (FormMain.formMaterialEditor != null && 
                    FormMaterialEditor.selectedMaterial != null &&
                    FormMaterialEditor.selectedMaterial.equals(mat)
                   ) {
                    FormMain.closeFormMaterialEditor();
                }
                
                Material.MATERIALS.remove(mat);
            }
        }
        // звуки
        for (String filePath : soundsForDelete) {
            Sound snd = Sound.getByPath(filePath);
            if (snd != null) {
                // закрыть окно с открытой удаляемым материалом
                if (FormMain.formSoundEditor != null && 
                    FormSoundEditor.selectedSound != null &&
                    FormSoundEditor.selectedSound.equals(snd)
                   ) {
                    FormMain.closeFormSoundEditor();
                }
                
                Sound.SOUNDS.remove(snd);
            }
        }
        
        texturesForDelete.clear();
        materialsForDelete.clear();
        soundsForDelete.clear();
 
        // всё успешно удалено?
        return !Files.exists(path);
    }
    
    private FileSystemUtils(){}
}
