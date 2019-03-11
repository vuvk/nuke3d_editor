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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vuvk.n3d.utils.FileSystemUtils;
import com.vuvk.n3d.utils.MessageDialog;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 * Класс-предок всех ресурсов
 * Путь содержит путь относительно корневой папки resources
 * @author Anton "Vuvk" Shcherbatykh
 */
public abstract class Resource {
    
    /**
     * типы ресурсов
     */
    public static enum Type {
        TEXTURE,
        MATERIAL,
        SOUND,
        SKYBOX
    }
    
    
    /** идентификатор */
    protected long id;
    /** имя */
    protected String name;  
    /** Путь до ресурса */
    protected String path;
    
    
    protected Resource(Path path) {        
        List<Resource> container = getContainer();
        
        // ищем максимальный id и инкрементируем его
        long newId = 0;
        for (Resource res : container) {
            if (res.getId() > newId) {
                newId = res.getId();
            }
        }
        ++newId;
        
        setId(newId);
        setPath(path);   
        container.add(this);        
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
     * Получить хранилище всех ресурсов данного типа
     * @return Список ресурсов
     */
    protected abstract List getContainer();
    
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
     * Проверить конфиг ресурса на валидность
     * @param config Ссылка на json-объект конфига
     * @param identificator Нужный идентификатор
     * @param version Версия формата
     * @return true, если конфиг валидный
     */
    protected static boolean checkConfig(JsonObject config, String identificator, double version) {
        if (config == null || identificator == null) {
            return false;
        }
        
        // идентификатор
        JsonElement jsonIdentificator = config.get("identificator");
        if (jsonIdentificator == null || 
            !jsonIdentificator.getAsString().equals(identificator)
           ) {
            return false;
        }
        
        // версия
        JsonElement jsonVersion = config.get("version");
        if (jsonVersion == null) {
            return false;
        }
        double configVersion = jsonVersion.getAsDouble();
        if (version < configVersion) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Получить ссылку на ресурс по пути до файла
     * @param path Путь до файла
     * @param type Тип ресурса для поиска
     * @return ресурс, если есть такой в базе, иначе null
     */
    public static Resource getByPath(String path, Type type) {
        List searchList = null;
        
        switch (type) {
            case TEXTURE:
                searchList = Texture.TEXTURES;
                break;
                
            case MATERIAL:
                searchList = Material.MATERIALS;
                break;
                
            case SOUND:
                searchList = Sound.SOUNDS;
                break;
                
            case SKYBOX:
                searchList = Skybox.SKYBOXES;
                break;
        }
        
        if (searchList != null) {
            for (Iterator it = searchList.iterator(); it.hasNext(); ) {
                Resource res = (Resource) it.next();
                if (res.getPath().equals(path)) {
                    return res;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Получить ссылку на ресурс по пути до файла
     * @param path Путь до файла
     * @param type Тип ресурса для поиска
     * @return ресурс, если есть такой в базе, иначе null
     */
    public static Resource getByPath(Path path, Type type) {
        return getByPath(path.toString(), type);
    }
    
    /**
     * Получить ссылку на неизвестный ресурс по пути до файла. Долго
     * @param path Путь до файла
     * @return ресурс, если есть такой в базе, иначе null
     */
    public static Resource getByPath(String path) {
        Resource res = null;
        if ((res = getByPath(path, Type.TEXTURE)) != null) {
            return res;
        } else if ((res = getByPath(path, Type.MATERIAL)) != null) {
            return res;
        } else if ((res = getByPath(path, Type.SOUND)) != null) {
            return res;
        } else if ((res = getByPath(path, Type.SKYBOX)) != null) {
            return res;
        }
        return null;
    }
    
    /**
     * Получить ссылку на неизвестный ресурс по пути до файла. Долго
     * @param path Путь до файла
     * @return ресурс, если есть такой в базе, иначе null
     */
    public static Resource getByPath(Path path) {        
        return getByPath(path.toString());
    }
    
    /**
     * Получить ссылку на ресурс по id
     * @param id Идентификатор материала
     * @param type Тип ресурса для поиска
     * @return ресурс, если есть такой в базе, иначе null
     */
    public static Resource getById(long id, Type type) {
        List searchList = null;
        
        switch (type) {
            case TEXTURE:
                searchList = Texture.TEXTURES;
                break;
                
            case MATERIAL:
                searchList = Material.MATERIALS;
                break;
                
            case SOUND:
                searchList = Sound.SOUNDS;
                break;
                
            case SKYBOX:
                searchList = Skybox.SKYBOXES;
                break;
        }
        
        if (searchList != null) {
            for (Iterator it = searchList.iterator(); it.hasNext(); ) {
                Resource res = (Resource) it.next();
                if (res.getId() == id) {
                    return res;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Деструктор
     */
    public void dispose() {
        getContainer().remove(this);
    }
    
    /**
     * Деструктор для GC
     */
    public void finalize() { 
        dispose();         
        try {
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }      
    }
}
