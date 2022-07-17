package es.ugr.swad.swadroid.model;

import androidx.room.Entity;

import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

import lombok.Data;

@Data
@Entity
public class LocationTimeStamp extends Location implements Serializable {

    private int checkInTime;

    public LocationTimeStamp(int institutionCode, String institutionShortName, String institutionFullName, int centerCode, String centerShortName, String centerFullName, int buildingCode, String buildingShortName, String buildingFullName, int floor, int roomCode, String roomShortName, String roomFullName, int checkInTime) {
        super(institutionCode, institutionShortName, institutionFullName, centerCode, centerShortName, centerFullName, buildingCode, buildingShortName, buildingFullName, floor, roomCode, roomShortName, roomFullName);
        this.checkInTime = checkInTime;
    }

    @Override
    public Object getProperty(int param) {
        Object object;
        if (param == 14) {
            object = checkInTime;
        } else {
            object = super.getProperty(param);
        }
        return object;
    }

    @Override
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    @Override
    public void setProperty(int param, Object obj) {
        if (param == 14) {
            checkInTime = (int) obj;
        } else {
            super.setProperty(param, obj);
        }
    }

    @Override
    public void getPropertyInfo(int param, Hashtable properties, PropertyInfo propertyInfo) {
        if (param == 14) {
            propertyInfo.type = PropertyInfo.INTEGER_CLASS;
            propertyInfo.name = "checkInTime";
        } else {
            super.getPropertyInfo(param, properties, propertyInfo);
        }
    }

}
