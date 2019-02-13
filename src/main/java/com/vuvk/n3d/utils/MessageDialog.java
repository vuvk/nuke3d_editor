/**
    Messages in Dialog (Nuke3D Editor)
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
package com.vuvk.n3d.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class MessageDialog {
    private MessageDialog(){}
    
    /**
     * Отобразить простое сообщение
     * @param msg Текст сообщения
     */
    public static void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg);       
    }
    
    /**
     * Отобразить сообщение с ошибкой из исключения
     * @param ex Исключение, сообщение которого необходимо отобразить
     */
    public static void showException(Throwable ex) {
        ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(stackTrace));
        showError(ex.getMessage() + "\nStackTrace:\n" + stackTrace.toString());
    }
    
    /**
     * Отобразить сообщение с текстом ошибки
     * @param msg Cообщение, которое необходимо отобразить
     */
    public static void showError(String msg) {
        JOptionPane.showMessageDialog(null, 
                                      msg, 
                                      "Error", 
                                      JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Отобразить сообщение с информацией
     * @param msg Текст сообщения
     */
    public static void showInformation(String msg) {
        JOptionPane.showMessageDialog(null, 
                                      msg, 
                                      "Information", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Отобразить сообщение с подтверждением (Да/Нет)
     * @param msg Текст сообщения
     * @return true, если ответ Да
     */
    public static boolean showConfirmationYesNo(String msg) {
        return JOptionPane.showConfirmDialog(null, 
                                             msg, 
                                             "Confirmation", 
                                             JOptionPane.YES_NO_OPTION, 
                                             JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
    
    /**
     * Отобразить сообщение с подтверждением (Да/Нет/Отмена)
     * @param msg Текст сообщения
     * @return Boolean.TRUE, если ответ Да, Boolean.FALSE, если нет, null - Отмена
     */
    public static Boolean showConfirmationYesNoCancel(String msg) {
        switch (JOptionPane.showConfirmDialog(null, 
                                             msg, 
                                             "Confirmation", 
                                             JOptionPane.YES_NO_CANCEL_OPTION, 
                                             JOptionPane.QUESTION_MESSAGE)) {
            case JOptionPane.YES_OPTION:
                return Boolean.TRUE;
            case JOptionPane.NO_OPTION:
                return Boolean.FALSE;
            default:
                return null;
        }
    }
    
    /**
     * Отобразить окно ввода значения
     * @param msg Текст сообщения
     * @return Введенное значение или null, если поле пустое или окно отменено
     */
    public static String showInput(String msg) {
        return JOptionPane.showInputDialog(null, msg, "Input", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Отобразить окно ввода значения с дефолтным значением в поле ввода
     * @param msg Текст сообщения
     * @param initialValue Значение в поле ввода при вызове
     * @return Введенное значение или null, если поле пустое или окно отменено
     */
    public static Object showInput(String msg, Object initialValue) {
        return JOptionPane.showInputDialog(null, 
                                           msg, 
                                           "Input", 
                                           JOptionPane.INFORMATION_MESSAGE,
                                           null, 
                                           null, 
                                           initialValue);
    }    
}
