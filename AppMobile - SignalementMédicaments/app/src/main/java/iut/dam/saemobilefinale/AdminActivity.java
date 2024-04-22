package iut.dam.saemobilefinale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.CountDownLatch;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id == R.id.statistique)
                    loadListeStatistiqueFragment();
                else if(id == R.id.gestion_Admin)
                    loadGestionAdminFragment();
                else if(id == R.id.LogOut){
                    logOut();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        loadListeStatistiqueFragment();
    }
    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmation_logout_message)
                .setPositiveButton(R.string.logOut, (dialog, id) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.dismiss(); // Ferme la boîte de dialogue
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void loadListeStatistiqueFragment (){
        getSupportFragmentManager().popBackStack();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StatistiqueFragment listeDesHabitatsFragment = new StatistiqueFragment();
        fragmentTransaction.replace(R.id.fragment_container, listeDesHabitatsFragment);
        fragmentTransaction.commit();
    }
    private void loadGestionAdminFragment (){
        getSupportFragmentManager().popBackStack();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GestionAdminFragment gestionAdminFragment = new GestionAdminFragment();
        fragmentTransaction.replace(R.id.fragment_container, gestionAdminFragment);
        fragmentTransaction.commit();
    }
}