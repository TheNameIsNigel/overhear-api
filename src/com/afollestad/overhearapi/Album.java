package com.afollestad.overhearapi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
	private String artist;
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
	public String getArtist() {
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

	public static Album fromCursor(Cursor cursor) {
		Album album = new Album();
		album.albumId = cursor.getInt(cursor.getColumnIndex("_id"));
		album.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
		album.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)); 
		album.albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_KEY));
		album.minYear = Calendar.getInstance();
		album.minYear.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR)));
		album.maxYear = Calendar.getInstance();
		album.maxYear.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR)));
		album.numSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS));
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
			albums.add(Album.fromCursor(cur));
		}
		return albums;
	}
}