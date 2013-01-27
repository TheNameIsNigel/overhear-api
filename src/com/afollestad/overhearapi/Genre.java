package com.afollestad.overhearapi;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Genre {

	private Genre() { }

	private int id;
	private String name;
	private static Uri GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public static Genre fromCursor(Cursor cursor) {
		Genre genre = new Genre();
		genre.id = cursor.getInt(cursor.getColumnIndex("_id"));
		genre.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.GenresColumns.NAME));
		if(genre.name == null || genre.name.trim().isEmpty()) {
			genre.name = "Unknown";
		}
		return genre;
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", this.id);
			json.put("name", this.name);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

    public static Genre fromJSON(String json) {
        try {
            return fromJSON(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
    }

	public static Genre fromJSON(JSONObject json) {
		Genre genre = new Genre();
		try {
			genre.id = json.getInt("id");
			genre.name = json.getString("name");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return genre;
	}

	public static List<Genre> getAllGenres(Context context) {
		Cursor cur = context.getContentResolver().query(
				GENRES_URI, 
				null, 
				null, 
				null, 
				MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);
		ArrayList<Genre> genres = new ArrayList<Genre>();
		while (cur.moveToNext()) {
			Genre toAdd = Genre.fromCursor(cur);
			if(toAdd != null)
				genres.add(toAdd);
		}
		return genres;
	}

	public List<Song> getAllSongs(Context context) {
		String CONTENTDIR = MediaStore.Audio.Genres.Members.CONTENT_DIRECTORY;
        Uri uri = Uri.parse(GENRES_URI.toString() + "/" + getId() + "/" + CONTENTDIR);
		Cursor cur = context.getContentResolver().query(
				uri, 
				null, 
				null, 
				null, 
				MediaStore.Audio.Genres.Members.DEFAULT_SORT_ORDER);
		ArrayList<Song> songs = new ArrayList<Song>();
		ArrayList<String> foundAlbums = new ArrayList<String>();
		while (cur.moveToNext()) {
			String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			if(foundAlbums.contains(album)) {
				continue;
			}
			foundAlbums.add(album);
			songs.add(Song.fromCursor(cur));
		}
		return songs;
	}
}
