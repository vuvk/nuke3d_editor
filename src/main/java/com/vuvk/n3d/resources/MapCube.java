/**
    Class of cube as map primitive (Nuke3D Editor)
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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.vuvk.n3d.forms.FormMapEditor;
import java.util.Arrays;

/**
 * Класс примитива - куб, из которых построена сцена
 * @author Anton "Vuvk" Shcherbatykh
 */
public class MapCube extends MapFigure {
    
    public MapCube() {
        renderer = new ImmediateModeRenderer20(false, false, 4);
    }
    
    /** конструктор с копированием материалов другой фигуры */
    public MapCube(MapFigure other) {
        super(other);
    }
    

    @Override
    public void render(Matrix4 projModelView) {
        com.badlogic.gdx.graphics.Texture txr;
        Material mat;
        
        float x = pos.x,
              y = pos.y,
              z = pos.z;
        
        // back
        mat = sides[Side.BACK.getNum()];
        if (mat != null) {
            txr = FormMapEditor.GDX_TEXTURES.get(mat);
            if (txr != null) {
                txr.bind();
                renderer.begin(projModelView, GL20.GL_TRIANGLE_FAN);
                renderer.vertex(x, y, z);
                renderer.texCoord(0, 1);
                renderer.vertex(x + 1, y, z);
                renderer.texCoord(1, 1);
                renderer.vertex(x + 1, y + 1, z);
                renderer.texCoord(1, 0);
                renderer.vertex(x, y + 1, z);
                renderer.texCoord(0, 0);
                renderer.end();
            }
        }
    }
    
}
