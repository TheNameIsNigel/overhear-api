package com.afollestad.overhearapi;

import android.database.Cursor;
import android.provider.MediaStore;
import org.json.JSONException;
import org.json.JSONObject;

public class Playlist {

	private Playlist() {
    }

    private long _id;
	private String name;
    private String data;
    private long dateAdded;
    private long dateModified;
	
	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
            json.put("_id", this._id);
			json.put("name", this.name);
			json.put("data", this.data);
			json.put("date_added", this.dateAdded);
            json.put("date_modified", this.dateModified);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static Playlist fromJSON(String json) {
		try {
			return fromJSON(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new Error(e.getMessage());
		}
	}
	public static Playlist fromJSON(JSONObject json) {
		Playlist playlist = new Playlist();
		try {
            playlist._id = json.getLong("_id");
			playlist.name = json.getString("name");
            playlist.data = json.getString("data");
            playlist.dateAdded = json.getLong("date_added");
            playlist.dateModified = json.getLong("date_modified");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playlist;
	}

	public static Playlist fromCursor(Cursor cursor) {
		Playlist playlist = new Playlist();

		playlist._id = cursor.getLong(cursor.getColumnIndex("_id"));
        playlist.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.PlaylistsColumns.NAME));
        playlist.data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATA));
        playlist.dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATE_ADDED));
        playlist.dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATE_MODIFIED));

		return playlist;
	}
}