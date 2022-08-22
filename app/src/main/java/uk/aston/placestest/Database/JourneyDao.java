package uk.aston.placestest.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JourneyDao {

    //Called when a Journey is inserted inside the room db
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Journey journey);



    @Query("DELETE from journey_table")
    void deleteAll();

    //delete journey by id
    @Query("DELETE FROM journey_table WHERE journeyID = :journeyID")
    abstract void deleteByJourneyId(long journeyID);

    @Delete
    void deleteJourney(Journey journey);

    //change from array
    @Query("SELECT * from journey_table LIMIT 1")
    Journey[] getAnyJourney();

    //order table by the date
    @Query("SELECT * from journey_table ORDER BY speed ASC")
    LiveData<List<Journey>> getAllJourneys();

    @Update
    void update(Journey... journey);

}
