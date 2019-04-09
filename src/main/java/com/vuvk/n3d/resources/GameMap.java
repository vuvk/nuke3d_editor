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

import com.vuvk.n3d.Const;

/**
 * Класс хранимой карты в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Map {
    
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
    
}
