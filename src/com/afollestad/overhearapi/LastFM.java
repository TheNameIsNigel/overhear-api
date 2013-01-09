package com.afollestad.overhearapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URLEncoder;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;

public class LastFM {

	private final static String API_KEY = "129b5bafd64fac60d0c096640b250c17";
	//private final static String API_SECRET = "9cba30d77042cd3224267340ea527e73";
	
	public static class ArtistInfo {
		
		private ArtistInfo() { }
		
		private String name;
		private String bioImageUrl;
		private String bioPublished;
		private String bioSummary;
		private String bioContent;
		private String yearFormed;
		
		public String getName() {
			return name;
		}
		public Bitmap getBioImage(Context context, float widthDp, float heightDp, boolean cache) throws Exception {
			if(cache) {
				File[] files = context.getCacheDir().listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase(Locale.getDefault()).equals(name + "_ART.jpg");
					}
				});
				if(files != null && files.length >= 1) {
					return Utils.loadImage(context, Uri.fromFile(files[0]), widthDp, heightDp);
				}
			}
			Bitmap loaded = Utils.loadImage(context, Uri.parse(bioImageUrl), widthDp, heightDp);
			if(cache) {
				loaded.compress(CompressFormat.JPEG, 100, new FileOutputStream(new File(context.getCacheDir(), name + "_ART.jpg")));
			}
			return loaded;
		}
		public String getBioPublishedDate() {
			return bioPublished;
		}
		public String getBioSummary() {
			return bioSummary;
		}
		public String getBioContent() {
			return bioContent;
		}
		public String getYearFormed() {
			return yearFormed;
		}
		
		public static ArtistInfo fromJSON(JSONObject json) {
			ArtistInfo info = new ArtistInfo();
			try {
				info.name = json.getString("name");
				JSONArray images = json.getJSONArray("image");
				for(int i = 0; i < images.length(); i++) {
					if(images.getJSONObject(i).getString("size").equals("extralarge")) {
						info.bioImageUrl = images.getJSONObject(i).getString("#text");
					}
				}
				JSONObject bio = json.getJSONObject("bio");
				info.bioPublished = bio.getString("published");
				info.bioSummary = bio.getString("summary");
				info.bioContent = bio.getString("content");
				info.yearFormed = bio.optString("yearformed");
			} catch(Exception e) {
				System.out.println(json.toString());
				throw new java.lang.Error(e.getMessage());
			}
			return info;
		}
	}
	
	public static ArtistInfo getArtistInfo(String artist) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + 
				URLEncoder.encode(artist, "UTF-8") + "&api_key=" + API_KEY + "&format=json";
		JSONObject urlContents = new JSONObject(Utils.loadURLString(url));
		if(urlContents.has("error")) {
			throw new Exception(urlContents.getString("message"));
		}
		return ArtistInfo.fromJSON(urlContents.getJSONObject("artist"));
	}
}