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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Класс хранимой текстуры в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class Texture extends Resource {   
    /** максимальная ширина текстуры */
    public static final int MAX_WIDTH  = 512;
    /** максимальная высота текстуры */
    public static final int MAX_HEIGHT = 512;    
    /** расширение файла импортированной текстуры */
    public static final String FORMAT_EXT = "png";
    /** доступные расширения текстур для загрузки */
    public static final List<String> EXTS = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");
    /** Путь до сохранённых параметров текстур */
    public static final String CONFIG_STRING = Const.CONFIG_STRING + "textures.sav";
    /** идентификатор конфига текстур */
    public static final String CONFIG_IDENTIFICATOR = "N3D_TEXTURES";
    /** версия конфига текстур */
    static final int CONFIG_MAJOR = 0;
    static final int CONFIG_MINOR = 1;
    public static final String CONFIG_VERSION = CONFIG_MAJOR + "." + CONFIG_MINOR;
    
    /** изображение текстуры */
    private BufferedImage image;
    
    /** пустое изображение */
    public static final BufferedImage IMAGE_EMPTY = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    /** пустая текстура */
    //public static final Texture TEXTURE_EMPTY = new Texture();
    /** Список всех текстур (контейнер) */
    public static final ArrayList<Texture> TEXTURES = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(Texture.class.getName());
    
    /**
     * Проверка является ли указанный путь текстурой
     * @param path путь для проверки
     * @return true, если по указанному пути текстура
     */
    public static boolean pathIsTexture(Path path) {
        return (path != null &&
                Files.exists(path) && 
                !Files.isDirectory(path) && 
                FileSystemUtils.getFileExtension(path).equals(FORMAT_EXT));
    }
    
    /**
     * Загрузить конфиг текстур и сами текстуры
     * @return true в случае успеха
     */
    public static boolean loadAll() {
        closeAll();
        
        File textureConfig = new File(CONFIG_STRING);
        
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
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
              
        // проверяем правильность конфига
        if (!Resource.checkConfig(config, 
                                  CONFIG_IDENTIFICATOR, 
                                  Double.parseDouble(CONFIG_VERSION))
           ) {
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
            if (pathIsTexture(path)) {
                // добавляем в базу новую текстуру и задаём ей Id
                new Texture(path)
                    .setId(jsonId.getAsInt());
            }
        }        
        
        return true;
    }
    
    /** 
     * Сохранить конфиг всех текстур 
     * @return true в случае успеха
     */
    public static boolean saveConfig() {        
        // создадим папку с конфигами, если нужно
        if (!Files.exists(Global.CONFIG_PATH)) {
            try {
                Files.createDirectory(Global.CONFIG_PATH);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
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
        config.addProperty("identificator", CONFIG_IDENTIFICATOR);
        config.addProperty("version", CONFIG_VERSION);
        config.add("data", array);
        
        // сохраняем конфиг текстур
        try (Writer writer = new FileWriter(CONFIG_STRING)) { 
            Gson gson = new GsonBuilder().create();   
            gson.toJson(config, writer);             
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Удалить все текстуры из памяти
     * @return true в случае успеха
     */
    public static boolean closeAll() {
        TEXTURES.clear();        
        return (TEXTURES.isEmpty());
    }
            
    public Texture(File path) {
        this(path.toPath());
    }
    public Texture(Path path) {
        super(path);
        load(path);
    }
        
    /**
     * Загрузить image текстуры из файла
     * @param path Путь до файла
     * @return true в случае успеха
     */
    protected boolean load(Path path) {
        /** если файл существует и он является текстурой */
        if (pathIsTexture(path)) {
            try {
                image = ImageUtils.prepareImage(ImageIO.read(path.toFile()));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                image = IMAGE_EMPTY;
                return false;
            }
        } else {
            image = IMAGE_EMPTY;
        }
        return true;
    }
    
    /**
     * Сохранить текстуру в файл, к которому она привязана
     * @return true в случае успеха
     */
    public boolean save() {
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        return true;
    }
    
    /**
     * Деструктор
     */
    public void dispose() {
        super.dispose();
        image = null;
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
    
    @Override
    protected List getContainer() {
        return TEXTURES;
    }
}