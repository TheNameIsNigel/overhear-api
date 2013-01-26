package com.afollestad.overhearapi;

import android.content.ContentValues;
import android.database.Cursor;

public class WebArt {

	private WebArt() { }
    public WebArt(String name, String key, String url) {
        this.name = name;
        this.key = key;
        this.url = url;
    }

	private String name;
	private String key;
	private String url;

    public final static String NAME = "name";
    public final static String KEY = "key";
    public final static String URL = "url";

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

	public static WebArt fromCursor(Cursor cursor) {
		WebArt art = new WebArt();
        art.name = cursor.getString(cursor.getColumnIndex(NAME));
        art.key = cursor.getString(cursor.getColumnIndex(KEY));
        art.url = cursor.getString(cursor.getColumnIndex(URL));
		return art;
	}

	public static String getCreateTableStatement(String tableName) {
		return "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
				NAME + " TEXT PRIMARY KEY," +
				KEY + " TEXT," +
				URL + " TEXT" +
			");";
	}

    public static WebArt fromAlbum(Album album, String url) {
        return new WebArt(album.getName(), album.getArtist().getName(), url);
    }

    public static String getAlbumWhereStatement(Album album) {
        return NAME + " = '" + album.getName().replace("'", "''") + "' AND " +
                KEY + " = '" + album.getArtist().getName().replace("'", "''") + "'";
    }

    public static WebArt fromArtist(Artist artist, String url) {
        return new WebArt(artist.getName(), artist.getKey(), url);
    }

    public static String getArtistWhereStatement(Artist artist) {
        return NAME + " = '" + artist.getName().replace("'", "''") + "' AND " +
                KEY + " = '" + artist.getKey().replace("'", "''") + "'";
    }
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(KEY, key);
        values.put(URL, url);
		return values;
	}
}