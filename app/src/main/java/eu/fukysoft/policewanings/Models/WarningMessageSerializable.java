package eu.fukysoft.policewanings.Models;

import android.widget.ImageView;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Marian on 4.12.2016.
 */
@IgnoreExtraProperties
public class WarningMessageSerializable implements Serializable {
    private String author;
    private String place;
    private String text;
    private double time;
    public int starCount = 0;
    private double latitude;
    private double longtude;
    public HashMap<String, Boolean> stars = new HashMap<>();

    public WarningMessageSerializable(){

    }

    public WarningMessageSerializable(HashMap<String, Object> toMap) {
        this.author = toMap.get("author").toString();
        this.place = toMap.get("place").toString();
        this.text = toMap.get("text").toString();
        this.time = Double.parseDouble(toMap.get("time").toString());
        this.latitude = Double.parseDouble(toMap.get("latitude").toString());
        this.longtude = Double.parseDouble(toMap.get("longtitude").toString());
    }

    public WarningMessageSerializable(String author, String place, String text, double time, double latitude, double longtude) {
        this.author = author;
        this.place = place;
        this.text = text;
        this.time = time;
        this.latitude = latitude;
        this.longtude = longtude;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("place", place);
        result.put("text", text);
        result.put("time", time);
        result.put("latitude", latitude);
        result.put("longtitude", longtude);

        return result;
    }

    public String getAuthor() {
        return author;
    }

    public String getPlace() {
        return place;
    }

    public String getText() {
        return text;
    }

    public double getTime() {
        return time;
    }

    public int getStarCount() {
        return starCount;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtude() {
        return longtude;
    }
}
