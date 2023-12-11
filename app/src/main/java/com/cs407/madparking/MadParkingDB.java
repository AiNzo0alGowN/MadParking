package com.cs407.madparking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.Duration;

import com.cs407.madparking.api.ParkingData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MadParkingDB extends SQLiteOpenHelper {
    private static final List<String> FakeParkingDataNames = Arrays.asList(
            "Blair Lot", " Buckeye Lot", "Capitol Square North Garage",
            "City of Madison State Street Campus Garage", "Engineering Drive Ramp (Lot 17)",
            "Evergreen Lot", "Fluno Center Garage (Lot 83)", "Grainger Hall Garage (Lot 7)",
            "HC White Garage (Lot 6)", "Lake & Johnson Ramp (Lot 46)", "Linden Drive Ramp (Lot 67)",
            "Lot 130", "Lot 20 Ramp", "Nancy Nicholas Garage (Lot 27)",
            "North Park Street Ramp (Lot 29)", "Observatory Drive Ramp (Lot 36)",
            "Overture Center Garage", "South Livingston Street Garage",
            "State Street Campus Garage", "State Street Capitol Garage",
            "UW Hospital Ramp (Lot 75)", "Union South Garage (Lot 80)",
            "University Bay Drive Ramp (Lot 76)", "Wilson Lot", "Wilson Street Garage", "Wingra Lot"
    );

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // 清空数据
        db.close();
    }

    private static final String DATABASE_NAME = "ParkingDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "ParkingData";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_GARAGE_NAME = "garageName";
    private static final String COLUMN_START_TIME = "startTime";
    private static final String COLUMN_END_TIME = "endTime";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_GARAGE_NAME + " TEXT NOT NULL, " +
                    COLUMN_START_TIME + " TEXT NOT NULL, " +
                    COLUMN_END_TIME + " TEXT);";

    public MadParkingDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database version upgrades here
    }

    public void addParkingData(LocalDate date, String garageName, LocalTime startTime, LocalTime endTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date.format(DATE_FORMAT)); // 将LocalDate转换为字符串
        values.put(COLUMN_GARAGE_NAME, garageName);
        values.put(COLUMN_START_TIME, startTime.format(TIME_FORMAT)); // 将LocalTime转换为字符串
        values.put(COLUMN_END_TIME, (endTime != null) ? endTime.format(TIME_FORMAT) : null); // 对于可选的endTime也做相同的处理

        Log.d("MadParkingDB", "addParkingData: " + values.toString());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    private boolean dataExistsForDate(LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_DATE + "=?",
                new String[]{date.format(DATE_FORMAT)},
                null, null, null
        );

        boolean dataExists = cursor.getCount() > 0;
        cursor.close();
        return dataExists;
    }

    private LocalTime randomTime() {
        int hour = ThreadLocalRandom.current().nextInt(0, 3);
        int minute = ThreadLocalRandom.current().nextInt(0, 60);
        return LocalTime.of(hour, minute);
    }

    public void fillDataIfEmpty(LocalDate date) {
        final Duration maxDuration = Duration.ofHours(5);

        if (dataExistsForDate(date) || date.isAfter(LocalDate.now())) {
            // 如果给定日期已经有数据或日期在今天之后，不执行任何操作
            return;
        }

        Random random = new Random();
        Duration totalDuration = Duration.ZERO;
        while (totalDuration.compareTo(maxDuration) < 0) {
            LocalTime startTime = randomTime();
            LocalTime endTime = startTime.plusHours(1).plusMinutes(random.nextInt(60));

            if (endTime.isBefore(startTime) || endTime.equals(LocalTime.MIDNIGHT)) {
                continue;
            }


            Duration parkingDuration = Duration.between(startTime, endTime);
            if (totalDuration.plus(parkingDuration).compareTo(maxDuration) > 0) {
                break;
            }

            addParkingData(date, FakeParkingDataNames.get(random.nextInt(FakeParkingDataNames.size())), startTime, endTime);
            totalDuration = totalDuration.plus(parkingDuration);
        }
    }


    public List<ParkingData> getParkingDataByDate(LocalDate date) {
        fillDataIfEmpty(date); // Should removed in production

        List<ParkingData> parkingDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_GARAGE_NAME, COLUMN_START_TIME, COLUMN_END_TIME},
                COLUMN_DATE + "=?",
                new String[]{date.format(DATE_FORMAT)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                ParkingData data = new ParkingData();
                data.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                data.setDate(LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)), DATE_FORMAT));
                data.setGarageName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GARAGE_NAME)));
                data.setStartTime(LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)), TIME_FORMAT));
                String endTimeString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));
                if (endTimeString != null && !endTimeString.isEmpty()) {
                    data.setEndTime(LocalTime.parse(endTimeString, TIME_FORMAT));
                }
                parkingDataList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return parkingDataList;
    }
}
