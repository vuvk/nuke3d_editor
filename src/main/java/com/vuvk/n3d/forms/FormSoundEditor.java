/**
    Form for preview sounds in Nuke3D Editor
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

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.vuvk.n3d.resources.Sound;
import com.vuvk.n3d.utils.MessageDialog;
import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.bean.playerbean.MediaPlayer;
//import javazoom.jlgui.basicplayer.BasicPlayer;
//import javazoom.jlgui.basicplayer.BasicPlayerException;
*/
/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class FormSoundEditor extends javax.swing.JInternalFrame {
    /** выбранный звук для редактирования */
    public static Sound selectedSound = null;
    /** файл для воспроизведения */
    com.badlogic.gdx.audio.Sound sndFile = null;
    com.badlogic.gdx.audio.Music musFile = null;
    long sndId = -1;
    /** воспроизвести */
    boolean isSoundPlaying = false;
    
    class AudioPlayer extends ApplicationAdapter {            
        @Override
        public void create() {
            disposeFiles();

            // если файл маленький, то загрузим его весь в память
            // а иначе воспроизводить в потоке
            FileHandle fh = Gdx.files.local(selectedSound.getPath());
            if (fh.length() < 1024000) {
                sndFile = Gdx.audio.newSound(fh);
            } else {
                musFile = Gdx.audio.newMusic(fh);
                musFile.setVolume(1.0f);
                musFile.setLooping(true); 
            }
        }
        
        @Override
        public void pause() {
            stopSound();
        }
        
        @Override
        public void resume() {
            stopSound();
        }
        
        @Override
        public void dispose() {
            disposeFiles();
        }    
    }
    AudioPlayer audioPlayer;
    LwjglAWTCanvas gdxEngine;
        
    /**
     * Освободить память от загруженных ранее файлов
     */
    void disposeFiles() {  
        stopSound();
        
        if (sndFile != null) {
            sndFile.dispose();
            sndFile = null;
        }   
        
        if (musFile != null) {
            musFile.dispose();
            musFile = null;
        }   
    }
    
    /**
     * Играть звук
     */
    void playSound() {  
        if (sndFile != null) {
            sndId = sndFile.loop();
        } else if (musFile != null) {
            musFile.setPosition(0);
            musFile.setLooping(true);
            musFile.play();
        }     
    }
    
    /**
     * Остановить звук
     */
    void stopSound() {
        if (sndFile != null) {
            sndId = -1;
            sndFile.stop();
        } else if (musFile != null) {
            musFile.stop();
        }               
    }
            
    /**
     * Подготовка формы для отображения
     * @param firstRun - первый запуск окна
     */
    public void prepareForm(boolean firstRun) {        
        if (selectedSound == null) {
            dispose();
            return;
        } 
        
        // получить имя редактируемого файла
        txtName.setText(selectedSound.getName());
        chkMusic.setSelected(selectedSound.isMusic());
                
        if (firstRun) {
            setLocation((getParent().getWidth() >> 1) - (getWidth() >> 1), 0); 
        } else {
            setSoundPlaying(false);
            gdxEngine.stop();
        }
        
        Container container = getContentPane();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.resizable = false;
        config.vSyncEnabled = false;
        config.useGL30 = false;
        
        gdxEngine = new LwjglAWTCanvas(new AudioPlayer(), config);
        gdxEngine.getCanvas().setSize(1, 1);

        container.add(gdxEngine.getCanvas(), BorderLayout.LINE_START);

        pack();
    }

    /**
     * Creates new form FormTextureEditor
     */
    public FormSoundEditor() {
        initComponents();   
    }
    
    /**
     * Установить режим воспроизведения звука
     * @param mode true - вкл/false - выкл воспроизведение
     */
    private void setSoundPlaying(boolean mode) {
        isSoundPlaying = mode;
        tglPlaying.setSelected(mode);
        
        if (mode) {
            tglPlaying.setToolTipText("Остановить");
            playSound();            
        } else {
            tglPlaying.setToolTipText("Воспроизвести");
            stopSound();
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
        tglPlaying = new javax.swing.JToggleButton();
        chkMusic = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setTitle("Редактор звуков");
        setToolTipText("");
        setMaximumSize(new java.awt.Dimension(544, 660));
        setMinimumSize(new java.awt.Dimension(544, 660));
        setNormalBounds(new java.awt.Rectangle(0, 0, 544, 660));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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

        tglPlaying.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_play_arrow_white_24dp.png"))); // NOI18N
        tglPlaying.setToolTipText("Воспроизведение");
        tglPlaying.setFocusPainted(false);
        tglPlaying.setMaximumSize(new java.awt.Dimension(64, 64));
        tglPlaying.setMinimumSize(new java.awt.Dimension(64, 64));
        tglPlaying.setPreferredSize(new java.awt.Dimension(64, 64));
        tglPlaying.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/ic_stop_white_24dp.png"))); // NOI18N
        tglPlaying.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglPlayingActionPerformed(evt);
            }
        });

        chkMusic.setText("Фоновая музыка");
        chkMusic.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMusicStateChanged(evt);
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
                        .addGap(64, 64, 64)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkMusic)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tglPlaying, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMusic)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tglPlaying, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        try {
            setClosed(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(FormSoundEditor.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }
    }//GEN-LAST:event_btnCloseActionPerformed
        
    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        FormMain.formSoundEditor = null;               
    }//GEN-LAST:event_formInternalFrameClosed

    private void tglPlayingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglPlayingActionPerformed
        setSoundPlaying(!isSoundPlaying);
    }//GEN-LAST:event_tglPlayingActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        gdxEngine.stop();        
        gdxEngine.exit();
    }//GEN-LAST:event_formInternalFrameClosing

    private void chkMusicStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMusicStateChanged
        selectedSound.setMusic(chkMusic.isSelected());
    }//GEN-LAST:event_chkMusicStateChanged
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JCheckBox chkMusic;
    private javax.swing.JLabel lblName;
    private javax.swing.JToggleButton tglPlaying;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
