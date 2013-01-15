package com.afollestad.overhearapi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class Utils {
	
	public static String loadURLString(String url) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		StringBuilder builder = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			builder.append(inputLine);
		in.close();
		return builder.toString();
	}

	public static Bitmap loadImage(Context context, Uri uri, int targetWidth, int targetHeight) {
		byte[] byteArray = getBytesFromSource(context, uri);
		if (byteArray != null) {
			return decodeBitmapFromBytes(context.getResources(), byteArray, targetWidth, targetHeight);
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
	
	private static String convertMonth(int month, boolean isShort) {
		String toReturn = "";
		switch (month) {
		case Calendar.JANUARY:
			toReturn = "January";
			if (isShort)
				toReturn = "Jan";
			break;
		case Calendar.FEBRUARY:
			toReturn = "February";
			if (isShort)
				toReturn = "Feb";
			break;
		case Calendar.MARCH:
			toReturn = "March";
			if (isShort)
				toReturn = "Mar";
			break;
		case Calendar.APRIL:
			toReturn = "April";
			if (isShort)
				toReturn = "Apr";
			break;
		case Calendar.MAY:
			toReturn = "May";
			break;
		case Calendar.JUNE:
			toReturn = "June";
			if (isShort)
				toReturn = "Jun";
			break;
		case Calendar.JULY:
			toReturn = "July";
			if (isShort)
				toReturn = "Jul";
			break;
		case Calendar.AUGUST:
			toReturn = "August";
			if (isShort)
				toReturn = "Aug";
			break;
		case Calendar.SEPTEMBER:
			toReturn = "September";
			if (isShort)
				toReturn = "Sep";
			break;
		case Calendar.OCTOBER:
			toReturn = "October";
			if (isShort)
				toReturn = "Oct";
			break;
		case Calendar.NOVEMBER:
			toReturn = "November";
			if (isShort)
				toReturn = "Nov";
			break;
		case Calendar.DECEMBER:
			toReturn = "December";
			if (isShort)
				toReturn = "Dec";
			break;
		}
		return toReturn;
	}

	public static String getFriendlyTime(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		return getFriendlyTime(cal);
	}
	
	public static String getFriendlyTime(Calendar time) {
		Calendar now = Calendar.getInstance();
		String am_pm = "AM";
		if (time.get(Calendar.AM_PM) == Calendar.PM)
			am_pm = "PM";
		String day = Integer.toString(time.get(Calendar.DAY_OF_MONTH));
		if (day.length() == 1)
			day = ("0" + day);
		if (now.get(Calendar.YEAR) == time.get(Calendar.YEAR)) {
			if (now.get(Calendar.MONTH) == time.get(Calendar.MONTH)) {
				if (now.get(Calendar.DAY_OF_YEAR) == time
						.get(Calendar.DAY_OF_YEAR)) {
					String minute = Integer.toString(time.get(Calendar.MINUTE));
					int hour = time.get(Calendar.HOUR);
					if (hour == 0)
						hour = 12;
					if (minute.length() == 1)
						minute = ("0" + minute);
					return hour + ":" + minute + am_pm;
				} else {
					return convertMonth(time.get(Calendar.MONTH), false) + " " + day;
				}
			} else {
				return convertMonth(time.get(Calendar.MONTH), false) + " " + day;
			}
		} else {
			String year = Integer.toString(time.get(Calendar.YEAR));
			if (now.get(Calendar.YEAR) < time.get(Calendar.YEAR))
				year = year.substring(1, 3);
			return convertMonth(time.get(Calendar.MONTH), false) + " " + day + ", " + year;
		}
	}
}