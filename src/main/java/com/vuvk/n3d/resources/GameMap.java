/**
    Map class (Nuke3D Editor)
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

import com.badlogic.gdx.math.Vector3;
import com.vuvk.n3d.Const;

/**
 * Класс хранимой карты в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class GameMap {

    /** идентификатор */
    public static final String IDENTIFICATOR = "N3D_MAP";
    /** версия */
    static final int MAJOR = 0;
    static final int MINOR = 1;
    public static final String VERSION = MAJOR + "." + MINOR;
    /** расширение файла */
    public static final String FORMAT_EXT = "map";
    /** Путь до сохранённых параметров */
    public static final String CONFIG_STRING = Const.CONFIG_STRING + "maps.sav";
    /** идентификатор конфига */
    public static final String CONFIG_IDENTIFICATOR = "N3D_MAPS";
    /** версия конфига */
    static final int CONFIG_MAJOR = 0;
    static final int CONFIG_MINOR = 1;
    public static final String CONFIG_VERSION = CONFIG_MAJOR + "." + CONFIG_MINOR;
    /** максимальные размеры */
    public static final int MAX_X = 64;
    public static final int MAX_Y = 64;
    public static final int MAX_Z = 64;

    /** элементы карты, хранимые в карте */
    private final MapElement[][][] elements;

    public GameMap() {
        elements = new MapElement[MAX_X][MAX_Y][MAX_Z];
    }
    
    /**
     * Установить элемент в позицию на карте
     * @param pos Позиция элемента в формате Vector3. Компоненты будут усечены до int
     * @param element 
     */
    public void setElement(Vector3 pos, MapElement element) {
        setElement((int)pos.x, (int)pos.y, (int)pos.z, element);
    }

    /**
     * Установить элемент в позицию на карте
     * @param x X-компонента позиции
     * @param y Y-компонента позиции
     * @param z Z-компонента позиции
     * @param element Элемент карты
     */
    public void setElement(int x, int y, int z, MapElement element) {
        if (x >= 0 && x <= MAX_X &&
            y >= 0 && y <= MAX_Y &&
            z >= 0 && z <= MAX_Z
           ) {
            elements[x][y][z] = element;
            if (element != null) {
                element.setPos(x, y, -z);
            }
        }
    }
    
    /**
     * Получить элемент по позиции в массиве элементов
     * @param pos Позиция элемента в формате Vector3. Компоненты будут усечены до int
     * @return Элемент карты, если есть, иначе null
     */
    public MapElement getElement(Vector3 pos) {
        return getElement((int)pos.x, (int)pos.y, (int)pos.z);
    }

    /**
     * Получить элемент по позиции в массиве элементов
     * @param x X-компонента позиции
     * @param y Y-компонента позиции
     * @param z Z-компонента позиции
     * @return Элемент карты, если есть, иначе null
     */
    public MapElement getElement(int x, int y, int z) {
        if (x >= 0 && x <= MAX_X &&
            y >= 0 && y <= MAX_Y &&
            z >= 0 && z <= MAX_Z
           ) {
            return elements[x][y][z];
        }
        
        return null;
    }

}
