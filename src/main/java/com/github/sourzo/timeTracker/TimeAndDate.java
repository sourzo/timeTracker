package com.github.sourzo.timeTracker;

import java.time.Duration;
import java.time.LocalTime;

public class TimeAndDate {
	/**Get the time right now, ignoring seconds and nanoseconds. Prints out in the format HH:mm.*/
	public static LocalTime timeNow() {
		LocalTime now = LocalTime.now();
		return time(now);
	}
	
	/**Set seconds & nanoseconds to zero, so that the local time is of the format HH:mm = HH:mm:00:0.0*/
	public static LocalTime time(LocalTime time) {
		time = time.withSecond(0);
		time = time.withNano(0);
		return time;
	}
	
	public static String parseDuration(Duration dur) {
        long hours = dur.toHours();
        Duration remainder = dur.minusHours(hours);
        int minutes = (int) remainder.toMinutes();
        return hours + "h " + minutes + "min";
	}
}
