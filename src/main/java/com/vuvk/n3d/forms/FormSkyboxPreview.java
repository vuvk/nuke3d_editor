/**
    Form for preview skybox in Nuke3D Editor
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.vuvk.n3d.resources.Skybox;
import com.vuvk.n3d.resources.Texture;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormSkyboxPreview extends javax.swing.JDialog {
    /** выбранный скайбокс для редактирования */
    public static Skybox selectedSkybox = null;
    /** текстуры куба в формате libGDX */
    com.badlogic.gdx.graphics.Texture skyTextures[] = new com.badlogic.gdx.graphics.Texture[6];
    Model skyModel;
    ModelInstance skyInstance;
    ModelBatch modelBatch;
    PerspectiveCamera cam;
    
    Vector2 lastMousePos = new Vector2(), 
            curMousePos  = new Vector2();
    Vector2 camLook = new Vector2();
    
    class SkyboxPlayer extends ApplicationAdapter {            
        @Override
        public void create() {
            disposeFiles();
            modelBatch = new ModelBatch();
                      
            // грузим текстуры формата libGDX
            for (int i = 0; i < skyTextures.length; ++i) {                
                Texture txr = selectedSkybox.getTexture(Skybox.Side.getByNum(i));
                if (txr != null) {  
                    skyTextures[i] = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal(txr.getPath()));
                }
            }
            
            // создаем модель куба из 6 мешей
            
            ModelBuilder modelBuilder = new ModelBuilder();
            MeshPartBuilder meshBuilder;
            VertexInfo v1, v2, v3, v4;  
            
            modelBuilder.begin();
            
            // FRONT
            if (skyTextures[Skybox.Side.FRONT.getNum()] != null) {
                v1 = new VertexInfo().setPos(-1, -1, -1).setUV(0.0f, 1.0f);
                v2 = new VertexInfo().setPos( 1, -1, -1).setUV(1.0f, 1.0f);
                v3 = new VertexInfo().setPos( 1,  1, -1).setUV(1.0f, 0.0f);
                v4 = new VertexInfo().setPos(-1,  1, -1).setUV(0.0f, 0.0f);

                meshBuilder = modelBuilder.part("front", 
                                                GL20.GL_TRIANGLES, 
                                                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, 
                                                new Material("front", TextureAttribute
                                                             .createDiffuse(skyTextures[Skybox.Side.FRONT.getNum()])));
                meshBuilder.rect(v1, v2, v3, v4);
            }
            
            // BACK
            if (skyTextures[Skybox.Side.BACK.getNum()] != null) {
                v1 = new VertexInfo().setPos( 1, -1,  1).setUV(0.0f, 1.0f);
                v2 = new VertexInfo().setPos(-1, -1,  1).setUV(1.0f, 1.0f);
                v3 = new VertexInfo().setPos(-1,  1,  1).setUV(1.0f, 0.0f);
                v4 = new VertexInfo().setPos( 1,  1,  1).setUV(0.0f, 0.0f);

                meshBuilder = modelBuilder.part("back", 
                                                GL20.GL_TRIANGLES, 
                                                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, 
                                                new Material("back", TextureAttribute
                                                             .createDiffuse(skyTextures[Skybox.Side.BACK.getNum()])));
                meshBuilder.rect(v1, v2, v3, v4);
            }
            
            // LEFT
            if (skyTextures[Skybox.Side.LEFT.getNum()] != null) {
                v1 = new VertexInfo().setPos(-1, -1,  1).setUV(0.0f, 1.0f);
                v2 = new VertexInfo().setPos(-1, -1, -1).setUV(1.0f, 1.0f);
                v3 = new VertexInfo().setPos(-1,  1, -1).setUV(1.0f, 0.0f);
                v4 = new VertexInfo().setPos(-1,  1,  1).setUV(0.0f, 0.0f);

                meshBuilder = modelBuilder.part("left", 
                                                GL20.GL_TRIANGLES, 
                                                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, 
                                                new Material("left", TextureAttribute
                                                             .createDiffuse(skyTextures[Skybox.Side.LEFT.getNum()])));
                meshBuilder.rect(v1, v2, v3, v4);
            }
            
            // RIGHT
            if (skyTextures[Skybox.Side.RIGHT.getNum()] != null) {
                v1 = new VertexInfo().setPos( 1, -1, -1).setUV(0.0f, 1.0f);
                v2 = new VertexInfo().setPos( 1, -1,  1).setUV(1.0f, 1.0f);
                v3 = new VertexInfo().setPos( 1,  1,  1).setUV(1.0f, 0.0f);
                v4 = new VertexInfo().setPos( 1,  1, -1).setUV(0.0f, 0.0f);

                meshBuilder = modelBuilder.part("right", 
                                                GL20.GL_TRIANGLES, 
                                                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, 
                                                new Material("right", TextureAttribute
                                                             .createDiffuse(skyTextures[Skybox.Side.RIGHT.getNum()])));
                meshBuilder.rect(v1, v2, v3, v4);
            }
            
            // TOP
            if (skyTextures[Skybox.Side.TOP.getNum()] != null) {
                v1 = new VertexInfo().setPos(-1,  1, -1).setUV(1.0f, 1.0f);
                v2 = new VertexInfo().setPos( 1,  1, -1).setUV(1.0f, 0.0f);
                v3 = new VertexInfo().setPos( 1,  1,  1).setUV(0.0f, 0.0f);
                v4 = new VertexInfo().setPos(-1,  1,  1).setUV(0.0f, 1.0f);
                
                meshBuilder = modelBuilder.part("top", 
                                                GL20.GL_TRIANGLES, 
                                                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, 
                                                new Material("top", TextureAttribute
                                                             .createDiffuse(skyTextures[Skybox.Side.TOP.getNum()])));
                meshBuilder.rect(v1, v2, v3, v4);
            }
            
            // BOTTOM
            if (skyTextures[Skybox.Side.BOTTOM.getNum()] != null) {
                v1 = new VertexInfo().setPos( 1, -1,  1).setUV(0.0f, 1.0f);
                v2 = new VertexInfo().setPos( 1, -1, -1).setUV(1.0f, 1.0f);
                v3 = new VertexInfo().setPos(-1, -1, -1).setUV(1.0f, 0.0f);
                v4 = new VertexInfo().setPos(-1, -1,  1).setUV(0.0f, 0.0f);
                
                meshBuilder = modelBuilder.part("bottom", 
                                                GL20.GL_TRIANGLES, 
                                                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, 
                                                new Material("bottom", TextureAttribute
                                                             .createDiffuse(skyTextures[Skybox.Side.BOTTOM.getNum()])));
                meshBuilder.rect(v1, v2, v3, v4);
            }
            
            skyModel = modelBuilder.end();
            skyInstance = new ModelInstance(skyModel);
            
            Gdx.gl.glClearColor(0.9f, 0.7f, 0.25f, 1.0f);
            
            cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            cam.position.setZero();
            cam.lookAt(0, 0, -1);
            cam.near = 0.0001f;
            cam.far  = 3;
            cam.update();
        }
        
        @Override
        public void render() {              
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            
            if (Gdx.input.justTouched()) {
                lastMousePos.set(Gdx.input.getX(), Gdx.input.getY());
            }
            
            if (Gdx.input.isTouched()) {
                curMousePos.set(Gdx.input.getX(), Gdx.input.getY()); 
                
                float dX = lastMousePos.x - curMousePos.x;
                float dY = lastMousePos.y - curMousePos.y;
                
                camLook.x += dX;
                camLook.y += dY;
                
                if (camLook.x <    0) { camLook.x += 360; }
                if (camLook.x >= 360) { camLook.x -= 360; }

                if (camLook.y < -89) { camLook.y = -89; }
                if (camLook.y >  89) { camLook.y =  89; }
                
                cam.lookAt((float) -Math.sin(Math.toRadians(camLook.x)), 
                           (float)  Math.tan(Math.toRadians(camLook.y)), 
                           (float) -Math.cos(Math.toRadians(camLook.x)));   
                cam.up.set(0, 1, 0);
                
                lastMousePos.set(curMousePos);
            }
            
            cam.update();
            
            modelBatch.begin(cam);
            modelBatch.render(skyInstance);
            modelBatch.end();
        }
        
        @Override
        public void pause() {
        }
        
        @Override
        public void resume() {
        }
        
        @Override
        public void dispose() {
            disposeFiles();
            modelBatch.dispose();
        }    
    }
    
    SkyboxPlayer skyPlayer;
    LwjglAWTCanvas gdxEngine;
    
    /**
     * Освободить память от загруженных ранее файлов
     */
    void disposeFiles() {  
        for (int i = 0; i < skyTextures.length; ++i) {            
            /*if (skyModel != null) {
                skyModel.dispose();
            }*/
            
            if (skyTextures[i] != null) {
                skyTextures[i].dispose();
                skyTextures[i] = null;
            }
        }
    }
    
    /**
     * Creates new form FormSkyboxPreview
     */
    public FormSkyboxPreview(Frame owner, boolean modal) {
        super(owner, modal);
        
        initComponents();
        
        setLocationRelativeTo(null);        
    } 
    
    /**
     * Подготовка формы для отображения и запуск
     */
    public void execute(Skybox sky) {  
        selectedSkybox = sky;      
        if (selectedSkybox == null) {
            dispose();
            return;
        } 
        
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.resizable = false;
        config.vSyncEnabled = false;
        config.useGL30 = false;
        
        gdxEngine = new LwjglAWTCanvas(new SkyboxPlayer(), config);
        gdxEngine.getCanvas().setSize(640, 480);

        pnlView3D.add(gdxEngine.getCanvas(), BorderLayout.LINE_START);
        gdxEngine.getCanvas().setLocation(5, 16); 

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

        btnClose = new javax.swing.JButton();
        pnlView3D = new javax.swing.JPanel();
        pnlLegend = new javax.swing.JPanel();
        pnlFront = new javax.swing.JPanel();
        pnlBack = new javax.swing.JPanel();
        pnlLeft = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        chkShowSides = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        pnlHint = new javax.swing.JPanel();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Предпросмотр скайбокса");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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

        pnlView3D.setBorder(javax.swing.BorderFactory.createTitledBorder("3D вид"));

        javax.swing.GroupLayout pnlView3DLayout = new javax.swing.GroupLayout(pnlView3D);
        pnlView3D.setLayout(pnlView3DLayout);
        pnlView3DLayout.setHorizontalGroup(
            pnlView3DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        pnlView3DLayout.setVerticalGroup(
            pnlView3DLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        pnlLegend.setBorder(javax.swing.BorderFactory.createTitledBorder("Легенда"));

        pnlFront.setBackground(new java.awt.Color(255, 0, 0));
        pnlFront.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlFrontLayout = new javax.swing.GroupLayout(pnlFront);
        pnlFront.setLayout(pnlFrontLayout);
        pnlFrontLayout.setHorizontalGroup(
            pnlFrontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlFrontLayout.setVerticalGroup(
            pnlFrontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        pnlBack.setBackground(new java.awt.Color(0, 255, 0));
        pnlBack.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlBackLayout = new javax.swing.GroupLayout(pnlBack);
        pnlBack.setLayout(pnlBackLayout);
        pnlBackLayout.setHorizontalGroup(
            pnlBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlBackLayout.setVerticalGroup(
            pnlBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        pnlLeft.setBackground(new java.awt.Color(0, 0, 255));
        pnlLeft.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlLeftLayout = new javax.swing.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        pnlRight.setBackground(new java.awt.Color(255, 0, 255));
        pnlRight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        pnlTop.setBackground(new java.awt.Color(255, 255, 0));
        pnlTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(0, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        chkShowSides.setText("Отобразить");
        chkShowSides.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkShowSidesStateChanged(evt);
            }
        });

        jLabel1.setText("Front");

        jLabel2.setText("Back");

        jLabel3.setText("Left");

        jLabel4.setText("Right");

        jLabel5.setText("Top");

        jLabel6.setText("Bottom");

        javax.swing.GroupLayout pnlLegendLayout = new javax.swing.GroupLayout(pnlLegend);
        pnlLegend.setLayout(pnlLegendLayout);
        pnlLegendLayout.setHorizontalGroup(
            pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLegendLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkShowSides)
                    .addGroup(pnlLegendLayout.createSequentialGroup()
                        .addComponent(pnlFront, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(pnlLegendLayout.createSequentialGroup()
                        .addComponent(pnlBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addGroup(pnlLegendLayout.createSequentialGroup()
                        .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(pnlLegendLayout.createSequentialGroup()
                        .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(pnlLegendLayout.createSequentialGroup()
                        .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addGroup(pnlLegendLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlLegendLayout.setVerticalGroup(
            pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLegendLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFront, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLegendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkShowSides)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlHint.setBorder(javax.swing.BorderFactory.createTitledBorder("Подсказка"));

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Нажать и удерживать кнопку мыши для вращения камеры");
        jTextArea1.setWrapStyleWord(true);

        javax.swing.GroupLayout pnlHintLayout = new javax.swing.GroupLayout(pnlHint);
        pnlHint.setLayout(pnlHintLayout);
        pnlHintLayout.setHorizontalGroup(
            pnlHintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHintLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHintLayout.setVerticalGroup(
            pnlHintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHintLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlView3D, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlHint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlLegend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlHint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlView3D, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_btnCloseActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        gdxEngine.stop();        
        gdxEngine.exit();
    }//GEN-LAST:event_formWindowClosing

    private void chkShowSidesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkShowSidesStateChanged
        if (skyInstance != null) {
            Material front  = skyInstance.getMaterial("front" );                                 
            Material back   = skyInstance.getMaterial("back"  );                            
            Material left   = skyInstance.getMaterial("left"  );                            
            Material right  = skyInstance.getMaterial("right" );                         
            Material top    = skyInstance.getMaterial("top"   );                            
            Material bottom = skyInstance.getMaterial("bottom");

            if (chkShowSides.isSelected()) {
                if (front  != null) { front .set(ColorAttribute.createDiffuse(Color.RED    )); }
                if (back   != null) { back  .set(ColorAttribute.createDiffuse(Color.GREEN  )); }
                if (left   != null) { left  .set(ColorAttribute.createDiffuse(Color.BLUE   )); }
                if (right  != null) { right .set(ColorAttribute.createDiffuse(Color.MAGENTA)); }
                if (top    != null) { top   .set(ColorAttribute.createDiffuse(Color.YELLOW )); }
                if (bottom != null) { bottom.set(ColorAttribute.createDiffuse(Color.CYAN   )); }
            } else {
                if (front  != null) { front .remove(ColorAttribute.Diffuse); }
                if (back   != null) { back  .remove(ColorAttribute.Diffuse); }
                if (left   != null) { left  .remove(ColorAttribute.Diffuse); }
                if (right  != null) { right .remove(ColorAttribute.Diffuse); }
                if (top    != null) { top   .remove(ColorAttribute.Diffuse); }
                if (bottom != null) { bottom.remove(ColorAttribute.Diffuse); }
            }
        }
    }//GEN-LAST:event_chkShowSidesStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JCheckBox chkShowSides;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel pnlBack;
    private javax.swing.JPanel pnlFront;
    private javax.swing.JPanel pnlHint;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlLegend;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlView3D;
    // End of variables declaration//GEN-END:variables
}
