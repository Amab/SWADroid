package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

public class LocationTimeStamp extends Location implements Serializable {

    private int checkInTime;

    public LocationTimeStamp(int institutionCode, String institutionShortName, String institutionFullName, int centerCode, String centerShortName, String centerFullName, int buildingCode, String buildingShortName, String buildingFullName, int floor, int roomCode, String roomShortName, String roomFullName, int checkInTime) {
        super(institutionCode, institutionShortName, institutionFullName, centerCode, centerShortName, centerFullName, buildingCode, buildingShortName, buildingFullName, floor, roomCode, roomShortName, roomFullName);
        this.checkInTime = checkInTime;
    }

    public int getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(int checkInTime) {
        this.checkInTime = checkInTime;
    }

    @Override
    public Object getProperty(int param) {
        Object object;
        if (param == 14) {
            object = checkInTime;
        }else{
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
        }else {
            super.setProperty(param, obj);
        }
    }

    @Override
    public void getPropertyInfo(int param, Hashtable properties, PropertyInfo propertyInfo) {
        if (param == 14) {
            propertyInfo.type = PropertyInfo.INTEGER_CLASS;
            propertyInfo.name = "checkInTime";
        }else {
            super.getPropertyInfo(param, properties, propertyInfo);
        }
    }

    @Override
    public String toString() {
        return "LocationTimeStamp{" +
                "institutionCode=" + institutionCode +
                ", institutionShortName='" + institutionShortName + '\'' +
                ", institutionFullName='" + institutionFullName + '\'' +
                ", centerCode=" + centerCode +
                ", centerShortName='" + centerShortName + '\'' +
                ", centerFullName='" + centerFullName + '\'' +
                ", buildingCode=" + buildingCode +
                ", buildingShortName='" + buildingShortName + '\'' +
                ", buildingFullName='" + buildingFullName + '\'' +
                ", floor=" + floor +
                ", roomCode=" + roomCode +
                ", roomShortName='" + roomShortName + '\'' +
                ", roomFullName='" + roomFullName + '\'' +
                ", checkInTime'" + checkInTime + '\'' +
                '}';
    }
}
