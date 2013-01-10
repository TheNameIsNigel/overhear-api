package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class Album {

	private Album() { }

	private int albumId;
	private String name;
	private Artist artist;
	private String albumKey;
	private Calendar minYear;
	private Calendar maxYear;
	private int numSongs;

	public int getAlbumId() {
		return albumId;
	}
	public String getName() {
		return name;
	}
	public Artist getArtist(Context context) {
		return artist;
	}
	public String getAlbumKey() {
		return albumKey;
	}
	public Calendar getFirstYear() {
		return minYear;
	}
	public Calendar getLastYear() {
		return maxYear;
	}
	public int getSongCount() {
		return numSongs;
	}
	public Bitmap getAlbumArt(Context context, float widthDp, float heightDp) {
		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		Uri uri = ContentUris.withAppendedId(sArtworkUri, getAlbumId());
		return Utils.loadImage(context, uri, widthDp, heightDp);
	}

	private static Album fromCursor(Context context, Cursor cursor) {
		Album album = new Album();
		album.albumId = cursor.getInt(cursor.getColumnIndex("_id"));
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
		album.artist = Artist.getArtist(context, cursor.getString(
				cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))); 
		album.albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_KEY));
		album.minYear = Calendar.getInstance();
		album.minYear.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR)));
		album.maxYear = Calendar.getInstance();
		album.maxYear.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR)));
		album.numSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS));
		return album;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", this.albumId);
			json.put("name", this.name);
			json.put("artist", this.artist);
			json.put("key", this.albumKey);
			json.put("min_year", this.minYear.getTimeInMillis());
			json.put("max_year", this.maxYear.getTimeInMillis());
			json.put("num_songs", this.numSongs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Album fromJSON(Context context, JSONObject json) {
		Album album = new Album();
		try {
			album.albumId = json.getInt("id");
			album.name = json.getString("name");
			album.artist = Artist.getArtist(context, json.getString("artist"));
			album.albumKey = json.getString("key");
			album.minYear = Calendar.getInstance();
			album.minYear.setTimeInMillis(json.getLong("min_year"));
			album.maxYear = Calendar.getInstance();
			album.maxYear.setTimeInMillis(json.getLong("max_year"));
			album.numSongs = json.getInt("num_songs");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return album;
	}
	
	public static List<Album> getAllAlbums(Context context) {
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				null, 
				null, 
				null, 
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		ArrayList<Album> albums = new ArrayList<Album>();
		while (cur.moveToNext()) {
			albums.add(Album.fromCursor(context, cur));
		}
		cur.close();
		return albums;
	}
	
	public static List<Album> getAlbumsForArtist(Context context, String artist) {
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				null, 
				MediaStore.Audio.AlbumColumns.ARTIST + " = '" + artist.replace("'", "''") + "'", 
				null, 
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		ArrayList<Album> albums = new ArrayList<Album>();
		while (cur.moveToNext()) {
			albums.add(Album.fromCursor(context, cur));
		}
		cur.close();
		return albums;
	}
}