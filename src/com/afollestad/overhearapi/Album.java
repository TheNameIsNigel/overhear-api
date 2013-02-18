package com.afollestad.overhearapi;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import org.json.JSONObject;

public class Album {

	private Album() { }

	private int albumId;
	private String name;
	private Artist artist;
	private String albumKey;
	private String minYear;
	private String maxYear;
	private int numSongs;
	private int queueId = -1;

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
	public String getFirstYear() {
		return minYear;
	}
	public String getLastYear() {
		return maxYear;
	}
	public int getSongCount() {
		return numSongs;
	}
	public Uri getAlbumArtUri(Context context) {
		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		return ContentUris.withAppendedId(sArtworkUri, getAlbumId());
	}
	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}
	public int getQueueId() {
		return queueId;
	}
	
	public static Album fromCursor(Context context, Cursor cursor) {
		Album album = new Album();
		album.albumId = cursor.getInt(cursor.getColumnIndex("_id"));
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
		album.artist = new Artist(
				cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)),
				cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST_KEY))); 
		album.albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_KEY));
		album.minYear = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR));
		album.maxYear = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.LAST_YEAR));		
		album.numSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS));
		
		int queueIdIndex = cursor.getColumnIndex(Song.QUEUE_ID);
		if(queueIdIndex > -1) {
			album.queueId = cursor.getInt(queueIdIndex);
		}
		
		return album;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", this.albumId);
			json.put("name", this.name);
			json.put("artist", this.artist.getJSON().toString());
			json.put("key", this.albumKey);
			json.put("min_year", this.minYear);
			json.put("max_year", this.maxYear);
			json.put("num_songs", this.numSongs);
            if(this.queueId > -1)
			    json.put(Song.QUEUE_ID, this.queueId);
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
			album.minYear = json.getString("min_year");
			album.maxYear = json.getString("max_year");
			album.numSongs = json.getInt("num_songs");
            if(json.has(Song.QUEUE_ID)) {
			    album.queueId = json.getInt(Song.QUEUE_ID);
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
		return album;
	}

	public static Album getAlbum(Context context, String name, String artist) {
		name = name.replace("'", "''");
		artist = artist.replace("'", "''");
		Cursor cur = context.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				null, 
				MediaStore.Audio.AlbumColumns.ALBUM + " = '" + name + "' AND " +
						MediaStore.Audio.AlbumColumns.ARTIST + " = '" + artist + "'", 
				null, 
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		Album toreturn = null;
		if(cur.moveToFirst()) {
			toreturn = Album.fromCursor(context, cur); 
		}
		cur.close();
		return toreturn;
	}
	
	public static String getCreateTableStatement(String tableName) {
		return "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
				Song.QUEUE_ID + " INTEGER PRIMARY KEY," +
				"_id INTEGER," +
				MediaStore.Audio.AlbumColumns.ALBUM_KEY +" TEXT," +
				MediaStore.Audio.ArtistColumns.ARTIST_KEY +" TEXT," +
				MediaStore.Audio.AlbumColumns.ALBUM + " TEXT," +
				MediaStore.Audio.AlbumColumns.ARTIST + " TEXT," +
				MediaStore.Audio.AlbumColumns.FIRST_YEAR + " TEXT," +
				MediaStore.Audio.AlbumColumns.LAST_YEAR + " TEXT," +
				MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS + " INTEGER" +
			");";
	}
	
	public ContentValues getContentValues(boolean forRecents) {
		ContentValues values = new ContentValues();
		values.put("_id", getAlbumId());
		values.put(MediaStore.Audio.AlbumColumns.ALBUM_KEY, getAlbumKey());
		values.put(MediaStore.Audio.ArtistColumns.ARTIST_KEY, getArtist().getKey());
		values.put(MediaStore.Audio.AlbumColumns.ALBUM, getName());
		values.put(MediaStore.Audio.AlbumColumns.ARTIST, getArtist().getName());
		values.put(MediaStore.Audio.AlbumColumns.FIRST_YEAR, getFirstYear());
		values.put(MediaStore.Audio.AlbumColumns.LAST_YEAR, getLastYear());
		values.put(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS, getSongCount());
		if(forRecents) {
			values.put(Song.QUEUE_ID, getQueueId());
		}
		return values;
	}
}