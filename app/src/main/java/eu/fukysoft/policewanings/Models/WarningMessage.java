package eu.fukysoft.policewanings.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marian on 4.12.2016.
 */

public class WarningMessage {
    private String author;
    private String place;
    private String text;
    private double time;
    private double latitude;
    private double longtude;

    public WarningMessage(String author, String place, String text, double time, double latitude, double longtude) {
        this.author = author;
        this.place = place;
        this.text = text;
        this.time = time;
        this.latitude = latitude;
        this.longtude = longtude;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("place", place);
        result.put("text", text);
        result.put("time",time);
        result.put("latitude", latitude);
        result.put("longtitude", longtude);

        return result;
    }
}
