package models;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gcm.server.Result;
import com.google.common.collect.Lists;

import controllers.FacebookAppClient;
import controllers.PushNotifications;
import uk.co.panaxiom.playjongo.PlayJongo;


/**
 * This class is for the database representation of users using Jungo 
 * 
 * ID is intended to be a unique ID in the System, one can for example use
 * the Facebook User-IDs, as we do here. The device-token is the Google
 * Cloud Message Service token received from the Android GCM API, which is device and,
 * ergo, user-specific.
 *
 * 
 *  
 * @author Niklas Klügel
 *
 */

public class User {
	// used by Jongo to map JVM objects to database Objects
	
    @JsonProperty("_id")
    public String id;
    
    // credentials for the services
    public String googleCloudDeviceId="";    
    public String facebookID = "";
    
    // a list of facebook IDs of the user's friends,
    // to simplifiy (circumvent inconsistencies when a user logs in and has a different list of friends), 
    // we will have to look up the users according to Facebook-ID
    // from the database
    public String[] facebookFriendIDs = new String[]{};
    
    public String name;
    
    public Double[] loc = new Double[]{11.5833, 48.15}; // Garching
    
    // In seconds in System time
    public Long lastLogin = -1L;
    
    // Indicates whether this profile is active in the game
    public Boolean participatesInGame = false;
   
    // Game score that the user achieved
    public Double score = 0.0;
    
    /****************
     * Class methods
     * -------------
     ***************/
	
   /**
    * Looks up the user collection from the database
    * @return
    */
    public static MongoCollection users() {
        MongoCollection userCollection =  PlayJongo.getCollection("users");
        
        // make sure we use 2d indices on a sphere to use geospatial queries
        userCollection.ensureIndex("{loc: '2dsphere'}");
        return userCollection;
    }    
    
    /**
     * User lookup by user/character name
     * @param name
     * @return
     */
    public static User findByName(String name) {
        return users().findOne("{name: #}", name).as(User.class);
    }
    
    /**
     * User lookup by Facebook ID 
     * @param fbID
     * @return
     */
    public static User findByFacebookID(String fbID) {
    	return users().findOne("{facebookID: #}", fbID).as(User.class);
    }
    
    /**
     * Adds a new user to the database
     * @param name - User/character name
     * @param fbID - Facebook ID
     * @return
     */
    private static User insertNewUserWithSimpleProfileData(String name, String fbID) {
    	User newUser = new User(name, fbID);
    	newUser.insert();    	
    	
    	return newUser;
    }  
    
    
    /**
     * This class method finds users that are nearby a location, 
     * it only returns users that are also participating in the game, you can uncomment the 
     * additional mongoDB parameters if you want
     * 
     * @param loc - geolocations as 2D double vector: {longitude,lattitude}
     * @param maxDistance - maximum distance in meters where users should be looked up
     * @param limit - a limit for the number of returned users
     * @return
     */
    
    public static Iterable<User> findUsersNearby(Double[] loc, Double maxDistance, int limit) {
    	// geoNear indicates a lookup based on spherical coordinates
 
    	// this DB request also serves as an example for a slightly more complex mongoDB request making use
    	// of the "native" mongoDB API
    	List<User> results = Lists.newArrayList(users().find("{loc: {$geoNear : {$geometry : {type: 'Point', " +
                "coordinates: # }, $maxDistance: # }}, participatesInGame: true}", loc, maxDistance).limit(limit) // 
                .as(User.class));
    	
    	return results;
    }    


    
    /****************
     * Object methods
     * -------------
     ***************/
	
	public User() {}

	public User(String name, String facebookID) {
		this.name = name;
		this.facebookID = facebookID;
	}
                  
    public void insert() {
        users().save(this);
    }

    public void remove() {
        users().remove(this.id);
    }
    
    /**
     * Performs a deep copy of the user's data
     * @return
     */
    private User copy() {
    	User copy = new User();
    	
    	copy.name = this.name;
    	copy.googleCloudDeviceId = this.googleCloudDeviceId;
    	copy.facebookID = this.facebookID;
    	copy.facebookFriendIDs = this.facebookFriendIDs.clone();
    	copy.loc = this.loc;
    	copy.participatesInGame = this.participatesInGame;
    	copy.lastLogin = this.lastLogin;
    	copy.score = this.score;
    	
    	return copy;    
    }
    
    /**
     * Updates all fields in the DB for the object by making a copy and swapping the original object in the database for it
     */
    private void update() {
    	// copy the user to be sure that database IDs will be taken care of
    	users().update("{facebookID: #}",this.facebookID).with(this.copy());
    }
    
