package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class Genre {

	private Genre() { }

	private int id;
	private String name;

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public static Genre fromCursor(Cursor cursor) {
		Genre genre = new Genre();
		genre.id = cursor.getInt(cursor.getColumnIndex("_id"));
		genre.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.GenresColumns.NAME));
		if(genre.name == null || genre.name.trim().isEmpty()) {
			return null;
		}
		return genre;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", this.id);
			json.put("name", this.name);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Genre fromJSON(JSONObject json) {
		Genre genre = new Genre();
		try {
			genre.id = json.getInt("id");
			genre.name = json.getString("name");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return genre;
	}

	public static List<Genre> getAllGenres(Context context) {
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, 
				null, 
				null, 
				null, 
				MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);
		ArrayList<Genre> genres = new ArrayList<Genre>();
		while (cur.moveToNext()) {
			Genre toAdd = Genre.fromCursor(cur);
			if(toAdd != null)
				genres.add(toAdd);
		}
		return genres;
	}
	
}
