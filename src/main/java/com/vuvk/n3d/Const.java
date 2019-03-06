/**
    Constants of Nuke3D Editor
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

import com.vuvk.n3d.utils.MessageDialog;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class Const {
    /** набор иконок */
    public final static HashMap<String, BufferedImage> ICONS = new HashMap<>();
    static {  
        try {
            // small icons
            ICONS.put("SmallFolderClose", ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/small/ic_folder_white_18dp.png")));        
            ICONS.put("SmallFolderOpen",  ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/small/ic_folder_open_white_18dp.png")));        

            // for preview element
            ICONS.put("LevelUp",  ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/ic_arrow_upward_white_48dp.png")));        
            ICONS.put("Folder",   ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/ic_folder_open_white_48dp.png")));      
            ICONS.put("Material", ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/ic_color_lens_white_48dp.png")));         
            ICONS.put("Sound",    ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/ic_music_note_white_48dp.png")));         
            ICONS.put("Unknown",  ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/ic_insert_drive_file_white_48dp.png")));  
            
            ICONS.put("Add",  ImageIO.read(Const.class.getResource("/com/vuvk/n3d/ico/ic_add_circle_white.png")));   

            // for jFrames
            ICONS.put("FormMainIcon", ImageIO.read(Const.class.getClass().getResource("/com/vuvk/n3d/ico/jframes/n3d.png")));              

        } catch (IOException ex) {
            Logger.getLogger(Const.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }        
    }
    
    
    /** имя папки, содержащей все ресурсы проекта */
    public static final String RESOURCES_STRING = "resources/";
    /** имя папки, в которой будут сохраняться настройки проекта */
    public static final String CONFIG_STRING = "config/";
    
    
    /** максимальная ширина текстуры */
    public static final int TEXTURE_MAX_WIDTH  = 512;
    /** максимальная высота текстуры */
    public static final int TEXTURE_MAX_HEIGHT = 512;    
    /** расширение файла импортированной текстуры */
    public static final String TEXTURE_FORMAT_EXT = "png";
    /** доступные расширения текстур для загрузки */
    public static final List<String> TEXTURE_EXTS = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");
    /** Путь до сохранённых параметров текстур */
    public static final String TEXTURE_CONFIG_STRING = CONFIG_STRING + "textures.sav";
    /** идентификатор конфига текстур */
    public static final String TEXTURE_CONFIG_IDENTIFICATOR = "N3D_TEXTURES";
    /** версия конфига текстур */
    static final int TEXTURE_CONFIG_MAJOR = 0;
    static final int TEXTURE_CONFIG_MINOR = 1;
    public static final String TEXTURE_CONFIG_VERSION = TEXTURE_CONFIG_MAJOR + "." + TEXTURE_CONFIG_MINOR;
    
    
    /** идентификатор материала */
    public static final String MATERIAL_IDENTIFICATOR = "N3D_MATERIAL";
    /** версия материала */
    static final int MATERIAL_MAJOR = 0;
    static final int MATERIAL_MINOR = 1;
    public static final String MATERIAL_VERSION = MATERIAL_MAJOR + "." + MATERIAL_MINOR;
    /** расширение файла импортированного материала */
    public static final String MATERIAL_FORMAT_EXT = "mat";
    /** Путь до сохранённых параметров материалов */
    public static final String MATERIAL_CONFIG_STRING = CONFIG_STRING + "materials.sav";
    /** идентификатор конфига материалов */
    public static final String MATERIAL_CONFIG_IDENTIFICATOR = "N3D_MATERIALS";
    /** версия конфига материалов */
    static final int MATERIAL_CONFIG_MAJOR = 0;
    static final int MATERIAL_CONFIG_MINOR = 1;
    public static final String MATERIAL_CONFIG_VERSION = MATERIAL_CONFIG_MAJOR + "." + MATERIAL_CONFIG_MINOR;
    
    
    /** расширение файла импортированного звука */
    public static final String SOUND_FORMAT_EXT = "ogg";
    /** доступные расширения звуков для загрузки */
    public static final List<String> SOUND_EXTS = Arrays.asList("aac", "aif", "aiff", "flac", "m4a", "m4p", "mp2", "mp3", "mpga", "ogg", "opus", "wav", "wma");
    /** Путь до сохранённых параметров звуков */
    public static final String SOUND_CONFIG_STRING = CONFIG_STRING + "sounds.sav";
    /** идентификатор конфига звуков */
    public static final String SOUND_CONFIG_IDENTIFICATOR = "N3D_SOUNDS";
    /** версия конфига звуков */
    static final int SOUND_CONFIG_MAJOR = 0;
    static final int SOUND_CONFIG_MINOR = 1;
    public static final String SOUND_CONFIG_VERSION = SOUND_CONFIG_MAJOR + "." + SOUND_CONFIG_MINOR;
    
    
    /** Размер иконок превью */
    public static final int ICON_PREVIEW_WIDTH  = 64;
    public static final int ICON_PREVIEW_HEIGHT = 64;
    
    
    private Const() {}
}
