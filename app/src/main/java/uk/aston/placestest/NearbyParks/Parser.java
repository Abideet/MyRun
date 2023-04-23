package uk.aston.placestest.NearbyParks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser
{



    public String lat = "";
    String lng = "";



    String latGlobal;
    String lngGlobal;




    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String, String> map = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";

        String ref = "";

        try
        {
            if (!googlePlaceJSON.isNull("name"))
            {
                placeName = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity"))
            {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            lat = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            lng = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            ref = googlePlaceJSON.getString("reference");

            //Log.e("latitude", getLat());
            Log.e("longitude", lng);


            map.put("place_name", placeName);
            map.put("vicinity", vicinity);
            map.put("lat", lat);
            map.put("lng", lng);
            map.put("reference", ref);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return map;
    }


    public List<HashMap<String, String>> parse(String jSONdata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
    {
        List<HashMap<String, String>> NearbyPlacesList = new ArrayList<>();

        int c = jsonArray.length();

        HashMap<String, String> Nearby = null;

        for (int i=0; i<c; i++)
        {
            try
            {
                Nearby = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                NearbyPlacesList.add(Nearby);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;
    }




    public String getLng()
    {

        return lngGlobal;
    }

    public String getLat()
    {
        return latGlobal;
    }





}
