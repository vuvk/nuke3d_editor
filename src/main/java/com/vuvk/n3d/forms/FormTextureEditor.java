/**
    Form for preview textures in Nuke3D Editor
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
package com.vuvk.n3d.forms;

import com.vuvk.n3d.resources.Texture;
import com.vuvk.n3d.components.PanelImagePreview;
import com.vuvk.n3d.utils.MessageDialog;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class FormTextureEditor extends javax.swing.JInternalFrame {
    /** Текстура только что добавлена? */
    //public static boolean isNewTexture = false;
    /** редактируемая в данный момент нода в списке */
    //public static DefaultMutableTreeNode selectedTreeNode = null;
    /** выбранный файл текстуры */
    //public static File selectedFile = null;
    /** выбранная текстура для редактирования */
    public static Texture selectedTexture = null;
    /** режим удаления цвета */
    public static boolean isDeleteColorMode = false;
    /** поле для рисования предпросмотра */
    static ImagePreview imagePreview;
    
    
    class ImagePreview extends PanelImagePreview implements MouseInputListener {
        
        public ImagePreview(Container window){
            super(window);
            
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        void mouseMove(MouseEvent evt) {            
            if (!isDeleteColorMode) {
                return;
            }

            if (image != null) {
                int x = evt.getX();
                int y = evt.getY();
                int imageWidth  = image.getWidth();
                int imageHeight = image.getHeight();
                int panelWidth  = getWidth();
                int panelHeight = getHeight();

                // если выключен режим растяжения, то надо проверить попал ли вообще курсор
                // в область рисования картинки
                if (!stretched) {                                
                    x -= (panelWidth  >> 1) - (imageWidth  >> 1);
                    y -= (panelHeight >> 1) - (imageHeight >> 1);                                
                // при включенном режиме растяжения нужно найти координаты на картинке с учетом
                // поправок размер/факт.размер
                } else {
                    double coeffX = (double)imageWidth  / panelWidth;
                    double coeffY = (double)imageHeight / panelHeight;
                    x *= coeffX;
                    y *= coeffY;
                }

                // если попал
                if ((x >= 0 && x < imageWidth ) && (y >= 0 && y < imageHeight)) {
                    // покажем, какой цвет был выбран
                    pnlColorChoosed.setBackground(new Color(image.getRGB(x, y)));
                } else {
                    pnlColorChoosed.setBackground(Color.BLACK);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //mouseMove(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseMove(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (isDeleteColorMode) {
                    if (image != null) {
                        int x = evt.getX();
                        int y = evt.getY();
                        int imageWidth  = image.getWidth();
                        int imageHeight = image.getHeight();
                        int panelWidth  = getWidth();
                        int panelHeight = getHeight();

                        // временная картинка - копия
                        BufferedImage newImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                        newImage.setData(image.getData());

                        // если выключен режим растяжения, то надо проверить попал ли вообще курсор
                        // в область рисования картинки
                        if (!stretched) {                                
                            x -= (panelWidth  >> 1) - (imageWidth  >> 1);
                            y -= (panelHeight >> 1) - (imageHeight >> 1);                                
                        // при включенном режиме растяжения нужно найти координаты на картинке с учетом
                        // поправок размер/факт.размер
                        } else {
                            double coeffX = (double)imageWidth  / panelWidth;
                            double coeffY = (double)imageHeight / panelHeight;
                            x *= coeffX;
                            y *= coeffY;
                        }      

                        // если попал
                        if ((x >= 0 && x < imageWidth ) && (y >= 0 && y < imageHeight)) {
                            // берем цвет-шаблон для удаления
                            int template = newImage.getRGB(x, y);
                            //System.out.printf("rgba = %d\t%d\t%d\t%d\t\n", pixel[0], pixel[1], pixel[2], pixel[3]);

                            // бежим по всем пикселям и если находим подобный, то затираем в ноль
                            for (int i = 0; i < imageWidth; ++i) {
                                for (int j = 0; j < imageHeight; ++j) {
                                    int color = newImage.getRGB(i, j);
                                    if (color == template) {
                                        newImage.setRGB(i, j, 0);
                                    }
                                }
                            }

                            image = newImage;
                            redraw();

                            // сохранить или отклонить изменения?
                            if (MessageDialog.showConfirmationYesNo("Сохранить изменения? Это действие невозможно отменить!")) {
                                selectedTexture.setImage(newImage);
                                setDeleteColorMode(false);
                                tglDeleteColor.setSelected(false);
                                selectedTexture.save();
                                FormMain.formMain.updateListProjectView(selectedTexture.getPath());
                            } else {
                                image = selectedTexture.getImage();
                                redraw();                                    
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (isDeleteColorMode) {
                pnlColorChoosed.setVisible(true);
            } else {
                pnlColorChoosed.setVisible(false);                
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            pnlColorChoosed.setVisible(false);   
        }        
    }
    
    /**
     * Подготовка формы для отображения - подгружается изображение из текстуры
     * @param firstRun - первый запуск окна
     */
    public void prepareForm(boolean firstRun) {        
        if (selectedTexture == null) {
            dispose();
            return;
        }
        
        // получить имя редактируемой текстуры
        txtName.setText(selectedTexture.getName());
        // получить в панель предпросмотра ссылку на картинку
        imagePreview.setImage(selectedTexture.getImage());
        imagePreview.redraw();
        
        if (firstRun) {
            setLocation((getParent().getWidth() >> 1) - (getWidth() >> 1), 0);
        }
    }

    /**
     * Creates new form FormTextureEditor
     */
    public FormTextureEditor() {
        initComponents();                
        
        imagePreview = new ImagePreview(this);
        imagePreview.setSize(512, 512);
        pnlPreview.add(imagePreview);
        imagePreview.setLocation(5, 16);  
        //imagePreview.setLocation(14, 26); 
        
        setDeleteColorMode(false);
    }
    
    
    private void setDeleteColorMode(boolean mode) {
        isDeleteColorMode = mode;
        pnlColorChoosed.setVisible(mode);
        tglDeleteColor.setSelected(mode);
        
        if (mode) {
            imagePreview.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        //    btnDeleteColor.setIcon(new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/zoom.png")));
        } else {
            imagePreview.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));   
        //    btnDeleteColor.setIcon(new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/clear.png")));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnClose = new javax.swing.JButton();
        pnlPreview = new javax.swing.JPanel();
        pnlColorChoosed = new javax.swing.JPanel();
        tglStretched = new javax.swing.JToggleButton();
        tglDeleteColor = new javax.swing.JToggleButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Редактор текстуры");
        setToolTipText("");
        setMaximumSize(new java.awt.Dimension(544, 660));
        setMinimumSize(new java.awt.Dimension(544, 660));
        setNormalBounds(new java.awt.Rectangle(0, 0, 544, 660));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        lblName.setText("Имя");

        txtName.setEditable(false);
        txtName.setText("<not available>");
        txtName.setMaximumSize(new java.awt.Dimension(432, 27));
        txtName.setMinimumSize(new java.awt.Dimension(432, 27));
        txtName.setPreferredSize(new java.awt.Dimension(432, 27));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_done_white_24dp.png"))); // NOI18N
        btnClose.setToolTipText("Закрыть");
        btnClose.setMaximumSize(new java.awt.Dimension(64, 64));
        btnClose.setMinimumSize(new java.awt.Dimension(64, 64));
        btnClose.setPreferredSize(new java.awt.Dimension(64, 64));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        pnlPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Предпросмотр"));
        pnlPreview.setMaximumSize(new java.awt.Dimension(524, 535));
        pnlPreview.setMinimumSize(new java.awt.Dimension(524, 535));
        pnlPreview.setPreferredSize(new java.awt.Dimension(524, 535));

        javax.swing.GroupLayout pnlPreviewLayout = new javax.swing.GroupLayout(pnlPreview);
        pnlPreview.setLayout(pnlPreviewLayout);
        pnlPreviewLayout.setHorizontalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 514, Short.MAX_VALUE)
        );
        pnlPreviewLayout.setVerticalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );

        pnlColorChoosed.setMaximumSize(new java.awt.Dimension(64, 64));
        pnlColorChoosed.setMinimumSize(new java.awt.Dimension(64, 64));

        javax.swing.GroupLayout pnlColorChoosedLayout = new javax.swing.GroupLayout(pnlColorChoosed);
        pnlColorChoosed.setLayout(pnlColorChoosedLayout);
        pnlColorChoosedLayout.setHorizontalGroup(
            pnlColorChoosedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        pnlColorChoosedLayout.setVerticalGroup(
            pnlColorChoosedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        tglStretched.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_photo_size_select_large_white_24dp.png"))); // NOI18N
        tglStretched.setToolTipText("Растянуть");
        tglStretched.setMaximumSize(new java.awt.Dimension(64, 64));
        tglStretched.setMinimumSize(new java.awt.Dimension(64, 64));
        tglStretched.setPreferredSize(new java.awt.Dimension(64, 64));
        tglStretched.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_photo_size_select_actual_white_24dp.png"))); // NOI18N
        tglStretched.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tglStretchedStateChanged(evt);
            }
        });

        tglDeleteColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_colorize_white_24dp.png"))); // NOI18N
        tglDeleteColor.setToolTipText("Удалить цвет");
        tglDeleteColor.setMaximumSize(new java.awt.Dimension(64, 64));
        tglDeleteColor.setMinimumSize(new java.awt.Dimension(64, 64));
        tglDeleteColor.setPreferredSize(new java.awt.Dimension(64, 64));
        tglDeleteColor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_center_focus_weak_white_24dp.png"))); // NOI18N
        tglDeleteColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDeleteColorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tglStretched, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(244, 244, 244)
                        .addComponent(pnlColorChoosed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tglDeleteColor, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblName)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pnlPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlColorChoosed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tglStretched, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tglDeleteColor, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed
        
    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        FormMain.formTextureEditor = null;
    }//GEN-LAST:event_formInternalFrameClosed
    
    private void tglStretchedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tglStretchedStateChanged
        imagePreview.setStretched(tglStretched.isSelected());
        imagePreview.redraw();
    }//GEN-LAST:event_tglStretchedStateChanged

    private void tglDeleteColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglDeleteColorActionPerformed
        setDeleteColorMode(!isDeleteColorMode);
    }//GEN-LAST:event_tglDeleteColorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel lblName;
    private javax.swing.JPanel pnlColorChoosed;
    private javax.swing.JPanel pnlPreview;
    private javax.swing.JToggleButton tglDeleteColor;
    private javax.swing.JToggleButton tglStretched;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
