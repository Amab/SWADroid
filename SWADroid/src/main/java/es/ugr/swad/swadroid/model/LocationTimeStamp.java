package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

public class LocationTimeStamp extends Model implements Serializable {

    private int institutionCode;

    private String institutionShortName;

    private String institutionFullName;

    private int centerCode;

    private String centerShortName;

    private String centerFullName;

    private int buildingCode;

    private String buildingShortName;

    private String buildingFullName;

    private int floor;

    private int roomCode;

    private String roomShortName;

    private String roomFullName;

    private int checkInTime;

    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_institutionCode = new PropertyInfo();
    private static final PropertyInfo PI_institutionShortName = new PropertyInfo();
    private static final PropertyInfo PI_institutionFullName = new PropertyInfo();
    private static final PropertyInfo PI_centerCode = new PropertyInfo();
    private static final PropertyInfo PI_centerShortName = new PropertyInfo();
    private static final PropertyInfo PI_centerFullName = new PropertyInfo();
    private static final PropertyInfo PI_buildingCode = new PropertyInfo();
    private static final PropertyInfo PI_buildingShortName = new PropertyInfo();
    private static final PropertyInfo PI_buildingFullName = new PropertyInfo();
    private static final PropertyInfo PI_floor = new PropertyInfo();
    private static final PropertyInfo PI_roomCode = new PropertyInfo();
    private static final PropertyInfo PI_roomShortName = new PropertyInfo();
    private static final PropertyInfo PI_roomFullName = new PropertyInfo();
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_institutionCode,
                    PI_institutionShortName,
                    PI_institutionFullName,
                    PI_centerCode,
                    PI_centerShortName,
                    PI_centerFullName,
                    PI_buildingCode,
                    PI_buildingShortName,
                    PI_buildingFullName,
                    PI_floor,
                    PI_roomCode,
                    PI_roomShortName,
                    PI_roomFullName
            };

    public LocationTimeStamp(int institutionCode, String institutionShortName, String institutionFullName, int centerCode, String centerShortName, String centerFullName, int buildingCode, String buildingShortName, String buildingFullName, int floor, int roomCode, String roomShortName, String roomFullName, int checkInTime) {
        this.institutionCode = institutionCode;
        this.institutionShortName = institutionShortName;
        this.institutionFullName = institutionFullName;
        this.centerCode = centerCode;
        this.centerShortName = centerShortName;
        this.centerFullName = centerFullName;
        this.buildingCode = buildingCode;
        this.buildingShortName = buildingShortName;
        this.buildingFullName = buildingFullName;
        this.floor = floor;
        this.roomCode = roomCode;
        this.roomShortName = roomShortName;
        this.roomFullName = roomFullName;
        this.checkInTime = checkInTime;
    }

    public int getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(int institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInstitutionShortName() {
        return institutionShortName;
    }

    public void setInstitutionShortName(String institutionShortName) {
        this.institutionShortName = institutionShortName;
    }

    public String getInstitutionFullName() {
        return institutionFullName;
    }

    public void setInstitutionFullName(String institutionFullName) {
        this.institutionFullName = institutionFullName;
    }

    public int getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(int centerCode) {
        this.centerCode = centerCode;
    }

    public String getCenterShortName() {
        return centerShortName;
    }

    public void setCenterShortName(String centerShortName) {
        this.centerShortName = centerShortName;
    }

    public String getCenterFullName() {
        return centerFullName;
    }

    public void setCenterFullName(String centerFullName) {
        this.centerFullName = centerFullName;
    }

    public int  getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(int buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingShortName() {
        return buildingShortName;
    }

    public void setBuildingShortName(String buildingShortName) {
        this.buildingShortName = buildingShortName;
    }

    public String getBuildingFullName() {
        return buildingFullName;
    }

    public void setBuildingFullName(String buildingFullName) {
        this.buildingFullName = buildingFullName;
    }

    public int getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(int checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getRoomFullName() {
        return roomFullName;
    }

    public void setRoomFullName(String roomFullName) {
        this.roomFullName = roomFullName;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = institutionCode;
                break;
            case 2:
                object = institutionShortName;
                break;
            case 3:
                object = institutionFullName;
                break;
            case 4:
                object = centerCode;
                break;
            case 5:
                object = centerShortName;
                break;
            case 6:
                object = centerFullName;
                break;
            case 7:
                object = buildingCode;
                break;
            case 8:
                object = buildingShortName;
                break;
            case 9:
                object = buildingFullName;
                break;
            case 10:
                object = floor;
                break;
            case 11:
                object = roomCode;
                break;
            case 12:
                object = roomShortName;
                break;
            case 13:
                object = roomFullName;
                break;
            case 14:
                object = checkInTime;
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
                institutionCode = (Integer) obj;
                break;
            case 2:
                institutionShortName = (String) obj;
                break;
            case 3:
                institutionFullName = (String) obj;
                break;
            case 4:
                centerCode = (Integer) obj;
                break;
            case 5:
                centerShortName = (String) obj;
                break;
            case 6:
                centerFullName = (String) obj;
                break;
            case 7:
                buildingCode = (Integer) obj;
                break;
            case 8:
                buildingShortName = (String) obj;
                break;
            case 9:
                buildingFullName = (String) obj;
                break;
            case 10:
                floor = (Integer) obj;
                break;
            case 11:
                roomCode = (Integer) obj;
                break;
            case 12:
                roomShortName = (String) obj;
                break;
            case 13:
                roomFullName = (String) obj;
                break;
            case 14:
                checkInTime = (int) obj;
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
                propertyInfo.name = "institutionCode";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "institutionShortName";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "institutionFullName";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "centerCode";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "centerShortName";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "centerFullName";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "buildingCode";
                break;
            case 8:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "buildingShortName";
                break;
            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "buildingFullName";
                break;
            case 10:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "floor";
                break;
            case 11:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "roomCode";
                break;
            case 12:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "roomShortName";
                break;
            case 13:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "roomFullName";
                break;
            case 14:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "checkInTime";
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
