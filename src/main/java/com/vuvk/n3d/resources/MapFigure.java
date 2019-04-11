/**
    Class of map primitives (Nuke3D Editor)
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

import java.util.Arrays;

/**
 * Класс фигур примитивов, из которых построена сцена
 * @author Anton "Vuvk" Shcherbatykh
 */
public abstract class MapFigure extends MapElement {    
    /**
     * материалы для сторон
     */
    protected final Material[] sides = new Material[6]; // 0 - FRONT, 1 - BACK, 2 - LEFT, 3 - RIGHT, 4 - TOP, 5 - BOTTOM
    
    protected MapFigure() {}
    /** конструктор с копированием материалов другой фигуры */
    protected MapFigure(MapFigure other) {
        duplicate(other);
    }
    
    /**
     * Установить материал на сторону
     * @param mat Устанавливаемый материал
     * @param side Сторона, на которую нужно установить материал
     */
    public void setMaterial(Material mat, Side side) {
        sides[side.getNum()] = mat;
    }
    
    /**
     * Получить материал на стороне
     * @param side Сторона, материал которой нужно получить
     * @return материал, если установлен
     */
    public Material getMaterial(Side side) {
        return sides[side.getNum()];
    }
    
    /**
     * копирование материалов другой фигуры
     * @param other Другая фигура
     */
    public void duplicate(MapFigure other) {     
        if (equals(other)) {
            return;
        }
        
        for (int i = 0; i < sides.length; ++i) {
            if (other != null) {        
                sides[i] = other.sides[i];
            } else {       
                sides[i] = null;
            }
        }    
    }
    
    /**
     * Очистить установленные на стороны материалы
     */
    public void clear() {
        Arrays.fill(sides, null);
    }
    
    /**
     * Проверить существуют ли материалы, использующиеся для сторон фигуры.
     * Несуществующие будут ОБНУЛЕНЫ.
     * @return true, если все существуют
     */
    public boolean check() {
        boolean result = true;
        
        for (int i = 0; i < sides.length; ++i) {
            if (sides[i] != null && !Material.MATERIALS.contains(sides[i])) {
                sides[i] = null;
                result = false;
            }
        }
        
        return result;
    }
}
