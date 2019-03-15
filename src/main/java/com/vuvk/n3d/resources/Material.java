/**
    Material class (Nuke3D Editor)
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
import com.vuvk.n3d.Const;
import com.vuvk.n3d.Global;
import com.vuvk.n3d.utils.FileSystemUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 * Класс хранимого материала в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Material extends Resource {
    
    /** идентификатор материала */
    public static final String IDENTIFICATOR = "N3D_MATERIAL";
    /** версия материала */
    static final int MAJOR = 0;
    static final int MINOR = 1;
    public static final String VERSION = MAJOR + "." + MINOR;
    /** расширение файла импортированного материала */
    public static final String FORMAT_EXT = "mat";
    /** Путь до сохранённых параметров материалов */
    public static final String CONFIG_STRING = Const.CONFIG_STRING + "materials.sav";
    /** идентификатор конфига материалов */
    public static final String CONFIG_IDENTIFICATOR = "N3D_MATERIALS";
    /** версия конфига материалов */
    static final int CONFIG_MAJOR = 0;
    static final int CONFIG_MINOR = 1;
    public static final String CONFIG_VERSION = CONFIG_MAJOR + "." + CONFIG_MINOR;
    
    /** енумератор типа материала */
    public static enum Type {
        Default,
        AlphaChannel,
        Transparent
    }
    
    /** один кадр анимации */
    public static class Frame {
        /** ссылка на текстуру кадра */
        private Texture texture;
        /** задержка на кадре в сек */
        private double delay;
        
        /** конструктор */
        public Frame() {
            this.texture = null;
            this.delay   = 0.5;
        }
        /** конструктор с переданной текстурой */
        public Frame(Texture texture) {
            this.texture = texture;
            this.delay   = 0.5;
        }
        /** конструктор с переданной текстурой и задержкой на кадре*/
        public Frame(Texture texture, double delay) {
            this.texture = texture;
            this.delay   = delay;
        }
        
        /**
         * Установить текстуру кадра
         * @param texture новая текстура кадра
         */
        public void setTexture(Texture texture) {
            this.texture = texture;
        }
        /**
         * Получить текстуру кадра
         * @return текстура кадра
         */
        public Texture getTexture() {
            return texture;
        }
        /**
         * Получить изображение кадра
         * @return изображение кадра
         */
        public BufferedImage getImage() {
            if (texture != null) {
                return texture.getImage();
            }
            return null;
        }
        /**
         * Установить задержку на кадре
         * @param pause время в сек
         */
        public void setDelay(double delay) {
            this.delay = (delay < 0.0) ? 0.0 : delay;
        }
        /**
         * Получить задрежку на кадре
         * @return время в сек
         */
        public double getDelay() {
            return delay;
        }
        
        /**
         * Проверить существует ли текстура кадра в базе текстур.
         * Несуществующая будет ОБНУЛЕНА.
         * @return true, если существует
         */
        public boolean check() {
            if (this.texture != null && Texture.TEXTURES.contains(this.texture)) {
                return true;
            } else {
                this.texture = null;
                return false;
            }
        }
    }
    
    /** тип материала */
    private Type type;    
    /** Список кадров материала */
    private ArrayList<Frame> frames;
    
    /** Список всех материалов (контейнер) */
    public static final ArrayList<Material> MATERIALS = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(Material.class.getName());
    
    /**
     * Проверка является ли указанный путь материалом
     * @param path путь для проверки
     * @return true, если по указанному пути материал
     */
    public static boolean pathIsMaterial(Path path) {
        return (path != null &&
                Files.exists(path) && 
                !Files.isDirectory(path) && 
                FileSystemUtils.getFileExtension(path).equals(FORMAT_EXT));
    }
    
    /**
     * Загрузить конфиг материалов и сами материалы
     * @return true в случае успеха
     */
    public static boolean loadAll() {
        closeAll();
        
        File materialConfig = new File(CONFIG_STRING);
        
        if (!Files.exists(Global.CONFIG_PATH) || 
            !materialConfig.exists()) {
            return false;
        }
        
        // читаем конфиг
        JsonObject config = new JsonObject();        
        try (Reader reader = new FileReader(materialConfig)){
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
        
        // создаем материалы по данным из конфига
        for (JsonElement element : jsonData.getAsJsonArray()) {
            JsonElement jsonId   = ((JsonObject)element).get("id");
            JsonElement jsonPath = ((JsonObject)element).get("path");
            
            if (jsonId == null || jsonPath == null) {
                continue;
            }
            
            // если материал существует
            Path path = Paths.get(jsonPath.getAsString());
            if (pathIsMaterial(path)) {
                // добавляем в базу новый материал
                new Material(path)
                    .setId(jsonId.getAsInt());
            }
        }
        
        // на всякий случай проверим валидность всех материалов
        checkAll();
        
        return true;
    }
    
    /** 
     * Сохранить все материалы
     * @return true в случае успеха
     */
    public static boolean saveAll() {
        boolean allOk = true;
        
        for (Material mat : MATERIALS) {
            if (!mat.save()) {
                allOk = false;
            }
        }
        
        return allOk;
    }
    /** 
     * Сохранить конфиг всех материалов 
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
        JsonArray array = new JsonArray(MATERIALS.size());
        for (Material mat : MATERIALS) {
            JsonObject object = new JsonObject();
            object.addProperty("id", mat.getId());
            object.addProperty("path", mat.getPath());
            array.add(object);
        }
        JsonObject config = new JsonObject();
        config.addProperty("identificator", CONFIG_IDENTIFICATOR);
        config.addProperty("version", CONFIG_VERSION);
        config.add("data", array);
        
        // сохраняем конфиг
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
     * Удалить все материалы из памяти
     * @return true в случае успеха
     */
    public static boolean closeAll() {
        MATERIALS.clear();        
        return (MATERIALS.isEmpty());
    }
    
    /**
     * Проверка всех кадров всех материалов на наличие текстуры в базе.
     * Если текстуры нет в базе, то она будет ОБНУЛЕНА.
     */
    public static void checkAll() {
        for (Material mat : MATERIALS) {
            mat.check();
        }
    }
    
    
    public Material(File path) {
        this(path.toPath());
    }
    public Material(Path path) {
        super(path);
        
        type = Type.Default; 
        frames = new ArrayList<>();        
        
        if (Files.exists(path)) {
            load(path);
        } else {
            save(); 
        }
    }
        
    /**
     * Загрузить материал из файла
     * @param path Путь до файла
     * @return true в случае успеха
     */
    protected boolean load(Path path) {
        frames.clear();
        
        /** если файл существует и он является текстурой */
        if (pathIsMaterial(path)) {            
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
                !jsonIdentificator.getAsString().equals(IDENTIFICATOR)
               ) {
                return false;
            }
            // версия
            JsonElement jsonVersion = config.get("version");
            if (jsonVersion == null) {
                return false;
            }
            double configVersion = jsonVersion.getAsDouble();
            double editorVersion = Double.parseDouble(VERSION);
            if (editorVersion < configVersion) {
                return false;
            }            
            // тип
            JsonElement jsonType = config.get("type");
            if (jsonType != null) {
                setType(Type.valueOf(jsonType.getAsString()));
            }
            // кадры
            JsonElement jsonFrames = config.get("frames");
            if (jsonFrames == null) {
                return false;
            }        
            // получаем текстуры по данным из конфига
            for (JsonElement element : jsonFrames.getAsJsonArray()) {
                JsonElement jsonTxrId = ((JsonObject)element).get("texture_id");
                JsonElement jsonDelay = ((JsonObject)element).get("delay");

                if (jsonTxrId == null || jsonDelay == null) {
                    continue;
                }

                // добавить кадр
                long id = jsonTxrId.getAsLong();
                double delay = jsonDelay.getAsDouble();
                Texture txr = (Texture) Resource.getById(id, Resource.Type.TEXTURE);
                
                pushFrame(new Frame(txr, delay));
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Сохранить материал в файл, к которому он привязан
     * @return true в случае успеха
     */
    public boolean save() { 
        check();
        
        // информация о кадрах
        JsonArray jsonFrames = new JsonArray();
        for (Frame frm : frames) {
            JsonObject jsonFrame = new JsonObject();
            Texture txr = frm.getTexture();
            if (txr != null) {
                jsonFrame.addProperty("texture_id", txr.getId());
            } else {
                jsonFrame.addProperty("texture_id", -1);                
            }
            jsonFrame.addProperty("delay", frm.getDelay());
            
            jsonFrames.add(jsonFrame);
        }
        
        // общая информация об объекте
        JsonObject object = new JsonObject();
        object.addProperty("identificator", IDENTIFICATOR);
        object.addProperty("version", VERSION);
        //object.addProperty("id", getId());
        object.addProperty("type",  type.toString());
        object.add("frames", jsonFrames);
        
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
    
    /**
     * Деструктор
     */
    @Override
    public void dispose() {
        super.dispose();
        frames.clear();
    }
        
    /**
     * Установить тип материала
     * @param type новый тип
     */
    public void setType(Type type) {
        this.type = type;
    }
    /**
     * Установить количество кадров (не менее 1).
     * Если новое количество будет больше предыдущего, то кадры будут забиты дефолтными.
     * Если новое количество меньше текущего, то лишние будут отсечены.
     * @param count количество кадров
     */
    public void setFramesCount(int count) {
        if (count > 0) {
            // сокращаем количество
            if (count < frames.size()) {
                while (frames.size() > count) {
                    popFrame();
                }
            // наращиваем
            } else if (count > frames.size()) {                
                while (frames.size() < count) {
                    frames.add(new Frame());
                }
            }
        }
    }
    /**
     * Получить тип материала
     * @return тип Type
     */
    public Type getType() {
        return type;
    }
    /**
     * Добавить кадр в конец (то же, что pushFrame)
     * @param frame Добавляемый кадр
     */
    public void addFrame(Frame frame) {
        pushFrame(frame);
    }
    /**
     * Добавить кадр в заданную позицию (не то же, что setFrame)
     * @param position Позиция для добавления нового кадра
     * @param frame Добавляемый кадр
     */
    public void addFrame(int position, Frame frame) {
        frames.add(position, frame);
    }
    /**
     * Добавить кадр в конец
     * @param frame Добавляемый кадр
     */
    public void pushFrame(Frame frame) {
        frames.add(frame);
    }
    /**
     * Удалить последний кадр
     */
    public void popFrame() {
        frames.remove(frames.size() - 1);
    }
    /**
     * Удалить кадр
     * @param position Позиция для удаления
     */
    public void removeFrame(int position) {
        if (position > -1 && position < frames.size()) {
            frames.remove(position);
        }
    }
    /**
     * Удалить все кадры
     */
    public void clearFrames() {
        frames.clear();
    }
    /**
     * Получить кадр из анимации
     * @param index номер кадра
     * @return кадр в классе MaterialFrame или null, если такого нет
     */
    public Frame getFrame(int index) {
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        } else {
            return null;
        }
    }
    /**
     * Получить количество кадров
     * @return целое число - количество кадров анимации
     */
    public int getFramesCount() {
        return frames.size();
    }    
    /**
     * Установить новый кадр вместо имеющегося
     * @param index позиция для замены
     * @param frame новый кадр
     */
    public void setFrame(int index, Frame frame) {
        if (index >= 0 && index < frames.size()) {
            frames.set(index, frame);
        }
    }    
    
    @Override
    protected List getContainer() {
        return MATERIALS;
    }
    
    /**
     * Проверка всех кадров на наличие текстуры в базе.
     * Если текстуры нет в базе, то она будет ОБНУЛЕНА.
     * @return true, если все кадры валидные
     */
    public boolean check() {
        int counter = 0;
        for (Frame frm : frames) {
            if (frm.check()) {
                ++counter;
            }
        }
        return (counter == frames.size());
    }
}
