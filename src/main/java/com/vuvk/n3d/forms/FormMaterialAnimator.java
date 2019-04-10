/**
    Form for edit materials in Nuke3D Editor
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

import com.vuvk.n3d.components.PanelImagePreview;
import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Texture;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormMaterialAnimator extends javax.swing.JDialog {
    /** выбранный материал для редактирования */
    public Material selectedMaterial = null;
    /** поле для рисования предпросмотра */
    static PanelImagePreview imagePreview;
    /** номер выбранного кадра */
    static int selectedFrameIndex = -1;
    /** играется анимация? */
    static boolean isAnimationPlay = false;
    /** поток анимации */
    static AnimatorThread animatorThread;
    
    
    /**
     * Поток анимации
     */
    class AnimatorThread extends Thread {  
        /** кадр для отображения */
        private int curFrame;
        /** время с предыдущей отработки потока */
        private long prevTime;
        
        public AnimatorThread() {
            setFrame(0);
            prevTime = System.currentTimeMillis();
        }
        
        /** 
         * Установить номер кадра для отображения
         * @param num Номер кадра, начинающийся с 0
         */
        public void setFrame(int num) {
            if (selectedMaterial != null) {
                if (num > -1 && num < selectedMaterial.getFramesCount()) {
                    curFrame = num;                    
                }
            }            
        }
        
        /** 
         * Получить номер текущего кадра для отображения
         * @return целое число >= 0
         */
        public int getFrame() {
            return curFrame;
        }
        
        /**
         * Событие смены кадра, срабатывающее раз в delay сек
         */
        @Override
        public void run() {
            super.run();
            
            if (selectedMaterial == null) {
                setAnimationPlaying(false);
                return;
            }
            
            while (isAnimationPlay) {
                Material.Frame frame = selectedMaterial.getFrame(curFrame);
                long delay = (long)(frame.getDelay() * 1000);
                long time = System.currentTimeMillis();

                if (imagePreview != null) {
                    if (frame != null) {
                        imagePreview.setImage(frame.getImage());
                    }
                    imagePreview.redraw();               
                }

                ++curFrame;
                if (curFrame >= selectedMaterial.getFramesCount()) {
                    curFrame = 0;
                }

                try {
                    /*long timeForSleep = delay - (time - prevTime);
                    if (timeForSleep < 0) {
                        timeForSleep = 0;
                    } else if (timeForSleep > delay) {
                        timeForSleep = delay;
                    }
                    sleep(timeForSleep);*/        
                    
                    sleep(delay);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(FormMaterialAnimator.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("sleep interrupted");
                }
                
                prevTime = time;
            }
        }
    }
        
    /**
     * Заполнение таблицы данными о кадрах материала
     */
    void fillTableFrames() {
        setAnimationPlaying(false);
                
        // собираем информацию о кадрах
        DefaultTableModel tableFramesModel = (DefaultTableModel)TableFrames.getModel();
        tableFramesModel.setRowCount(0);
        for (int i = 0; i < selectedMaterial.getFramesCount(); ++i) {
            Material.Frame frame = selectedMaterial.getFrame(i);
            if (frame != null) {
                tableFramesModel.addRow(new Object[]{"Frame #" + i, frame.getDelay()});
            }
        }
    }

    /**
     * вызвать окно выбора текстуры и назначить выбранную в кадр
     */
    void chooseTexture() {
        setAnimationPlaying(false);
            
        //int prevRow = TableFrames.getSelectedRow();
        
        FormTextureSelector form = new FormTextureSelector(FormMain.formMain);
        Texture txr = form.execute(true);
        
        if (txr != null) {
            selectedMaterial.getFrame(selectedFrameIndex).setTexture(txr);
            
            imagePreview.setImage(selectedMaterial.getFrame(selectedFrameIndex).getImage());
            imagePreview.redraw();
        }
        
        form.dispose();
        
        // выбрать кадр, ранее выбранный
        TableFrames.changeSelection(selectedFrameIndex, 0, false, false);
    }
    
    /**
     * Creates new form FormMaterialAnimator
     */
    public FormMaterialAnimator(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        imagePreview = new PanelImagePreview(this);
        imagePreview.setSize(256, 256);
        pnlPreview.add(imagePreview);
        imagePreview.setLocation(5, 16);  
        //imagePreview.setLocation(14, 26); 
        
        ListSelectionModel model = TableFrames.getSelectionModel();
        model.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                setAnimationPlaying(false);
                
                if (selectedMaterial.getFramesCount() > 0 && 
                    TableFrames.getSelectedRowCount() > 0
                   ) {
                    selectedFrameIndex = TableFrames.getSelectedRow();
                    tglStretched.setEnabled(true);
                    tglAnimation.setEnabled(true);
                    btnChooseTexture.setEnabled(true);
                    SpinnerFrameDelay.setEnabled(true);
                    Material.Frame frame = selectedMaterial.getFrame(selectedFrameIndex);
                    if (frame != null) {
                        imagePreview.setImage(frame.getImage());
                        SpinnerFrameDelay.setValue(frame.getDelay());
                    } else {
                        imagePreview.setImage(null);
                        SpinnerFrameDelay.setValue(1.0);
                    }
                } else {
                    selectedFrameIndex = -1;
                    imagePreview.setImage(null);
                    tglStretched.setEnabled(false);
                    tglAnimation.setEnabled(false);
                    btnChooseTexture.setEnabled(false);
                    SpinnerFrameDelay.setEnabled(false);
                    SpinnerFrameDelay.setValue(1.0);
                }
                
                imagePreview.redraw();
            }
        });
                
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFrames = new javax.swing.JPanel();
        btnMoveDown = new javax.swing.JButton();
        btnAddFrame = new javax.swing.JButton();
        btnDelFrame = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableFrames = new javax.swing.JTable();
        btnMoveUp = new javax.swing.JButton();
        pnlProperties = new javax.swing.JPanel();
        btnChooseTexture = new javax.swing.JButton();
        lblDelay = new javax.swing.JLabel();
        SpinnerFrameDelay = new javax.swing.JSpinner();
        pnlPreview = new javax.swing.JPanel();
        tglAnimation = new javax.swing.JToggleButton();
        tglStretched = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Анимация");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        pnlFrames.setBorder(javax.swing.BorderFactory.createTitledBorder("Кадры"));

        btnMoveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_arrow_downward_white_24dp.png"))); // NOI18N
        btnMoveDown.setToolTipText("Опустить кадр вниз");
        btnMoveDown.setMaximumSize(new java.awt.Dimension(64, 64));
        btnMoveDown.setMinimumSize(new java.awt.Dimension(64, 64));
        btnMoveDown.setPreferredSize(new java.awt.Dimension(64, 64));
        btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownActionPerformed(evt);
            }
        });

        btnAddFrame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_add_white_24dp.png"))); // NOI18N
        btnAddFrame.setToolTipText("Добавить кадр");
        btnAddFrame.setMaximumSize(new java.awt.Dimension(64, 64));
        btnAddFrame.setMinimumSize(new java.awt.Dimension(64, 64));
        btnAddFrame.setPreferredSize(new java.awt.Dimension(64, 64));
        btnAddFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFrameActionPerformed(evt);
            }
        });

        btnDelFrame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_remove_white_24dp.png"))); // NOI18N
        btnDelFrame.setToolTipText("Удалить кадр");
        btnDelFrame.setMaximumSize(new java.awt.Dimension(64, 64));
        btnDelFrame.setMinimumSize(new java.awt.Dimension(64, 64));
        btnDelFrame.setPreferredSize(new java.awt.Dimension(64, 64));
        btnDelFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelFrameActionPerformed(evt);
            }
        });

        TableFrames.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Frame #0",  new Double(1.0)}
            },
            new String [] {
                "Кадр", "Задержка"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TableFrames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(TableFrames);

        btnMoveUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_arrow_upward_white_24dp.png"))); // NOI18N
        btnMoveUp.setToolTipText("Поднять кадр вверх");
        btnMoveUp.setMaximumSize(new java.awt.Dimension(64, 64));
        btnMoveUp.setMinimumSize(new java.awt.Dimension(64, 64));
        btnMoveUp.setPreferredSize(new java.awt.Dimension(64, 64));
        btnMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFramesLayout = new javax.swing.GroupLayout(pnlFrames);
        pnlFrames.setLayout(pnlFramesLayout);
        pnlFramesLayout.setHorizontalGroup(
            pnlFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFramesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFramesLayout.setVerticalGroup(
            pnlFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFramesLayout.createSequentialGroup()
                .addGroup(pnlFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFramesLayout.createSequentialGroup()
                        .addComponent(btnAddFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlProperties.setBorder(javax.swing.BorderFactory.createTitledBorder("Свойства кадра"));

        btnChooseTexture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_add_to_photos_white_24dp.png"))); // NOI18N
        btnChooseTexture.setToolTipText("Выбрать текстуру");
        btnChooseTexture.setEnabled(false);
        btnChooseTexture.setMaximumSize(new java.awt.Dimension(64, 64));
        btnChooseTexture.setMinimumSize(new java.awt.Dimension(64, 64));
        btnChooseTexture.setPreferredSize(new java.awt.Dimension(64, 64));
        btnChooseTexture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseTextureActionPerformed(evt);
            }
        });

        lblDelay.setText("Задержка (в сек)");

        SpinnerFrameDelay.setModel(new javax.swing.SpinnerNumberModel(1.0d, null, null, 1.0d));
        SpinnerFrameDelay.setDoubleBuffered(true);
        SpinnerFrameDelay.setEditor(new javax.swing.JSpinner.NumberEditor(SpinnerFrameDelay, "0.000"));
        SpinnerFrameDelay.setEnabled(false);
        SpinnerFrameDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SpinnerFrameDelayStateChanged(evt);
            }
        });

        pnlPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Предпросмотр"));
        pnlPreview.setDoubleBuffered(false);

        javax.swing.GroupLayout pnlPreviewLayout = new javax.swing.GroupLayout(pnlPreview);
        pnlPreview.setLayout(pnlPreviewLayout);
        pnlPreviewLayout.setHorizontalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );
        pnlPreviewLayout.setVerticalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        tglAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_play_arrow_white_24dp.png"))); // NOI18N
        tglAnimation.setToolTipText("Воспроизведение");
        tglAnimation.setEnabled(false);
        tglAnimation.setFocusPainted(false);
        tglAnimation.setMaximumSize(new java.awt.Dimension(64, 64));
        tglAnimation.setMinimumSize(new java.awt.Dimension(64, 64));
        tglAnimation.setPreferredSize(new java.awt.Dimension(64, 64));
        tglAnimation.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_stop_white_24dp.png"))); // NOI18N
        tglAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglAnimationActionPerformed(evt);
            }
        });

        tglStretched.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_photo_size_select_large_white_24dp.png"))); // NOI18N
        tglStretched.setToolTipText("Растянуть");
        tglStretched.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tglStretched.setEnabled(false);
        tglStretched.setMaximumSize(new java.awt.Dimension(64, 64));
        tglStretched.setMinimumSize(new java.awt.Dimension(64, 64));
        tglStretched.setPreferredSize(new java.awt.Dimension(64, 64));
        tglStretched.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_photo_size_select_actual_white_24dp.png"))); // NOI18N
        tglStretched.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tglStretchedStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlPropertiesLayout = new javax.swing.GroupLayout(pnlProperties);
        pnlProperties.setLayout(pnlPropertiesLayout);
        pnlPropertiesLayout.setHorizontalGroup(
            pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlPropertiesLayout.createSequentialGroup()
                        .addComponent(pnlPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnChooseTexture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tglStretched, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tglAnimation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPropertiesLayout.createSequentialGroup()
                        .addComponent(lblDelay)
                        .addGap(36, 36, 36)
                        .addComponent(SpinnerFrameDelay)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPropertiesLayout.setVerticalGroup(
            pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPropertiesLayout.createSequentialGroup()
                .addGroup(pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlPropertiesLayout.createSequentialGroup()
                        .addComponent(btnChooseTexture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tglStretched, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tglAnimation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDelay)
                    .addComponent(SpinnerFrameDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(pnlFrames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFrames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SpinnerFrameDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SpinnerFrameDelayStateChanged
        if (selectedMaterial != null && selectedFrameIndex > -1) {
            Double value = (Double)SpinnerFrameDelay.getValue();
            if (value < 0.0) {
                value = Math.abs(value);
                SpinnerFrameDelay.setValue(value);
            }
            selectedMaterial.getFrame(selectedFrameIndex).setDelay(value);
            TableFrames.setValueAt(value, selectedFrameIndex, 1);
        }
    }//GEN-LAST:event_SpinnerFrameDelayStateChanged

    private void btnAddFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFrameActionPerformed
        setAnimationPlaying(false);
        
        if (selectedMaterial != null) {
            int index;
            Material.Frame frame = new Material.Frame();
            if (selectedFrameIndex < 0) {
                selectedMaterial.addFrame(frame);
                index = selectedMaterial.getFramesCount() - 1;
            } else {
                index = selectedFrameIndex + 1;
                selectedMaterial.addFrame(index, frame);
            }
            
            // попробуем взять задержку с предыдущего кадра
            double prevDelay = 0.5;
            Material.Frame prev = selectedMaterial.getFrame(index - 1);
            if (prev != null) {
                prevDelay = prev.getDelay();
            }
            frame.setDelay(prevDelay);
            
            fillTableFrames();
            TableFrames.changeSelection(index, 0, false, false);    
            
            chooseTexture();
        }
    }//GEN-LAST:event_btnAddFrameActionPerformed

    private void btnChooseTextureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseTextureActionPerformed
        chooseTexture();
    }//GEN-LAST:event_btnChooseTextureActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (selectedMaterial == null) {
            dispose();
            return;
        }
        
        fillTableFrames();
        TableFrames.changeSelection(0, 0, false, false);   
    }//GEN-LAST:event_formWindowOpened

    private void btnDelFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelFrameActionPerformed
        setAnimationPlaying(false);
        
        if (selectedMaterial.getFramesCount() > 0 && 
            TableFrames.getSelectedRowCount() > 0
           ) {
            int prevRow = TableFrames.getSelectedRow();
            selectedMaterial.removeFrame(selectedFrameIndex);
            
            fillTableFrames();
            
            // если индекс заехал за размер кадров, то выбрать предыдущий
            if (prevRow >= selectedMaterial.getFramesCount()) {
                --prevRow;
            }
            
            if (prevRow >= 0) {
                TableFrames.changeSelection(prevRow, 0, false, false);
            }
        }
    }//GEN-LAST:event_btnDelFrameActionPerformed

    private void btnMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveUpActionPerformed
        setAnimationPlaying(false);
        
        if (selectedMaterial.getFramesCount() > 1 && TableFrames.getSelectedRowCount() > 0) {
            int prevRow = TableFrames.getSelectedRow();
            // выше верхнего не поднять
            if (prevRow == 0) {
                return;
            }
            
            // меняем кадры местами
            Material.Frame frameTemp = selectedMaterial.getFrame(prevRow - 1);
            selectedMaterial.setFrame(prevRow - 1, selectedMaterial.getFrame(prevRow));
            selectedMaterial.setFrame(prevRow, frameTemp);
            
            fillTableFrames();
            TableFrames.changeSelection(prevRow - 1, 0, false, false);            
        }
    }//GEN-LAST:event_btnMoveUpActionPerformed

    /**
     * Установить режим воспроизведения анимации
     * @param mode true - вкл/false - выкл воспроизведение анимации
     */
    private void setAnimationPlaying(boolean mode) {
        isAnimationPlay = mode;
        tglAnimation.setSelected(mode);
        
        if (isAnimationPlay) {
            tglAnimation.setToolTipText("Остановить");
            
            if (animatorThread != null) {
                animatorThread.interrupt();
            }
            animatorThread = new AnimatorThread();
            animatorThread.start();
        } else {
            tglAnimation.setToolTipText("Воспроизвести");
            
            if (animatorThread != null) {
                animatorThread.interrupt();
                animatorThread = null;
            }
        }        
    }
    
    private void btnMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveDownActionPerformed
        setAnimationPlaying(false);
        
        if (selectedMaterial.getFramesCount() > 1 && TableFrames.getSelectedRowCount() > 0) {
            int prevRow = TableFrames.getSelectedRow();
            // ниже нижнего не опустить
            if (prevRow >= selectedMaterial.getFramesCount() - 1) {
                return;
            }
            
            // меняем кадры местами
            Material.Frame frameTemp = selectedMaterial.getFrame(prevRow);
            selectedMaterial.setFrame(prevRow, selectedMaterial.getFrame(prevRow + 1));
            selectedMaterial.setFrame(prevRow + 1, frameTemp);
            
            fillTableFrames();
            TableFrames.changeSelection(prevRow + 1, 0, false, false);            
        }
    }//GEN-LAST:event_btnMoveDownActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        setAnimationPlaying(false);
    }//GEN-LAST:event_formWindowClosed

    private void tglAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglAnimationActionPerformed
        setAnimationPlaying(!isAnimationPlay);   
    }//GEN-LAST:event_tglAnimationActionPerformed

    private void tglStretchedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tglStretchedStateChanged
        imagePreview.setStretched(tglStretched.isSelected());
        imagePreview.redraw();
    }//GEN-LAST:event_tglStretchedStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner SpinnerFrameDelay;
    private javax.swing.JTable TableFrames;
    private javax.swing.JButton btnAddFrame;
    private javax.swing.JButton btnChooseTexture;
    private javax.swing.JButton btnDelFrame;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDelay;
    private javax.swing.JPanel pnlFrames;
    private javax.swing.JPanel pnlPreview;
    private javax.swing.JPanel pnlProperties;
    private javax.swing.JToggleButton tglAnimation;
    private javax.swing.JToggleButton tglStretched;
    // End of variables declaration//GEN-END:variables
}
