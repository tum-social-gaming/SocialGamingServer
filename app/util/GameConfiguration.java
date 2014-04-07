package util;

/**
 * Simple class that holds all configuration specific information for the game.
 * 
 * @author Niklas Klügel
 *
 */
public class GameConfiguration {
	// Facebook App credentials
	public static String fbAppID = "1449813248586394";	
	public static String fbAppSecret  = "8e5d8e08c05b7195fa946c31fc19db73";
	
	// Google App credentials
	public static String googleAppID = "1085161367964";
	public static String googleAppKey = "AIzaSyBllofTlAQ-lbs-fhnaJezzGbA1T5q8oUY";
	
	// limits the maximum distance for user lookups (meters)
	public static double MaxDistanceOfUserForNearbyUsers = 100.0;
	public static int MaxNumberOfReturnedUsers = 20;
	
	// this is the time to live for a user login to be still valid
	// e.g. if chosen as an opponent the user has to logged less than these seconds ago
	public static long MaxTimeForLoginTimeOutInSeconds = 3600;

}
