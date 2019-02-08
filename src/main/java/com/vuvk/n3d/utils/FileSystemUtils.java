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
import com.vuvk.n3d.editor.forms.FormMain;
import com.vuvk.n3d.resources.Texture;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class FileSystemUtils {
    private FileSystemUtils(){}
    
    
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
     * Рекурсивное удаление всех файлов и папок (включая вложенные подпапки и файлы) в пути
     * @param path Путь, в котором будет удалено всё, включая сам путь
     * @return true, если удалены, false - возникла ошибка
     */
    /*public static boolean removeFiles(Path path) { 
        File fPath = path.toFile();
        if (fPath.isDirectory()) {            
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(fPath);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } else {
            return fPath.delete();
        }
    }*/    
    
    /**
     * Удалить файл с проверкой на тип объекта (с удалением из настроек проекта)
     * @param path Путь, по которому располагается файл
     * @return true, если удален, false - это не файл или возникла ошибка
     */
    static boolean deleteFile(Path path) {   
        if (Files.exists(path) && !Files.isDirectory(path)) {
            
            // по расширению файла определяем что это
            // для того, чтобы удалить файл из настроек проекта
            switch (getFileExtension(path)) {
                case "txr" :
                    String filePath = getProjectPath(path);
                    for (Iterator it = Texture.TEXTURES.iterator(); it.hasNext(); ) {
                        if (((Texture)it.next()).getPath().equals(filePath)) {
                            it.remove();
                        }
                    }
                    FormMain.closeFormTextureEditor();
                    break;
            }

            return path.toFile().delete();
        }
        
        return false;
    }
    
    /**
     * Рекурсивное удаление всех файлов и папок в пути
     * @param path Путь, в котором будет удалено всё, включая сам путь
     * @return true, если удалены, false - возникла ошибка
     */
    public static boolean recursiveRemoveFiles(Path path) {
        // пробежаться по содержимому
        boolean finished = true;        
        File fPath = path.toFile();
        // путь это папка?
        if (fPath.isDirectory()) {
            for (File file : fPath.listFiles()) {
                if (file.isDirectory()) {
                    if (!recursiveRemoveFiles(file.toPath())) {
                        finished = false;
                    }
                } else {
                    if (!deleteFile(path)) {
                        finished = false;
                    }
                }
            }
            
            // удалить сам путь
            try {
                Files.delete(path);
            } catch (IOException ex) {
                Logger.getLogger(FileSystemUtils.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                finished = false;
            }
        // по пути передан файл
        } else {
            if (!deleteFile(path)) {
                finished = false;
            }
        }        
        
        return finished;
    }

}
