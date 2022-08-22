package uk.aston.placestest.Database;

import androidx.room.TypeConverter;

import java.math.BigInteger;
import java.sql.Date;

//This class allows Room to save complex data inside the SQLite database
public class Converters {

    @TypeConverter
    public static BigInteger fromBigInt(Integer value){
        return value == null ? null : new BigInteger(String.valueOf(value));
    }

    @TypeConverter
    public static Integer bigIntToInteger(BigInteger value){
        return value == null ? null : Integer.valueOf(String.valueOf(value));
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
