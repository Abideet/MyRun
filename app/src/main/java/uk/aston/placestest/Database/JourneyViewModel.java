package uk.aston.placestest.Database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

//provides data to the UI
public class JourneyViewModel extends AndroidViewModel
{
    private JourneyRepository mRepository;

    private LiveData<List<Journey>> mAllJourneys;

    public JourneyViewModel (Application application)
    {
        super(application);
        mRepository = new JourneyRepository(application);
        mAllJourneys = mRepository.getmAllJourneys();
    }

    public LiveData<List<Journey>> getAllJourneys() { return mAllJourneys; }

    //wrapper insert method hides the repositories insert method from the UI
    public void insert(Journey journey) { mRepository.insert(journey); }

    public void deleteAll() {mRepository.deleteAll();}

    public void deleteJourney(Journey journey){ mRepository.deleteJourney(journey); }

    public void update(Journey journey)
    {
        mRepository.update(journey);
    }

}
