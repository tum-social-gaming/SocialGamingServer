package util;

/**
 * Simple class containing utility functions.
 * 
 * @author Niklas Klügel
 *
 */
public class Util {
	
	/*
	 * Converts two locations to a distance in meters, taken from:
	 * http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	 */
	public static Double geoLocToDistInMeters(Double loc, Double loc2, Double loc3, Double loc4) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(loc4-loc2);
	    double dLng = Math.toRadians(loc3-loc);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(loc2)) * Math.cos(Math.toRadians(loc4)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return Math.abs((dist * meterConversion));
	    }
}
