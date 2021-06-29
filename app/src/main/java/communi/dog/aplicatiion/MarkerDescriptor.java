package communi.dog.aplicatiion;

import java.io.Serializable;

/**
 * class that stores the information of a marker
 */
public class MarkerDescriptor implements Serializable {
    private String text;
    private double latitude;
    private double longitude;
    private boolean dogsitter;
    private boolean food;
    private boolean medication;
    private String id; //final

    public MarkerDescriptor(String text, double latitude, double longitude, boolean isDogsitter, boolean isFood, boolean isMedication, String creatorUserId) {
        this.text = text;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dogsitter = isDogsitter;
        this.food = isFood;
        this.medication = isMedication;
        this.id = creatorUserId;
    }

    // empty constructor for FireBase redundant
    public MarkerDescriptor() {
    }

    public void setNewLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setServices(boolean isDogsitter, boolean isFood, boolean isMedication) {
        this.dogsitter = isDogsitter;
        this.food = isFood;
        this.medication = isMedication;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDogsitter() {
        return dogsitter;
    }

    public boolean isFood() {
        return food;
    }

    public boolean isMedication() {
        return medication;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        User user = CommuniDogApp.getInstance().getDb().getUser(id);
        if (user != null) {
            text = generateText(user);
        }

        return text;
    }

    private String generateText(User user) {
        String msg = user.getUserName() + " offers:\n";
        if (dogsitter) msg += "Dogsitter services\n";
        if (food) msg += "Extra food\n";
        if (medication) msg += "Extra medication\n";
        String contacts = "";
        if (!user.getEmail().isEmpty())
            contacts += "Email - " + user.getEmail() + "\n";
        if (!user.getPhoneNumber().isEmpty())
            contacts += "Phone - " + user.getPhoneNumber() + "\n";
        if (!contacts.isEmpty()) msg += "In order to contact:\n" + contacts;
        return msg;
    }
}
