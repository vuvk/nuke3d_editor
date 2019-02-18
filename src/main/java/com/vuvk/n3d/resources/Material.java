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
            texture = null;
            delay = 0.5;
        }
        /** конструктор с переданной текстурой */
        public Frame(Texture texture) {
            this.texture = texture;
            delay = 0.5;
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
     * Сохранить материалы и конфиг всех материалов 
     */
    public static boolean saveAll() {   
        /*try {                                   
            // создать путь папок, если его нет
            File folder = new File(Const.MATERIAL_PATH);
            if (!folder.exists()) {
                folder.mkdirs();
            } else {
                // проверяем все файлы в папке и если там есть такие, 
                // которые не в списке нужных, то удаляем
                // остальные будут переписаны
                for (File file : folder.listFiles()) {
                    if (!file.getName().equals("config.json")) {
                        file.delete();
                    }
                }                
            }
            
            // заполняем массив объектами материалов
            JsonArray jsonMaterials = new JsonArray(list.size());
            for (int i = 0; i < list.size(); ++i) {
                Material mat = list.get(i);
                
                // проверяем материал на правильность
                mat.check();
                
                // массив кадров
                JsonArray jsonFrames = new JsonArray(mat.getFramesCount());
                for (Frame frame : mat.frames) {
                    JsonObject jsonFrame = new JsonObject();
                    
                    // ищем номер текстуры
                    int index = -1;
                    Texture txr = frame.getTexture();
                    if (txr != null) {
                        for (int t = 0; t < Texture.TEXTURES.size(); ++t) {
                            if (txr.equals(Texture.TEXTURES.get(t))) {
                                index = t;
                                break;
                            }
                        }
                    }
                    
                    jsonFrame.addProperty("texture", index);
                    jsonFrame.addProperty("delay", frame.getDelay());
                    
                    jsonFrames.add(jsonFrame);
                }
                
                // объект материала
                JsonObject jsonMaterial = new JsonObject();
                jsonMaterial.addProperty("name", mat.getName());
                jsonMaterial.addProperty("type", mat.getMaterialType().name());
                jsonMaterial.add("frames", jsonFrames);
                
                jsonMaterials.add(jsonMaterial);
            }

            // сохраняем имена текстур
            Writer writer = new FileWriter(Const.MATERIAL_PATH + "config.json"); 
            Gson gson = new GsonBuilder().create();   
            gson.toJson(jsonMaterials, writer);             
            writer.close(); 
            
        } catch (Exception e) {
            Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        */
        return true;
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
    
    /**
     * Конструктор текстуры по умолчанию.
     * Имя по порядку и изображение 1*1 в формате ARGB
     */
    /*public Material() {
        name = "material_" + list.size();
        type = Type.Default;
        
        // имя подходящее?
        for (int i = 0; i < list.size(); ++i) {
            // уже есть такое имя и надо подобрать новое
            if (name.equals(list.get(i).name)) {
                name = "material_" + list.size() + "_" + Math.round(Math.random() * 1000);
                i = 0;
            }
        }
        
        frames.add(new Frame());
        
        list.add(this);
    }    */
    
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
        /** если файл существует и он является текстурой */
        if (Files.exists(path) &&
            !Files.isDirectory(path) &&  
            FilenameUtils.isExtension(path.getFileName().toString(), Const.TEXTURE_FORMAT_EXT)
           ) {
            //
        } else {
            frames.clear();
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
        if (count > 1) {
            // сокращаем количество
            if (count < frames.size()) {
                while (frames.size() > count) {
                    frames.remove(frames.size() - 1);
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
