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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
    
        
    public PreviewElement(File path) {
        this(path.toPath());        
    }    
    public PreviewElement(Path path) {
        this(path, false);
    }    
    public PreviewElement(File path, boolean isUp) {
        this(path.toPath(), isUp);        
    }    
    
    /**
     * Инициализация элемента
     * @param path Путь, которому соответствует элемент
     * @param isUp Это кнопка "ВВЕРХ"?
     */
    public PreviewElement(Path path, boolean isUp) {        
        this.path = FileSystemUtils.getProjectPath(path);      
        this.fileName = path.getFileName().toString();
        
        if (Files.isDirectory(path)) {
            if (isUp) {
                type = Type.LEVELUP;
                name = "Вверх";
                icon = Const.ICONS.get("LevelUp");
            } else {
                type = Type.FOLDER;
                name = fileName;
                icon = Const.ICONS.get("Folder");
            }
            
            extension = "";
        } else {
            extension = FileSystemUtils.getFileExtension(path.toFile());
            switch (extension) {
                case Const.TEXTURE_FORMAT_EXT:
                    try {
                        icon = ImageUtils.resizeImage(ImageIO.read(path.toFile()), Const.ICON_PREVIEW_WIDTH, Const.ICON_PREVIEW_HEIGHT);
                    } catch (IOException ex) {
                        Logger.getLogger(PreviewElement.class.getName()).log(Level.SEVERE, null, ex);
                        MessageDialog.showException(ex);
                    }
                    type = Type.TEXTURE;
                    break;
                
                case Const.MATERIAL_FORMAT_EXT:
                    type = Type.MATERIAL;
                    icon = Const.ICONS.get("Material");
                    break;
                    
                case Const.SOUND_FORMAT_EXT:
                    type = Type.SOUND;
                    icon = Const.ICONS.get("Sound");
                    break;
                    
                default :
                    type = Type.UNKNOWN;
                    name = fileName;
                    icon = Const.ICONS.get("Unknown");
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
