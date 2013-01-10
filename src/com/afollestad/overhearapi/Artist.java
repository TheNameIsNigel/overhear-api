package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class Artist {

	private Artist() { }

	private int id;
	private String name;
	private String key;
	private int albumCount;
	private int trackCount;

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getKey() {
		return key;
	}
	public int getAlbumCount() {
		return albumCount;
	}
	public int getTrackCount() {
		return trackCount;
	}

	private static Artist fromCursor(Cursor cursor) {
		Artist album = new Artist();
		album.id = cursor.getInt(cursor.getColumnIndex("_id"));
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST));
		album.key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST_KEY));
		album.albumCount = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS));
		album.trackCount = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS));
		return album;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", this.id);
			json.put("name", this.name);
			json.put("key", this.key);
			json.put("album_count", this.albumCount);
			json.put("track_count", this.trackCount);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Artist fromJSON(JSONObject json) {
		Artist artist = new Artist();
		try {
			artist.id = json.getInt("id");
			artist.name = json.getString("name");
			artist.key = json.getString("key");
			artist.albumCount = json.getInt("album_count");
			artist.trackCount = json.getInt("track_count");
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