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
package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.KvmSerializable;

/**
 * Superclass for encapsulate common behavior of all Models.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public abstract class Model implements KvmSerializable {
    /**
     * Model identifier
     */
    private long id;

    Model(long id) {
        super();
        this.id = id;
    }

    protected Model() {
    }

    /**
     * Gets model identifier
     *
     * @return Model identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Sets model identifier
     *
     * @param id Model identifier
     */
    void setId(long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Model other = (Model) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                '}';
    }
}
