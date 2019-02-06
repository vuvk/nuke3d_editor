/**
    Globals of Nuke3D Editor
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
package com.vuvk.n3d;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TODO: delete?
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Global {    
    /** путь до папки, содержащей все ресурсы проекта */
    public static Path RESOURCES_PATH = null;
    /** путь до папки, содержащей все конфиги проекта */
    public static Path CONFIG_PATH = null;
    
    /**
     * Получение пути до папки с ресурсами
     */
    public static void initPathResources() {
        RESOURCES_PATH = Paths.get(Const.RESOURCES_STRING);
    }
    
    public static void initPathConfig() {
        CONFIG_PATH = Paths.get(Const.CONFIG_STRING);        
    }
}
