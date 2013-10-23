package com.afollestad.overhearapi;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Genre {

    private int id;
    private String name;

    private Genre() {
    }

    public static Genre fromCursor(Cursor cursor) {
        Genre genre = new Genre();
        genre.id = cursor.getInt(cursor.getColumnIndex("_id"));
        genre.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.GenresColumns.NAME));
        if (genre.name == null || genre.name.trim().isEmpty()) {
            genre.name = "Unknown";
        }
        return genre;
    }

    public static Genre fromJSON(String json) {
        try {
            return fromJSON(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
    }

    private static Genre fromJSON(JSONObject json) {
        Genre genre = new Genre();
        try {
            genre.id = json.getInt("id");
            genre.name = json.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genre;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("name", this.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public ArrayList<Integer> getSongs(Context context) {
        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", getId());
        Cursor cur = context.getContentResolver().query(
                uri,
                new String[]{"_id"},
                null,
                null,
                null);
        ArrayList<Integer> ids = new ArrayList<Integer>();
        while (cur.moveToNext()) {
            ids.add(cur.getInt(0));
        }
        return ids;
    }
}
