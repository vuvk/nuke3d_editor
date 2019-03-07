/**
    Main form of Nuke3D Editor
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

import com.bulenkov.darcula.DarculaLaf;
import com.vuvk.n3d.Const;
import com.vuvk.n3d.Global;
import com.vuvk.n3d.components.PreviewElement;
import com.vuvk.n3d.resources.Material;
import com.vuvk.n3d.resources.Sound;
import com.vuvk.n3d.resources.Texture;
import com.vuvk.n3d.utils.FileSystemUtils;
import com.vuvk.n3d.utils.ImageUtils;
import com.vuvk.n3d.utils.MessageDialog;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public class FormMain extends javax.swing.JFrame {
        
    /** ссылка на главную форму */
    public static FormMain formMain;
    /** ссылка на форму редактора текстуры */
    public static FormTextureEditor formTextureEditor = null;
    /** ссылка на форму редактора материала */
    public static FormMaterialEditor formMaterialEditor = null;
    /** ссылка на форму редактора звуков */
    public static FormSoundEditor formSoundEditor = null;
        
    /** проект открыт? */
    public static boolean isProjectOpened = false;
        
    
    /** текущий выбранный путь, в котором находится пользователь */
    static Path currentPath = null;
    //static Path resourcesPath = null;
    /** пути для копирования */
    static List<Path> copyPaths = null;
    /** Режим выезания */
    static boolean isCutMode = false;
    
    
    /**
     * кастомный рендерер для ячеек списка
     */
    class ProjectViewCellRenderer extends JLabel implements ListCellRenderer {
        public ProjectViewCellRenderer() {        
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setHorizontalTextPosition(CENTER);
            setVerticalTextPosition(BOTTOM);
            
            Font font = getFont().deriveFont(Font.BOLD);
            setFont(font);
        }
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {        
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }         
            
            PreviewElement element = (PreviewElement) value;
            BufferedImage img = element.getIcon();
            ImageIcon icon = new ImageIcon();
            int iconWidth  = 64;
            int iconHeight = 64;
            
            if (img != null) {
                double imageWidth  = img.getWidth();
                double imageHeight = img.getHeight();

                if (imageWidth != imageHeight) {
                    if (imageWidth > imageHeight) {
                        iconHeight = (int)((double)iconHeight * (imageHeight / imageWidth ));
                    } else {
                        iconWidth  = (int)((double)iconWidth  * (imageWidth  / imageHeight));
                    }
                }

                icon.setImage(ImageUtils.resizeImage(img, iconWidth, iconHeight));
            } else {
                icon.setImage(new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB));
            }
            
            setIcon(icon);            
            setText(element.getName());   

            return this;
        }        
    }
    
    /**
     * Открыть проект
     */
    void projectOpen() {
        if (isProjectOpened) {
            return;
        }
        
        closeChildWindows();
        Texture.loadAll();
        Material.loadAll();
        Sound.loadAll();

        MenuItemOpenProject.setEnabled (false);
        MenuItemSaveProject.setEnabled (true );
        MenuItemCloseProject.setEnabled(true );
        
        isProjectOpened = true; 
        fillTreeFolders(true);
        listProjectView.setEnabled(true);
        treeFolders.setEnabled(true);
        listProjectView.setComponentPopupMenu(popupPV);
        
        MessageDialog.showInformation("Проект открыт.");  
    }
    
    /**
     * Сохранить проект
     */
    void projectSave() {
        if (!isProjectOpened) {
            return;
        }
        
        if (!Texture.saveConfig()) {
            MessageDialog.showError("Не удалось сохранить текстуры проекта! Повторите попытку.");
        }
        
        if (!Material.saveAll() || !Material.saveConfig()) {
            MessageDialog.showError("Не удалось сохранить материалы проекта! Повторите попытку.");
        }
        
        if (!Sound.saveConfig()) {
            MessageDialog.showError("Не удалось сохранить звуки проекта! Повторите попытку.");
        }
        
        MessageDialog.showInformation("Процедура сохранения проекта завершена.");   
    }
    
    /**
     * Закрыть проект
     * @return true если операция закрытия завершена, false если закрывать нечего или выбрана кнопка CANCEL
     */
    boolean projectClose() {
        if (!isProjectOpened) {
            return true;
        }  
        
        Boolean answer = MessageDialog.showConfirmationYesNoCancel("Сохранить проект перед его закрытием? Все несохраненные данные будут утеряны.");
        // CANCEL
        if (answer == null) {
            return false;
        // YES
        } else if (answer.booleanValue()) {
            projectSave();
        }
    
        closeChildWindows();
        Texture.closeAll();
        Material.closeAll();
        Sound.closeAll();

        MenuItemOpenProject.setEnabled (true );
        MenuItemSaveProject.setEnabled (false);
        MenuItemCloseProject.setEnabled(false);

        isProjectOpened = false;
        clearTreeFolders();
        clearListProjectView();
        listProjectView.setEnabled(false);
        treeFolders.setEnabled(false);
        listProjectView.setComponentPopupMenu(null);
        currentPath = null;    
        copyPaths = null;
        isCutMode = false;

        MessageDialog.showInformation("Проект закрыт");
        return true;
    }

    /**
     * Открыть форму редактирования текстуры
     */
    void openFormTextureEditor() {
        List list = listProjectView.getSelectedValuesList();
        if (list.size() > 0) {
            PreviewElement element = (PreviewElement)list.get(0);
            if (element.getType() == PreviewElement.Type.TEXTURE) {
                
                Texture txr = Texture.getByPath(element.getPath());
                if (txr != null) {
                    boolean firstRun = false;
                    if (formTextureEditor == null) {
                        formTextureEditor = new FormTextureEditor();
                        Desktop.add(formTextureEditor);  
                        firstRun = true;
                    }

                    formTextureEditor.selectedTexture = txr;
                    // свернуто?
                    if (formTextureEditor.isIcon()) {
                        try {
                            formTextureEditor.setIcon(false);
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                            MessageDialog.showException(ex);
                        }
                    // невидимое?
                    } else {
                        formTextureEditor.setVisible(true); 
                        formTextureEditor.prepareForm(firstRun);
                    }
                    Desktop.moveToFront(formTextureEditor);
                } else {
                    // если не нашёл, а файл есть, значит проект битый 
                    // или файл был "подброшен"
                    MessageDialog.showError("Не удалось найти файл \"" + element.getFileName() + "\" в настройках проекта.\n" +
                                            "Возможно, нарушились связи проекта или файл был подброшен. Импортируйте его заново." );
                }
            }
        }
    }
    
    /**
     * Открыть форму редактирования материала
     */
    void openFormMaterialEditor() {
        List list = listProjectView.getSelectedValuesList();
        if (list.size() > 0) {
            PreviewElement element = (PreviewElement)list.get(0);
            if (element.getType() == PreviewElement.Type.MATERIAL) {
                
                Material mat = Material.getByPath(element.getPath());
                if (mat != null) {
                    boolean firstRun = false;
                    if (formMaterialEditor == null) {
                        formMaterialEditor = new FormMaterialEditor();
                        Desktop.add(formMaterialEditor);
                        firstRun = true;
                    }

                    formMaterialEditor.selectedMaterial = mat;
                    // свернуто?
                    if (formMaterialEditor.isIcon()) {
                        try {
                            formMaterialEditor.setIcon(false);
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                            MessageDialog.showException(ex);
                        }
                    // невидимое?
                    } else {
                        formMaterialEditor.setVisible(true); 
                        formMaterialEditor.prepareForm(firstRun);
                    }
                    Desktop.moveToFront(formMaterialEditor);
                } else {
                    // если не нашёл, а файл есть, значит проект битый 
                    // или файл был "подброшен"
                    MessageDialog.showError("Не удалось найти файл \"" + element.getFileName() + "\" в настройках проекта.\n" +
                                            "Возможно, нарушились связи проекта или файл был подброшен. Импортируйте его заново." );
                }
            }
        }        
    }
    
    /**
     * Открыть форму редактирования звука
     */
    void openFormSoundEditor() {
        List list = listProjectView.getSelectedValuesList();
        if (list.size() > 0) {
            PreviewElement element = (PreviewElement)list.get(0);
            if (element.getType() == PreviewElement.Type.SOUND) {
                
                Sound snd = Sound.getByPath(element.getPath());
                if (snd != null) {
                    boolean firstRun = false;
                    if (formSoundEditor == null) {
                        formSoundEditor = new FormSoundEditor();
                        Desktop.add(formSoundEditor);
                        firstRun = true;
                    }

                    formSoundEditor.selectedSound = snd;
                    // свернуто?
                    if (formSoundEditor.isIcon()) {
                        try {
                            formSoundEditor.setIcon(false);
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                            MessageDialog.showException(ex);
                        }
                    // невидимое?
                    } else {
                        formSoundEditor.setVisible(true); 
                        formSoundEditor.prepareForm(firstRun);
                    }
                    Desktop.moveToFront(formSoundEditor);
                } else {
                    // если не нашёл, а файл есть, значит проект битый 
                    // или файл был "подброшен"
                    MessageDialog.showError("Не удалось найти файл \"" + element.getFileName() + "\" в настройках проекта.\n" +
                                            "Возможно, нарушились связи проекта или файл был подброшен. Импортируйте его заново." );
                }
            }
        }       
    }
    
    /**
     * Закрыть окно редактирования текстур
     */
    public static void closeFormTextureEditor() {
        if (formTextureEditor != null) {
            try {
                formTextureEditor.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
            formTextureEditor = null;
        }        
    }
    
    /**
     * Закрыть окно редактирования материалов
     */
    public static void closeFormMaterialEditor() {  
        if (formMaterialEditor != null) {
            try {
                formMaterialEditor.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
            formMaterialEditor = null;
        }
    }
    
    /**
     * Закрыть окно редактирования звуков
     */
    public static void closeFormSoundEditor() {  
        if (formSoundEditor != null) {
            try {
                formSoundEditor.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showException(ex);
            }
            formSoundEditor = null;
        }
    }
    
    /**
     * Перезагрузить открытые окна
     */
    public static void reloadChildWindows() {
        if (formTextureEditor != null) {
            formTextureEditor.prepareForm(false);
        }
        if (formMaterialEditor != null) {
            formMaterialEditor.prepareForm(false);
        }
        if (formSoundEditor != null) {
            formSoundEditor.prepareForm(false);
        }
    }
    /** 
     * Закрыть все вызванные ранее дочерние окна
     */
    public static void closeChildWindows() {
        closeFormTextureEditor();
        closeFormMaterialEditor();
        closeFormSoundEditor();
    }
        
    /**
     * Проверка папки и обход подпапок
     * @param node Нода, к которой добавлять ветку с подпапкой, если есть
     * @param path Путь, в котором искать подпапки
     */
    void fillNodeTreeFolders(DefaultMutableTreeNode node, File path) {
        
        class FolderFilter implements FileFilter {
            @Override
            public boolean accept(File path) {
                return path.isDirectory();
            }            
        }
        
        if (path.isDirectory()) {
            File[] files = path.listFiles(new FolderFilter());
            Arrays.sort(files);
            for (File file : files) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getName());
                node.add(newNode);
                fillNodeTreeFolders(newNode, file);
            }
        }
    }
    
    /**
     * Получить ноду в TreeFolders, которой соответствует currentPath
     * @return Возвращает искомую ноду или корнеь, если не нашёл
     */
    DefaultMutableTreeNode getNodeFromCurrentPath() {
        DefaultTreeModel model = (DefaultTreeModel) treeFolders.getModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
                
        if (currentPath != null) {
            Path checkPath = currentPath;
            LinkedList<String> pathToRoot = new LinkedList<>();

            // собираем имена папок для перехода в путь от корня (resources)
            while (checkPath.compareTo(Global.RESOURCES_PATH) != 0) {
                pathToRoot.addFirst(checkPath.getFileName().toString());
                checkPath = checkPath.getParent();
                if (checkPath == null) {
                    break;
                }
            }

            // для каждого имени ищем ноду, которую нужно выбрать
            for (String nodeName : pathToRoot) {
                for (int i = 0; i < node.getChildCount(); ++i) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (((String)child.getUserObject()).equals(nodeName)) {
                        node = child;
                        break;
                    }
                }
            }
        }
        
        return node;
    }
    
    /**
     * Выделить в дереве TreeFolders выбранный путь (используется currentPath)
     */
    void selectNodeTreeFolders() {        
        TreePath treePath = new TreePath(getNodeFromCurrentPath().getPath());
        treeFolders.scrollPathToVisible(treePath);
        treeFolders.setSelectionPath(treePath);
    }
    
    /**
     * Очистить дерево TreeFolders
     * */
    void clearTreeFolders() {
        treeFolders.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("resources")));
    }
    
    /**
     * Заполнить дерево TreeFolders папками проекта
     * @param reloadAll Перегрузить всё дерево или только измененный путь currentPath
     */
    void fillTreeFolders(boolean reloadAll) {
        DefaultTreeModel model = (DefaultTreeModel) treeFolders.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.setUserObject("resources");
        
        // создать папку ресурсов, если такой нет
        if (Files.notExists(Global.RESOURCES_PATH) || !Files.isDirectory(Global.RESOURCES_PATH)) {
            try {
                Files.createDirectories(Global.RESOURCES_PATH);
            } catch (IOException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.showError("Не удалось создать папку ресурсов проекта!");
                MessageDialog.showException(ex);
            }
        }
                
        // перегрузить всё дерево?
        if (reloadAll) {
            root.removeAllChildren();
            // рекурсивно обходим дерево папок и рисуем дерево
            fillNodeTreeFolders(root, Global.RESOURCES_PATH.toFile());
            model.reload();
        // перегрузить только лист 
        } else {                            
            DefaultMutableTreeNode node = getNodeFromCurrentPath();
            node.removeAllChildren();
            fillNodeTreeFolders(node, currentPath.toFile());
            model.reload(node);            
        }
                    
        selectNodeTreeFolders();
    }
    
    /**
     * Очистить представление содержимого папки
     */
    void clearListProjectView() {
        listProjectView.setModel(new DefaultListModel());        
    }
    
    /**
     * Заполнить представление содержимого папки
     */
    void fillListProjectView() {
        DefaultListModel listModel = new DefaultListModel();
        
        if (currentPath == null) {
            currentPath = Global.RESOURCES_PATH;
        }
        
        File path = currentPath.toFile();
        if (path != null) {
            // добавить кнопку "Вверх", если это не корень
            if (currentPath.compareTo(Global.RESOURCES_PATH) != 0) {
                Path parent = currentPath.getParent();
                if (parent != null) {
                    listModel.addElement(new PreviewElement(parent, true));
                }
            }
            
            File[] listFiles = path.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                // теперь собираем массив папок и файлов
                List<File> folders = new LinkedList<>();
                List<File> files   = new LinkedList<>();
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        folders.add(file);
                    } else {
                        files.add(file);
                    }
                }
                
                // первые для отображения папки
                if (folders.size() > 0) {
                    Collections.sort(folders);
                    for (File file : folders) {
                        listModel.addElement(new PreviewElement(file));            
                    }
                }
                
                // а затем файлы
                if (files.size() > 0) {
                    Collections.sort(files);
                    for (File file : files) {
                        listModel.addElement(new PreviewElement(file));            
                    }
                }
            }
        }
        
        listProjectView.setModel(listModel);

        //selectNodeTreeFolders();
    }
    
    /**
     * Обновить окно предпросмотра, если путь отображается в нём
     * @param path Путь, который нужно обновить, если он просматривается
     */
    public void updateListProjectView(String path) {
        Path checkPath = Paths.get(path);
        if (currentPath.equals(checkPath.getParent())) {
            fillListProjectView();
        }
    }
    
    /**
     * Получить строковое представление пути из выбранной ноды в дереве
     * @param node Нода, для которой нужно вернуть путь
     * @return Путь в виде строки с корнем в resources/
     */
    String getPathFromTreeFolders(DefaultMutableTreeNode node) {
        String path = "";
        
        while (!node.isRoot()) {
            path = node.getUserObject().toString() + "/" + path;
            node = (DefaultMutableTreeNode) node.getParent();
        }        
        path = Const.RESOURCES_STRING + path;
        //System.out.println(path);
        
        return path;
    }
    
    
    /**
     * Выбрать файлы для копирования из выбранных имён в ListProjectView
     */
    void chooseFilesForCopy() {
        if (listProjectView.getSelectedIndex() != -1) {
            List list = listProjectView.getSelectedValuesList();
            copyPaths = new LinkedList<>();
            
            // получить пути из реальных элементов
            for (Iterator it = list.iterator(); it.hasNext();) {
                PreviewElement element = (PreviewElement) it.next();
                if (element.getType() != PreviewElement.Type.LEVELUP) {
                    Path path = Paths.get(element.getPath());
                    if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                        copyPaths.add(path);
                    }
                }
            }
        }
    }
        
    /**
     * Creates new form FormMain
     */
    public FormMain() {
        initComponents();   
        
        Global.initPathResources();
        Global.initPathConfig();
        
        clearTreeFolders();
        clearListProjectView();
        listProjectView.setEnabled(false);
        treeFolders.setEnabled(false);  

        // задаем кастомный рендерер
        listProjectView.setCellRenderer(new ProjectViewCellRenderer());        
        
        setLocationRelativeTo(null);
        //fillListProjectView();        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupPV = new javax.swing.JPopupMenu();
        popupPVMenuAdd = new javax.swing.JMenu();
        popupPVMIFolder = new javax.swing.JMenuItem();
        popupPVResource = new javax.swing.JMenu();
        popupPVMITexture = new javax.swing.JMenuItem();
        popupPVMIMaterial = new javax.swing.JMenuItem();
        popupPVMISound = new javax.swing.JMenuItem();
        popupPVMICopy = new javax.swing.JMenuItem();
        popupPVMICut = new javax.swing.JMenuItem();
        popuvPVMIPaste = new javax.swing.JMenuItem();
        popupPVMIRename = new javax.swing.JMenuItem();
        popupPVMIRemove = new javax.swing.JMenuItem();
        jSplitPane3 = new javax.swing.JSplitPane();
        Desktop = new javax.swing.JDesktopPane();
        splProjectManager = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeFolders = new javax.swing.JTree();
        jScrollPane3 = new javax.swing.JScrollPane();
        listProjectView = new javax.swing.JList<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        MenuFile = new javax.swing.JMenu();
        MenuItemOpenProject = new javax.swing.JMenuItem();
        MenuItemSaveProject = new javax.swing.JMenuItem();
        MenuItemCloseProject = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        MenuItemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        popupPVMenuAdd.setText("Добавить");

        popupPVMIFolder.setText("Папка");
        popupPVMIFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMIFolderActionPerformed(evt);
            }
        });
        popupPVMenuAdd.add(popupPVMIFolder);

        popupPVResource.setText("Ресурс");

        popupPVMITexture.setText("Текстура");
        popupPVMITexture.setToolTipText("");
        popupPVMITexture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMITextureActionPerformed(evt);
            }
        });
        popupPVResource.add(popupPVMITexture);

        popupPVMIMaterial.setText("Материал");
        popupPVMIMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMIMaterialActionPerformed(evt);
            }
        });
        popupPVResource.add(popupPVMIMaterial);

        popupPVMISound.setText("Звук/Музыка");
        popupPVMISound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMISoundActionPerformed(evt);
            }
        });
        popupPVResource.add(popupPVMISound);

        popupPVMenuAdd.add(popupPVResource);

        popupPV.add(popupPVMenuAdd);

        popupPVMICopy.setText("Копировать");
        popupPVMICopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMICopyActionPerformed(evt);
            }
        });
        popupPV.add(popupPVMICopy);

        popupPVMICut.setText("Вырезать");
        popupPVMICut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMICutActionPerformed(evt);
            }
        });
        popupPV.add(popupPVMICut);

        popuvPVMIPaste.setText("Вставить");
        popuvPVMIPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popuvPVMIPasteActionPerformed(evt);
            }
        });
        popupPV.add(popuvPVMIPaste);

        popupPVMIRename.setText("Переименовать");
        popupPVMIRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMIRenameActionPerformed(evt);
            }
        });
        popupPV.add(popupPVMIRename);

        popupPVMIRemove.setText("Удалить");
        popupPVMIRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupPVMIRemoveActionPerformed(evt);
            }
        });
        popupPV.add(popupPVMIRemove);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nuke3D Editor");
        setName("mainForm"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1024, 768));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane3.setDividerLocation(550);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        Desktop.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.focus"));
        Desktop.setMinimumSize(new java.awt.Dimension(512, 384));

        org.jdesktop.layout.GroupLayout DesktopLayout = new org.jdesktop.layout.GroupLayout(Desktop);
        Desktop.setLayout(DesktopLayout);
        DesktopLayout.setHorizontalGroup(
            DesktopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1095, Short.MAX_VALUE)
        );
        DesktopLayout.setVerticalGroup(
            DesktopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 549, Short.MAX_VALUE)
        );

        jSplitPane3.setLeftComponent(Desktop);

        splProjectManager.setMinimumSize(new java.awt.Dimension(128, 32));
        splProjectManager.setPreferredSize(new java.awt.Dimension(340, 250));

        jScrollPane1.setMinimumSize(new java.awt.Dimension(128, 128));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 386));

        treeFolders.setMaximumSize(new java.awt.Dimension(32767, 32767));
        treeFolders.setMinimumSize(new java.awt.Dimension(128, 128));
        treeFolders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeFoldersMousePressed(evt);
            }
        });
        treeFolders.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeFoldersValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(treeFolders);

        splProjectManager.setLeftComponent(jScrollPane1);

        jScrollPane3.setMinimumSize(new java.awt.Dimension(128, 128));

        listProjectView.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12", "Item 13", "Item 14", "Item 15", "Item 16", "Item 17", "Item 18", "Item 19", "Item 20", "Item 21", "Item 22", "Item 23", "Item 24", "Item 25", "Item 26", "Item 27", "Item 28", "Item 29" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listProjectView.setAutoscrolls(false);
        listProjectView.setDoubleBuffered(true);
        listProjectView.setFixedCellHeight(128);
        listProjectView.setFixedCellWidth(128);
        listProjectView.setInheritsPopupMenu(true);
        listProjectView.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        listProjectView.setMaximumSize(new java.awt.Dimension(32767, 32767));
        listProjectView.setMinimumSize(new java.awt.Dimension(128, 128));
        listProjectView.setPreferredSize(new java.awt.Dimension(128, 300));
        listProjectView.setVisibleRowCount(-1);
        listProjectView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProjectViewMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(listProjectView);

        splProjectManager.setRightComponent(jScrollPane3);

        jSplitPane3.setRightComponent(splProjectManager);

        MenuFile.setText("File");

        MenuItemOpenProject.setText("Open Project");
        MenuItemOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemOpenProjectActionPerformed(evt);
            }
        });
        MenuFile.add(MenuItemOpenProject);

        MenuItemSaveProject.setText("Save Project");
        MenuItemSaveProject.setEnabled(false);
        MenuItemSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemSaveProjectActionPerformed(evt);
            }
        });
        MenuFile.add(MenuItemSaveProject);

        MenuItemCloseProject.setText("Close Project");
        MenuItemCloseProject.setEnabled(false);
        MenuItemCloseProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemCloseProjectActionPerformed(evt);
            }
        });
        MenuFile.add(MenuItemCloseProject);
        MenuFile.add(jSeparator1);

        MenuItemExit.setText("Exit");
        MenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemExitActionPerformed(evt);
            }
        });
        MenuFile.add(MenuItemExit);

        jMenuBar1.add(MenuFile);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 731, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void MenuItemSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemSaveProjectActionPerformed
        projectSave();     
    }//GEN-LAST:event_MenuItemSaveProjectActionPerformed

    private void MenuItemCloseProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemCloseProjectActionPerformed
        projectClose();
    }//GEN-LAST:event_MenuItemCloseProjectActionPerformed

    private void MenuItemOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemOpenProjectActionPerformed
        projectOpen();
    }//GEN-LAST:event_MenuItemOpenProjectActionPerformed

    private void MenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemExitActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_MenuItemExitActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (MessageDialog.showConfirmationYesNo("Вы действительно хотите закрыть программу?")) {
            if (!projectClose()) {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            } else {
                setDefaultCloseOperation(EXIT_ON_CLOSE);                    
            }
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_formWindowClosing
    
    private void treeFoldersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeFoldersMousePressed
        if (evt.getButton() == MouseEvent.BUTTON1) {  
            
        }
    }//GEN-LAST:event_treeFoldersMousePressed

    private void listProjectViewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listProjectViewMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() > 1) {  
            
            List list = listProjectView.getSelectedValuesList();
            if (list.size() > 0) {
                PreviewElement element = (PreviewElement)list.get(0);
                switch (element.getType()) {
                    case LEVELUP:
                    case FOLDER:
                        currentPath = Paths.get(element.getPath());
                        selectNodeTreeFolders();    // выберет в списке папку, а выбор 
                                                    // папки перезагрузит представление вьюхи
                        break;
                        
                    case TEXTURE:                        
                        openFormTextureEditor();
                        break;
                        
                    case MATERIAL:                        
                        openFormMaterialEditor();
                        break;
                        
                    case SOUND:                        
                        openFormSoundEditor();
                        break;
                        
                    default:
                        break;
                }
            }
        }
    }//GEN-LAST:event_listProjectViewMouseClicked

    private void treeFoldersValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeFoldersValueChanged
        if (treeFolders.getSelectionCount() > 0) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeFolders.getSelectionPath().getLastPathComponent();
            currentPath = Paths.get(getPathFromTreeFolders(node));
            fillListProjectView();
        }
    }//GEN-LAST:event_treeFoldersValueChanged

    private void popupPVMIFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMIFolderActionPerformed
        String folderName = MessageDialog.showInput("Введите имя новой папки");
        if (folderName != null) {
            String newFolderPath = currentPath.toString() + "/" + folderName;
            File newFolder = new File(newFolderPath);
            if (newFolder != null) {
                newFolder.mkdirs();

                fillTreeFolders(false);
                fillListProjectView();
            }
        }
    }//GEN-LAST:event_popupPVMIFolderActionPerformed

    private void popupPVMIRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMIRenameActionPerformed
        if (listProjectView.getSelectedIndex() != -1) {
        
            // сохраняем имеющиеся ресурсы, чтобы перенести правильно их конфиги
            Material.saveAll();
            
            List list = listProjectView.getSelectedValuesList();
            
            PreviewElement element = (PreviewElement) list.get(0);            
            PreviewElement.Type elementType = element.getType();
            
            String oldName = element.getName(); 
            String newName = null;
            
            boolean done = false;
            while (!done) {          
                newName = (String) MessageDialog.showInput("Введите новое имя объекта", oldName);
                
                // пользователь отказался от ввода
                if (newName == null) {
                    return;
                }

                // имена идентичны - повторить ввод
                if (newName.equals(oldName)) {
                    continue;
                } else {
                    Path path = Paths.get(element.getPath());
                    String newPathStr = path.getParent().toString() + File.separator + newName;
                    switch (element.getType()) {
                        case TEXTURE:
                        case MATERIAL:
                        case SOUND:
                            newPathStr += "." + element.getExtension();
                            break;
                    }

                    Path newPath = Paths.get(newPathStr);
                    if (Files.exists(newPath)) {
                        MessageDialog.showError("Файл \"" + newPathStr + "\" уже существует!");
                        continue;
                    }

                    FileSystemUtils.move(path, newPath);
                
                    done = true;
                }
            }

            fillTreeFolders(false);
            fillListProjectView();
            
            // выбираем "тот же" объект
            ListModel model = listProjectView.getModel();
            for (int i = 0; i < model.getSize(); ++i) {
                element = (PreviewElement) model.getElementAt(i);
                if (element.getType() == elementType && element.getName().equals(newName)) {
                    listProjectView.setSelectedIndex(i);
                }
            }      
            
            // проверяем валидность материалов
            Material.checkAll();        
                        
            // перезагрузить окна на случай, если был переименован открытый объект в редакторе
            reloadChildWindows();
        }
    }//GEN-LAST:event_popupPVMIRenameActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // кастомные иконки для дерева
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) treeFolders.getCellRenderer();
        ImageIcon leafIcon = new ImageIcon(Const.ICONS.get("SmallFolderClose"));
        ImageIcon openIcon = new ImageIcon(Const.ICONS.get("SmallFolderOpen"));
        renderer.setOpenIcon(openIcon);
        renderer.setLeafIcon(leafIcon);
        renderer.setClosedIcon(leafIcon);
        
        setIconImage(Const.ICONS.get("FormMainIcon"));
        
        listProjectView.repaint();
        treeFolders.repaint();
    }//GEN-LAST:event_formWindowOpened

    private void popupPVMIRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMIRemoveActionPerformed
        if (listProjectView.getSelectedIndex() != -1) {
            List list = listProjectView.getSelectedValuesList();
            // удалить все абстрактные элементы (типа "Вверх")
            for (Iterator it = list.iterator(); it.hasNext();) {
                PreviewElement element = (PreviewElement) it.next();
                if (element.getType() == PreviewElement.Type.LEVELUP) {
                    it.remove();
                }
            }
                      
            // Элемент один? Спросить с именем
            String message;
            if (list.size() == 1) {
                message = "Действительно удалить \"" + ((PreviewElement)list.get(0)).getName() + "\"?";     
            // с количеством
            } else if (list.size() > 1) {
                message = "Действительно удалить " + list.size() + " элемента(-ов)?";                
            // нечего удалять!
            } else {
                return;
            }
            
            // последний шанс ничего не удалять
            if (!MessageDialog.showConfirmationYesNo(message)) {
                return;
            }
            
            // удаляем рекурсивно выбранные объекты
            for (Object obj : list) {
                PreviewElement element = (PreviewElement) obj;
                String name = element.getName();
                
                switch (element.getType()) {
                    case FOLDER:
                    case TEXTURE:
                    case MATERIAL:
                    case SOUND:
                    default:
                        if (!FileSystemUtils.remove(Paths.get(element.getPath()))) {
                            MessageDialog.showError("Возникли ошибки во время удаления \"" + name + "\"");
                        }        
                        break;

                    case LEVELUP:
                        continue;
                }                
            }  
            
            // проверяем валидность материалов
            Material.checkAll();  

            fillTreeFolders(false);
            fillListProjectView();         

            reloadChildWindows();          
        }
    }//GEN-LAST:event_popupPVMIRemoveActionPerformed
    
    private void popupPVMICopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMICopyActionPerformed
        chooseFilesForCopy();
        isCutMode = false;
    }//GEN-LAST:event_popupPVMICopyActionPerformed

    private void popuvPVMIPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popuvPVMIPasteActionPerformed
        if (copyPaths != null && copyPaths.size() > 0) {          
        
            // сохраняем имеющиеся ресурсы, чтобы перенести правильно их конфиги
            Material.saveAll();    
            
            String currentPathString = FileSystemUtils.getProjectPath(currentPath);
            for (Path path : copyPaths) {    
                Path dest = Paths.get(currentPathString + path.getFileName());
                if (!FileSystemUtils.repath(path, dest, isCutMode)) {
                    if (!isCutMode) {
                        MessageDialog.showError("Возникли ошибки при копировании \"" + path.toString() + "\".");
                    } else {
                        MessageDialog.showError("Возникли ошибки при переносе \"" + path.toString() + "\".");
                    }
                }              
            }
        
            // проверяем валидность материалов
            Material.checkAll();
                   
            // несколько раз вырезать нельзя
            if (isCutMode) {
                isCutMode = false;
                copyPaths.clear();
                copyPaths = null; 
            }
            
            fillTreeFolders(true);
            fillListProjectView();            
        
            reloadChildWindows();
        
            System.gc();
        }
    }//GEN-LAST:event_popuvPVMIPasteActionPerformed

    private void popupPVMICutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMICutActionPerformed
        chooseFilesForCopy();
        isCutMode = true;
    }//GEN-LAST:event_popupPVMICutActionPerformed

    private void popupPVMITextureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMITextureActionPerformed
        File[] files = new DialogOpenTexture(this, true).execute();
        if (files == null || files.length == 0) {
            return;
        }
        
        String lastName = null; 
        
        for (File txrFile : files) {
            if (txrFile != null) {
                // копируем текстуру к себе в папку ресурсов
                String baseName = FilenameUtils.getBaseName(txrFile.getName());     
                String extension = "." + Const.TEXTURE_FORMAT_EXT;
                Path newPath = Paths.get(currentPath.toString() + "/" + baseName + extension);

                // файл с таким же именем существует?
                if (Files.exists(newPath)) {
                    Boolean answer = MessageDialog.showConfirmationYesNoCancel("\"" + baseName + "\"\nуже существует! Перезаписать?");
                    // CANCEL
                    if (answer == null) {
                        continue;
                    // NO
                    } else if (!answer.booleanValue()) {
                        // решил переименовать
                        while (Files.exists(newPath)) {
                            String newName = (String) MessageDialog.showInput("Введите новое имя для объекта\n\"" + baseName + "\":", baseName);
                            if (newName == null) {
                                continue;    // отмена?
                            } else {
                                baseName = newName;
                                newPath = Paths.get(currentPath.toString() + "/" + newName + extension);
                            }
                        }   
                    // YES
                    } else {
                        FileSystemUtils.remove(newPath);
                    }
                }

                // создаем файл у себя
                try {
                    BufferedImage img = ImageUtils.prepareImage(ImageIO.read(txrFile));
                    ImageIO.write(img, "png", newPath.toFile());
                } catch (Exception ex) {
                    Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.showException(ex);
                    continue;
                }

                fillListProjectView();            
                new Texture(newPath);
                
                lastName = baseName;
            }
        }
        
        // открываем окно редактирования текстуры
        if (lastName != null) {
            DefaultListModel model = (DefaultListModel) listProjectView.getModel();
            for (Object obj : model.toArray()) {
                PreviewElement element = (PreviewElement)obj;
                if (element.getType() == PreviewElement.Type.TEXTURE && 
                    element.getName().equals(lastName)
                   ) {                    
                    listProjectView.setSelectedValue(element, true);
                    openFormTextureEditor();
                    break;
                }
            }
        }
    }//GEN-LAST:event_popupPVMITextureActionPerformed

    private void popupPVMIMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMIMaterialActionPerformed
        String name = MessageDialog.showInput("Введите имя для нового материала");
        if (name == null) {
            return;    // отмена?
        } else {
            Path matPath = Paths.get(currentPath.toString() + "/" + name + "." + Const.MATERIAL_FORMAT_EXT);
            
            // файл с таким же именем существует?
            if (Files.exists(matPath)) {
                Boolean answer = MessageDialog.showConfirmationYesNoCancel("\"" + name + "\"\nуже существует! Перезаписать?");
                // CANCEL
                if (answer == null) {
                    return;
                // NO
                } else if (!answer.booleanValue()) {
                    // решил переименовать
                    while (Files.exists(matPath)) {
                        String newName = (String) MessageDialog.showInput("Введите новое имя для объекта\n\"" + name + "\":", name);
                        if (newName == null) {
                            return;    // отмена?
                        } else {
                            name = newName;
                            matPath = Paths.get(currentPath.toString() + "/" + newName + "." + Const.MATERIAL_FORMAT_EXT);
                        }
                    }
                // YES
                } else {
                    FileSystemUtils.remove(matPath);
                }
            }
            
            new Material(matPath);
            
            fillListProjectView(); 
            
            // открываем окно редактирования материала
            DefaultListModel model = (DefaultListModel) listProjectView.getModel();
            for (Object obj : model.toArray()) {
                PreviewElement element = (PreviewElement)obj;
                if (element.getType() == PreviewElement.Type.MATERIAL && 
                    element.getName().equals(name)
                   ) {                    
                    listProjectView.setSelectedValue(element, true);
                    openFormMaterialEditor();
                    break;
                }
            }
        }
    }//GEN-LAST:event_popupPVMIMaterialActionPerformed

    private void popupPVMISoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPVMISoundActionPerformed
        File[] files = new DialogOpenSound(this, true).execute();
        if (files == null || files.length == 0) {
            return;
        }
        
        String lastName = null;
        
        for (File sndFile : files) {
            if (sndFile != null) {
                String baseName = FilenameUtils.getBaseName(sndFile.getName());     
                String extension = "." + Const.SOUND_FORMAT_EXT;
                Path newPath = Paths.get(currentPath.toString() + "/" + baseName + extension);

                // файл с таким же именем существует?
                if (Files.exists(newPath)) {
                    Boolean answer = MessageDialog.showConfirmationYesNoCancel("\"" + baseName + "\"\nуже существует! Перезаписать?");
                    // CANCEL
                    if (answer == null) {
                        return;
                    // NO
                    } else if (!answer.booleanValue()) {
                        // решил переименовать
                        while (Files.exists(newPath)) {
                            String newName = (String) MessageDialog.showInput("Введите новое имя для объекта\n\"" + baseName + "\":", baseName);
                            if (newName == null) {
                                return;    // отмена?
                            } else {
                                baseName = newName;
                                newPath = Paths.get(currentPath.toString() + "/" + newName + extension);
                            }
                        }
                    // YES
                    } else {
                        FileSystemUtils.remove(newPath);
                    }
                }

                // если исходный файл с расширением ogg, то просто его копировать. А иначе конвертировать в ogg
                if (Const.SOUND_FORMAT_EXT.equals(FileSystemUtils.getFileExtension(sndFile))) {
                    // создаем файл у себя
                    try {
                        FileUtils.copyFile(sndFile, newPath.toFile());
                    } catch (IOException ex) {
                        Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                        MessageDialog.showException(ex);
                        return;
                    }
                // файл не ogg
                } else {
                    File importedFile = new FormAudioConverter(this, true)
                                            .execute(sndFile, newPath.toFile());
                    if (importedFile == null) {
                        return;
                    } else {
                        newPath = importedFile.toPath();
                    }
                }

                fillListProjectView();            
                new Sound(newPath);
                
                lastName = baseName;
            }
        }
        
        if (lastName != null) {
            // открываем окно редактирования
            DefaultListModel model = (DefaultListModel) listProjectView.getModel();
            for (Object obj : model.toArray()) {
                PreviewElement element = (PreviewElement)obj;
                if (element.getType() == PreviewElement.Type.SOUND && 
                    element.getName().equals(lastName)
                   ) {                    
                    listProjectView.setSelectedValue(element, true);
                    openFormSoundEditor();
                    break;
                }
            }
        }
    }//GEN-LAST:event_popupPVMISoundActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */           
        /*
        UIManager.put( "info", new Color(128,128,128) );
        UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
        UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
        UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
        UIManager.put( "nimbusFocus", new Color(115,164,209) );
        UIManager.put( "nimbusGreen", new Color(176,179,50) );
        UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
        UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
        UIManager.put( "nimbusOrange", new Color(191,98,4) );
        UIManager.put( "nimbusRed", new Color(169,46,34) );
        UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
        UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
        UIManager.put( "text", new Color( 230, 230, 230) );
        */
        /*try {            
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName()) 
                    //|| "GTK+".equals(info.getName()) 
                    //"Nimbus".equals(info.getName()) 
                    //|| "Windows".equals(info.getName()) 
                    //|| "Windows Classic".equals(info.getName())    
                    ) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());    
                    break;
                }
            }
            
            // GTK
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

            // motif
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

            // nimbus                    
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

            // windows classic
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

            // windows
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  
            
            // metal
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            
            //UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
            
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }*/
        try {            
            UIManager.getFont("Label.font");
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.showException(ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
                        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                formMain = new FormMain();
                formMain.setVisible(true);
            }
        }); 
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane Desktop;
    private javax.swing.JMenu MenuFile;
    private javax.swing.JMenuItem MenuItemCloseProject;
    private javax.swing.JMenuItem MenuItemExit;
    private javax.swing.JMenuItem MenuItemOpenProject;
    private javax.swing.JMenuItem MenuItemSaveProject;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JList<String> listProjectView;
    private javax.swing.JPopupMenu popupPV;
    private javax.swing.JMenuItem popupPVMICopy;
    private javax.swing.JMenuItem popupPVMICut;
    private javax.swing.JMenuItem popupPVMIFolder;
    private javax.swing.JMenuItem popupPVMIMaterial;
    private javax.swing.JMenuItem popupPVMIRemove;
    private javax.swing.JMenuItem popupPVMIRename;
    private javax.swing.JMenuItem popupPVMISound;
    private javax.swing.JMenuItem popupPVMITexture;
    private javax.swing.JMenu popupPVMenuAdd;
    private javax.swing.JMenu popupPVResource;
    private javax.swing.JMenuItem popuvPVMIPaste;
    private javax.swing.JSplitPane splProjectManager;
    private javax.swing.JTree treeFolders;
    // End of variables declaration//GEN-END:variables

}
