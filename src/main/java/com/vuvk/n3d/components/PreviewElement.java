/**
    Icon in project view (Nuke3D Editor)
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
package com.vuvk.n3d.components;

import com.vuvk.n3d.Const;
import com.vuvk.n3d.utils.FileSystemUtils;
import com.vuvk.n3d.utils.ImageUtils;
import com.vuvk.n3d.utils.MessageDialog;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;


public class PreviewElement {
    /** типы элементов */
    public static enum Type {
        LEVELUP,    // вверх
        FOLDER,     // папка
        TEXTURE,    // текстура
        MATERIAL,   // материал
        SOUND,      // звуковой файл
        UNKNOWN     // неизвестная фигня
    }
    /** набор иконок */
    public static ArrayList<BufferedImage> icons = null;    

    /** тип элемента */
    private Type type;
    /** полный путь относительно папки ресурсов (путь проекта) */
    private String path;
    /** имя файла без пути */
    private String fileName;
    /** имя элемента */
    private String name;
    /** расширение файла, если есть */
    private String extension;
    /** какую иконку рисовать с этим элементом */
    private BufferedImage icon;
    
    /**
     * Загрузка иконок для элементов
     */
    private void loadIcons() {
        if (icons == null) {
            icons = new ArrayList<>();
            try {
                icons.add(ImageIO.read(getClass().getResource("/com/vuvk/n3d/ico/ic_arrow_upward_white_48dp.png")));        // Level Up
                icons.add(ImageIO.read(getClass().getResource("/com/vuvk/n3d/ico/ic_folder_open_white_48dp.png")));         // Folder
                icons.add(ImageIO.read(getClass().getResource("/com/vuvk/n3d/ico/ic_color_lens_white_48dp.png")));          // Material
                icons.add(ImageIO.read(getClass().getResource("/com/vuvk/n3d/ico/ic_music_note_white_48dp.png")));          // Audio
                icons.add(ImageIO.read(getClass().getResource("/com/vuvk/n3d/ico/ic_insert_drive_file_white_48dp.png")));   // Unknown
                
            } catch (IOException ex) {
                Logger.getLogger(PreviewElement.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
        }
    }
    
    /**
     * Инициализация элемента
     * @param path Путь, которому соответствует элемент
     * @param isUp Это кнопка "ВВЕРХ"?
     */
    private void init(Path path, boolean isUp) {
        loadIcons();
        
        this.path = FileSystemUtils.getProjectPath(path);      
        this.fileName = path.getFileName().toString();
        
        if (Files.isDirectory(path)) {
            if (isUp) {
                type = Type.LEVELUP;
                name = "Вверх";
                icon = icons.get(0);
            } else {
                type = Type.FOLDER;
                name = fileName;
                icon = icons.get(1);
            }
            
            extension = "";
        } else {
            extension = FileSystemUtils.getFileExtension(path.toFile());
            switch (extension) {
                case "txr":
                    try {
                        icon = ImageUtils.resizeImage(ImageIO.read(path.toFile()), Const.ICON_PREVIEW_WIDTH, Const.ICON_PREVIEW_HEIGHT);
                    } catch (IOException ex) {
                        Logger.getLogger(PreviewElement.class.getName()).log(Level.SEVERE, null, ex);
                        MessageDialog.showException(ex);
                    }
                    type = Type.TEXTURE;
                    break;
                
                case "mat":
                    type = Type.MATERIAL;
                    icon = icons.get(2);
                    break;
                    
                case "wav":
                case "mp3":
                case "ogg":
                    type = Type.SOUND;
                    icon = icons.get(3);
                    break;
                    
                default :
                    type = Type.UNKNOWN;
                    name = fileName;
                    icon = icons.get(4);
                    break;
            }  
        }    

        // отрезать расширение 
        switch (type) {
            case TEXTURE:
            case MATERIAL:
            case SOUND:
                name = FilenameUtils.getBaseName(fileName);
                break;
        }
    }
    
    /**
     * Инициализация элемента, которому соответствует реальный путь
     * @param path Путь, которому соответствует элемент
     */
    private void init(Path path) {
        init(path, false);
    }
    
    public PreviewElement(File path) {
        init(path.toPath());        
    }    
    public PreviewElement(Path path) {
        init(path);
    }    
    public PreviewElement(File path, boolean isUp) {
        init(path.toPath(), isUp);        
    }    
    public PreviewElement(Path path, boolean isUp) {
        init(path, isUp);
    }
    
    /**
     * Задать имя элемента
     * @param name Новое имя
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Получить соответствующее имя файла
     * @return 
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Получить имя элемента
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Получить расширение файла
     * @return 
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Получить иконку объекта
     * @return 
     */
    public BufferedImage getIcon() {
        return icon;
    }
    
    /**
     * Получить тип объекта
     * @return 
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Получить хранимый объектом путь
     * @return 
     */
    public String getPath() {
        return path;
    }
}
