package com.afollestad.overhearapi;

import java.util.Calendar;

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
	public Artist getArtist() {
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

	public static Album fromCursor(Context context, Cursor cursor) {
		Album album = new Album();
		album.albumId = cursor.getInt(cursor.getColumnIndex("_id"));
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
		album.artist = new Artist(
				cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)),
				cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST_KEY))); 
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
			json.put("artist", this.artist.getJSON().toString());
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
			album.artist = Artist.fromJSON(new JSONObject(json.getString("artist")));
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

	public static Album getAlbum(Context context, String name) {
		name = name.replace("'", "''");
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				null, 
				MediaStore.Audio.AlbumColumns.ALBUM + " = '" + name + "'", 
				null, 
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		Album toreturn = null;
		if(cur.moveToFirst()) {
			toreturn = Album.fromCursor(context, cur); 
		}
		cur.close();
		return toreturn;
	}
}