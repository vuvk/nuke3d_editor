/**
    Class for change icons in jFileChooser (Nuke3D Editor)
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

import com.vuvk.n3d.Const;
import com.vuvk.n3d.utils.FileSystemUtils;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class ImageFileView extends FileView {
    private Icon textureIcon, 
                 soundIcon, 
                 folderIcon,
                 unknownIcon;
    
    public ImageFileView() {
        textureIcon = new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/small/ic_crop_original_white_18dp.png"));
        soundIcon   = new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/small/ic_audiotrack_white_18dp.png"));
        folderIcon  = new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/small/ic_folder_white_18dp.png")); 
        unknownIcon = new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/small/ic_description_white_18dp.png")); 
    }
    
    @Override
    public Icon getIcon(File f)
    {
        if (f.isDirectory()) {
            return folderIcon;
        }
        
        String ext = FileSystemUtils.getFileExtension(f);
        
        // изображение?
        if (Const.TEXTURE_EXTS.contains(ext)) {
            return textureIcon;
        } else if (Const.SOUND_EXTS.contains(ext)) {
            return soundIcon;
        }

        return unknownIcon;
    }
}
