package iut.dam.saemobilefinale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Signalement {
    private String CODE_CIS;
    private String Code_CIP;

    private String date_signalement;

    public static Signalement getFromJson(String json) throws Exception {
        Gson gson = new Gson();
        Signalement obj = gson.fromJson(json, Signalement.class);
        return obj;
    }
    public static List<Signalement> getListFromJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Signalement>>(){}.getType();
        List<Signalement> list = gson.fromJson(json, type);
        return list;
    }

    public Signalement(String cip, String date){
        this.Code_CIP = cip;
        this.date_signalement = date;
    }
    public String getCIP(){
        return Code_CIP;
    }
    public String getDate(){return date_signalement;}

    public String getCODE_CIS(){
        return CODE_CIS;
    }
    public static void sortSignalementsByDateDescending(List<Signalement> signalements) {
        Collections.sort(signalements, new Comparator<Signalement>() {
            @Override
            public int compare(Signalement s1, Signalement s2) {
                // Parse les dates et compare en utilisant compareTo, mais dans l'ordre inverse
                return s2.getDate().compareTo(s1.getDate());
            }
        });
    }
}
