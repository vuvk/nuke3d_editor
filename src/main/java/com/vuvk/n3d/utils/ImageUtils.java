/**
    Image utilities of Nuke3D Editor
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
package com.vuvk.n3d.utils;

import com.vuvk.n3d.Const;
import com.vuvk.n3d.utils.MathUtils;
import com.vuvk.n3d.resources.Texture;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class ImageUtils {
    private ImageUtils(){}
    
    /**
     * Изменить размер изображения. Исходное изображение НЕ МЕНЯЕТСЯ
     * @param image изображение для изменения
     * @param width новая ширина
     * @param height новая высота
     * @return новое изображение с заданным размером
     */
    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {       
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // если размеры те же самые, то вернуть копию    
        if (image.getWidth()  == width && 
            image.getHeight() == height && 
            image.getType()   == BufferedImage.TYPE_INT_ARGB
           ) {  
            outputImage.setData(image.getData());
        } else {            
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(image, 0, 0, width, height, null);
            g2d.dispose();
        }
        
        return outputImage;
    }
    
    /**
     * Подготовить изображение для импорта
     * Просто подгон размеров картинки под степень двойки и формат ARGB
     * @param image изображение-пациент
     */
    public static BufferedImage prepareImage(BufferedImage image) {
        if (image == null) {
            return Texture.IMAGE_EMPTY;
        }
        
        int width  = MathUtils.getPowerOfTwo(image.getWidth());
        int height = MathUtils.getPowerOfTwo(image.getHeight());
            
        if (width <= 0) {
            width  = 1;
        } else if (width > Const.TEXTURE_MAX_WIDTH) {
            width  = Const.TEXTURE_MAX_WIDTH;
        }
        
        if (height <= 0) {
            height  = 1;
        } else if (height > Const.TEXTURE_MAX_HEIGHT) {
            height  = Const.TEXTURE_MAX_HEIGHT;
        }
                
        return resizeImage(image, width, height);
    }    
}
