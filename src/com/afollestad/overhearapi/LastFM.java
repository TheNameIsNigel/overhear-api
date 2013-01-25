package com.afollestad.overhearapi;

import android.text.Html;
import android.text.Spanned;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class LastFM {

	private final static String API_KEY = "129b5bafd64fac60d0c096640b250c17";
	//private final static String API_SECRET = "9cba30d77042cd3224267340ea527e73";
	
	public static class AlbumInfo {
		
		private AlbumInfo() { }
		
		private String name;
		private String artist;
		private String releaseDate;
		private String coverUrl;
		
		public String getName() {
			return name;
		}
		public String getArtist() {
			return artist;
		}
		public String getReleaseDate() {
			return releaseDate;
		}
		public String getCoverImageURL() throws Exception {
			return coverUrl;
		}
		
		public static AlbumInfo fromJSON(JSONObject json) {
			AlbumInfo info = new AlbumInfo();
			try {
				info.name = json.getString("name");
				info.artist = json.getString("artist");
				info.releaseDate = json.getString("releasedate");
				JSONArray images = json.getJSONArray("image");
				info.coverUrl = images.getJSONObject(images.length() - 2).getString("#text");
			} catch(Exception e) {
				System.out.println(json.toString());
				throw new java.lang.Error(e.getMessage());
			}
			return info;
		}
	}
	
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
		public String getBioImageURL() throws Exception {
			return bioImageUrl;
		}
		public String getBioPublishedDate() {
			return bioPublished;
		}
		public Spanned getBioSummary() {
			if(bioSummary == null || bioSummary.trim().isEmpty()) {
				return null;
			}
			return Html.fromHtml(bioSummary.replace("\n", "<br/>"));
		}
		public Spanned getBioContent() {
			if(bioContent == null || bioContent.trim().isEmpty()) {
				return null;
			}
			return Html.fromHtml(bioContent.replace("\n", "<br/>"));
		}
		public String getYearFormed() {
			return yearFormed;
		}
		
		public static ArtistInfo fromJSON(JSONObject json) {
			ArtistInfo info = new ArtistInfo();
			try {
				info.name = json.getString("name");
				JSONArray images = json.getJSONArray("image");
//				for(int i = images.length() - 1; i > 0; i--) {
//					JSONObject img = images.getJSONObject(i);
//					if(img.getString("size").equals("extralarge") || img.getString("size").equals("large")) {
//						info.bioImageUrl = img.getString("#text");
//					}
//				}
				info.bioImageUrl = images.getJSONObject(images.length() - 2).getString("#text");
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
	
	public static AlbumInfo getAlbumInfo(String artist, String album) throws Exception {
		String url = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo" +
				"&artist=" + URLEncoder.encode(artist, "UTF-8") +
				"&album=" + URLEncoder.encode(album, "UTF-8") +
				"&api_key=" + API_KEY + 
				"&format=json";
		JSONObject urlContents = new JSONObject(Utils.loadURLString(url));
		if(urlContents.has("error")) {
			throw new Exception(urlContents.getString("message"));
		}
		return AlbumInfo.fromJSON(urlContents.getJSONObject("album"));
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