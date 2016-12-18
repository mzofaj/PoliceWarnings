package eu.fukysoft.policewanings.Models;

import java.util.ArrayList;

/**
 * Created by Marian on 11.12.2016.
 */
public class JsonDataConfig
{
    private ArrayList<Country> country;

    public ArrayList<Country> getCountry() { return this.country; }

    public void setCountry(ArrayList<Country> country) { this.country = country; }
public class District
{
    private String display;

    public String getDisplay() { return this.display; }

    public void setDisplay(String display) { this.display = display; }

    private ArrayList<String> places;

    public ArrayList<String> getPlaces() { return this.places; }

    public void setPlaces(ArrayList<String> places) { this.places = places; }
}

public class Country
{
    private String idindex;

    public String getIdindex() { return this. idindex; }

    public void setIdindex(String  idindex) { this. idindex =  idindex; }
    private String display;

    public String getDisplay() { return this.display; }

    public void setDisplay(String display) { this.display = display; }

    private ArrayList<District> district;

    public ArrayList<District> getDistrict() { return this.district; }

    public void setDistrict(ArrayList<District> district) { this.district = district; }
}


}