/**
    Form for choose material in Nuke3D Editor
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

import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Texture;
import com.vuvk.n3d.utils.ImageUtils;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormMaterialSelector extends javax.swing.JDialog {
    
    /** доступен ли выбор материала или это просто просмотр */
    private boolean selectionMode = true;
    /** выбранный материал */
    private Material selectedMaterial = null;
    
    /**
     * кастомный рендерер для ячеек списка
     */
    class MaterialCellRenderer extends DefaultListCellRenderer {
        public MaterialCellRenderer() {        
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setHorizontalTextPosition(CENTER);
            setVerticalTextPosition(BOTTOM);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {        
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }         
            
            Material mat = (Material) value;
            
            setIcon(null);  
            setText(mat.getName());
            
            Material.Frame frm = mat.getFrame(0);
            if (frm == null) {
                return this;
            }
            
            Texture txr = frm.getTexture();
            if (txr == null) {
                return this;
            }            
            
            double imageWidth = txr.getImage().getWidth();
            double imageHeight = txr.getImage().getHeight();
            
            int iconWidth  = 96;
            int iconHeight = 96;
            
            if (imageWidth != imageHeight) {
                if (imageWidth > imageHeight) {
                    iconHeight = (int)((double)iconHeight * (imageHeight / imageWidth));
                } else {
                    iconWidth = (int)((double)iconWidth * (imageWidth / imageHeight));
                }
            }
            
            ImageIcon icon = new ImageIcon();
            icon.setImage(ImageUtils.resizeImage(txr.getImage(), iconWidth, iconHeight));
            
            setIcon(icon);   
            
            return this;
        }        
    }
    
    /**
     * Creates new form FormMaterialSelector
     */
    public FormMaterialSelector(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        
        setLocationRelativeTo(null);
        
        DefaultListModel listModel = new DefaultListModel();
        //model.setSize(Texture.list.size());
        for (Material mat : Material.MATERIALS) {
            listModel.addElement(mat);
        }
        lstMaterials.setModel(listModel);       
        
        // задаем кастомный рендерер
        lstMaterials.setCellRenderer(new MaterialCellRenderer());
        
        // кастомное событие выбора ячейки        
        ListSelectionModel selModel = lstMaterials.getSelectionModel();
        selModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = lstMaterials.getSelectedIndex();
                if (index > -1 && index < Material.MATERIALS.size()) {
                    selectedMaterial = Material.MATERIALS.get(index);
                } else {
                    selectedMaterial = null;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnClose = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        scrlPane = new javax.swing.JScrollPane();
        lstMaterials = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Просмотр материалов");

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_close_white_24dp.png"))); // NOI18N
        btnClose.setToolTipText("Закрыть");
        btnClose.setMaximumSize(new java.awt.Dimension(64, 64));
        btnClose.setMinimumSize(new java.awt.Dimension(64, 64));
        btnClose.setPreferredSize(new java.awt.Dimension(64, 64));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnApply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_check_white_24dp.png"))); // NOI18N
        btnApply.setToolTipText("Выбрать");
        btnApply.setMaximumSize(new java.awt.Dimension(64, 64));
        btnApply.setMinimumSize(new java.awt.Dimension(64, 64));
        btnApply.setPreferredSize(new java.awt.Dimension(64, 64));
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        lstMaterials.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12", "Item 13", "Item 14", "Item 15", "Item 16", "Item 17", "Item 18", "Item 19", "Item 20", "Item 21", "Item 22", "Item 23", "Item 24", "Item 25", "Item 26", "Item 27", "Item 28", "Item 29" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstMaterials.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lstMaterials.setAutoscrolls(false);
        lstMaterials.setDoubleBuffered(true);
        lstMaterials.setFixedCellHeight(128);
        lstMaterials.setFixedCellWidth(128);
        lstMaterials.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        lstMaterials.setVisibleRowCount(-1);
        lstMaterials.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstMaterialsMouseClicked(evt);
            }
        });
        scrlPane.setViewportView(lstMaterials);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrlPane, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrlPane, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Вызвать окно выбора материала
     * @param selectionMode true-режим выбора материала, false - режим просмотра всех материалов
     * @return Материал, если выбран, иначе null
     */
    public Material execute(boolean selectionMode) {
        this.selectionMode = selectionMode;
        btnApply.setVisible(selectionMode);
        setVisible(true);
        return selectedMaterial;
    }
    
    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        selectedMaterial = null;
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_btnApplyActionPerformed

    private void lstMaterialsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstMaterialsMouseClicked
        // если режим выбора, то на двойной клик закрыть окно
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            if (lstMaterials.getSelectedIndex() != -1) {
                if (selectionMode) {
                    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }
            }
        }
    }//GEN-LAST:event_lstMaterialsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnClose;
    private javax.swing.JList<String> lstMaterials;
    private javax.swing.JScrollPane scrlPane;
    // End of variables declaration//GEN-END:variables
}
