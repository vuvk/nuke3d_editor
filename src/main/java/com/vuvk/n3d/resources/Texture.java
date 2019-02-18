/**
    Texture class (Nuke3D Editor)
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vuvk.n3d.Const;
import com.vuvk.n3d.Global;
import com.vuvk.n3d.utils.FileSystemUtils;
import com.vuvk.n3d.utils.ImageUtils;
import com.vuvk.n3d.utils.MessageDialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

/**
 * Класс хранимой текстуры в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class Texture extends Resource {        
    /** изображение текстуры */
    private BufferedImage image;
    
    /** пустое изображение */
    public static final BufferedImage IMAGE_EMPTY = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    /** пустая текстура */
    //public static final Texture TEXTURE_EMPTY = new Texture();
    /** Список всех текстур (контейнер) */
    public static final ArrayList<Texture> TEXTURES = new ArrayList<>();
    
    
    /**
     * Загрузить конфиг текстур и сами текстуры
     */
    public static boolean loadAll() {
        closeAll();
        
        File textureConfig = new File(Const.TEXTURE_CONFIG_STRING);
        
        if (!Files.exists(Global.CONFIG_PATH) || 
            !textureConfig.exists()) {
            return false;
        }
        
        // читаем конфиг
        JsonObject config = new JsonObject();        
        try (Reader reader = new FileReader(textureConfig)){
            Gson gson = new GsonBuilder().create();  
            config = gson.fromJson(reader, JsonObject.class);
        } catch (Exception ex) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        
        // проверяем правильность конфига
        // идентификатор
        JsonElement jsonIdentificator = config.get("identificator");
        if (jsonIdentificator == null || 
            !jsonIdentificator.getAsString().equals(Const.TEXTURE_CONFIG_IDENTIFICATOR)
           ) {
            return false;
        }
        // версия
        JsonElement jsonVersion = config.get("version");
        if (jsonVersion == null) {
            return false;
        }
        double configVersion = jsonVersion.getAsDouble();
        double editorVersion = Double.parseDouble(Const.TEXTURE_CONFIG_VERSION);
        if (editorVersion < configVersion) {
            return false;
        }
        // данные
        JsonElement jsonData = config.get("data");
        if (jsonData == null) {
            return false;
        }
        
        // создаем текстуры по данным из конфига
        for (JsonElement element : jsonData.getAsJsonArray()) {
            JsonElement jsonId   = ((JsonObject)element).get("id");
            JsonElement jsonPath = ((JsonObject)element).get("path");
            
            if (jsonId == null || jsonPath == null) {
                continue;
            }
            
            // если текстура существует
            Path path = Paths.get(jsonPath.getAsString());
            if (Files.exists(path) && 
                !Files.isDirectory(path) && 
                FileSystemUtils.getFileExtension(path).equals(Const.TEXTURE_FORMAT_EXT)
               ) {
                // добавляем в базу новую текстуру и задаём ей Id
                new Texture(path)
                    .setId(jsonId.getAsInt());
            }
        }        
        
        return true;
    }
    
    /** 
     * Сохранить конфиг всех текстур 
     */
    public static boolean saveConfig() {        
        // создадим папку с конфигами, если нужно
        if (!Files.exists(Global.CONFIG_PATH)) {
            try {
                Files.createDirectory(Global.CONFIG_PATH);
            } catch (IOException ex) {
                Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }
        }
        
        // формируем конфиг с описанием текстур проекта
        JsonArray array = new JsonArray(TEXTURES.size());
        for (Texture txr : TEXTURES) {
            JsonObject object = new JsonObject();
            object.addProperty("id", txr.getId());
            object.addProperty("path", txr.getPath());
            array.add(object);
        }
        JsonObject config = new JsonObject();
        config.addProperty("identificator", Const.TEXTURE_CONFIG_IDENTIFICATOR);
        config.addProperty("version", Const.TEXTURE_CONFIG_VERSION);
        config.add("data", array);
        
        // сохраняем конфиг текстур
        try (Writer writer = new FileWriter(Const.TEXTURE_CONFIG_STRING)) { 
            Gson gson = new GsonBuilder().create();   
            gson.toJson(config, writer);             
        } catch (IOException ex) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Удалить все текстуры из памяти
     */
    public static boolean closeAll() {
        TEXTURES.clear();        
        return (TEXTURES.isEmpty());
    }
            
    protected void init(Path path) {   
        // ищем максимальный id и инкрементируем его
        long newId = 0;
        for (Iterator it = TEXTURES.iterator(); it.hasNext(); ) {
            Texture txr = (Texture)it.next();
            if (txr.getId() > newId) {
                newId = txr.getId();
            }
        }
        ++newId;
        
        setId(newId);
        setPath(path);   
        load(path);
        TEXTURES.add(this);
    }
    
    public Texture(Path path) {
        super(path);
    }
    public Texture(File path) {
        super(path.toPath());
    }
        
    /**
     * Загрузить image текстуры из файла
     * @param path Путь до файла
     */
    protected void load(Path path) {
        /** если файл существует и он является текстурой */
        if (Files.exists(path) &&
            !Files.isDirectory(path) &&  
            FilenameUtils.isExtension(path.getFileName().toString(), Const.TEXTURE_FORMAT_EXT)
           ) {
            try {
                image = ImageUtils.prepareImage(ImageIO.read(path.toFile()));
            } catch (IOException ex) {
                Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
                image = IMAGE_EMPTY;
            }
        } else {
            image = IMAGE_EMPTY;
        }
    }
    
    /**
     * Сохранить текстуру в файл, к которому она привязана
     */
    public void save() {
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException ex) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }
    }
    
    /**
     * Деструктор
     */
    public void dispose() {
        image = null;
        TEXTURES.remove(this);
    }
        
    /**
     * Получить изображение
     * @return image изображение текстуры
     */
    public BufferedImage getImage() {
        return image;
    }
    
    /**
     * Присвоить изображение
     * @param image новое изображение
     */
    public void setImage(BufferedImage image) {
        this.image = ImageUtils.prepareImage(image);
    }
    
    /**
     * Очистить изображение
     */
    public void clearImage() {
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
    
    /**
     * Получить ссылку на текстуру по пути до файла
     * @param path Путь до файла
     * @return Текстура, если есть такая в базе, иначе null
     */
    public static Texture getByPath(Path path) {
        if (Files.exists(path) && 
            !Files.isDirectory(path) && 
            FileSystemUtils.getFileExtension(path).equals(Const.TEXTURE_FORMAT_EXT)
           ) {
            String checkPath = FileSystemUtils.getProjectPath(path);
            for (Texture txr : TEXTURES) {
                if (txr.getPath().equals(checkPath)) {
                    return txr;
                }
            }
        }
        
        return null;
    }
}