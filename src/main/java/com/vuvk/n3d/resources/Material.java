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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 * Класс хранимого материала в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Material extends Resource {
    
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
    
    /**
     * Загрузить конфиг материалов и сами материалы
     */
    public static boolean loadAll() {
        /*closeAll();
        
        try {
            File folder = new File(Const.MATERIAL_PATH);
            if (!folder.exists()) {
                return true;
            }

            // читаем конфиг в массив
            Reader reader = new FileReader(Const.MATERIAL_PATH + "config.json");
            Gson gson = new GsonBuilder().create();  
            JsonArray jsonMaterials = gson.fromJson(reader, JsonArray.class);
            reader.close();                

            
            for (int i = 0; i < jsonMaterials.size(); ++i) {
                Material mat = new Material();
                
                JsonObject jsonMaterial = jsonMaterials.get(i).getAsJsonObject();
                
                // дергаем имя и тип
                String name = jsonMaterial.get("name").getAsString();
                Type type = Type.Default;
                switch (jsonMaterial.get("type").getAsString()) {
                    case "AlphaChannel" : 
                        type = Type.AlphaChannel;
                        break;
                        
                    case "Transparent" : 
                        type = Type.Transparent;
                        break;  
                }
                
                mat.setName(name);
                mat.setMaterialType(type);
                
                // теперь дергаем кадры
                JsonArray jsonFrames = jsonMaterial.getAsJsonArray("frames");
                mat.setFramesCount(jsonFrames.size());
                for (int f = 0; f < jsonFrames.size(); ++f) {
                    Frame frame = new Frame();
                    
                    JsonObject jsonFrame = jsonFrames.get(f).getAsJsonObject();
                    int index = jsonFrame.get("texture").getAsInt();
                    if (index > -1 && index < Texture.TEXTURES.size()) {
                        frame.setTexture(Texture.TEXTURES.get(index));
                    }
                    
                    frame.setDelay(jsonFrame.get("delay").getAsDouble());
                        
                    mat.setFrame(f, frame);
                }
            }   
            
        } catch (Exception e) {
            Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        */
        return true;
    }
    
    /** 
     * Сохранить все материалы
     */
    public static void saveAll() {   
        for (Material mat : MATERIALS) {
            mat.save();
        }
    }
    
    /**
     * Удалить все материалы из памяти
     */
    public static boolean closeAll() {
        MATERIALS.clear();        
        return (MATERIALS.isEmpty());
    }
    
    /**
     * Проверка всех кадров всех материалов на наличие текстуры в базе.
     * Если текстуры нет в базе, то она будет ОБНУЛЕНА.
     * @return true, если все кадры валидные
     */
    public static void checkAll() {
        for (Material mat : MATERIALS) {
            mat.check();
        }
    }
    
    protected void init(Path path) {   
        // ищем максимальный id и инкрементируем его
        long newId = 0;
        for (Iterator it = MATERIALS.iterator(); it.hasNext(); ) {
            Material mat = (Material)it.next();
            if (mat.getId() > newId) {
                newId = mat.getId();
            }
        }
        ++newId;
        
        type = Type.Default; 
        frames = new ArrayList<>();
        
        setId(newId);
        setPath(path);  
        save(); 
        MATERIALS.add(this);
    }
    
    public Material(Path path) {
        super(path);
    }
    public Material(File path) {
        super(path.toPath());
    }
        
    /**
     * Загрузить материал из файла
     * @param path Путь до файла
     */
    protected void load(Path path) {
        frames.clear();
        
        /** если файл существует и он является текстурой */
        if (pathIsMaterial(path)) {            
            // читаем конфиг
            JsonObject config = new JsonObject();        
            try (Reader reader = new FileReader(path.toFile())) {
                Gson gson = new GsonBuilder().create();  
                config = gson.fromJson(reader, JsonObject.class);
            } catch (Exception ex) {
                Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
                return;
            }

            // проверяем правильность конфига
            // идентификатор
            JsonElement jsonIdentificator = config.get("identificator");
            if (jsonIdentificator == null || 
                !jsonIdentificator.getAsString().equals(Const.MATERIAL_IDENTIFICATOR)
               ) {
                return;
            }
            // версия
            JsonElement jsonVersion = config.get("version");
            if (jsonVersion == null) {
                return;
            }
            double configVersion = jsonVersion.getAsDouble();
            double editorVersion = Double.parseDouble(Const.MATERIAL_VERSION);
            if (editorVersion < configVersion) {
                return;
            }
            
            // кадры
            JsonElement jsonFrames = config.get("data");
            if (jsonFrames == null) {
                return;
            }
        
            // получаем текстуры по данным из конфига
            for (JsonElement element : jsonFrames.getAsJsonArray()) {
                JsonElement jsonId    = ((JsonObject)element).get("texture_id");
                JsonElement jsonDelay = ((JsonObject)element).get("delay");

                if (jsonId == null || jsonDelay == null) {
                    continue;
                }

                // добавить кадр
                pushFrame(new Frame(Texture.getById(jsonId.getAsLong()), jsonDelay.getAsDouble()));
            }
        }
    }
    
    /**
     * Сохранить материал в файл, к которому он привязан
     */
    public void save() { 
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
        object.addProperty("identificator", Const.MATERIAL_IDENTIFICATOR);
        object.addProperty("version", Const.MATERIAL_VERSION);
        object.addProperty("id", getId());
        object.add("frames", jsonFrames);
        
        // сохраняем в привязанный файл
        try (Writer writer = new FileWriter(getPath())) { 
            Gson gson = new GsonBuilder().create();   
            gson.toJson(object, writer);             
        } catch (IOException ex) {
            Logger.getLogger(Texture.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }        
    }
    
    /**
     * Деструктор
     */
    public void dispose() {
        frames.clear();
        MATERIALS.remove(this);
    }
        
    /**
     * Установить тип материала
     * @param materialType новый тип
     */
    public void setMaterialType(Type materialType) {
        this.type = materialType;
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
    public Type getMaterialType() {
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
    /**
     * Получить ссылку на материал по пути до файла
     * @param path Путь до файла
     * @return Материал, если есть такой в базе, иначе null
     */
    public static Material getByPath(String path) {
        return getByPath(Paths.get(path));
    }
    /**
     * Получить ссылку на материал по пути до файла
     * @param path Путь до файла
     * @return Материал, если есть такой в базе, иначе null
     */
    public static Material getByPath(Path path) {
        if (pathIsMaterial(path)) {
            String checkPath = FileSystemUtils.getProjectPath(path);
            for (Material mat : MATERIALS) {
                if (mat.getPath().equals(checkPath)) {
                    return mat;
                }
            }
        }
        
        return null;
    }
    /**
     * Получить ссылку на материал по id
     * @param id Идентификатор материала
     * @return Материал, если есть такой в базе, иначе null
     */
    public static Material getById(long id) {
        for (Material mat : MATERIALS) {
            if (mat.getId() == id) {
                return mat;
            }
        }
        return null;
    }
    /**
     * Проверка является ли указанный путь материалом
     * @param path путь для проверки
     * @return true, если по указанному пути материал
     */
    public static boolean pathIsMaterial(Path path) {
        return (path != null &&
                Files.exists(path) && 
                !Files.isDirectory(path) && 
                FileSystemUtils.getFileExtension(path).equals(Const.MATERIAL_FORMAT_EXT));
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
