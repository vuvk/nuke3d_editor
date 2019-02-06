/**
    JPanel with image drawing (Nuke3D Editor)
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
package com.vuvk.n3d.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */

/** класс поля для рисования предпросмотра */
public class PanelImagePreview extends JPanel {
    /** изображение, которое рисуется в предпросмотре */
    public BufferedImage image = null;
    /** режим растяжения на всю область */
    public boolean isStretched = false;      
    /** окно для перерисовки */
    public Container window = null;
    
    public PanelImagePreview() {
        super();
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);            

        if (image != null) {
            int imageWidth  = image.getWidth();
            int imageHeight = image.getHeight();
            int width  = getWidth();
            int height = getHeight();
                                    
            // размеры превью не больше размеров панели
            if (imageWidth > width) {
                imageWidth = width;
            }
            if (imageHeight > height) {
                imageHeight = height;
            }
                
            // цвет рамки
            g.setColor(Color.black);
            
            // если размеры картинки большие или включен режим растяжения
            if (imageWidth == width || imageHeight == height || isStretched) {
                // если стороны равны, то рисовать во всю картинку
                if (imageWidth == imageHeight) {
                    g.drawImage(image, 0, 0, width, height, null);
                    g.drawRect(0, 0, width - 1, height - 1);
                // а иначе учитывать соотношение
                } else {
                    double coeffX = 1.0,
                           coeffY = 1.0;
                    if (imageWidth > imageHeight) {
                        coeffY = (double)imageHeight / imageWidth;
                    } else {
                        coeffX = (double)imageWidth / imageHeight;
                    }
                    
                    imageWidth  = (int)(width  * coeffX);
                    imageHeight = (int)(height * coeffY);
                    // по середине
                    int x = (width  >> 1) - (imageWidth  >> 1);
                    int y = (height >> 1) - (imageHeight >> 1);
                    g.drawImage(image, x, y, imageWidth, imageHeight, null);
                    g.drawRect(x, y, imageWidth - 1, imageHeight - 1);
                }
            // картинки влазит и не включено растяжение
            } else {
                // по середине
                int x = (width  >> 1) - (imageWidth  >> 1);
                int y = (height >> 1) - (imageHeight >> 1);
                g.drawImage(image, x, y, null);
                g.drawRect(x, y, imageWidth - 1, imageHeight - 1);
            }
        }            
    }

    /** перерисовать панель */
    public void redraw() {            
        paintImmediately(0, 0, getWidth(), getHeight());
        
        if (window != null) {
            window.repaint();
        }
    }
}