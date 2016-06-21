package nl.tudelft.inpoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    public ArrayList<String> TABLES = new ArrayList<>();
    private final String PREFIX_FREQUENCY = "frequency_";
    private final String PREFIX_GAUSSIAN = "gaussian_";
    private final String PREFIX_PMF = "pmf_";
    private final String FILTER_TABLE = "filter";


    public SQLiteHelper(Context context) {
        super(context, context.getExternalFilesDir(null) + File.separator + Globals.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS ";
        for (String table : TABLES)
            db.execSQL(query + table);
        onCreate(db);
    }

    public void createFrequencyTable(String name) {
        name = PREFIX_FREQUENCY + name;
        TABLES.add(name);
        SQLiteDatabase db = getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS " + name + " (room_id INTEGER PRIMARY KEY";
        for (int i = 10; i <= 100; i++)
            query += ", rss" + i + " INTEGER DEFAULT 0";
        query += ");";
        db.execSQL(query);
    }

    public void createFilterTable() {
        TABLES.add(FILTER_TABLE);
        SQLiteDatabase db = getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS " + FILTER_TABLE + " (mac TEXT PRIMARY KEY);";
        db.execSQL(query);
    }

    public void createPMFTable(String name) {
        name = PREFIX_PMF + name;
        TABLES.add(name);
        SQLiteDatabase db = getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS " + name + " (room_id INTEGER PRIMARY KEY";
        for (int i = 10; i <= 100; i++)
            query += ", rss" + i + " REAL DEFAULT 0";
        query += ");";
        db.execSQL(query);
    }

    public void createGaussianTable(String name) {
        name = PREFIX_GAUSSIAN + name;
        TABLES.add(name);
        SQLiteDatabase db = getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS " + name + " (room_id INTEGER PRIMARY KEY, mean REAL DEFAULT 0, standard_deviation REAL DEFAULT 0);";
        db.execSQL(query);
    }

    public void updateRSSFrequencies(String table, int roomID, int[] rss) {
        SQLiteDatabase db = getWritableDatabase();
        String frequencyTable = PREFIX_FREQUENCY + table;
        int[] frequencies = readRSSFrequencies(frequencyTable, roomID);
        ContentValues values = new ContentValues();
        values.put("room_id", roomID);
        for (int i = 10; i <= 100; i++) {
            frequencies[i] += rss[i];
            values.put("rss" + i, frequencies[i]);
        }
        db.delete(frequencyTable, "room_id = " + roomID, null);
        db.insert(frequencyTable, null, values);
        db.close();
        updateRSSGuassian(table, roomID, frequencies);
    }

    public void updateRSSGuassian(String table, int roomID, int[] rss) {
        SQLiteDatabase db = getWritableDatabase();
        table = PREFIX_GAUSSIAN + table;

        float mean = mean(rss);
        float sd = standardDeviation(mean, rss);

        ContentValues values = new ContentValues();
        values.put("room_id", roomID);
        values.put("mean", mean);
        values.put("standard_deviation", sd);
        db.delete(table, "room_id = " + roomID, null);
        db.insert(table, null, values);
        db.close();
    }

    public float mean(int[] rss) {
        float sum = 0;
        float n = 0;
        for (int i = 10; i <= 100; i++) {
            sum += rss[i] * i;
            n += rss[i];
        }
        return sum / n;
    }

    public float standardDeviation(float mean, int[] rss) {
        float sum = 0;
        float n = -1;
        for (int i = 10; i <= 100; i++) {
            sum += rss[i] * Math.pow(i - mean, 2);
            n += rss[i];
        }
        if (n == 0) return 0;
        return (float) Math.sqrt((1 / n) * sum);
    }

    public void updateRSSPMF(String table, int roomID, int[] rss) {
        SQLiteDatabase db = getWritableDatabase();
        table = PREFIX_PMF + table;
        float[] pmf = toPMF(rss);
        ContentValues values = new ContentValues();
        values.put("room_id", roomID);
        for (int i = 10; i <= 100; i++)
            values.put("rss" + i, pmf[i]);
        db.delete(table, "room_id = " + roomID, null);
        db.insert(table, null, values);
        db.close();
    }

    public int[] readRSSFrequencies(String table, int roomID) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.query(table, null, "room_id =" + roomID, null, null, null, null);
            cursor.moveToNext();
            int[] frequencies = new int[101];
            for (int i = 10; i <= 100; i++)
                frequencies[i] = cursor.getInt(cursor.getColumnIndex("rss" + i));
            return frequencies;
        } catch (Exception e) {
            return new int[101];
        }
    }

    public float[] getRSSProbabilities(String table, int rss) {
        SQLiteDatabase db = getReadableDatabase();
        table = PREFIX_GAUSSIAN + table;
        String[] columns = { "room_id", "mean", "standard_deviation" };
        try {
            Cursor cursor = db.query(table, columns, null, null, null, null, null);
            float[] probabilities = new float[Globals.NUMBER_OF_ROOMS + 1];
            while (cursor.moveToNext()) {
                int roomID = cursor.getInt(cursor.getColumnIndex("room_id"));
                float mean = cursor.getFloat(cursor.getColumnIndex("mean"));
                float sd = cursor.getFloat(cursor.getColumnIndex("standard_deviation"));
                probabilities[roomID] = pdf(mean, sd, rss);
            }
            return probabilities;
        } catch (Exception e) {
            return null;
        }
    }

    public float[][] getStatisticalParameters(String table) {
        float[][] result = new float[Globals.NUMBER_OF_ROOMS + 1][2];
        SQLiteDatabase db = getReadableDatabase();
        table = PREFIX_GAUSSIAN + table;
        try {
            Cursor cursor = db.query(table, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                int roomID = cursor.getInt(cursor.getColumnIndex("room_id"));
                float mean = cursor.getFloat(cursor.getColumnIndex("mean"));
                float sd = cursor.getFloat(cursor.getColumnIndex("standard_deviation"));
                result[roomID][0] = mean;
                result[roomID][1] = sd;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private float pdf(float mean, float sd, float x) {
        double a = 1 / (sd * Math.sqrt(2 * Math.PI));
        double b = Math.exp(-1 * Math.pow(x - mean, 2) / (2 * sd * sd));
        float r = (float) (a * b);
        if (x == mean && sd == 0) return 1;
        if (Float.isNaN(r)) return 0;
        return r;
    }

    public float[] toPMF(int[] rss) {
        float[] result = new float[101];
        float sum = 0f;
        for (int i : rss)
            sum += (float) i;
        for (int i = 10; i <= 100; i++)
            result[i] = (float) rss[i] / sum;
        return result;
    }

    public void filterAP() {
        createFilterTable();

        SQLiteDatabase db = getWritableDatabase();
        db.delete(FILTER_TABLE, null, null);

        List<String> macs = getAccessPoints();
        Log.i("Amount of MACs: ", macs.size() + "");
        for (String mac : macs) {
            if (isSignificant(mac)) {
                ContentValues values = new ContentValues();
                values.put("mac", mac);
                db.insert(FILTER_TABLE, null, values);
            }
        }
        db.close();
    }

    private boolean isSignificant(String mac) {
        float[][] params = getStatisticalParameters(mac);
        for (int i = 1; i <= Globals.NUMBER_OF_ROOMS; i++) {
            float mean1 = params[i][0];
            float sd1 = params[i][1];
            if (!(mean1 == 0 && sd1 == 0)) {
                for (int j = i + 1; j <= Globals.NUMBER_OF_ROOMS; j++) {
                    float mean2 = params[j][0];
                    if (mean2 != 0 && pdf(mean1, sd1, mean2) <= Globals.CONFIDENCE_INTERVAL) {
                        Log.i(mac, ", " + pdf(mean1, sd1, mean2) + ", mean1: " + mean1 + ", mean2: " + mean2);
                        return true;
                    } else if (mean2 != 0) {
                        Log.i(mac, ", " + pdf(mean1, sd1, mean2) + ", mean1: " + mean1 + ", mean2: " + mean2 + ", INSIGNIFICANT");
                    }
                }
            }
        }
        return false;
    }

    private List<String> getAccessPoints() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tableNames = new ArrayList<>();
        while (c.moveToNext()) {
            tableNames.add(c.getString( c.getColumnIndex("name")));
        }
        List<String> tables = new ArrayList<>();
        for (String t : tableNames) {
            if (t.contains(PREFIX_GAUSSIAN))
                tables.add(t.replace(PREFIX_GAUSSIAN, ""));
        }
        return tables;
    }

    public List<String> getSignificantAccessPoints() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(FILTER_TABLE, null, null, null, null, null, null);
        List<String> macs = new ArrayList<>();
        while (c.moveToNext()) {
            macs.add(c.getString( c.getColumnIndex("mac")));
        }
        return macs;
    }

    public static String encodeMAC(String mac) {
        return mac.replace(":", "");
    }

}
