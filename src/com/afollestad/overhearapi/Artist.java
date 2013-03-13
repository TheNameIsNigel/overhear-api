package com.afollestad.overhearapi;

import org.json.JSONException;
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

	private long _id;
	private String name;
	private String key;
	private int numAlbums;
	private int numTracks;

	public long getId() {
		return _id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getKey() {
		return key;
	}
	
	public int getAlbumCount() {
		return numAlbums;
	}
	
	public int getTrackCount() {
		return numTracks;
	}

	public static Artist fromCursor(Cursor cursor) {
		Artist album = new Artist();
		album._id = cursor.getLong(cursor.getColumnIndex("_id"));
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST));
		album.key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST_KEY));
		album.numAlbums = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS));
		album.numTracks = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS));
		return album;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("_id", this._id);
			json.put(MediaStore.Audio.ArtistColumns.ARTIST, this.name);
			json.put(MediaStore.Audio.ArtistColumns.ARTIST_KEY, this.key);
			json.put(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS, this.numAlbums);
			json.put(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS, this.numTracks);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Artist fromJSON(String json) {
		try {
			return fromJSON(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Artist fromJSON(JSONObject json) {
		Artist artist = new Artist();
		try {
			artist._id = json.getLong("_id");
			artist.name = json.getString(MediaStore.Audio.ArtistColumns.ARTIST);
			artist.key = json.getString(MediaStore.Audio.ArtistColumns.ARTIST_KEY);
			artist.numAlbums = json.getInt(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS);
			artist.numTracks = json.getInt(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return artist;
	}

	public static Artist getArtist(Context context, String name) {
		name = name.replace("'", "''");
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, 
				null, 
				MediaStore.Audio.ArtistColumns.ARTIST + " = '" + name + "'", 
				null, 
				MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
		if(!cur.moveToFirst())
            return null;
		Artist toreturn = Artist.fromCursor(cur);
		cur.close();
		return toreturn;
	}
}