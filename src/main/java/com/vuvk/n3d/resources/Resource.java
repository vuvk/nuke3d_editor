/**
    Parent class of resources (Nuke3D Editor)
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
package com.vuvk.n3d.resources;

import com.vuvk.n3d.utils.FileSystemUtils;
import com.vuvk.n3d.utils.MessageDialog;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 * Класс-предок всех ресурсов
 * Путь содержит путь относительно корневой папки resources
 * @author Anton "Vuvk" Shcherbatykh
 */
public abstract class Resource {
    /** идентификатор */
    protected long id;
    /** имя */
    protected String name;  
    /** Путь до ресурса */
    protected String path;
    
    /** Initialization */
    protected abstract void init(Path path);
    
    protected Resource() {
        init((Path)null);
    }
    public Resource(Path path) {
        init(path);
    }
    /*public Resource(long id, Path path) {
        setId(id);
        setPath(path);
    }*/
    
    /**
     * Присвоить id 
     * @param id новый идентификатор
     */
    protected void setId(long id) {
        this.id = id;
    }
    
    /**
     * Присвоить имя
     * @param name Новое имя
     */
    protected void setName(String name) {
        this.name = name;
    }
    
    /**
     * Установить путь расположения ресурса
     * @param path Новый путь
     */
    public void setPath(Path path) {
        if (path != null) {
            this.path = FileSystemUtils.getProjectPath(path);   
            setName(FilenameUtils.getBaseName(this.path));
        } else {
            this.path = "";
            setName("");
        }
    }
    
    /**
     * Установить путь расположения ресурса
     * @param path Новый путь
     */
    public void setPath(File path) {
        if (path != null) {
            setPath(path.toPath());
        } else {
            setPath((Path)null);
        }
    }
    
    /**
     * Установить путь расположения ресурса
     * @param path Новый путь
     */
    public void setPath(String path) {
        if (path != null) {
            setPath(Paths.get(path));
        }
    }
    
    /**
     * Получить идентификатор
     * @return long идентификатор ресурса
     */
    public long getId() {
        return id;
    }
    
    /**
     * Получить имя
     * @return name имя ресурса
     */
    public String getName() {
        return name;
    }
    
    /**
     * Получить путь ресурса
     * @return path путь ресурса
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Загрузить из файла
     * @param path Путь до файла
     * @return true в случае успеха
     */
    public boolean load(File path) {
        if (path != null) {
            return load(path.toPath());
        } else {
            return false;
        }        
    }
    
    /**
     * Загрузить из файла
     * @param path Путь до файла
     * @return true в случае успеха
     */
    protected abstract boolean load(Path path);

    /**
     * Сохранить ресурс в файл, к которому он привязан
     * @return true в случае успеха
     */
    protected abstract boolean save();
    
    /**
     * Деструктор
     */
    public abstract void dispose();
    
    /**
     * Деструктор для GC
     */
    public void finalize() {        
        try {
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }
        dispose();        
    }
}
