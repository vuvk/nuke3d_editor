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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс хранимого звука в редакторе
 * @author Anton "Vuvk" Shcherbatykh
 */
public class Sound extends Resource {
    
    /** Список всех звуков (контейнер) */
    public static final ArrayList<Sound> SOUNDS = new ArrayList<>();

    public static boolean loadAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static boolean saveConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static boolean closeAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /** Initialization */
    /*@Override
    protected void init(Path path) {
        super.init(path);
    }*/
    
    public Sound(Path path) {
        super(path);
    }
    public Sound(File path) {
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

    @Override
    protected List getContainer() {
        return SOUNDS;
    }
    
}
