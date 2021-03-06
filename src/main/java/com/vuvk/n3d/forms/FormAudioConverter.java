/**
    Form of Audio Converter (Nuke3D Editor)
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

import com.vuvk.n3d.utils.AudioUtils;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.SpinnerNumberModel;
import org.apache.commons.io.FilenameUtils;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.MultimediaInfo;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormAudioConverter extends javax.swing.JDialog {
    /** входной аудио файл */
    private File inputFile;
    /** выходной аудио файл */
    private File outputFile;
    /** результат конвертации */
    private File resultFile = null;
    /** ссылка на текущее окно */
    FormAudioConverter formAudioConverter;
    
    /**
     * Класс отображения процесса конвертации
     */
    class EncoderListener implements EncoderProgressListener {
        
        @Override
        public void sourceInfo(MultimediaInfo mi) {
            //
        }

        @Override
        public void progress(int permil) {                                         
            double progress = permil * 0.1;
            barProgress.setValue((int)progress);
            barProgress.getParent().repaint();
        }

        @Override
        public void message(String message) {
            System.out.println(message);
        }        
    }

    /**
     * Управление доступностью элементов настройки конвертации
     * @param mode true - элементы доступны
     */
    void enableOptions(boolean mode) {
        cmbChannels.setEnabled(mode);
        cmbSampleRate.setEnabled(mode);
        sprBitrate.setEnabled(mode);
        btnConvert.setEnabled(mode);
    }
    
    /**
     * Управление видимостью элементов прогресса конвертации
     * @param mode true - элементы видимы
     */
    void visibleProgress(boolean mode) {
        lblProgress.setVisible(mode);
        barProgress.setVisible(mode);
        btnCancel.setVisible(mode);
    }
    
    /**
     * Установить возможный диапазон значений битрейта
     */
    void updateBitrateRange() {
        SpinnerNumberModel model = (SpinnerNumberModel) sprBitrate.getModel();
        
        int min = 0, 
            max = 0;
        
        switch (cmbChannels.getSelectedIndex()) {
            // моно
            case 0 : 
                switch (cmbSampleRate.getSelectedIndex()) {
                    case 0 : min = 8;  max = 40;  break; // 8000
                    case 1 : min = 16; max = 48;  break; // 11025
                    case 2 : min = 16; max = 96;  break; // 16000
                    case 3 : min = 16; max = 88;  break; // 22050
                    case 4 : min = 16; max = 88;  break; // 24000
                    case 5 : min = 32; max = 184; break; // 32000
                    case 6 : min = 32; max = 240; break; // 44100
                    case 7 : min = 32; max = 240; break; // 48000
                }  
                break;
                
            // стерео
            case 1 : 
                switch (cmbSampleRate.getSelectedIndex()) {
                    case 0 : min = 16; max = 80;  break; // 8000
                    case 1 : min = 16; max = 96;  break; // 11025
                    case 2 : min = 24; max = 200; break; // 16000
                    case 3 : min = 32; max = 176; break; // 22050
                    case 4 : min = 32; max = 176; break; // 24000
                    case 5 : min = 64; max = 376; break; // 32000
                    case 6 : min = 64; max = 496; break; // 44100
                    case 7 : min = 48; max = 496; break; // 48000
                }  
                break;
        }
        
        int value = (Integer) model.getValue();
        
        if (value < min) {
            model.setValue(min);
        } else if (value > max) {
            model.setValue(max);
        }
        
        model.setMinimum(min);
        model.setMaximum(max);
    }
    
    /**
     * Creates new form FormAudioConverter
     */
    public FormAudioConverter(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setLocationRelativeTo(null);
        
        enableOptions(true);
        visibleProgress(false);
        updateBitrateRange();
                
        formAudioConverter = this;
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
        cmbChannels = new javax.swing.JComboBox<>();
        lblChannels = new javax.swing.JLabel();
        lblBitrate = new javax.swing.JLabel();
        lblSampleRate = new javax.swing.JLabel();
        cmbSampleRate = new javax.swing.JComboBox<>();
        btnConvert = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        sprBitrate = new javax.swing.JSpinner();
        barProgress = new javax.swing.JProgressBar();
        lblProgress = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Аудио конвертер");
        setMaximumSize(new java.awt.Dimension(544, 2147483647));
        setMinimumSize(new java.awt.Dimension(544, 0));
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblName.setText("Имя");

        txtName.setEditable(false);
        txtName.setText("<not available>");
        txtName.setMaximumSize(new java.awt.Dimension(432, 27));
        txtName.setMinimumSize(new java.awt.Dimension(432, 27));
        txtName.setPreferredSize(new java.awt.Dimension(432, 27));

        cmbChannels.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Моно", "Стерео" }));
        cmbChannels.setSelectedIndex(1);
        cmbChannels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbChannelsActionPerformed(evt);
            }
        });

        lblChannels.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblChannels.setText("Канал");

        lblBitrate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBitrate.setText("Битрейт (кбит/с)");

        lblSampleRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSampleRate.setText("Частота");

        cmbSampleRate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8000 Гц", "11025 Гц", "16000 Гц", "22050 Гц", "24000 Гц", "32000 Гц", "44100 Гц", "48000 Гц" }));
        cmbSampleRate.setSelectedIndex(6);
        cmbSampleRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSampleRateActionPerformed(evt);
            }
        });

        btnConvert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_forward_white_48dp.png"))); // NOI18N
        btnConvert.setToolTipText("Конвертировать");
        btnConvert.setMaximumSize(new java.awt.Dimension(64, 64));
        btnConvert.setMinimumSize(new java.awt.Dimension(64, 64));
        btnConvert.setPreferredSize(new java.awt.Dimension(64, 64));
        btnConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConvertActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_block_white_48dp.png"))); // NOI18N
        btnCancel.setToolTipText("Отмена");
        btnCancel.setMaximumSize(new java.awt.Dimension(64, 64));
        btnCancel.setMinimumSize(new java.awt.Dimension(64, 64));
        btnCancel.setPreferredSize(new java.awt.Dimension(64, 64));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        sprBitrate.setModel(new javax.swing.SpinnerNumberModel(256, 8, 1024, 8));

        lblProgress.setText("Прогресс");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblSampleRate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblBitrate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblChannels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbChannels, 0, 128, Short.MAX_VALUE)
                                    .addComponent(cmbSampleRate, 0, 128, Short.MAX_VALUE)
                                    .addComponent(sprBitrate))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConvert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(barProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblProgress))
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbChannels, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblChannels))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBitrate)
                    .addComponent(sprBitrate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSampleRate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSampleRate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnConvert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblProgress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConvertActionPerformed
        enableOptions(false);
        visibleProgress(true);
        barProgress.setValue(0);             
        
        //битрейт = частота дискретизации × разрядность × каналы
        
        new Thread(() -> {
            int channels = cmbChannels.getSelectedIndex() + 1;     // 1 or 2
            int bitrate = (Integer)sprBitrate.getValue() * 1000;
            int sampleRate = 0;
            switch (cmbSampleRate.getSelectedIndex()) {
                case 0 : sampleRate = 8000;  break;
                case 1 : sampleRate = 11025; break;
                case 2 : sampleRate = 16000; break;
                case 3 : sampleRate = 22050; break;
                case 4 : sampleRate = 24000; break;
                case 5 : sampleRate = 32000; break;
                case 6 : sampleRate = 44100; break;
                case 7 : sampleRate = 48000; break;
            }   

            if (AudioUtils.convert(inputFile, outputFile,
                                   AudioUtils.AudioFormat.OGG,
                                   new EncoderListener(),
                                   bitrate, channels, sampleRate)
               ) {            
                // запоминаем результат и пора закрывать окно
                resultFile = outputFile;
                formAudioConverter.dispatchEvent(new WindowEvent(formAudioConverter, WindowEvent.WINDOW_CLOSING));
            } else {
                resultFile = null;
            }
        }).start();
    }//GEN-LAST:event_btnConvertActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        AudioUtils.getEncoder().abortEncoding();
        
        if (outputFile.exists()) {
            outputFile.delete();
        }
        resultFile = null;
        
        enableOptions(true);
        visibleProgress(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        AudioUtils.getEncoder().abortEncoding();
    }//GEN-LAST:event_formWindowClosing

    private void cmbChannelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbChannelsActionPerformed
        updateBitrateRange();
    }//GEN-LAST:event_cmbChannelsActionPerformed

    private void cmbSampleRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSampleRateActionPerformed
        updateBitrateRange();
    }//GEN-LAST:event_cmbSampleRateActionPerformed

    @Override
    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Use 'execute()' for set visible AudioConverter!");
    }        
    
    /**
     * Открыть окно аудиоконвертера. 
     * @param inputFile Входной файл для конверсии
     * @param outputFile Выходной файл конверсии
     * @return Полученный файл, либо null, если конверсия отменена или не удалась.
     */
    public File execute(File inputFile, File outputFile) {
        if (!inputFile.exists()) {
            return null;
        }
        
        this.inputFile  = inputFile;
        this.outputFile = outputFile;
        txtName.setText(FilenameUtils.getBaseName(outputFile.getName()));
        
        super.setVisible(true);
        
        return resultFile;
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barProgress;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConvert;
    private javax.swing.JComboBox<String> cmbChannels;
    private javax.swing.JComboBox<String> cmbSampleRate;
    private javax.swing.JLabel lblBitrate;
    private javax.swing.JLabel lblChannels;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblProgress;
    private javax.swing.JLabel lblSampleRate;
    private javax.swing.JSpinner sprBitrate;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
