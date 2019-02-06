/**
    Utilities of Nuke3D Editor
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
package com.vuvk.n3d;

import com.vuvk.n3d.resources.Texture;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Utils {
    
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
     * проверка является ли число степенью двойки
     * @param number число для проверки
     * @return true, если является
     */
    public static boolean isPowerOfTwo(int number) {
        return ((number - 1) & number) == 0;
    }
    
    /**
     * возврат ближайшего числа степени двойки к данному (не больше)
     * @param number исходное число
     * @return Правильное число
     */
    public static int getPowerOfTwo(int number) {
        if (isPowerOfTwo(number)) {
            return number;
        }
        
        int res = 0;
        while (number > 1) {
            number >>= 1;
            ++res;
        }
        
        return 1 << res;
    }
    
    /**
     * Изменить размер изображения. Исходное изображение НЕ МЕНЯЕТСЯ
     * @param image изображение для изменения
     * @param width новая ширина
     * @param height новая высота
     * @return новое изображение с заданным размером
     */
    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {       
        // если размеры те же самые, то просто вернуть изображение    
        if (image.getWidth()  == width && 
            image.getHeight() == height && 
            image.getType()   == BufferedImage.TYPE_INT_ARGB
           ) {            
            return image;
        }
        
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return outputImage;
    }
    
    /**
     * Подготовить изображение для импорта
     * Просто подгон размеров картинки под степень двойки и формат ARGB
     * @param image изображение-пациент
     */
    public static BufferedImage prepareImage(BufferedImage image) {
        if (image == null) {
            return Texture.IMAGE_EMPTY;
        }
        
        int width  = getPowerOfTwo(image.getWidth());
        int height = getPowerOfTwo(image.getHeight());
            
        if (width <= 0) {
            width  = 1;
        } else if (width > Const.TEXTURE_MAX_WIDTH) {
            width  = Const.TEXTURE_MAX_WIDTH;
        }
        
        if (height <= 0) {
            height  = 1;
        } else if (height > Const.TEXTURE_MAX_HEIGHT) {
            height  = Const.TEXTURE_MAX_HEIGHT;
        }
                
        return resizeImage(image, width, height);
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
    public static boolean removeFiles(Path path) { 
        File fPath = path.toFile();
        if (fPath.isDirectory()) {            
            try {
                FileUtils.deleteDirectory(fPath);
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } else {
            return fPath.delete();
        }
    }
    
    /**
     * Отобразить сообщение с ошибкой из исключения
     * @param ex Исключение, сообщение которого необходимо отобразить
     */
    public static void showMessageException(Exception ex) {
        ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(stackTrace));
        showMessageError(ex.getMessage() + "\nStackTrace:\n" + stackTrace.toString());
    }
    
    /**
     * Отобразить сообщение с текстом ошибки
     * @param msg Cообщение, которое необходимо отобразить
     */
    public static void showMessageError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
