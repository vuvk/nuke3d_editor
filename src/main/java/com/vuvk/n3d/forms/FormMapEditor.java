/**
    Form of LevelEditor in Nuke3D Editor
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
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.vuvk.n3d.Const;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormMapEditor extends javax.swing.JDialog {
    
    static Map selectedMap = null;    
    LwjglAWTCanvas gdxEngine;
    PerspectiveCamera cam;
    
    /**
     * Класс плеера окна предпросмотра и редактирования карт
     */
    class MapPlayer extends ApplicationAdapter {       
        @Override
        public void create() {
        }
        
        @Override
        public void render() {
        }
        
        @Override
        public void pause() {
        }
        
        @Override
        public void resume() {
        }
        
        @Override
        public void dispose() {
        }            
    }
    
    /**
     * Класс рендера для списка фигур
     */
    class ComboBoxFiguresRenderer extends DefaultListCellRenderer {
        Map<String, ImageIcon> iconMap = new HashMap<>();
        
        public ComboBoxFiguresRenderer() {
            iconMap.put("Куб", new ImageIcon(Const.ICONS.get("Cube")));
            iconMap.put("Пирамида", new ImageIcon(Const.ICONS.get("Pyramid")));
            iconMap.put("Параллелепипед 1", new ImageIcon(Const.ICONS.get("Parallelepiped_1")));            
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            String figure = (String) value;
            
            setText(figure);
            setIcon(iconMap.get(figure));
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }   
            
            return this;
        }
    }

    
    /**
     * Creates new form FormMapEditor
     */
    public FormMapEditor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        ComboBoxFiguresRenderer renderer = new ComboBoxFiguresRenderer();
        cmbFigures.setRenderer(renderer);
        
        // все наименования фигур
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("Куб");
        model.addElement("Пирамида");
        model.addElement("Параллелепипед 1");
        cmbFigures.setModel(model);
                
        tabPane.setTitleAt(0, "");
        tabPane.setIconAt(0, new ImageIcon(Const.ICONS.get("Cube")));
        tabPane.setToolTipTextAt(0, "Создание сцены из примитивов");
        
        setLocationRelativeTo(null);
    }
    
    /**
     * Подготовка формы для отображения и запуск
     */
    public void execute(Map map) {  
        selectedMap = map;      
        /*if (selectedMap == null) {
            dispose();
            return;
        } */
        
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.resizable = true;
        config.vSyncEnabled = true;
        
        gdxEngine = new LwjglAWTCanvas(new MapPlayer(), config);
        Canvas canvas = gdxEngine.getCanvas();
        pnlView3D.add(canvas, BorderLayout.CENTER);
        canvas.setLocation(5, 16); 
        canvas.setSize(pnlView3D.getWidth() - 10, pnlView3D.getHeight() - 21);
        
        pack();
        
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPane = new javax.swing.JTabbedPane();
        pnlLevelElements = new javax.swing.JPanel();
        lblFigures = new javax.swing.JLabel();
        cmbFigures = new javax.swing.JComboBox<>();
        pnlSides = new javax.swing.JPanel();
        pnlLeft = new javax.swing.JPanel();
        pnlFront = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        pnlBack = new javax.swing.JPanel();
        pnlBottom = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();
        pnlTools = new javax.swing.JPanel();
        tglSelect = new javax.swing.JToggleButton();
        pnlView3D = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        lblFigures.setText("Фигура");

        cmbFigures.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        pnlSides.setBorder(javax.swing.BorderFactory.createTitledBorder("Материалы сторон фигуры"));

        pnlLeft.setBorder(javax.swing.BorderFactory.createTitledBorder("Left"));
        pnlLeft.setPreferredSize(new java.awt.Dimension(76, 87));

        javax.swing.GroupLayout pnlLeftLayout = new javax.swing.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        pnlFront.setBorder(javax.swing.BorderFactory.createTitledBorder("Front"));
        pnlFront.setPreferredSize(new java.awt.Dimension(76, 87));

        javax.swing.GroupLayout pnlFrontLayout = new javax.swing.GroupLayout(pnlFront);
        pnlFront.setLayout(pnlFrontLayout);
        pnlFrontLayout.setHorizontalGroup(
            pnlFrontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        pnlFrontLayout.setVerticalGroup(
            pnlFrontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder("Top"));
        pnlTop.setPreferredSize(new java.awt.Dimension(76, 87));

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        pnlBack.setBorder(javax.swing.BorderFactory.createTitledBorder("Back"));
        pnlBack.setPreferredSize(new java.awt.Dimension(76, 87));

        javax.swing.GroupLayout pnlBackLayout = new javax.swing.GroupLayout(pnlBack);
        pnlBack.setLayout(pnlBackLayout);
        pnlBackLayout.setHorizontalGroup(
            pnlBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        pnlBackLayout.setVerticalGroup(
            pnlBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        pnlBottom.setBorder(javax.swing.BorderFactory.createTitledBorder("Bottom"));
        pnlBottom.setPreferredSize(new java.awt.Dimension(76, 87));

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        pnlRight.setBorder(javax.swing.BorderFactory.createTitledBorder("Right"));
        pnlRight.setPreferredSize(new java.awt.Dimension(76, 87));

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlSidesLayout = new javax.swing.GroupLayout(pnlSides);
        pnlSides.setLayout(pnlSidesLayout);
        pnlSidesLayout.setHorizontalGroup(
            pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFront, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addComponent(pnlBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlSidesLayout.setVerticalGroup(
            pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlFront, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlLevelElementsLayout = new javax.swing.GroupLayout(pnlLevelElements);
        pnlLevelElements.setLayout(pnlLevelElementsLayout);
        pnlLevelElementsLayout.setHorizontalGroup(
            pnlLevelElementsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLevelElementsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLevelElementsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbFigures, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlLevelElementsLayout.createSequentialGroup()
                        .addComponent(lblFigures)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pnlSides, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlLevelElementsLayout.setVerticalGroup(
            pnlLevelElementsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLevelElementsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFigures)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbFigures, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSides, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(226, Short.MAX_VALUE))
        );

        tabPane.addTab("tab1", pnlLevelElements);

        tglSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/cursor.png"))); // NOI18N
        tglSelect.setToolTipText("Выделить");
        tglSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tglSelect.setMaximumSize(new java.awt.Dimension(64, 64));
        tglSelect.setMinimumSize(new java.awt.Dimension(64, 64));
        tglSelect.setPreferredSize(new java.awt.Dimension(64, 64));
        tglSelect.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/vuvk/n3d/ico/cursor_filled.png"))); // NOI18N
        tglSelect.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tglSelectStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlToolsLayout = new javax.swing.GroupLayout(pnlTools);
        pnlTools.setLayout(pnlToolsLayout);
        pnlToolsLayout.setHorizontalGroup(
            pnlToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToolsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tglSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlToolsLayout.setVerticalGroup(
            pnlToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToolsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tglSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlView3D.setBorder(javax.swing.BorderFactory.createTitledBorder("Уровень"));

        javax.swing.GroupLayout pnlView3DLayout = new javax.swing.GroupLayout(pnlView3D);
        pnlView3D.setLayout(pnlView3DLayout);
        pnlView3DLayout.setHorizontalGroup(
            pnlView3DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        pnlView3DLayout.setVerticalGroup(
            pnlView3DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlView3D, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTools, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlTools, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlView3D, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tglSelectStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tglSelectStateChanged
    }//GEN-LAST:event_tglSelectStateChanged

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        
    }//GEN-LAST:event_formWindowStateChanged

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        gdxEngine.getCanvas().setSize(pnlView3D.getWidth() - 10, pnlView3D.getHeight() - 21);
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbFigures;
    private javax.swing.JLabel lblFigures;
    private javax.swing.JPanel pnlBack;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlFront;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlLevelElements;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JPanel pnlSides;
    private javax.swing.JPanel pnlTools;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlView3D;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JToggleButton tglSelect;
    // End of variables declaration//GEN-END:variables
}
