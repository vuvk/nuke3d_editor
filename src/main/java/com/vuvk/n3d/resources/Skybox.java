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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс хранимого скайбокса в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Skybox extends Resource {
    
    /**
     * текстуры для сторон куба
     */
    private final Texture[] sides = new Texture[6]; // 0 - FRONT, 1 - BACK, 2 - LEFT, 3 - RIGHT, 4 - TOP, 5 - BOTTOM
    
    /**
     * стороны скайбокса
     */
    public static enum Side {        
        FRONT(0), BACK  (1),
        LEFT (2), RIGHT (3),
        TOP  (4), BOTTOM(5);
        
        // номер стороны в массиве сторон
        private int num;
        int getNum() {
            return num;
        }
        
        Side(int num) {
            this.num = num;
        }        
    }
    
    /** Список всех скайбоксов (контейнер) */
    public static final ArrayList<Skybox> SKYBOXES = new ArrayList<>();

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Сохранить скайбокс в файл, к которому он привязан
     * @return true в случае успеха
     */
    @Override
    protected boolean save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
