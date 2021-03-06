package com.afollestad.overhearapi;

import android.text.Html;
import android.text.Spanned;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LastFM {

    private final static String API_KEY = "129b5bafd64fac60d0c096640b250c17";
    //private final static String API_SECRET = "9cba30d77042cd3224267340ea527e73";

    public static AlbumInfo getAlbumInfo(String artist, String album) throws Exception {
        String url = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo" +
                "&artist=" + URLEncoder.encode(artist, "UTF-8") +
                "&album=" + URLEncoder.encode(album, "UTF-8") +
                "&api_key=" + API_KEY +
                "&lang=" + Locale.getDefault().getLanguage() +
                "&format=json";
        JSONObject urlContents = new JSONObject(Utils.loadURLString(url));
        if (urlContents.has("error")) {
            throw new Exception(urlContents.getString("message"));
        }
        return AlbumInfo.fromJSON(urlContents.getJSONObject("album"));
    }

    public static ArtistInfo getArtistInfo(String artist) throws Exception {
        String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo" +
                "&artist=" + URLEncoder.encode(artist, "UTF-8") +
                "&api_key=" + API_KEY +
                "&lang=" + Locale.getDefault().getLanguage() +
                "&format=json";
        JSONObject urlContents = new JSONObject(Utils.loadURLString(url));
        if (urlContents.has("error")) {
            throw new Exception(urlContents.getString("message"));
        }
        return ArtistInfo.fromJSON(urlContents.getJSONObject("artist"));
    }

    public static class AlbumInfo {

        private String name;
        private String artist;
        private String releaseDate;
        private String coverUrl;

        private AlbumInfo() {
        }

        public static AlbumInfo fromJSON(JSONObject json) {
            AlbumInfo info = new AlbumInfo();
            try {
                info.name = json.getString("name");
                info.artist = json.getString("artist");
                info.releaseDate = json.getString("releasedate");
                JSONArray images = json.getJSONArray("image");
                info.coverUrl = images.getJSONObject(images.length() - 2).getString("#text");
            } catch (Exception e) {
                System.out.println(json.toString());
                throw new java.lang.Error(e.getMessage());
            }
            return info;
        }

        public String getName() {
            return name;
        }

        public String getArtist() {
            return artist;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getCoverImageURL() {
            return coverUrl;
        }
    }

    public static class ArtistInfo {

        private final List<ArtistInfo> similarArtists;
        private String name;
        private String bioImageUrl;
        private String bioPublished;
        private String bioSummary;
        private String bioContent;
        private String yearFormed;

        private ArtistInfo() {
            similarArtists = new ArrayList<ArtistInfo>();
        }

        private ArtistInfo(String name, String bioImageUrl) {
            this();
            this.name = name;
            this.bioImageUrl = bioImageUrl;
        }

        public static ArtistInfo fromJSON(JSONObject json) {
            ArtistInfo info = new ArtistInfo();
            try {
                info.name = json.getString("name");
                JSONArray images = json.getJSONArray("image");
                info.bioImageUrl = images.getJSONObject(images.length() - 1).getString("#text");
                JSONObject bio = json.getJSONObject("bio");
                info.bioPublished = bio.getString("published");
                info.bioSummary = bio.getString("summary");
                info.bioContent = bio.getString("content");
                info.yearFormed = bio.optString("yearformed");

                if (json.has("similar")) {
                    try {
                        JSONObject similar = json.getJSONObject("similar");
                        if (similar.has("artist")) {
                            JSONArray artists = similar.getJSONArray("artist");
                            for (int i = 0; i < artists.length(); i++) {
                                JSONObject simArt = artists.getJSONObject(i);
                                images = simArt.getJSONArray("image");
                                info.similarArtists.add(new ArtistInfo(
                                        simArt.getString("name"),
                                        images.getJSONObject(images.length() - 1).getString("#text")
                                ));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                System.out.println(json.toString());
                throw new java.lang.Error(e.getMessage());
            }
            return info;
        }

        public String getName() {
            return name;
        }

        public String getBioImageURL() {
            return bioImageUrl;
        }

        public String getBioPublishedDate() {
            return bioPublished;
        }

        public Spanned getBioSummary() {
            if (bioSummary == null || bioSummary.trim().isEmpty()) {
                return null;
            }
            return Html.fromHtml(bioSummary.replace("\n", "<br/>"));
        }

        public Spanned getBioContent() {
            if (bioContent == null || bioContent.trim().isEmpty()) {
                return null;
            }
            return Html.fromHtml(bioContent.replace("\n", "<br/>"));
        }

        public String getYearFormed() {
            return yearFormed;
        }

        public List<ArtistInfo> getSimilarArtists() {
            return similarArtists;
        }
    }
}