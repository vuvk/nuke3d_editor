/**
    Form for preview materials in Nuke3D Editor
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
package com.vuvk.n3d.editor.forms;

import com.vuvk.n3d.components.PanelImagePreview;
import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Texture;
import java.awt.Color;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormMaterialEditor extends javax.swing.JInternalFrame {

    /** редактируемая в данный момент нода в списке */
    public static DefaultMutableTreeNode selectedTreeNode = null;
    /** выбранный материал для редактирования */
    public static Material selectedMaterial = null;
    /** окно редактирования материала */
    static FormMaterialAnimator formMaterialAnimator = null;
    /** поле для рисования предпросмотра */
    static PanelImagePreview imagePreview;
    
    /**
     * Подготовка формы для отображения - подгружается изображение из текстуры первого кадра материала
     */
    public void prepareForm() {  
        if (selectedMaterial == null) {
            dispose();
        }
        
        // получить имя редактируемой текстуры
        txtName.setText(selectedMaterial.getName());
        // получить в панель предпросмотра ссылку на картинку
        Material.Frame frame = selectedMaterial.getFrame(0);
        if (frame != null) {
            imagePreview.image = frame.getImage();
        } else {
            imagePreview.image = null;
        }
        imagePreview.redraw();
        
        // установить тип материала
        int index = selectedMaterial.getMaterialType().ordinal();
        cmbMaterialType.setSelectedItem(index);
        cmbMaterialType.getModel().setSelectedItem(cmbMaterialType.getItemAt(index));
    }
    
    /**
     * Creates new form FormMaterialEditor
     */
    public FormMaterialEditor() {
        initComponents();
        
        imagePreview = new PanelImagePreview();
        imagePreview.setSize(512, 512);
        pnlPreview.add(imagePreview);
        //imagePreview.setLocation(5, 16);  
        imagePreview.setLocation(14, 26); 
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
        pnlPreview = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        cmbMaterialType = new javax.swing.JComboBox<>();
        lblType = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        tglStretched = new javax.swing.JToggleButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Редактор материала");
        setPreferredSize(new java.awt.Dimension(775, 629));
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
                formInternalFrameActivated(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        lblName.setText("Имя");

        pnlPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Предпросмотр"));
        pnlPreview.setDoubleBuffered(false);

        javax.swing.GroupLayout pnlPreviewLayout = new javax.swing.GroupLayout(pnlPreview);
        pnlPreview.setLayout(pnlPreviewLayout);
        pnlPreviewLayout.setHorizontalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );
        pnlPreviewLayout.setVerticalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );

        txtName.setEditable(false);
        txtName.setText("<not available>");

        cmbMaterialType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "По умолчанию", "С альфа-каналом", "Полупрозрачность" }));
        cmbMaterialType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMaterialTypeActionPerformed(evt);
            }
        });

        lblType.setText("Тип");

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_done_white_24dp.png"))); // NOI18N
        btnClose.setToolTipText("Закрыть");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        tglStretched.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_photo_size_select_large_white_24dp.png"))); // NOI18N
        tglStretched.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_photo_size_select_actual_white_24dp.png"))); // NOI18N
        tglStretched.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tglStretchedStateChanged(evt);
            }
        });
        tglStretched.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglStretchedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblName)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(lblType)
                            .addGap(33, 33, 33)
                            .addComponent(cmbMaterialType, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pnlPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tglStretched, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbMaterialType, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblType)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tglStretched, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        FormMain.formMaterialEditor = null;
    }//GEN-LAST:event_formInternalFrameClosed
    
    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        if (selectedMaterial == null) {
            dispose();
        }
        
        // получить имя редактируемой текстуры
        txtName.setText(selectedMaterial.getName());
        // получить в панель предпросмотра ссылку на картинку
        imagePreview.image = selectedMaterial.getFrame(0).getImage();
        imagePreview.redraw();
        // установить тип материала
        int index = selectedMaterial.getMaterialType().ordinal();
        cmbMaterialType.setSelectedItem(index);
        cmbMaterialType.getModel().setSelectedItem(cmbMaterialType.getItemAt(index));
    }//GEN-LAST:event_formInternalFrameActivated

    private void cmbMaterialTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMaterialTypeActionPerformed
        if (selectedMaterial != null) {
            switch (cmbMaterialType.getSelectedIndex()) {
                default:
                case 0 : 
                    selectedMaterial.setMaterialType(Material.Type.Default);
                    break;     
                    
                case 1 : 
                    selectedMaterial.setMaterialType(Material.Type.AlphaChannel);
                    break;
                    
                case 2 : 
                    selectedMaterial.setMaterialType(Material.Type.Transparent);
                    break;
            }
        }
    }//GEN-LAST:event_cmbMaterialTypeActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void tglStretchedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tglStretchedStateChanged
        imagePreview.isStretched = tglStretched.isSelected();
        imagePreview.redraw();
    }//GEN-LAST:event_tglStretchedStateChanged

    private void tglStretchedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglStretchedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tglStretchedActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JComboBox<String> cmbMaterialType;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblType;
    private javax.swing.JPanel pnlPreview;
    private javax.swing.JToggleButton tglStretched;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
