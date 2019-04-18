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
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */

/** класс поля для рисования предпросмотра */
public class PanelImagePreview extends JPanel {

    /** изображение, которое рисуется в предпросмотре */
    protected BufferedImage image = null;
    /** режим растяжения на всю область */
    protected boolean stretched = false;   
    /** рисовать наружнюю обводку */
    protected boolean drawBorder = true;
    /** окно для перерисовки */
    protected Container window = null;
    
    public PanelImagePreview(Container window) {
        super();
        setWindow(window);
        setOpaque(true);
    }
    
    /**
     * Установить изображение для отрисовки
     * @param image Изображение для установки
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Установить режим растяжения
     * @param stretched the stretched to set
     */
    public void setStretched(boolean stretched) {
        this.stretched = stretched;
    }

    /**
     * Рисовать ли обводку
     * @param draw true - рисовать / false - нет  
     */
    public void setDrawBorder(boolean draw) {
        this.drawBorder = draw;
    }
    
    /**
     * Установить контейнер
     * @param window the window to set
     */
    public void setWindow(Container window) {
        this.window = window;
    }
    
    /**
     * Получить рисуемое изображение
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Рисуется ли обводка
     * @return true, если рисуется
     */
    public boolean isDrawBorder() {
        return drawBorder;
    }
    
    /**
     * Вернуть режим растяжения
     * @return the stretched
     */
    public boolean isStretched() {
        return stretched;
    }

    /**
     * Вернуть окно-контейнер
     * @return the window
     */
    public Container getWindow() {
        return window;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);            

        if (image != null) {
            int imageWidth  = image.getWidth(),
                imageHeight = image.getHeight();
            int width  = getWidth(),
                height = getHeight();
            int bX = 0,     // border X 
                bY = 0,     // border Y
                bW = 0,     // border Width
                bH = 0;     // border Height
                                    
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
            if (imageWidth == width || imageHeight == height || stretched) {
                // если стороны равны, то рисовать во всю картинку
                if (imageWidth == imageHeight) {
                    g.drawImage(image, 0, 0, width, height, null);
                    
                    bX = bY = 0;
                    bW = width - 1;
                    bH = height - 1;
                // а иначе учитывать соотношение
                } else {
                    double coeffX = 1.0,
                           coeffY = 1.0;
                    if (imageWidth > imageHeight) {
                        coeffY = (double)imageHeight / imageWidth;
                    } else {
                        coeffX = (double)imageWidth / imageHeight;
                    }
                    
                    imageWidth  = bW = (int)(width  * coeffX);
                    imageHeight = bH = (int)(height * coeffY);
                    // по середине
                    int x = bX = (width  >> 1) - (imageWidth  >> 1);
                    int y = bY = (height >> 1) - (imageHeight >> 1);
                    g.drawImage(image, x, y, imageWidth, imageHeight, null);
                }
            // картинки влазит и не включено растяжение
            } else {
                // по середине
                int x = bX = (width  >> 1) - (imageWidth  >> 1);
                int y = bY = (height >> 1) - (imageHeight >> 1);
                g.drawImage(image, x, y, null);
                    
                bW = imageWidth;
                bH = imageHeight;
            }
            
            if (drawBorder) {
                g.drawRect(bX, bY, bW, bH);
            }
        }            
    }

    /** 
     * перерисовать панель 
     */
    public void redraw() {            
        paintImmediately(0, 0, getWidth(), getHeight());
        
        if (getWindow() != null) {
            getWindow().repaint();
        }
    }
}