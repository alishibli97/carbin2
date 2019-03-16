package com.example.carbin2;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public int frequency = 50;
    public int speed = 0;
    public int distance = 0;
    public int fuel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openFragment(new frag_maps());

        BottomNavigationView view = findViewById(R.id.nav);
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_map:
                        //openFragment(frag_maps.newInstance("hi1","hi2"));
                        openFragment(new frag_maps());
                        break;
                    case R.id.action_downloads:
                        openFragment(new frag_downloads());
                        break;
                    case R.id.action_settings:
                        openFragment(new frag_settings());
                        break;
                }

                return true;
            }
        });

    }

    private void openFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.commit();
        transaction.replace(R.id.content, fragment);
    }

    private void toast(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

}
