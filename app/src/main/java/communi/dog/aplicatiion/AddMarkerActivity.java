package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddMarkerActivity extends AppCompatActivity {
    private final static int MISSING_COORD = -1;
    private Intent incomingIntent = null;
    private MapState mapState;
    private User currentUser;
    DB appDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        this.appDB = CommuniDogApp.getInstance().getDb();
        incomingIntent = getIntent();
        currentUser = CommuniDogApp.getInstance().getDb().getCurrentUser();
        ImageView buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        ImageView buttonDeleteMarker = findViewById(R.id.buttonDeleteMarker);

        mapState = CommuniDogApp.getInstance().getMapState();
        final MarkerDescriptor markerToEdit = mapState.getMarker(incomingIntent.getStringExtra("marker_id_to_edit"));

        if (markerToEdit != null) {
            buttonDeleteMarker.setVisibility(View.VISIBLE);
            buttonDeleteMarker.setClickable(true);
            buttonSaveMarker.setImageResource(R.drawable.ic_save_marker);
            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.edit_marker_page_title));
            ((CheckBox) findViewById(R.id.checkboxDogsitter)).setChecked(markerToEdit.isDogsitter());
            ((CheckBox) findViewById(R.id.checkboxFood)).setChecked(markerToEdit.isFood());
            ((CheckBox) findViewById(R.id.checkboxMedication)).setChecked(markerToEdit.isMedication());
        } else {
            buttonDeleteMarker.setVisibility(View.GONE);
            buttonDeleteMarker.setClickable(false);
            buttonSaveMarker.setImageResource(R.drawable.ic_add_marker);
            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.add_marker_page_title));
        }

        buttonSaveMarker.setOnClickListener(view -> {
            boolean isDogsitter = ((CheckBox) findViewById(R.id.checkboxDogsitter)).isChecked();
            boolean isFood = ((CheckBox) findViewById(R.id.checkboxFood)).isChecked();
            boolean isMedication = ((CheckBox) findViewById(R.id.checkboxMedication)).isChecked();
            if (!(isDogsitter || isFood || isMedication)) {
                Toast.makeText(this, "check at least one service", Toast.LENGTH_SHORT).show();
                return;
            }

            double latitude = incomingIntent.getDoubleExtra("new_latitude", MISSING_COORD);
            double longitude = incomingIntent.getDoubleExtra("new_longitude", MISSING_COORD);
            String newText = getMarkerTitle(isDogsitter, isFood, isMedication);

            if (markerToEdit != null) {
                // edit existing marker
                if (latitude == MISSING_COORD || longitude == MISSING_COORD) {
                    // don't change location
                    latitude = markerToEdit.getLatitude();
                    longitude = markerToEdit.getLongitude();
                }
                mapState.updateMarker(markerToEdit.getId(), newText, latitude, longitude, isDogsitter, isFood, isMedication);
                mapState.setCenter(markerToEdit.getLatitude(), markerToEdit.getLongitude());
                this.appDB.updateMarkerDescriptor(markerToEdit);

            } else {
                // add new marker
                MarkerDescriptor newMarker = new MarkerDescriptor(
                        newText, latitude, longitude, isDogsitter, isFood, isMedication, currentUser.getId());
                mapState.addMarker(newMarker);
                appDB.updateMarkerDescriptor(newMarker);
            }
            backToMap();
        });

        buttonDeleteMarker.setOnClickListener(v -> {
            if (markerToEdit == null) return;
            mapState.removeMarker(markerToEdit.getId());
            this.appDB.removeMarker(markerToEdit.getId());
            backToMap();
        });

        ImageView buttonCancel = findViewById(R.id.buttonCancelMarker);
        buttonCancel.setOnClickListener(view -> onBackPressed());
    }

    private String getMarkerTitle(boolean isDogsitter, boolean isFood, boolean isMedication) {
        String msg = currentUser.getUserName() + " offers:\n";
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedication) msg += "Extra medication\n";
        String contacts = "";
        if (!currentUser.getEmail().isEmpty())
            contacts += "Email - " + currentUser.getEmail() + "\n";
        if (!currentUser.getPhoneNumber().isEmpty())
            contacts += "Phone - " + currentUser.getPhoneNumber() + "\n";
        if (!contacts.isEmpty()) msg += "In order to contact:\n" + contacts;
        return msg;
    }

    private void backToMap() {
        Intent backToMapIntent = new Intent(this, MapScreenActivity.class);
        backToMapIntent.putExtra("center_to_my_location", false);
        startActivity(backToMapIntent);
        finish();
    }
}
