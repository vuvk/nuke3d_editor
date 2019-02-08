/**
    Math utilities of Nuke3D Editor
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

/**
 *
 * @author Anton "Vuvk" Shcherbatykh
 */
public final class MathUtils {    
    private MathUtils(){}
    
    /** 
     * проверка является ли число степенью двойки
     * @param number число для проверки
     * @return true, если является
     */
    public static boolean isPowerOfTwo(int number) {
        return ((number - 1) & number) == 0;
    }
    
    /**
     * возврат ближайшего числа степени двойки к данному (не больше)
     * @param number исходное число
     * @return Правильное число
     */
    public static int getPowerOfTwo(int number) {
        if (isPowerOfTwo(number)) {
            return number;
        }
        
        int res = 0;
        while (number > 1) {
            number >>= 1;
            ++res;
        }
        
        return 1 << res;
    }
}
