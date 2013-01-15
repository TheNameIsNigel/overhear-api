package com.afollestad.overhearapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

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