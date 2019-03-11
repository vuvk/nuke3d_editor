/**
    Sound class (Nuke3D Editor)
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс хранимого звука в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Sound extends Resource {
    
    /** Список всех звуков (контейнер) */
    public static final ArrayList<Sound> SOUNDS = new ArrayList<>();

    /**
     * Загрузить конфиг звуков
     * @return true в случае успеха
     */
    public static boolean loadAll() {
        closeAll();
        
        File soundConfig = new File(Const.SOUND_CONFIG_STRING);
        
        if (!Files.exists(Global.CONFIG_PATH) || 
            !soundConfig.exists()) {
            return false;
        }
        
        // читаем конфиг
        JsonObject config = new JsonObject();        
        try (Reader reader = new FileReader(soundConfig)){
            Gson gson = new GsonBuilder().create();  
            config = gson.fromJson(reader, JsonObject.class);
        } catch (Exception ex) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
            return false;
        }
              
        // проверяем правильность конфига
        if (!Resource.checkConfig(config, 
                                  Const.SOUND_CONFIG_IDENTIFICATOR, 
                                  Double.parseDouble(Const.SOUND_CONFIG_VERSION))
           ) {
            return false;
        }
        
        // данные
        JsonElement jsonData = config.get("data");
        if (jsonData == null) {
            return false;
        }
        
        // создаем звуки по данным из конфига
        for (JsonElement element : jsonData.getAsJsonArray()) {
            JsonElement jsonId      = ((JsonObject)element).get("id");
            JsonElement jsonIsMusic = ((JsonObject)element).get("is_music");
            JsonElement jsonPath    = ((JsonObject)element).get("path");
            
            if (jsonId == null || jsonPath == null) {
                continue;
            }
            
            // если текстура существует
            Path path = Paths.get(jsonPath.getAsString());
            if (pathIsSound(path)) {
                // добавляем в базу
                Sound snd = new Sound(path);
                snd.setId(jsonId.getAsInt());
                if (jsonIsMusic != null) {
                    snd.setMusic(jsonIsMusic.getAsBoolean());
                }
            }
        }        
        
        return true;
    }

    /** 
     * Сохранить конфиг всех звуков 
     * @return true в случае успеха
     */
    public static boolean saveConfig() {        
        // создадим папку с конфигами, если нужно
        if (!Files.exists(Global.CONFIG_PATH)) {
            try {
                Files.createDirectory(Global.CONFIG_PATH);
            } catch (IOException ex) {
                Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return false;
            }
        }
        
        // формируем конфиг с описанием звуков проекта
        JsonArray array = new JsonArray(SOUNDS.size());
        for (Sound snd : SOUNDS) {
            JsonObject object = new JsonObject();
            object.addProperty("id", snd.getId());
            object.addProperty("is_music", snd.isMusic());
            object.addProperty("path", snd.getPath());
            array.add(object);
        }
        JsonObject config = new JsonObject();
        config.addProperty("identificator", Const.SOUND_CONFIG_IDENTIFICATOR);
        config.addProperty("version", Const.SOUND_CONFIG_VERSION);
        config.add("data", array);
        
        // сохраняем конфиг текстур
        try (Writer writer = new FileWriter(Const.SOUND_CONFIG_STRING)) { 
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
     * Удалить все звуки из памяти
     * @return true в случае успеха
     */
    public static boolean closeAll() {
        SOUNDS.clear();        
        return (SOUNDS.isEmpty());
    }

    /** Является ли файл музыкой */
    private boolean isMusic = false;
    
    public Sound(File path) {
        this(path.toPath());
    }
    public Sound(Path path) {
        super(path);
    }

    @Override
    protected boolean load(Path path) {
        return true;
    }

    @Override
    protected boolean save() {
        return true;
    }
    
    /**
     * Установить является ли файл фоновой музыкой
     * @param isMusic true - является
     */
    public void setMusic(boolean isMusic) {
        this.isMusic = isMusic;
    }
    
    /**
     * Является ли файл фоновой музыкой
     * @return является
     */
    public boolean isMusic() {
        return isMusic;
    }
    
    @Override
    protected List getContainer() {
        return SOUNDS;
    }
    /**
     * Проверка является ли указанный путь звуком
     * @param path путь для проверки
     * @return true, если по указанному пути звук
     */
    public static boolean pathIsSound(Path path) {
        return (path != null &&
                Files.exists(path) && 
                !Files.isDirectory(path) && 
                FileSystemUtils.getFileExtension(path).equals(Const.SOUND_FORMAT_EXT));
    }
    
}
