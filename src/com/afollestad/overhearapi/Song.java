package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Song {

	private Song() { }

	private int id;
	private String displayName;
	private String mimeType;
	private Calendar dateAdded;
	private Calendar dateModified;
	private String title;
	private long duration;
	private int track;
	private String artist;
	private String album;
	private int year;
	private String data;

	public int getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public Calendar getDateAdded() {
		return dateAdded;
	}

	public Calendar getDateModified() {
		return dateModified;
	}

	public String getTitle() {
		return title;
	}

	public long getDuration() {
		return duration;
	}

	public String getDurationString() {
		String minute = TimeUnit.MILLISECONDS.toMinutes(getDuration()) + "";
		String seconds = Long.toString(TimeUnit.MILLISECONDS
				.toSeconds(getDuration())
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(getDuration())));
		if (seconds.length() == 1) {
			seconds = "0" + seconds;
		}
		return minute + ":" + seconds;
	}

	public int getTrack() {
		return track;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public int getYear() {
		return year;
	}

	public String getData() {
		return data;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", this.id);
			json.put("name", this.displayName);
			json.put("mime", this.mimeType);
			json.put("date_added", this.dateAdded.getTimeInMillis());
			json.put("date_modified", this.dateModified.getTimeInMillis());
			json.put("title", this.title);
			json.put("duration", this.duration);
			json.put("track", this.track);
			json.put("artist", this.artist);
			json.put("album", this.album);
			json.put("year", this.year);
			json.put("data", this.data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Song fromJSON(String json) {
		try {
			return fromJSON(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new Error(e.getMessage());
		}
	}
	public static Song fromJSON(JSONObject json) {
		Song song = new Song();
		try {
			song.id = json.getInt("id");
			song.displayName = json.getString("name");
			song.mimeType = json.getString("mime");
			song.dateAdded = Calendar.getInstance();
			song.dateAdded.setTimeInMillis(json.getLong("date_added"));
			song.dateModified = Calendar.getInstance();
			song.dateModified.setTimeInMillis(json.getLong("date_modified"));
			song.title = json.getString("title");
			song.duration = json.getLong("duration");
			song.track = json.getInt("track");
			song.artist = json.getString("artist");
			song.album = json.getString("album");
			song.year = json.getInt("year");
			song.data = json.getString("data");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return song;
	}

	public static Song fromCursor(Cursor cursor) {
		Song album = new Song();
		album.id = cursor.getInt(cursor.getColumnIndex("_id"));
		album.displayName = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		album.mimeType = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
		album.dateAdded = Calendar.getInstance();
		album.dateAdded.setTimeInMillis(cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
		album.dateModified = Calendar.getInstance();
		album.dateModified.setTimeInMillis(cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
		album.title = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.TITLE));
		album.duration = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.Media.DURATION));
		album.track = cursor.getInt(cursor
				.getColumnIndex(MediaStore.Audio.Media.TRACK));
		album.artist = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		album.album = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		album.year = cursor.getInt(cursor
				.getColumnIndex(MediaStore.Audio.Media.YEAR));
		album.data = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.Media.DATA));
		return album;
	}

	public static ArrayList<Song> getAllFromAlbum(Context context, String album, String artist) {
		artist = artist.replace("'", "''");
		album = album.replace("'", "''");
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String where = MediaStore.Audio.Media.IS_MUSIC + " = 1 " +
				"AND " + MediaStore.Audio.Media.ARTIST + " = '" + artist + "' " +
				"AND " + MediaStore.Audio.Media.ALBUM + " = '" + album + "'";
		String sort = MediaStore.Audio.Media.TRACK;
		Cursor cursor = context.getContentResolver().query(uri, null, where, null, sort);
		ArrayList<Song> songs = new ArrayList<Song>();
		while(cursor.moveToNext()) {
			songs.add(Song.fromCursor(cursor));
		}
		cursor.close();
		return songs;
	}

	public static String getCreateTableStatement(String tableName) {
		return "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
				"_id INTEGER PRIMARY KEY," +
				MediaStore.Audio.Media.DISPLAY_NAME +" TEXT," +
				MediaStore.Audio.Media.MIME_TYPE + " TEXT," +
				MediaStore.Audio.Media.DATE_ADDED + " INTEGER," +
				MediaStore.Audio.Media.DATE_MODIFIED + " INTEGER," +
				MediaStore.Audio.Media.TITLE + " TEXT," +
				MediaStore.Audio.Media.DURATION + " INTEGER," +
				MediaStore.Audio.Media.TRACK + " INTEGER," +
				MediaStore.Audio.Media.ARTIST + " TEXT," +
				MediaStore.Audio.Media.ALBUM + " TEXT," +
				MediaStore.Audio.Media.YEAR + " INTEGER," +
				MediaStore.Audio.Media.DATA + " TEXT" +
			");"; 
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues(12);
		values.put("_id", getId()); 
		values.put(MediaStore.Audio.Media.DISPLAY_NAME, getDisplayName()); 
		values.put(MediaStore.Audio.Media.MIME_TYPE, getMimeType());
		values.put(MediaStore.Audio.Media.DATE_ADDED, getDateAdded().getTimeInMillis());
		values.put(MediaStore.Audio.Media.DATE_MODIFIED, getDateModified().getTimeInMillis());
		values.put(MediaStore.Audio.Media.TITLE, getTitle());
		values.put(MediaStore.Audio.Media.DURATION, getDuration());
		values.put(MediaStore.Audio.Media.TRACK, getTrack());
		values.put(MediaStore.Audio.Media.ARTIST, getArtist());
		values.put(MediaStore.Audio.Media.ALBUM, getAlbum());
		values.put(MediaStore.Audio.Media.YEAR, getYear());
		values.put(MediaStore.Audio.Media.DATA, getData());
		return values;
	}
}