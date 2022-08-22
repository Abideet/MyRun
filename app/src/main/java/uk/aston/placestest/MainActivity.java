package uk.aston.placestest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import java.util.HashSet;
import java.util.Set;

import uk.aston.placestest.NearbyParks.MapsActivity;

public class MainActivity extends AppCompatActivity
{

    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.mainActivityLayout);
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.runFragment);

        if (null != drawerLayout)
        {
            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                    .setDrawerLayout(drawerLayout)
                    .build();
        }

    }


    public void onClick(View v)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
