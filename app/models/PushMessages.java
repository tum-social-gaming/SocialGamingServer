package models;


import play.libs.Json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
/**
* This class is used for convenience to generate the specific messages used to 
* communicate to the client application.
*
* The pattern is always the same:
* type - for identifying the message type (event)
* subtype - for indicating the nature of the event
* and additional payload
* 
* The messages are always encoded into a JSON that is sent to the client application
*
*
*/

public class PushMessages {
	
	public static ObjectNode createGameRequestMessage(Game game){
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "request");
		json.put("gameID", game.id);
		json.put("user1ID", game.firstUserFbID);
		json.put("user2ID", game.secondUserFbID);
		json.put("user1Name", game.firstUserName);
		json.put("user2Name", game.secondUserName);
		
		return json;
	} 
	
	public static ObjectNode createAbortGameMessage(Game game, User abortingUser) {
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "aborted");
		json.put("gameID", game.id);
		json.put("aborterID", abortingUser.facebookID);
		json.put("aborterName", abortingUser.name);
		
		return json;
	}
	
	public static ObjectNode createEstablishedGameMessage(Game game) {
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "established");
		json.put("gameID", game.id);
		
		return json;
	}
	
	public static ObjectNode createWonGameMessage(User winner, User loser) {
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "won");
		json.put("score", winner.score);
		json.put("opponent", loser.name);
		
		return json;
	}
	

	public static ObjectNode createLostGameMessage(User loser, User winner) {
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "lost");
		json.put("score", loser.score);
		json.put("opponent", winner.name);
		
		return json;
	}
	
	public static ObjectNode createDrawGameMessage(User opponent) {
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "draw");
		json.put("opponent", opponent.name);
		
		return json;
	}

	public static ObjectNode createPokeMessage(User sender) {
		ObjectNode json = Json.newObject(); 
		
		json.put("type", "game");
		json.put("subtype", "poke");
		json.put("senderName", sender.name);
		json.put("senderID", sender.facebookID);
		
		return json;
	}

}
