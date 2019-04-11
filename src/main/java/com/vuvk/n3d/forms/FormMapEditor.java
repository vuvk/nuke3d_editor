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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.vuvk.n3d.Const;
import com.vuvk.n3d.components.PanelImagePreview;
import com.vuvk.n3d.resources.GameMap;
import com.vuvk.n3d.resources.MapCube;
import com.vuvk.n3d.resources.MapElement;
import com.vuvk.n3d.resources.MapFigure;
import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Side;
import com.vuvk.n3d.resources.Texture;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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
    
    static GameMap selectedMap = null; 
    
    MapFigure selectedFigure = null;
    
    /** стороны фигуры */
    static FigurePreview[] sidePreviews = new FigurePreview[6];
    
    LwjglAWTCanvas gdxEngine;
    PerspectiveCamera cam;
    ImmediateModeRenderer20 lineRenderer;
//  ImmediateModeRenderer20 figureRenderer;
    
    Vector3 camPosPoint  = new Vector3(5, 8, -2.5f);
    Vector3 camViewPoint = new Vector3(5, 0, -4.5f);
    Vector3 worldPos;
    
    /** уровень рисования по высоте */
    float levelDraw = 0;
    
    /** словарь соответствия Материал Nuke3D - Текстура libGDX */
    public static Map<Material, com.badlogic.gdx.graphics.Texture> GDX_TEXTURES = new HashMap<>();
    
    /**
     * Класс для обработки ввода в GDX
     */
    class InputCore implements InputProcessor {   
        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {            
            Ray ray = cam.getPickRay(screenX,screenY);

            Plane plane = new Plane();
            plane.set(0, 1, 0, -levelDraw);// the xy plane with direction z facing screen

            Vector3 pos = new Vector3();
            Intersector.intersectRayPlane(ray, plane, pos);
            
            if (pos.x >= 0 && pos.x <=  10 &&
                pos.z <= 0 && pos.z >= -10
               ) {
                worldPos = new Vector3((int)pos.x, levelDraw, (int)pos.z);
                return true;
            } else {
                worldPos = null;
                return false;
            }
        }

        @Override
        public boolean scrolled(int amount) {
            // движение камеры и уровня рисования вверх/вниз с помощью колесика   
            
            if (amount == 0) {
                return false;
            }
            
            levelDraw += amount;
            if (levelDraw < 0) {
                levelDraw = 0;
            } else if (levelDraw > GameMap.MAX_Y) {
                levelDraw = GameMap.MAX_Y;
            }
            
            if (worldPos != null) {
                worldPos.y = levelDraw;
            }
                
            return true;            
        }
    }
    
    /**
     * Класс плеера окна предпросмотра и редактирования карт
     */
    class MapPlayer extends ApplicationAdapter {    
        public void drawLine(float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float r, float g, float b, float a) {
            lineRenderer.color(r, g, b, a);
            lineRenderer.vertex(x1, y1, z1);
            lineRenderer.color(r, g, b, a);
            lineRenderer.vertex(x2, y2, z2);
        }
        public void drawLine(float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             Color color) {
            drawLine(x1, y1, z1,
                     x2, y2, z2,
                     color.r, color.g, color.b, color.a);
        }
        public void drawLine(Vector3 a, Vector3 b, Color color) {
            drawLine(a.x, a.y, a.z,
                     b.x, b.y, b.z,
                     color.r, color.g, color.b, color.a);
        }
        
        
        public void drawGrid(float x, float y, float z, 
                             int horCount, int verCount, 
                             float stepWidth, float stepHeight,
                             Color color) {
            for (int i = 0; i <= horCount; ++i) {
                // draw vertical
                drawLine(x + i * stepWidth, y, z,
                         x + i * stepWidth, y, z - verCount * stepHeight,
                         color);
            }

            for (int i = 0; i <= verCount; ++i) {
                // draw horizontal
                drawLine(x,         y, z - i * stepHeight,
                         x + verCount * stepWidth, y, z - i * stepHeight,
                         color);
            }
        }
        public void drawGrid(float x, float y, float z, 
                             int horCount, int verCount,  
                             Color color) {
            drawGrid(x, y, z, horCount, verCount, 1f, 1f, color);
        }
        public void drawGrid(int horCount, int verCount, Color color) {
            drawGrid(0, 0, 0, horCount, verCount, 1f, 1f, color);
        }
        
        public void drawBox(Vector3 pos, Color color) {
            drawLine(pos.x,     pos.y,     pos.z,
                     pos.x,     pos.y + 1, pos.z,
                     color);
            drawLine(pos.x,     pos.y,     pos.z,
                     pos.x + 1, pos.y,     pos.z,
                     color);
            drawLine(pos.x,     pos.y,     pos.z,
                     pos.x,     pos.y,     pos.z - 1,
                     color);             
            drawLine(pos.x,     pos.y,     pos.z - 1,
                     pos.x,     pos.y + 1, pos.z - 1,
                     color);
            drawLine(pos.x,     pos.y,     pos.z - 1,
                     pos.x + 1, pos.y,     pos.z - 1,
                     color);              
            drawLine(pos.x + 1, pos.y,     pos.z,
                     pos.x + 1, pos.y + 1, pos.z,
                     color);       
            drawLine(pos.x + 1, pos.y,     pos.z - 1,
                     pos.x + 1, pos.y + 1, pos.z - 1,
                     color);   
            drawLine(pos.x + 1, pos.y,     pos.z,
                     pos.x + 1, pos.y,     pos.z - 1,
                     color);        
            drawLine(pos.x,     pos.y + 1, pos.z,
                     pos.x,     pos.y + 1, pos.z - 1,
                     color);      
            drawLine(pos.x + 1, pos.y + 1, pos.z,
                     pos.x + 1, pos.y + 1, pos.z - 1,
                     color);     
            drawLine(pos.x,     pos.y + 1, pos.z,
                     pos.x + 1, pos.y + 1, pos.z,
                     color);      
            drawLine(pos.x,     pos.y + 1, pos.z - 1,
                     pos.x + 1, pos.y + 1, pos.z - 1,
                     color);        
        }

        @Override
        public void create() {            
            Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            
            lineRenderer = new ImmediateModeRenderer20(false, true, 0);
//          figureRenderer = new ImmediateModeRenderer20(false, false, 4);
            
            Gdx.input.setInputProcessor(new InputCore());
            
            // грузим все известные текстуры
            GDX_TEXTURES.clear();
            // пробегаемся по всем материалам и если у материала есть на первом 
            // кадре известная текстура, то грузим её
            for (Material mat : Material.MATERIALS) {
                Material.Frame frm = mat.getFrame(0);
                if (frm != null) {
                    Texture txr = frm.getTexture();
                    if (txr != null) {
                        GDX_TEXTURES.put(mat, new com.badlogic.gdx.graphics.Texture(txr.getPath()));
                    }
                }
            }
            
            cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            cam.position.set(camPosPoint);
            cam.lookAt(camViewPoint);
            cam.up.set(Vector3.Y);
            cam.near = 0.0001f;
            cam.far  = 100;
            cam.update();            
        
            selectedFigure = new MapCube();
        }
        
        void mouseClicked() {
            selectedMap.setElement((int)worldPos.x, 
                                   (int)worldPos.y, 
                                   (int)-worldPos.z, 
                                   new MapCube(selectedFigure));
        }
        
        @Override
        public void render() {         
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);         
                                             
            // сетка основания
            if (levelDraw > 0) { 
                Gdx.gl.glLineWidth(1);
                lineRenderer.begin(cam.combined, GL20.GL_LINES);  
                drawGrid(10, 10, Color.DARK_GRAY);
                lineRenderer.end();
            }
            
            // рисуем элементы
            for (int x = 0; x < GameMap.MAX_X; ++x) {
                for (int y = 0; y < GameMap.MAX_Y; ++y) {
                    for (int z = 0; z < GameMap.MAX_Z; ++z) {
                        MapElement element = selectedMap.getElement(x, y, z);
                        if (element != null) {
                            element.render(cam.combined);
                        }
                    }    
                }    
            }
            
            // сетка позиции
            Gdx.gl.glLineWidth(3);
            lineRenderer.begin(cam.combined, GL20.GL_LINES);   
            drawGrid(0, levelDraw, 0, 10, 10, Color.LIGHT_GRAY);
            /*drawLine(Vector3.Zero, Vector3.X, Color.RED);
            drawLine(Vector3.Zero, Vector3.Y, Color.YELLOW);
            drawLine(Vector3.Zero, new Vector3(0, 0, -1), Color.BLUE);*/
            lineRenderer.end();
            
            // позиция курсора
            if (worldPos != null) {
                Gdx.gl.glLineWidth(3);
                lineRenderer.begin(cam.combined, GL20.GL_LINES);   
                drawBox(worldPos, Color.RED);
                lineRenderer.end();
                
                // рисуем?
                if (Gdx.input.justTouched()) {
                    mouseClicked();
                }
            }            
                        
            cam.position.set(camPosPoint.x, camPosPoint.y + levelDraw, camPosPoint.z);
            cam.viewportWidth  = Gdx.graphics.getWidth();
            cam.viewportHeight = Gdx.graphics.getHeight();   
            cam.update();
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
     * Класс для отображения сторон фигуры и управления ими
     */
    class FigurePreview extends PanelImagePreview implements MouseListener {
        /** сторона фигуры, за которую отвечает этот элемент */
        Side side;

        public FigurePreview(Container window, Side side) {
            super(window);
            setStretched(true);
            setImage(null);
            this.side = side;
            
            addMouseListener(this);
        }
        
        /**
         * Получить сторону, которая рисуется в превью
         * @return значение енумератора Side
         */
        public Side getSide() {
            return side;
        }        
        
        @Override
        public void setImage(BufferedImage image) {
            if (image != null) {
                super.setImage(image);
                setDrawBorder(true);
            } else {
                super.setImage(Const.ICONS.get("Add"));
                setDrawBorder(false);
            }
        }
        
        /**
          * вызвать окно выбора материала и назначить на сторону
          */
        void chooseMaterial() {
            FormMaterialSelector form = new FormMaterialSelector(FormMain.formMain);
            Material mat = form.execute(true);

            if (mat != null) {
                selectedFigure.setMaterial(mat, side);                
                
                sidePreviews[side.getNum()].setImage(null);
                setImage(null);
                Material.Frame frm = mat.getFrame(0);
                
                if (frm != null) {
                    Texture txr = frm.getTexture();
                    if (txr != null) {
                        setImage(txr.getImage());
                    }
                    
                    sidePreviews[side.getNum()].setImage(txr.getImage());
                }
                    
                sidePreviews[side.getNum()].redraw();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                chooseMaterial();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                selectedFigure.setMaterial(null, side);   
                
                sidePreviews[side.getNum()].setImage(null);
                sidePreviews[side.getNum()].redraw();  
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    
    /**
     * Creates new form FormMapEditor
     */
    public FormMapEditor(java.awt.Frame parent) {
        super(parent, true);
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
                        
        for (int i = 0; i < 6; ++i) {
            sidePreviews[i] = new FigurePreview(this, Side.getByNum(i));
        }
        
        pnlFront .add(sidePreviews[Side.FRONT .getNum()]);        
        pnlBack  .add(sidePreviews[Side.BACK  .getNum()]);        
        pnlLeft  .add(sidePreviews[Side.LEFT  .getNum()]);        
        pnlRight .add(sidePreviews[Side.RIGHT .getNum()]);        
        pnlTop   .add(sidePreviews[Side.TOP   .getNum()]);        
        pnlBottom.add(sidePreviews[Side.BOTTOM.getNum()]);
        
        for (int i = 0; i < 6; ++i) {
            sidePreviews[i].setSize(64, 64);
            sidePreviews[i].setLocation(5, 16);
        }
        
        setLocationRelativeTo(null);
    }
    
    /**
     * Подготовка формы для отображения и запуск
     */
    public void execute(GameMap map) {  
        selectedMap = map;      
        if (selectedMap == null) {
            dispose();
            return;
        } 
        
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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
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

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        GDX_TEXTURES.clear();
    }//GEN-LAST:event_formWindowClosed

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
