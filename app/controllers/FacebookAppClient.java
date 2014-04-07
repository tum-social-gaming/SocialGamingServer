package controllers;

import java.util.Iterator;
import java.util.List;

import play.Logger;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;
import com.restfb.types.User;

/**
 * We are using RestFB here as library for Facebook calls since FB does not offer
 * a JAVA SDK on their own:
 * http://restfb.com/
 * 
 * 
 * Since the server will request user data on behalf of the user (client) we need to use
 * the access token send over by the facebook client *running on the Android device*.
 * 
 * We are using public data of the user's friends only, since otherweise we would have to
 * get access tokens on their behalf as well.
 * 
 *  
 * @author Niklas Klügel
 *
 */

public class FacebookAppClient {
	
	/**
	 * Uses access token send to the web service to request user data
	 * and returns the user's friends.
	 * 
	 * This method will also output all data to the log, so we know what
	 * data we get.
	 * 
	 * @param accessToken
	 * @return
	 */
	
	public static List<User> getFriendsOfUser(String accessToken) {
		FacebookClient fbc = new DefaultFacebookClient(accessToken);
		
		Connection<User> myFriends = fbc.fetchConnection("me/friends", User.class);				
		Connection<Post> myFeed = fbc.fetchConnection("me/feed", Post.class);

		Logger.info("Count of my friends: " + myFriends.getData().size());
		Logger.info("First item in my feed: " + myFeed.getData().get(0));
		
		User thatsMe = fbc.fetchObject("me", User.class);
		
		Logger.info("User: "+thatsMe);
		
		List<User> usersFriends = myFriends.getData();
		
		
			for(User myBestestFriend: usersFriends) {
				Logger.info("---");
				Logger.info("his/her bestest friend is "+ myBestestFriend.getName() + " " +myBestestFriend.getFirstName() + " "+myBestestFriend.getLastName()+ " -- "+myBestestFriend.getId());
			}			
		
		
		return usersFriends;
				
	}
	
	/**
	 * Returns a Facebook user based on the delivered access token.
	 * @param accessToken
	 * @return
	 */
	
	public static User getUser(String accessToken) {
		FacebookClient fbc = new DefaultFacebookClient(accessToken);
		User facebookUser = fbc.fetchObject("me", User.class);
		
		return facebookUser;
	}

}
