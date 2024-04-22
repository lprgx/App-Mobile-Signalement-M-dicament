package iut.dam.saemobilefinale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Medicament {
    String CIS;
    String CIP_13;
    String nom;
    public static Medicament getFromJson(String json) throws Exception {
        Gson gson = new Gson();
        Medicament obj = gson.fromJson(json, Medicament.class);
        return obj;
    }
    public Medicament(String CIP_13, String nom){
        this.CIP_13 = CIP_13;
        this.nom = nom;
    }

    public static List<Medicament> getListFromJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Medicament>>(){}.getType();
        List<Medicament> list = gson.fromJson(json, type);
        return list;
    }

    public String getCIS(){
        return CIS;
    }
    public String getCIP_13(){
        return CIP_13;
    }
    public String getNom(){
        return nom;
    }
}
