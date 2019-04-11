/**
    Class of map element (Nuke3D Editor)
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

import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Класс элементов хранимой карты
 * @author Anton "Vuvk" Shcherbatykh
 */
public abstract class MapElement {    
    /** Позиция в 3Д-пространстве */
    protected Vector3 pos;
    /** рендерер */
    protected ImmediateModeRenderer20 renderer;
    
    protected MapElement() {
        pos = new Vector3();
    }
    
    /** 
     * Получить вектор позиции
     * @return Позиция в формате вектора
     */
    public Vector3 getPos() {
        return pos;
    }
    
    /**
     * Получить компоненту X позиции
     * @return Компонента X
     */
    public float getPosX() { return pos.x; }
    /**
     * Получить компоненту Y позиции
     * @return Компонента Y
     */
    public float getPosY() { return pos.y; }
    /**
     * Получить компоненту Z позиции
     * @return Компонента Z
     */
    public float getPosZ() { return pos.z; }

    /**
     * Установить вектор позиции
     * @param pos Вектор позиции
     */
    public void setPos(Vector3 pos) {
        pos = pos;
    }
    
    /**
     * Установить вектор позиции
     * @param x Компонента X
     * @param y Компонента Y
     * @param z Компонента Z 
     */
    public void setPos(float x, float y, float z) {
        pos.set(x, y, z);
    }
    
    /**
     * Задать компоненту X позиции
     * @param x Компонента X
     */
    public void setPosX(float x) { pos.x = x; }
    /**
     * Задать компоненту Y позиции
     * @param y Компонента Y
     */
    public void setPosY(float y) { pos.y = y; }
    /**
     * Задать компоненту Z позиции
     * @param z Компонента Z
     */
    public void setPosZ(float z) { pos.z = z; }
    
    /**
     * Рисование в 3D-режиме
     */
    public abstract void render(Matrix4 projModelView);
}
