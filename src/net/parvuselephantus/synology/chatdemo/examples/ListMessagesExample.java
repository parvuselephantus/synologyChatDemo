package net.parvuselephantus.synology.chatdemo.examples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import net.parvuselephantus.synology.chatdemo.ConfigProperties;
import net.parvuselephantus.synology.chatdemo.SynologyChatConnector;

public class ListMessagesExample {
	private SynologyChatConnector connector;
	//Display friendly user names - let's map user_id to user name
	private Map<Integer, String> userMap;

	
	public static void main(String[] args) throws Exception {
		ListMessagesExample example = new ListMessagesExample();
		example.listAvailableMessages();
	}
	
	public ListMessagesExample() throws IOException {
		//Find your best way to store Chat URL and bot Token
		ConfigProperties props = new ConfigProperties();
		props.load();
		
		//You need to create and pass chat URL and bot token to connector
		connector = new SynologyChatConnector();
		connector.initiate(props.getChatURL(), props.getChatToken());
		
		userMap = createUserMap();
	}
	
	private Map<Integer, String> createUserMap() {
		Map<Integer, String> userMap = new HashMap<>();
		JsonNode reply = connector.listUsers();
		
		reply.get("data").get("users").forEach((userNode) -> {
			userMap.put(userNode.get("user_id").asInt(), userNode.get("username").toString());
		});
		return userMap;
	}

	public void listAvailableMessages() {
		JsonNode conversations = connector.listChannels();
		conversations.get("data").get("channels").forEach((channel) -> {
			int channel_id = channel.get("channel_id").asInt();
			System.out.println("******************************************************");
			System.out.println("Here is conversation in channel " + channel_id + ":");
			System.out.println("******************************************************");
			listAvailableMessages(channel_id);
		});
	}

	public void listAvailableMessages(int channel_id) {
		int next_count = 30;
		int prev_count = 30;
		
		
		//Calling this one you will get all the messages - next_count and prev_count are skipped
		JsonNode reply = connector.listPosts(channel_id, next_count, prev_count);
		
		//Once you have last post_id, you can just get few posts by using next_count and prev_count
//		JsonNode reply = connector.listMesssages(channel_id, next_count, prev_count, 21474836508L);
		
		reply.get("data").get("posts").forEach((postNode) -> {
			int user_id = postNode.get("creator_id").intValue();
			String userName;
			if (user_id == 1) {
				userName = "System";
			} else {
				userName = userMap.get(user_id); 
			}
			if (userName == null) {
				userName = "Bot's message"; 
			}
			System.out.println("(post_id: " + postNode.get("post_id") + ") " + userName + "[" + user_id + "]: " + postNode.get("message"));
		});
	}
}
