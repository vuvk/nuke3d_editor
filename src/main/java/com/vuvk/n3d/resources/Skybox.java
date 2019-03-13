/**
    Skybox class (Nuke3D Editor)
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
import com.vuvk.n3d.utils.MessageDialog;
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

/**
 * Класс хранимого скайбокса в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Skybox extends Resource {    
    /**
     * стороны скайбокса
     */
    public static enum Side {        
        FRONT(0), BACK  (1),
        LEFT (2), RIGHT (3),
        TOP  (4), BOTTOM(5);
        
        // номер стороны в массиве сторон
        private int num;
        public int getNum() {
            return num;
        }
        
        Side(int num) {
            this.num = num;
        }        
        
        /** 
         * Получить сторону по номеру
         * @param num номер стороны в промежутке [0-5]
         * @return Константа енумератора: 0 - FRONT, 1 - BACK, 2 - LEFT, 3 - RIGHT, 4 - TOP, 5 - BOTTOM
         */
        public static Side getByNum(int num) {
            if (num < 0) {
                num = 0;
            } else if (num >= 6) {
                num = 5;
            }
            
            switch (num) {
                case 0 : return FRONT;
                case 1 : return BACK;
                case 2 : return LEFT;
                case 3 : return RIGHT;
                case 4 : return TOP;
                default: return BOTTOM;
            }
        }
    }
    
    /**
     * текстуры для сторон куба
     */
    private final Texture[] sides = new Texture[6]; // 0 - FRONT, 1 - BACK, 2 - LEFT, 3 - RIGHT, 4 - TOP, 5 - BOTTOM
    
    /** Список всех скайбоксов (контейнер) */
    public static final ArrayList<Skybox> SKYBOXES = new ArrayList<>();

    private static final Logger LOG = Logger.getLogger(Skybox.class.getName());
    
    
    /**
     * Проверка является ли указанный путь скайбоксом
     * @param path путь для проверки
     * @return true, если по указанному пути скайбокс
     */
    public static boolean pathIsSkybox(Path path) {
        return (path != null &&
                Files.exists(path) && 
                !Files.isDirectory(path) && 
                FileSystemUtils.getFileExtension(path).equals(Const.SKYBOX_FORMAT_EXT));
    }
    
    /**
     * Загрузить конфиг и сами скайбоксы
     * @return true в случае успеха
     */
    public static boolean loadAll() {
        closeAll();
        
        File skyboxConfig = new File(Const.SKYBOXES_CONFIG_STRING);
        
        if (!Files.exists(Global.CONFIG_PATH) || 
            !skyboxConfig.exists()) {
            return false;
        }
        
        // читаем конфиг
        JsonObject config = new JsonObject();        
        try (Reader reader = new FileReader(skyboxConfig)){
            Gson gson = new GsonBuilder().create();  
            config = gson.fromJson(reader, JsonObject.class);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        
        // проверяем правильность конфига
        if (!Resource.checkConfig(config, 
                                  Const.SKYBOXES_CONFIG_IDENTIFICATOR, 
                                  Double.parseDouble(Const.SKYBOXES_CONFIG_VERSION))
           ) {
            return false;
        }
        
        // данные
        JsonElement jsonData = config.get("data");
        if (jsonData == null) {
            return false;
        }
        
        // создаем небеса по данным из конфига
        for (JsonElement element : jsonData.getAsJsonArray()) {
            JsonElement jsonId   = ((JsonObject)element).get("id");
            JsonElement jsonPath = ((JsonObject)element).get("path");
            
            if (jsonId == null || jsonPath == null) {
                continue;
            }
            
            // если существует
            Path path = Paths.get(jsonPath.getAsString());
            if (pathIsSkybox(path)) {
                // добавляем в базу
                new Skybox(path)
                    .setId(jsonId.getAsInt());
            }
        }
        
        // на всякий случай проверим валидность всех небес
        checkAll();
        
        return true;
    }
    
    /** 
     * Сохранить все небеса
     * @return true в случае успеха
     */
    public static boolean saveAll() {
        boolean allOk = true;
        
        for (Skybox sky : SKYBOXES) {
            if (!sky.save()) {
                allOk = false;
            }
        }
        
        return allOk;
    }
    
    /** 
     * Сохранить конфиг всех небес 
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
        
        // формируем конфиг с описанием
        JsonArray array = new JsonArray(SKYBOXES.size());
        for (Skybox sky : SKYBOXES) {
            JsonObject object = new JsonObject();
            object.addProperty("id", sky.getId());
            object.addProperty("path", sky.getPath());
            array.add(object);
        }
        JsonObject config = new JsonObject();
        config.addProperty("identificator", Const.SKYBOXES_CONFIG_IDENTIFICATOR);
        config.addProperty("version", Const.SKYBOXES_CONFIG_VERSION);
        config.add("data", array);
        
        // сохраняем конфиг
        try (Writer writer = new FileWriter(Const.SKYBOXES_CONFIG_STRING)) { 
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
     * Удалить все скайбоксы из памяти
     * @return true в случае успеха
     */
    public static boolean closeAll() {
        SKYBOXES.clear();        
        return (SKYBOXES.isEmpty());
    }
    
    /**
     * Проверка всех сторон всех скайбоксов на наличие текстуры в базе.
     * Если текстуры нет в базе, то она будет ОБНУЛЕНА.
     */
    public static void checkAll() {
        for (Skybox skybox : SKYBOXES) {
            skybox.check();
        }
    }
    
    

    public Skybox(Path path) {
        super(path);     
        
        if (Files.exists(path)) {
            load(path);
        } else {
            save(); 
        }
    }
    
    /**
     * Установить текстуру на сторону
     * @param txr Устанавливаемая текстура
     * @param side Сторона, на которую нужно установить текстуру
     */
    public void setTexture(Texture txr, Side side) {
        sides[side.getNum()] = txr;
    }
    
    /**
     * Получить текстуру на стороне
     * @param side Сторона, текстуру которой нужно получить
     * @return Текстура, если установлена
     */
    public Texture getTexture(Side side) {
        return sides[side.getNum()];
    }
    
    /**
     * Очистить установленные на стороны текстуры
     */
    public void clear() {
        Arrays.fill(sides, null);
    }
    
    /**
     * Деструктор
     */
    @Override
    public void dispose() {
        super.dispose();
        clear();
    }
    
    /**
     * Проверить существуют ли текстуры, использующиеся для сторон куба.
     * Несуществующие будут ОБНУЛЕНЫ.
     * @return true, если все существуют
     */
    public boolean check() {
        boolean result = true;
        
        for (int i = 0; i < sides.length; ++i) {
            if (sides[i] != null && !Texture.TEXTURES.contains(sides[i])) {
                sides[i] = null;
                result = false;
            }
        }
        
        return result;
    }
    
    @Override
    protected List getContainer() {
        return SKYBOXES;
    }
        
    /**
     * Загрузить скайбокс из файла
     * @param path Путь до файла
     * @return true в случае успеха
     */
    @Override
    protected boolean load(Path path) {
        clear();
        
        /** если файл существует и он является текстурой */
        if (pathIsSkybox(path)) {            
            // читаем конфиг
            JsonObject config = new JsonObject();        
            try (Reader reader = new FileReader(path.toFile())) {
                Gson gson = new GsonBuilder().create();  
                config = gson.fromJson(reader, JsonObject.class);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }

            // проверяем правильность конфига
            // идентификатор
            JsonElement jsonIdentificator = config.get("identificator");
            if (jsonIdentificator == null || 
                !jsonIdentificator.getAsString().equals(Const.SKYBOX_IDENTIFICATOR)
               ) {
                return false;
            }
            // версия
            JsonElement jsonVersion = config.get("version");
            if (jsonVersion == null) {
                return false;
            }
            double configVersion = jsonVersion.getAsDouble();
            double editorVersion = Double.parseDouble(Const.SKYBOX_VERSION);
            if (editorVersion < configVersion) {
                return false;
            }   
            // стороны
            JsonElement jsonSides = config.get("sides");
            if (jsonSides == null) {
                return false;
            }
            JsonArray jsonSidesArray = jsonSides.getAsJsonArray();
            // получаем текстуры по данным из конфига
            for (int i = 0; i < 6; ++i) {
                JsonElement element = jsonSidesArray.get(i);
                if (element == null) {
                    continue;
                }
                
                JsonElement jsonTxrId = ((JsonObject)element).get("texture_id");
                if (jsonTxrId == null) {
                    continue;
                }

                // добавить
                long id = jsonTxrId.getAsLong();
                sides[i] = (Texture) Resource.getById(id, Resource.Type.TEXTURE);
            }
            
            return true;
        }
        
        return false;
    }

    /**
     * Сохранить скайбокс в файл, к которому он привязан
     * @return true в случае успеха
     */
    @Override
    protected boolean save() {
        check();
        
        // информация о сторонах
        JsonArray jsonSides = new JsonArray();
        for (int i = 0; i < 6; ++i) {
            JsonObject jsonSide = new JsonObject();
            Texture txr = sides[i];
            if (txr != null) {
                jsonSide.addProperty("texture_id", txr.getId());
            } else {
                jsonSide.addProperty("texture_id", -1);                
            }
            
            jsonSides.add(jsonSide);
        }
        
        // общая информация об объекте
        JsonObject object = new JsonObject();
        object.addProperty("identificator", Const.SKYBOX_IDENTIFICATOR);
        object.addProperty("version", Const.SKYBOX_VERSION);
        object.add("sides", jsonSides);
        
        // сохраняем в привязанный файл
        try (Writer writer = new FileWriter(getPath())) { 
            Gson gson = new GsonBuilder().create();   
            gson.toJson(object, writer);             
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
        
        return true;
    }
}
