package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
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
		String seconds = Long.toString(TimeUnit.MILLISECONDS.toSeconds(getDuration()) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDuration()))); 
		if(seconds.length() == 1) {
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		return song;
	}
	
	private static Song fromCursor(Cursor cursor) {
		Song album = new Song();
		album.id = cursor.getInt(cursor.getColumnIndex("_id"));
		album.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
		album.mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
		album.dateAdded = Calendar.getInstance();
		album.dateAdded.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
		album.dateModified = Calendar.getInstance();
		album.dateModified.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
		album.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		album.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
		album.track = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
		album.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		album.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		album.year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
		return album;
	}

	public static List<Song> getAllSongs(Context context, String album) {
		String where = null;
		String sortOrder = MediaStore.Audio.Media.TITLE;
		if(album != null) {
			where = MediaStore.Audio.Media.ALBUM + " = '" + album + "'";
			sortOrder = MediaStore.Audio.Media.TRACK;
		}
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
				null, 
				where, 
				null, 
				sortOrder);
		ArrayList<Song> songs = new ArrayList<Song>();
		while (cur.moveToNext()) {
			songs.add(Song.fromCursor(cur));
		}
		return songs;
	}
}