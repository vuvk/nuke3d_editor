/**
    Form for edit skybox in Nuke3D Editor
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

import com.vuvk.n3d.Const;
import com.vuvk.n3d.components.PanelImagePreview;
import com.vuvk.n3d.resources.Side;
import com.vuvk.n3d.resources.Skybox;
import com.vuvk.n3d.resources.Texture;
import com.vuvk.n3d.utils.MessageDialog;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.MouseInputListener;


/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormSkyboxEditor extends javax.swing.JInternalFrame {
    /** выбранный для редактирования скайбокс */
    public static Skybox selectedSkybox = null;
        
    /** стороны куба */
    static SkyboxPreview[] sidePreviews = new SkyboxPreview[6];
        
    private static final Logger LOG = Logger.getLogger(FormSkyboxEditor.class.getName());

    class SkyboxPreview extends PanelImagePreview implements MouseListener {
        /** сторона скайбокса, за которую отвечает этот элемент */
        Side side;

        public SkyboxPreview(Container window, Side side) {
            super(window);
            setImage(null);
            this.side = side;
            
            addMouseListener(this);
        }
        
        /**
         * Получить сторону куба, которая рисуется в превью
         * @return значение енумератора Side
         */
        public Side getSide() {
            return side;
        }        
        
        @Override
        public void setImage(BufferedImage image) {
            if (image != null) {
                super.setImage(image);
                setStretched(true);
                setDrawBorder(true);
            } else {
                super.setImage(Const.ICONS.get("Add"));
                setStretched(false);
                setDrawBorder(false);
            }
        }
        
        /**
          * вызвать окно выбора текстуры и назначить выбранную в сторону
          */
        void chooseTexture() {
            FormTextureSelector form = new FormTextureSelector(FormMain.formMain);
            Texture txr = form.execute(true);

            if (txr != null) {
                selectedSkybox.setTexture(txr, side);                
                
                sidePreviews[side.getNum()].setImage(form.selectedTexture.getImage());
                sidePreviews[side.getNum()].redraw();
            }

            form.dispose();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                chooseTexture();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                selectedSkybox.setTexture(null, side);   
                
                sidePreviews[side.getNum()].setImage(null);
                sidePreviews[side.getNum()].redraw();               
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}  
    }
    
    /**
     * Creates new form FormSkyboxEditor
     */
    public FormSkyboxEditor() {
        initComponents();
                
        for (int i = 0; i < 6; ++i) {
            sidePreviews[i] = new SkyboxPreview(this, Side.getByNum(i));
        }
        
        pnlFront .add(sidePreviews[0]);        
        pnlBack  .add(sidePreviews[1]);        
        pnlLeft  .add(sidePreviews[2]);        
        pnlRight .add(sidePreviews[3]);        
        pnlTop   .add(sidePreviews[4]);        
        pnlBottom.add(sidePreviews[5]);
        
        for (int i = 0; i < 6; ++i) {
            sidePreviews[i].setSize(128, 128);
            sidePreviews[i].setLocation(5, 16);
        }
    }
    /**
     * Подготовка формы для отображения - подгружаются изображения текстур сторон скайбокса
     * @param firstRun - первый запуск окна
     */
    public void prepareForm(boolean firstRun) {  
        if (selectedSkybox == null) {
            dispose();
        }
        
        // получить имя
        txtName.setText(selectedSkybox.getName());
        // получить в панель предпросмотра ссылку на картинку
        Texture[] textures = new Texture[6];
        for (int i = 0; i < 6; ++i) {
            textures[i] = selectedSkybox.getTexture(Side.getByNum(i));
            if (textures[i] != null) {
                sidePreviews[i].setImage(textures[i].getImage());
            } else {
                sidePreviews[i].setImage(null);
            }
            sidePreviews[i].redraw();
        }
        
        if (firstRun) {
            setLocation((getParent().getWidth() >> 1) - (getWidth() >> 1), 0);
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
        pnlSides = new javax.swing.JPanel();
        pnlLeft = new javax.swing.JPanel();
        pnlFront = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        pnlBack = new javax.swing.JPanel();
        pnlBottom = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();
        btnPreview = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Редактор скайбокса");
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

        pnlSides.setBorder(javax.swing.BorderFactory.createTitledBorder("Текстуры сторон скайбокса"));

        pnlLeft.setBorder(javax.swing.BorderFactory.createTitledBorder("Left"));
        pnlLeft.setPreferredSize(new java.awt.Dimension(140, 151));

        javax.swing.GroupLayout pnlLeftLayout = new javax.swing.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );

        pnlFront.setBorder(javax.swing.BorderFactory.createTitledBorder("Front"));
        pnlFront.setPreferredSize(new java.awt.Dimension(140, 151));

        javax.swing.GroupLayout pnlFrontLayout = new javax.swing.GroupLayout(pnlFront);
        pnlFront.setLayout(pnlFrontLayout);
        pnlFrontLayout.setHorizontalGroup(
            pnlFrontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        pnlFrontLayout.setVerticalGroup(
            pnlFrontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );

        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder("Top"));
        pnlTop.setPreferredSize(new java.awt.Dimension(140, 151));

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );

        pnlBack.setBorder(javax.swing.BorderFactory.createTitledBorder("Back"));
        pnlBack.setPreferredSize(new java.awt.Dimension(140, 151));

        javax.swing.GroupLayout pnlBackLayout = new javax.swing.GroupLayout(pnlBack);
        pnlBack.setLayout(pnlBackLayout);
        pnlBackLayout.setHorizontalGroup(
            pnlBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        pnlBackLayout.setVerticalGroup(
            pnlBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );

        pnlBottom.setBorder(javax.swing.BorderFactory.createTitledBorder("Bottom"));
        pnlBottom.setPreferredSize(new java.awt.Dimension(140, 151));

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );

        pnlRight.setBorder(javax.swing.BorderFactory.createTitledBorder("Right"));
        pnlRight.setPreferredSize(new java.awt.Dimension(140, 151));

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlSidesLayout = new javax.swing.GroupLayout(pnlSides);
        pnlSides.setLayout(pnlSidesLayout);
        pnlSidesLayout.setHorizontalGroup(
            pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addComponent(pnlBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pnlFront, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlTop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSidesLayout.setVerticalGroup(
            pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlFront, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_3d_rotation_white.png"))); // NOI18N
        btnPreview.setToolTipText("Предпросмотр");
        btnPreview.setMaximumSize(new java.awt.Dimension(64, 64));
        btnPreview.setMinimumSize(new java.awt.Dimension(64, 64));
        btnPreview.setPreferredSize(new java.awt.Dimension(64, 64));
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
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
                        .addComponent(lblName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSides, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlSides, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        try {
            setClosed(true);
        } catch (PropertyVetoException ex) {
            LOG.log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        FormMain.closeFormSoundEditor();
        new FormSkyboxPreview(FormMain.formMain).execute(selectedSkybox);
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        FormMain.formSkyboxEditor = null;
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPreview;
    private javax.swing.JLabel lblName;
    private javax.swing.JPanel pnlBack;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlFront;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JPanel pnlSides;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
