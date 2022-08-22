package uk.aston.placestest.Database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;


public class JourneyTypeConverters
{

    @TypeConverter
    public static List<BigInteger> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<BigInteger>>() {}.getType();

        return new Gson().fromJson(data, listType);
    }



    @TypeConverter
    public static String BigIntegerListToString(List<BigInteger> bigIntObject) {
        Gson gson = new Gson();
        return gson.toJson(bigIntObject);
    }

}
