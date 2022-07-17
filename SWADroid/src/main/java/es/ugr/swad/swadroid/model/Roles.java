package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

import lombok.Data;

@Data
public class Roles implements Serializable, KvmSerializable {

    private long id;
    private int rol;

    protected static final PropertyInfo PI_id = new PropertyInfo();
    protected static final PropertyInfo PI_roles = new PropertyInfo();
    protected static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_roles
            };

    public Roles(int rol) {
        this.rol = rol;
    }

    @Override
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = rol;
                break;
        }
        return object;
    }

    @Override
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    @Override
    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                rol = (int) obj;
                break;
        }
    }

    @Override
    public void getPropertyInfo(int param, Hashtable properties, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "roles";
                break;
        }
    }
}
