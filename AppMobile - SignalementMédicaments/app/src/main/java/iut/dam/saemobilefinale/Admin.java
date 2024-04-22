package iut.dam.saemobilefinale;

import com.google.gson.Gson;

import org.json.JSONException;

public class Admin {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String designation;

    public static Admin getFromJson(String json) throws Exception {
        Gson gson = new Gson();
        Admin obj = gson.fromJson(json, Admin.class);
        return obj;
    }
    public int getId(){return id;}
}
