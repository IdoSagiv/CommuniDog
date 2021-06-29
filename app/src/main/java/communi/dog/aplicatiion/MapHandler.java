package communi.dog.aplicatiion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.util.Collection;

import communi.dog.aplicatiion.Callbacks.OnMapLongPressCallback;

import static android.content.Context.LOCATION_SERVICE;

public class MapHandler {
    private static final double MAP_DEFAULT_ZOOM = 18.0;
    private static final double MAP_MAX_ZOOM = 20.0;
    private static final double MAP_MIN_ZOOM = 9.0;
    private static final double MARKERS_MIN_DISPLAY_ZOOM = 15;
    private final MapView mMapView;
    private GeoPoint currentLocation = null;
    private final boolean centerToLoc;

    private OnMapLongPressCallback longPressCallback = null;

    private final MapState mapState;

    private final Context context;

    /**
     * @param mapView      the founded mapView
     * @param initialState initial state of the map
     */
    public MapHandler(MapView mapView, MapState initialState, boolean centerToLoc) {
        this.mMapView = mapView;
        this.mapState = initialState;
        this.context = CommuniDogApp.getInstance();
        this.centerToLoc = centerToLoc;
        initMap();
        restoreState();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (currentLocation == null && centerToLoc) {
                // on the first update -> animate to current location
                currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapToCurrentLocation();
            }
            currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @SuppressLint("MissingPermission")
    public void initMap() {
        // initialize the map
        mMapView.getOverlay().clear();
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        mMapView.setMultiTouchControls(true);
        mMapView.getController().setZoom(mapState.getZoom());
        mMapView.setMaxZoomLevel(MAP_MAX_ZOOM);
        mMapView.setMinZoomLevel(MAP_MIN_ZOOM);

        // enable user location
        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener);

        final MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                if (longPressCallback != null) {
                    longPressCallback.OnLongPressCallback(p);
                }
                return false;
            }
        };
        mMapView.getOverlays().add(new MapEventsOverlay(mReceive));

        addMyLocationIconOnMap();
        addScaleBarOnMap();
    }


    private void addMyLocationIconOnMap() {
        // set my location on the map
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);

        // add to map
        mMapView.getOverlays().add(mLocationOverlay);
    }

    private void addScaleBarOnMap() {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        // set scale bar
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        // add to map
        mMapView.getOverlays().add(mScaleBarOverlay);
    }

    public void mapToCurrentLocation() {
        if (currentLocation != null) centerMap(currentLocation, true, true);
    }

    void centerMap(IGeoPoint newCenter, boolean animate, boolean zoomToDefault) {
        if (animate) {
            mMapView.getController().animateTo(newCenter);
        } else {
            mMapView.setExpectedCenter(newCenter);
        }
        if (zoomToDefault) mMapView.getController().setZoom(MAP_DEFAULT_ZOOM);

        updateCenter();
    }

    private void showMarkerOnMap(MarkerDescriptor descriptor) {
        User currUser = CommuniDogApp.getInstance().getDb().getCurrentUser();
        GeoPoint location = new GeoPoint(descriptor.getLatitude(), descriptor.getLongitude());

        Marker myMarker = new Marker(mMapView);
        myMarker.setPosition(location);
        myMarker.setTitle(descriptor.getText());
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        if (currUser != null && currUser.getId().equals(descriptor.getId())){
            myMarker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_my_marker, context.getTheme()));
        }
        else {
            myMarker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_general_marker, context.getTheme()));
        }
        myMarker.setId(descriptor.getId());

        mMapView.getOverlays().add(myMarker);
    }

    public void updateCenter() {
        mapState.setCenter(mMapView.getMapCenter());
        mapState.setZoom(mMapView.getZoomLevelDouble());
    }

    public void setLongPressCallback(OnMapLongPressCallback longPressCallback) {
        this.longPressCallback = longPressCallback;
    }

    void restoreState() {
        centerMap(mapState.getCenter(), false, false);
        mMapView.getController().setZoom(mapState.getZoom());
    }

    public void showMarkers(Collection<MarkerDescriptor> markers) {
        // remove all old markers
        for (Overlay overlay : mMapView.getOverlays()) {
            if (overlay instanceof Marker) {
                mMapView.getOverlays().remove(overlay);
            }
        }

        // show the new markers
        for (MarkerDescriptor descriptor : markers) {
            showMarkerOnMap(descriptor);
        }
    }

    MapState currentState() {
        mapState.setCenter(mMapView.getMapCenter());
        mapState.setZoom(mMapView.getZoomLevelDouble());
        return mapState;
    }
}
