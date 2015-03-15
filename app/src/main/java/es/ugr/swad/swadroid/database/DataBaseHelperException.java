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
package es.ugr.swad.swadroid.database;

/**
 * Exception handler for DataBaseHelper class
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class DataBaseHelperException extends Exception {

    private static final long serialVersionUID = 2517884856120859095L;

    /**
     * Parameterless Constructor
     */
    public DataBaseHelperException() {
    }

    /**
     * Constructor that accepts a message
     *
     * @param detailMessage message to be displayed
     */
    public DataBaseHelperException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor that accepts an Exception
     *
     * @param throwable Exception to be thrown
     */
    public DataBaseHelperException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor that accepts a message and an Exception
     *
     * @param detailMessage message to be displayed
     * @param throwable     Exception to be thrown
     */
    public DataBaseHelperException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
