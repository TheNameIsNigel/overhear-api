package com.afollestad.overhearapi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class Utils {

	public static int convertDpToPx(Context context, float dp) {
		//Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		//Convert the dps to pixels, based on density scale
		return (int)(dp * scale + 0.5f);
	}

	public static String loadURLString(String url) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		StringBuilder builder = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			builder.append(inputLine);
		in.close();
		return builder.toString();
	}

	public static Bitmap loadImage(Context context, Uri uri, float targetWidthDp, float targetHeightDp) {
		int widthPx = Utils.convertDpToPx(context, targetWidthDp);
		int heightPx = Utils.convertDpToPx(context, targetHeightDp);
		byte[] byteArray = getBytesFromSource(context, uri);
		if (byteArray != null) {
			return decodeBitmapFromBytes(context.getResources(), byteArray, widthPx, heightPx);
		}
		return null;
	}

	private static byte[] getBytesFromSource(Context context, Uri source) {
		InputStream stream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			if(source.getScheme().equals("file")) {
				stream = new FileInputStream(new File(source.getPath()));
			} else if(source.getScheme().startsWith("http")) {
				stream = new URL(source.toString()).openStream();
			} else if(source.getScheme().equals("content")) {
				stream = context.getContentResolver().openInputStream(source);
			}
			byteArrayOutputStream = new ByteArrayOutputStream();
			IOUtils.copy(stream, byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(byteArrayOutputStream);
		}
		return null;
	}

	private static Bitmap decodeBitmapFromBytes(Resources res, byte[] data, int targetWidth, int targetHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)reqHeight);
			} else {
				inSampleSize = Math.round((float)width / (float)reqWidth);
			}
		}
		return inSampleSize;
	}
}