    /**
     * Alters the score of a user
     * @param add
     */
    public void addToScoreAndUpdate(double add){
    	double newScore = this.score + add;
    	users().update("{facebookID: #}", this.facebookID).with("{$set: {score: #}}", (Object) new Double(newScore));
    }
    
    /**
     * Alters the 2d spherical position of a user
     * 
     * @param longitude
     * @param latitude
     */

    public void updateLocation(Double longitude, Double latitude){
    	users().update("{facebookID: #}", this.facebookID).with("{$set: {loc: #}}", (Object) new Double[]{longitude, latitude});
    }
    
    /**
     * A primitve function that checks whether a user is another user's friend on Facebook
     * @param other
     * @return
     */
    public boolean isFriendOf(User other) {
    	boolean ret = false;
    	
    	for(String facebookFriendID : this.facebookFriendIDs) {
    		if(facebookFriendID.equals(other.facebookID)) {
    			ret = true;
    			
    			break;
    		}
    	}
    	
    	return ret;
    }    
    
    /**
     * Send a message to the User's device using Google Cloud Messaging
     * 
     * @param json - Json of the message contents to be sent
     * @return
     * @throws IOException
     */
    
    public Result sendMessage(ObjectNode json) throws IOException {
    	return this.sendMessageCached(json, 0);
    }
    
    /**
     * Send a message to the User's device using Google Cloud Messaging,
     * this time the message is cached in the GCM system and delivered
     * as soon as the device is online
     * 
     * @param json - Json of the message contents to be sent
     * @param ttl - Time to live of the message in GCM
     * @return
     * @throws IOException
     */
    
    public Result sendMessageCached(ObjectNode json, int ttl) throws IOException {
    	return PushNotifications.getInstance().sendMessage(this.googleCloudDeviceId, json.toString(), ttl);
    }
        
    /**
     * Updates or inserts a new User
     * This method has the overhead of requesting most of the user's data and updating them in the database, it could be performed in a more compact/less bandwidth demanding way,
     * but for the sake of being able to expand this method with additional processing/information all data is being requested from facebook 
     * 
     * @param facebookAuthToken
     * @param googleCDSToken
     * @param longitude
     * @param latitude
     * @return
     */
    
    public static User updateUserProfileFromLoginCredentials(String facebookAuthToken, String googleCDSToken, Double longitude, Double latitude) {
    	User user = null;
    	
    	
    	com.restfb.types.User facebookUserProfile = FacebookAppClient.getUser(facebookAuthToken);    	
    	
    	if(facebookUserProfile != null) {
    		user = User.findByFacebookID(facebookUserProfile.getId());
    		
    		// if the user does not exist, create one
    		if(user == null) {
    			user =  insertNewUserWithSimpleProfileData(facebookUserProfile.getName(), facebookUserProfile.getId());    			
    		}
    		
			// as the user logged in, we can set her/his profile as active    			
			user.participatesInGame = true;
    		
    		user.googleCloudDeviceId = googleCDSToken;
    		user.loc = new Double[]{longitude,latitude};
    		user.lastLogin = System.currentTimeMillis()/1000;
    		    		    		
    		// Update the user profile's friends entries
        	List<com.restfb.types.User> usersFacebookFriends = FacebookAppClient.getFriendsOfUser(facebookAuthToken);        	
        	String[] fbFriendIDs = new String[usersFacebookFriends.size()];
        	
        	int idx = 0;
        	for(com.restfb.types.User facebookFriend : usersFacebookFriends) {
        		
        		// check if the friend is already in the database, else add her/him
        		if(User.findByFacebookID(facebookFriend.getId()) == null){
        			User.insertNewUserWithSimpleProfileData(facebookFriend.getName(), facebookFriend.getId());
        		}   
        		fbFriendIDs[idx] = facebookFriend.getId();
        		
        		idx++;
        	}
        	
        	user.facebookFriendIDs = fbFriendIDs;
        	
        	user.update();
    	}
    			    			    	    	    	    	
    	
    	return user;
    }
    
    public String toString() {
    	String friendsString = "";
    	
    	for(String friend: facebookFriendIDs){
    		friendsString = friendsString + ", "+friend;
    	}
    	
    	return "User \tname: "+this.name
    			+"\n\tlocation: "+this.loc[0]+","+this.loc[1]
    			+"\n\tFacebook ID: "+this.facebookID
    			+"\n\tfriends ("+facebookFriendIDs.length+"): "+friendsString
    			+"\n\tGoogle Device ID: "+this.googleCloudDeviceId;
    }
}
