package uk.aston.placestest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import uk.aston.placestest.Database.Journey;
import uk.aston.placestest.Database.JourneyListAdapter;
import uk.aston.placestest.Database.JourneyViewModel;
import uk.aston.placestest.Database.MyRoomDatabase;

public class ViewJourneyActivity extends AppCompatActivity
{
    private View view;
    private JourneyViewModel mJourneyViewModel;

    private EditText journeyName;

    Journey journey;

    JourneyListAdapter adapter;// = new JourneyListAdapter(getContext());
    private List<Journey> mJourneys;

    public static final int UPDATE_NAME_ACTIVITY_REQUEST_CODE = 2;

    public static final String EXTRA_DATA_UPDATE_NAME = "extra_name_to_be_updated";
    public static final String EXTRA_DATA_ID = "extra_data_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_journey);
        mJourneys = new ArrayList<>();




        //View saveView = View.inflate(R.layout.fragment_save_name, , );
        View inflatedView = getLayoutInflater().inflate(R.layout.fragment_save_name, null);
        journeyName = inflatedView.findViewById(R.id.editTexRunName);


        //journeyName = findViewById(R.id.);

        //Set up recylerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new JourneyListAdapter(mJourneys, this);
        //final JourneyListAdapter adapter = new JourneyListAdapter(view.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //setting up the recycler view so it takes the user to their run
        recyclerView.setClickable(true);



        for(int i = 0; i < recyclerView.getChildCount(); i++)
        {

            mJourneys.add(adapter.getJourneyAtPosition(i));


        }

        // Functionality to swipe items in the
        // recycler view to delete that item
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
                {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target)
                    {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction)
                    {
                        int position = viewHolder.getAdapterPosition();
                        Journey myJourney = adapter.getJourneyAtPosition(position);


                        // Delete the word
                        mJourneyViewModel.deleteJourney(myJourney);
                    }
                });
        helper.attachToRecyclerView(recyclerView);

        //Functionality to update name
        adapter.setOnItemClickListener(new JourneyListAdapter.ClickListener()
        {
            @Override
            public void onItemClick(View v, int position)
            {
                Log.d("clicked", "journey clicked");

                journey = adapter.getJourneyAtPosition(position);

                launchUpdateNameActivity(journey);
            }
        });

        //Code for search functionality
        EditText searchEdit = findViewById(R.id.searchText);

        searchEdit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                //filters the content of our edit text
                filter(s.toString());
            }

        });


        // Set up the JourneyViewModel.
        mJourneyViewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);
        // Get all the journeys from the database
        // and associate them to the adapter.
        //TODO - Update with lambda later
        mJourneyViewModel.getAllJourneys().observe(this, new Observer<List<Journey>>()
        {
            @Override
            public void onChanged(@Nullable final List<Journey> journeys)
            {
                // Update the cached copy of the journeys in the adapter.
                adapter.setmJourneysList(journeys);
            }
        });

        //Database retrieval
        final MyRoomDatabase db = MyRoomDatabase.getDatabase(this);
        JourneyViewModel mJourneyViewModel;

        BottomNavigationView navView = findViewById(R.id.nav);



        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {


                    case R.id.savedFragment:

                        startActivity(new Intent(getApplicationContext(), ViewJourneyActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeFragment:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });


    }

    //filter method which is called everytime something is entered in edit text
    private void filter(String input)
    {
        ArrayList<Journey> filteredList = new ArrayList<>();

//        Journey journey = new Journey();
//        journey.setmName("ss");
//        mJourneys.add(journey);

        //TODO: Change var name
        for (Journey item : mJourneys)
        {
            //Log.d("name", item.getmName());
            //turn all characters to lower case so that the condition is not case sensitive

            if (item.getmName().toLowerCase().contains(input.toLowerCase()))
            {
                //Nothing getting added to journey
                filteredList.add(item);
            }
        }


        Log.d("size", String.valueOf(filteredList.size()));

        adapter.filterList(filteredList);
    }



    //Click mechanism for each recycler view journey element
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_NAME_ACTIVITY_REQUEST_CODE
                    && resultCode == RESULT_OK) {

            String word_data = data.getStringExtra(UpdateJourneyActivity.EXTRA_REPLY);

            int id = data.getIntExtra(UpdateJourneyActivity.EXTRA_REPLY_ID, -1);

            //update name of the clicked journey
            if (id != -1)
            {
                    journey.setmName(word_data);
                    //update the new journey
                    mJourneyViewModel.update(journey);
            }

        }

    }

    public void launchUpdateNameActivity(Journey journey)
    {
        Intent intent = new Intent(this, UpdateJourneyActivity.class);
        intent.putExtra(EXTRA_DATA_UPDATE_NAME, journey.getmName());
        intent.putExtra(EXTRA_DATA_ID, journey.getJourneyID());
        startActivityForResult(intent, UPDATE_NAME_ACTIVITY_REQUEST_CODE);
    }
}
