package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

public class Location extends Model implements Serializable {

    protected int institutionCode;

    protected String institutionShortName;

    protected String institutionFullName;

    protected int centerCode;

    protected String centerShortName;

    protected String centerFullName;

    protected int buildingCode;

    protected String buildingShortName;

    protected String buildingFullName;

    protected int floor;

    protected int roomCode;

    protected String roomShortName;

    protected String roomFullName;

    protected static final PropertyInfo PI_id = new PropertyInfo();
    protected static final PropertyInfo PI_institutionCode = new PropertyInfo();
    protected static final PropertyInfo PI_institutionShortName = new PropertyInfo();
    protected static final PropertyInfo PI_institutionFullName = new PropertyInfo();
    protected static final PropertyInfo PI_centerCode = new PropertyInfo();
    protected static final PropertyInfo PI_centerShortName = new PropertyInfo();
    protected static final PropertyInfo PI_centerFullName = new PropertyInfo();
    protected static final PropertyInfo PI_buildingCode = new PropertyInfo();
    protected static final PropertyInfo PI_buildingShortName = new PropertyInfo();
    protected static final PropertyInfo PI_buildingFullName = new PropertyInfo();
    protected static final PropertyInfo PI_floor = new PropertyInfo();
    protected static final PropertyInfo PI_roomCode = new PropertyInfo();
    protected static final PropertyInfo PI_roomShortName = new PropertyInfo();
    protected static final PropertyInfo PI_roomFullName = new PropertyInfo();
    protected static PropertyInfo[] PI_PROP_ARRAY =
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

    public Location(int institutionCode, String institutionShortName, String institutionFullName, int centerCode, String centerShortName, String centerFullName, int buildingCode, String buildingShortName, String buildingFullName, int floor, int roomCode, String roomShortName, String roomFullName) {
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

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(int roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomShortName() {
        return roomShortName;
    }

    public void setRoomShortName(String roomShortName) {
        this.roomShortName = roomShortName;
    }

    public String getRoomFullName() {
        return roomFullName;
    }

    public void setRoomFullName(String roomFullName) {
        this.roomFullName = roomFullName;
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
        }
    }

    @Override
    public String toString() {
        return "Location{" +
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
                '}';
    }
}
