package iut.dam.saemobilefinale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Pathologie {
    private String pathologie;
    private int nb_signalements;

    public static List<Pathologie> getListFromJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Pathologie>>(){}.getType();
        List<Pathologie> list = gson.fromJson(json, type);
        return list;
    }

    public String getPathologie() {
        return pathologie;
    }

    public int getNb_signalements() {
        return nb_signalements;
    }
}
