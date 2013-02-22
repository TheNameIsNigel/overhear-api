package com.afollestad.overhearapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Song {

	private Song() { }

    public final static String QUEUE_ID = "queue_id";
	public final static String NOW_PLAYING = "is_playing";
	public final static String QUEUE_FOCUS = "has_focus";
	
	private int id;
    private int queueId = -1;
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
	private boolean isPlaying;
	private boolean hasFocus;
    private long fromPlaylist;

 	public int getId() {
		return id;
	}

    public int getQueueId() {
        return queueId;
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
		return getDurationString(getDuration());
	}
	
	public static String getDurationString(long milliseconds) {
		if(milliseconds == 0) {
			return "0:00";
		} else if((milliseconds % 1000) == 0) {
			return (milliseconds / 1000) + ":00";
		}
		String minute = TimeUnit.MILLISECONDS.toMinutes(milliseconds) + "";
		String seconds = Long.toString(TimeUnit.MILLISECONDS
				.toSeconds(milliseconds)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(milliseconds)));
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

    public long getFromPlaylist() {
        return fromPlaylist;
    }

    public Song setFromPlaylist(long id) {
        fromPlaylist = id;
        return this;
    }

	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void setIsPlaying(boolean playing) {
		isPlaying = playing;
	}

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }
	
	public boolean hasFocus() {
		return hasFocus;
	}
	
	public void setHasFocus(boolean focus) {
		hasFocus = focus;
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
			if(queueId > -1) {
                json.put(NOW_PLAYING, this.isPlaying);
                json.put(QUEUE_FOCUS, this.hasFocus);
			}
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

            if(json.has(QUEUE_ID)) {
                song.queueId = json.getInt(QUEUE_ID);
			    song.isPlaying = json.getBoolean(NOW_PLAYING);
			    song.hasFocus = json.getBoolean(QUEUE_FOCUS);
            }
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

        int queueIdIndex = cursor.getColumnIndex(QUEUE_ID);
        if(queueIdIndex > -1) {
            album.queueId = cursor.getInt(queueIdIndex);
            album.isPlaying = (cursor.getInt(cursor.getColumnIndex(NOW_PLAYING)) == 1);
            album.hasFocus = (cursor.getInt(cursor.getColumnIndex(QUEUE_FOCUS)) == 1);
        }
		
		return album;
	}

	public static ArrayList<Song> getAllFromScope(Context context, String[] scope) {
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		return getAllFromUri(context, uri, scope[0], scope[1]);
	}

    public static ArrayList<Song> getAllFromUri(Context context, Uri uri, String where, String sort) {
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
				QUEUE_ID + " INTEGER PRIMARY KEY," +
				"_id INTEGER," +
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
				MediaStore.Audio.Media.DATA + " TEXT," +
				NOW_PLAYING + " INTEGER," +
				QUEUE_FOCUS + " INTEGER" +
			");"; 
	}
	
	public ContentValues getContentValues(boolean forQueue) {
		ContentValues values = new ContentValues();
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
		
		if(forQueue) {
			values.put(QUEUE_ID, getQueueId());
			values.put(NOW_PLAYING, isPlaying() ? 1 : 0);
			values.put(QUEUE_FOCUS, hasFocus() ? 1 : 0);
		}
		
		return values;
	}
}