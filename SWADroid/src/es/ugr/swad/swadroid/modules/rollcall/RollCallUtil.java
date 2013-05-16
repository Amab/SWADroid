/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.swad.swadroid.modules.rollcall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Useful functions.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class RollCallUtil {
    public static boolean isValidDni(String dni) {
        String dniPattern = "^[A-Z]?\\d{1,16}[A-Z]?$";    // (0 or 1 letter) + (1 to 16 digits) + (0 or 1 letter)

        Pattern pattern = Pattern.compile(dniPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dni);

        return matcher.matches();
        /*if (matcher.matches())
			return checkDniLetter(dni);
		return false;*/
    }

    @SuppressWarnings("unused")
    private static boolean checkDniLetter(String n) {
        String number = n.substring(0, n.length() - 1);
        String letter = n.substring(n.length() - 1, n.length());

        int code = (Integer.valueOf(number)) % 23;
        String[] abc = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E", "T"};

        return abc[code].compareToIgnoreCase(letter) == 0;
    }

    public static boolean isValidNickname(String nickname) {
        String patronNickname = "@[a-zA-Z_0-9]{1,17}";    // 1 to 17 letters, underscored or digits

        Pattern pattern = Pattern.compile(patronNickname, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }
}
