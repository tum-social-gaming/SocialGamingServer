package controllers;

import java.io.IOException;

import util.GameConfiguration;

import com.google.android.gcm.server.*;
import com.google.android.gcm.server.Message.Builder;



/**
 * This class is used for Push communication to Android devices using
 * Google Cloud Messaging.
 * 
 * More info:
 * http://developer.android.com/google/gcm/index.html
 * 
 * It uses the google gcm framework (see build.sbt for dependencies):
 * https://code.google.com/p/gcm/
 * 
 * @author Niklas Klügel
 *
 */

public class PushNotifications {

	private static PushNotifications instance;
	
	/**
	 * Simple singleton pattern, use this method to 
	 * receive a (shared) instance of PushNotifications
	 * @return
	 */
	public static PushNotifications getInstance() {
		if(instance == null) {
			instance = new PushNotifications();
		} 		
		return instance;
	}

	
	private Sender sender;	
	
	private PushNotifications() {
		sender = new Sender(GameConfiguration.googleAppKey);
	}
	
	/**
	 * Sends a message using Google Cloud Messaging Service
	 * @param deviceToken - the token received from the GCM registrar
	 * 						on Android
	 * @param message - String message to be sent
	 * @return
	 * @throws IOException
	 */
	
	  // when the time to live is set to 0 , Google does not throttle the
	  // messages, but they are not stored in between and may get lost
	public Result sendMessage(String deviceToken, String message, int ttl) throws IOException {
		
		Message msg = new Message.Builder()
						  //.collapseKey("1")
						  .timeToLive(ttl)
						  //.delayWhileIdle(true)
						  .addData("message", message)
						  .build();
		return this.sender.send(msg, deviceToken, 1);
	}
	
}
