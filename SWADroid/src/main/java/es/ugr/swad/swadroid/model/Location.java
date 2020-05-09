package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.List;

/**
 * Class for store a location
 *
 * @author Javier Bueno López <javibuenolopez8@gmail.com> *
 */
public class Location extends Model {

    private int numLocations;

    private List<LocationInfo> locationInfoList;

    private static final PropertyInfo PI_numLocations = new PropertyInfo();
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_numLocations
            };

    @Override
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 1:
                object = numLocations;
                break;
        }

        return object;
    }

    @Override
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    @Override
    public void setProperty(int index, Object value) {

    }

    @Override
    public void getPropertyInfo(int param, Hashtable properties, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "numLocations";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "locationInfoList";
                break;
        }
    }

    public int getNumLocations() {
        return numLocations;
    }

    public void setNumLocations(int numLocations) {
        this.numLocations = numLocations;
    }

    public List<LocationInfo> getLocationInfoList() {
        return locationInfoList;
    }

    public void setLocationInfoList(List<LocationInfo> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }

    @Override
    public String toString() {
        return "Location{" +
                "numLocations=" + numLocations +
                ", locationInfoList=" + locationInfoList +
                '}';
    }
}
