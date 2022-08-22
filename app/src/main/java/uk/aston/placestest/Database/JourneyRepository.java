package uk.aston.placestest.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class JourneyRepository {

    private JourneyDao mJourneyDao;
    private LiveData<List<Journey>> mAllJourneys;

    JourneyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mJourneyDao = db.journeyDao();
        mAllJourneys = mJourneyDao.getAllJourneys();
    }

    LiveData<List<Journey>> getmAllJourneys() {
        return mAllJourneys;
    }

    public void insert(Journey journey) {
        new insertAsyncTask(mJourneyDao).execute(journey);
    }

    public void deleteAll() {
        new deleteAllJourneysAsyncTask(mJourneyDao).execute();
    }

    public void deleteJourney(Journey journey) {
        new deleteJourneyAsyncTask(mJourneyDao).execute(journey);
    }

    public void update(Journey journey)
    {
        new updateJourneyAsyncTask(mJourneyDao).execute(journey);
    }



    //inner class to insert journey without blocking the UI thread
    private static class insertAsyncTask extends AsyncTask<Journey, Void, Void> {
        private JourneyDao mAsyncTaskDao;

        insertAsyncTask(JourneyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Journey... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    //inner class to delete journey without blocking the UI thread
    private static class deleteAllJourneysAsyncTask extends AsyncTask<Void, Void, Void> {
        private JourneyDao mAsyncTaskDao;

        deleteAllJourneysAsyncTask(JourneyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }


    private static class deleteJourneyAsyncTask extends AsyncTask<Journey, Void, Void> {
        private JourneyDao mAsyncTaskDao;

        deleteJourneyAsyncTask(JourneyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Journey... params) {
            mAsyncTaskDao.deleteJourney(params[0]);
            return null;
        }
    }


    private static class updateJourneyAsyncTask extends AsyncTask<Journey, Void, Void>
    {
        private JourneyDao mAsyncTaskDao;

        updateJourneyAsyncTask(JourneyDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Journey... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }


}


