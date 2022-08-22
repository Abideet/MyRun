package uk.aston.placestest.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigInteger;


//Used to get and set values that correspond to table columns
@Entity(tableName = "journey_table")
public class Journey {

    //
    @PrimaryKey(autoGenerate = true)
    private int journeyID;



    //Later, get the user to enter their name and save it in the database after pressing the save button
    //TODO: Ask tony why I cant leave this as private
    @NonNull
    @ColumnInfo(name = "name")
    public String mName;

    @NonNull
    @ColumnInfo(name = "duration")
    public BigInteger mDuration;

    @NonNull
    @ColumnInfo(name = "distance")
    public double mDistance;

    @NonNull
    @ColumnInfo(name = "speed")
    public Double mSpeed;


    public Journey()
    {

    }

    public Journey(@NonNull String name, @NonNull BigInteger duration, @NonNull double distance, @NonNull Double speed)
    {
        this.mName = name;
        this.mDuration = duration;
        this.mDistance = distance;
        this.mSpeed = speed;
    }
    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */

    @Ignore
    public Journey(int id,@NonNull String name , @NonNull BigInteger duration, @NonNull float distance, @NonNull Double speed)
    {
        this.journeyID = id;
        this.mDuration = duration;
        this.mDistance = distance;
        this.mSpeed = speed;
    }

    public int getJourneyID() {
        return journeyID;
    }

    @NonNull
    public BigInteger getMduration() {
        return mDuration;
    }

    public double getMdistance() {
        return mDistance;
    }

    @NonNull
    public Double getMSpeed() {
        return mSpeed;
    }

    public int getLocationID() {return journeyID;}


    public void setJourneyID(int id)
    {
        this.journeyID = id;
    }

    public void setMSpeed(@NonNull Double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public void setMdistance(double mdistance) {
        this.mDistance = mdistance;
    }

    public void setMduration(@NonNull BigInteger mduration) {
        this.mDuration = mduration;
    }

    @NonNull
    public String getmName()
    {
        return mName;
    }

    public void setmName(@NonNull String mName) {
        this.mName = mName;
    }

}
