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

import com.vuvk.n3d.Utils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Класс-предок всех ресурсов
 * Путь содержит путь относительно корневой папки resources
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Resource {
    /** идентификатор */
    protected long id;
    /** имя */
    protected String name;  
    /** Путь до ресурса */
    protected String path;
    
    
    public Resource() {
        //setPath((Path)null);
    }
    public Resource(Path path) {
        setId(0);
        setPath(path);
    }
    public Resource(long id, Path path) {
        setId(id);
        setPath(path);
    }
    
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
    protected void setPath(Path path) {
        if (path != null) {
            this.path = Utils.getProjectPath(path);   
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
    protected void setPath(File path) {
        if (path != null) {
            setPath(path.toPath());
        } else {
            setPath((Path)null);
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
}
