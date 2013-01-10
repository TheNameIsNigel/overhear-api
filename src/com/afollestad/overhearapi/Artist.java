package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class Artist {

	private Artist() { }
	public Artist(String name, String key) { 
		this.name = name;
		this.key = key;
	}

	private String name;
	private String key;

	public String getName() {
		return name;
	}
	public String getKey() {
		return key;
	}

	private static Artist fromCursor(Cursor cursor) {
		Artist album = new Artist();
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST));
		album.key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST_KEY));
		return album;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("name", this.name);
			json.put("key", this.key);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Artist fromJSON(JSONObject json) {
		Artist artist = new Artist();
		try {
			artist.name = json.getString("name");
			artist.key = json.getString("key");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return artist;
	}

	public static List<Artist> getAllArtists(Context context) {
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, 
				null, 
				null, 
				null, 
				MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
		ArrayList<Artist> artists = new ArrayList<Artist>();
		while (cur.moveToNext()) {
			artists.add(Artist.fromCursor(cur));
		}
		cur.close();
		return artists;
	}
	
	public static Artist getArtist(Context context, String name) {
		name = name.replace("'", "''");
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, 
				null, 
				MediaStore.Audio.ArtistColumns.ARTIST + " = '" + name + "'", 
				null, 
				MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
		cur.moveToFirst();
		Artist toreturn = Artist.fromCursor(cur);
		cur.close();
		return toreturn;
	}
}