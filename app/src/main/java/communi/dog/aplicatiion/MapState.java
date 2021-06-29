package communi.dog.aplicatiion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * class that stores the information of a map
 */
public class MapState implements Serializable {
    public static final float DEF_LATITUDE = 32.1007f;
    public static final float DEF_LONGITUDE = 34.8070f;
    private final HashMap<String, MarkerDescriptor> markersDescriptors;
    private double mapCenterLatitude;
    private double mapCenterLongitude;
    private double zoom;

    private final MutableLiveData<Collection<MarkerDescriptor>> markersDescriptorsMutableLD = new MutableLiveData<>();
    public final LiveData<Collection<MarkerDescriptor>> markersDescriptorsLD = markersDescriptorsMutableLD;

    private static MapState instance = null;

    public static MapState getInstance() {
        if (instance == null) {
            instance = new MapState();
        }
        return instance;
    }

    private MapState() {
        this.markersDescriptors = new HashMap<>();
        this.mapCenterLatitude = DEF_LATITUDE;
        this.mapCenterLongitude = DEF_LONGITUDE;
        this.zoom = 18;
    }

    public void setCenter(IGeoPoint newCenter) {
        setCenter(newCenter.getLatitude(), newCenter.getLongitude());
    }

    public void setCenter(double latitude, double longitude) {
        mapCenterLatitude = latitude;
        mapCenterLongitude = longitude;
        if (CommuniDogApp.getInstance().getDb() != null) {
            CommuniDogApp.getInstance().getDb().saveLocationToSp(latitude, longitude);
        }
    }

    public IGeoPoint getCenter() {
        return new GeoPoint(mapCenterLatitude, mapCenterLongitude);
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void addMarker(MarkerDescriptor toAdd) {
        markersDescriptors.put(toAdd.getId(), toAdd);
        setCenter(toAdd.getLatitude(), toAdd.getLongitude());

        markersDescriptorsMutableLD.setValue(getMarkersDescriptors());
    }

    public boolean hasMarker(String idToSearch) {
        return markersDescriptors.containsKey(idToSearch);
    }

    public void removeMarker(String idToRemove) {
        if (markersDescriptors.remove(idToRemove) != null) {
            markersDescriptorsMutableLD.setValue(getMarkersDescriptors());
        }
    }

    public ArrayList<MarkerDescriptor> getMarkersDescriptors() {
        return new ArrayList<>(markersDescriptors.values());
    }

    public MarkerDescriptor getMarker(String markerId) {
        if (markersDescriptors.containsKey(markerId)) return markersDescriptors.get(markerId);
        return null;
    }

    public void updateMarker(String markerId, String newText, double newLat, double newLon,
                             boolean isDogsitter, boolean isFood, boolean isMedication) {
        MarkerDescriptor marker = (markersDescriptors.containsKey(markerId)) ? markersDescriptors.get(markerId) : null;
        if (marker != null) {
            marker.setNewLocation(newLat, newLon);
            marker.setServices(isDogsitter, isFood, isMedication);
            marker.setText(newText);
            markersDescriptorsMutableLD.setValue(getMarkersDescriptors());
        }
    }

    public void setMarkersDescriptors(HashMap<String, MarkerDescriptor> markersDescriptors) {
        this.markersDescriptors.clear();
        this.markersDescriptors.putAll(markersDescriptors);

        markersDescriptorsMutableLD.setValue(getMarkersDescriptors());
    }
}
