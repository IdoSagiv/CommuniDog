package communi.dog.aplicatiion;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;


public class MapScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout moreInfoDrawerLayout;
    NavigationView navigationView;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapHandler mMapHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // more info bar
        initMoreInfoBar();

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });
        boolean centerToMyLocation = getIntent().getBooleanExtra("center_to_my_location", true);
        mMapHandler = new MapHandler(findViewById(R.id.mapView), CommuniDogApp.getInstance().getMapState(), centerToMyLocation);

        mMapHandler.setLongPressCallback(p -> {
            Intent intent = new Intent(this, AddMarkerActivity.class);
            String userId = CommuniDogApp.getInstance().getDb().getCurrentUser().getId();
            intent.putExtra("new_latitude", p.getLatitude());
            intent.putExtra("new_longitude", p.getLongitude());
            if (CommuniDogApp.getInstance().getMapState().hasMarker(userId)) {
                Log.i(MapHandler.class.getSimpleName(), "edit existing marker");
                intent.putExtra("marker_id_to_edit", userId);
            } else {
                Log.i(MapHandler.class.getSimpleName(), "create new marker");
            }
            this.startActivity(intent);
        });

        ImageView btCenterMap = findViewById(R.id.buttonCenterMap);
        btCenterMap.setOnClickListener(v -> mMapHandler.mapToCurrentLocation());

        ImageView btnMyProfile = findViewById(R.id.buttonMyProfileInMapActivity);
        btnMyProfile.setOnClickListener(v -> {
            mMapHandler.updateCenter();
            startActivity(new Intent(this, ProfilePageActivity.class));
        });

        ImageView btnMoreInfo = findViewById(R.id.buttonMoreInfoMapActivity);
        btnMoreInfo.setOnClickListener(v -> {
            mMapHandler.updateCenter();
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        });

        MapState.getInstance().markersDescriptorsLD.observe(this, markers -> {
            mMapHandler.showMarkers(markers);
        });

        CommuniDogApp.getInstance().getDb().currentUserLiveData.observe(this, user -> {
            ImageView btnNotification = findViewById(R.id.buttonNotificationActivity);
            if (user.isManager()) {
                btnNotification.setVisibility(View.VISIBLE);
            } else {
                btnNotification.setVisibility(View.GONE);
            }
        });
    }

    private void initMoreInfoBar() {
        moreInfoDrawerLayout = findViewById(R.id.drawer_layout_more_info);
        navigationView = findViewById(R.id.nav_view);
        moreInfoDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest =
                new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        if (moreInfoDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            moreInfoDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    finish();
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close the app?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.law:
                goToUrl("https://fs.knesset.gov.il/20/law/20_lsr_346609.pdf");
                break;
            case R.id.dog_site_link:
                goToUrl("https://israelguidedog.org.il/");
                break;
            case R.id.link_to_drive:
                goToUrl("https://drive.google.com/drive/u/1/folders/1tnP3SC9jdjHN-3QWdvIyb17k71k93hdF");
                break;
            case R.id.emergency_num:
                startActivity(new Intent(MapScreenActivity.this, Emergency_numbers.class));
                break;
            case R.id.about_section:
                startActivity(new Intent(MapScreenActivity.this, AboutPage.class));
                break;

        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapHandler.updateCenter();
        outState.putBoolean("is_more_info_open", moreInfoDrawerLayout.isDrawerOpen(GravityCompat.START));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean openDrawer = savedInstanceState.getBoolean("is_more_info_open", false);
        if (openDrawer) {
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void goToUrl(String s) {
        Uri url = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, url));
    }


    public void notificationsActivity(View view) {
        startActivity(new Intent(getApplicationContext(), UserApprovalActivity.class));
    }
}
