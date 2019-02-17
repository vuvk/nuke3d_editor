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
     * Creates new form FormMaterialAnimator
     */
    public FormMaterialAnimator(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        imagePreview = new PanelImagePreview(this);
        imagePreview.setSize(256, 256);
        PanelPreview.add(imagePreview);
        imagePreview.setLocation(5, 16);  
        //imagePreview.setLocation(14, 26); 
        
        ListSelectionModel model = TableFrames.getSelectionModel();
        model.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                setAnimationPlaying(false);
                
                if (TableFrames.getSelectedRowCount() > 0) {
                    selectedFrameIndex = TableFrames.getSelectedRow();
                    Material.Frame frame = selectedMaterial.getFrame(selectedFrameIndex);
                    imagePreview.setImage(frame.getImage());
                    CheckStretched.setEnabled(true);
                    ButtonChooseTexture.setEnabled(true);
                    SpinnerFrameDelay.setEnabled(true);
                    SpinnerFrameDelay.setValue(Double.valueOf(frame.getDelay()));
                } else {
                    selectedFrameIndex = -1;
                    imagePreview.setImage(null);
                    CheckStretched.setEnabled(false);
                    ButtonChooseTexture.setEnabled(false);
                    SpinnerFrameDelay.setEnabled(false);
                    SpinnerFrameDelay.setValue(Double.valueOf(1.0));
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

        jPanel1 = new javax.swing.JPanel();
        ButtonMoveUp = new javax.swing.JButton();
        ButtonMoveDown = new javax.swing.JButton();
        ButtonAddFrame = new javax.swing.JButton();
        ButtonDelFrame = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableFrames = new javax.swing.JTable();
        PanelProperties = new javax.swing.JPanel();
        CheckStretched = new javax.swing.JCheckBox();
        ButtonChooseTexture = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        SpinnerFrameDelay = new javax.swing.JSpinner();
        PanelPreview = new javax.swing.JPanel();
        PanelAnimation = new javax.swing.JPanel();
        ButtonAnimation = new javax.swing.JButton();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Кадры"));

        ButtonMoveUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/up.png"))); // NOI18N
        ButtonMoveUp.setToolTipText("Поднять кадр вверх");
        ButtonMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonMoveUpActionPerformed(evt);
            }
        });

        ButtonMoveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/down.png"))); // NOI18N
        ButtonMoveDown.setToolTipText("Опустить кадр вниз");
        ButtonMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonMoveDownActionPerformed(evt);
            }
        });

        ButtonAddFrame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/new.png"))); // NOI18N
        ButtonAddFrame.setText("Добавить кадр");
        ButtonAddFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonAddFrameActionPerformed(evt);
            }
        });

        ButtonDelFrame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/delete.png"))); // NOI18N
        ButtonDelFrame.setText("Удалить кадр");
        ButtonDelFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonDelFrameActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ButtonAddFrame, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(ButtonDelFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ButtonMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ButtonMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ButtonMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonAddFrame)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonDelFrame)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelProperties.setBorder(javax.swing.BorderFactory.createTitledBorder("Свойства кадра"));

        CheckStretched.setText("Растянуть");
        CheckStretched.setEnabled(false);
        CheckStretched.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CheckStretchedStateChanged(evt);
            }
        });

        ButtonChooseTexture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/open.png"))); // NOI18N
        ButtonChooseTexture.setText("Выбрать текстуру");
        ButtonChooseTexture.setEnabled(false);
        ButtonChooseTexture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonChooseTextureActionPerformed(evt);
            }
        });

        jLabel1.setText("Задержка (в сек)");

        SpinnerFrameDelay.setModel(new javax.swing.SpinnerNumberModel(1.0d, null, null, 1.0d));
        SpinnerFrameDelay.setDoubleBuffered(true);
        SpinnerFrameDelay.setEditor(new javax.swing.JSpinner.NumberEditor(SpinnerFrameDelay, "0.000"));
        SpinnerFrameDelay.setEnabled(false);
        SpinnerFrameDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SpinnerFrameDelayStateChanged(evt);
            }
        });

        PanelPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Предпросмотр"));
        PanelPreview.setDoubleBuffered(false);

        javax.swing.GroupLayout PanelPreviewLayout = new javax.swing.GroupLayout(PanelPreview);
        PanelPreview.setLayout(PanelPreviewLayout);
        PanelPreviewLayout.setHorizontalGroup(
            PanelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );
        PanelPreviewLayout.setVerticalGroup(
            PanelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelPropertiesLayout = new javax.swing.GroupLayout(PanelProperties);
        PanelProperties.setLayout(PanelPropertiesLayout);
        PanelPropertiesLayout.setHorizontalGroup(
            PanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPropertiesLayout.createSequentialGroup()
                .addGroup(PanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelPropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(CheckStretched))
                    .addGroup(PanelPropertiesLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(PanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ButtonChooseTexture, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelPropertiesLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(SpinnerFrameDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PanelPropertiesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(PanelPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelPropertiesLayout.setVerticalGroup(
            PanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CheckStretched)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ButtonChooseTexture)
                .addGap(12, 12, 12)
                .addGroup(PanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(SpinnerFrameDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        PanelAnimation.setBorder(javax.swing.BorderFactory.createTitledBorder("Анимация"));

        ButtonAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/play.png"))); // NOI18N
        ButtonAnimation.setText("Воспроизвести");
        ButtonAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonAnimationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelAnimationLayout = new javax.swing.GroupLayout(PanelAnimation);
        PanelAnimation.setLayout(PanelAnimationLayout);
        PanelAnimationLayout.setHorizontalGroup(
            PanelAnimationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(PanelAnimationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelAnimationLayout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addComponent(ButtonAnimation, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addGap(31, 31, 31)))
        );
        PanelAnimationLayout.setVerticalGroup(
            PanelAnimationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 57, Short.MAX_VALUE)
            .addGroup(PanelAnimationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelAnimationLayout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(ButtonAnimation)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelAnimation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelAnimation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CheckStretchedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CheckStretchedStateChanged
        imagePreview.setStretched(CheckStretched.isSelected());
        imagePreview.redraw();
    }//GEN-LAST:event_CheckStretchedStateChanged

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

    private void ButtonAddFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAddFrameActionPerformed
        setAnimationPlaying(false);
        
        if (selectedMaterial != null) {
            int index;
            Material.Frame frame = new Material.Frame();
            if (selectedFrameIndex == -1) {
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
        }
    }//GEN-LAST:event_ButtonAddFrameActionPerformed

    private void ButtonChooseTextureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonChooseTextureActionPerformed
        setAnimationPlaying(false);
            
        //int prevRow = TableFrames.getSelectedRow();
        
        FormTextureSelector form = new FormTextureSelector(FormMain.formMain, true);
        form.setVisible(true);
        
        if (form.selectedTexture != null) {
            selectedMaterial.getFrame(selectedFrameIndex).setTexture(form.selectedTexture);
            
            imagePreview.setImage(selectedMaterial.getFrame(selectedFrameIndex).getImage());
            imagePreview.redraw();
        }
        
        form.dispose();
        
        // выбрать кадр, ранее выбранный
        TableFrames.changeSelection(selectedFrameIndex, 0, false, false);
    }//GEN-LAST:event_ButtonChooseTextureActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (selectedMaterial == null) {
            dispose();
            return;
        }
        
        fillTableFrames();
        TableFrames.changeSelection(0, 0, false, false);   
    }//GEN-LAST:event_formWindowOpened

    private void ButtonDelFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonDelFrameActionPerformed
        setAnimationPlaying(false);
        
        if (selectedMaterial.getFramesCount() > 1 && TableFrames.getSelectedRowCount() > 0) {
            int prevRow = TableFrames.getSelectedRow();
            selectedMaterial.removeFrame(selectedFrameIndex);
            
            fillTableFrames();
            
            // если индекс заехал за размер кадров, то выбрать предыдущий
            if (prevRow >= selectedMaterial.getFramesCount()) {
                --prevRow;
            }
            
            TableFrames.changeSelection(prevRow, 0, false, false);
        }
    }//GEN-LAST:event_ButtonDelFrameActionPerformed

    private void ButtonMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonMoveUpActionPerformed
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
    }//GEN-LAST:event_ButtonMoveUpActionPerformed

    /**
     * Установить режим воспроизведения анимации
     * @param mode true - вкл/false - выкл воспроизведение анимации
     */
    private void setAnimationPlaying(boolean mode) {
        isAnimationPlay = mode;
        
        if (isAnimationPlay) {
            ButtonAnimation.setText("Остановить");
            ButtonAnimation.setIcon(new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/stop.png")));
            
            if (animatorThread != null) {
                animatorThread.interrupt();
            }
            animatorThread = new AnimatorThread();
            animatorThread.start();
        } else {
            ButtonAnimation.setText("Воспроизвести");
            ButtonAnimation.setIcon(new ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/play.png")));
            
            if (animatorThread != null) {
                animatorThread.interrupt();
                animatorThread = null;
            }
        }        
    }
    
    private void ButtonAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAnimationActionPerformed
        setAnimationPlaying(!isAnimationPlay);        
    }//GEN-LAST:event_ButtonAnimationActionPerformed

    private void ButtonMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonMoveDownActionPerformed
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
    }//GEN-LAST:event_ButtonMoveDownActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        setAnimationPlaying(false);
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonAddFrame;
    private javax.swing.JButton ButtonAnimation;
    private javax.swing.JButton ButtonChooseTexture;
    private javax.swing.JButton ButtonDelFrame;
    private javax.swing.JButton ButtonMoveDown;
    private javax.swing.JButton ButtonMoveUp;
    private javax.swing.JCheckBox CheckStretched;
    private javax.swing.JPanel PanelAnimation;
    private javax.swing.JPanel PanelPreview;
    private javax.swing.JPanel PanelProperties;
    private javax.swing.JSpinner SpinnerFrameDelay;
    private javax.swing.JTable TableFrames;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
