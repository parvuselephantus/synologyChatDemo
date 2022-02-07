package net.parvuselephantus.synology.chatdemo.examples;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

import net.parvuselephantus.synology.chatdemo.ConfigProperties;
import net.parvuselephantus.synology.chatdemo.SynologyChatConnector;

public class SimpleExampleListChats {
	private SynologyChatConnector connector;
	
	public static void main(String[] args) throws Exception {
		SimpleExampleListChats example = new SimpleExampleListChats();
		example.listAvailableConversations();
	}
	
	public SimpleExampleListChats() throws IOException {
		//Find your best way to store Chat URL and bot Token
		ConfigProperties props = new ConfigProperties();
		props.load();
		
		//You need to create and pass chat URL and bot token to connector
		connector = new SynologyChatConnector();
		connector.initiate(props.getChatURL(), props.getChatToken());
	}

	public void listAvailableConversations() {
		JsonNode reply = connector.listChannels();
		
		System.out.println("Following channels are available");
		reply.get("data").get("channels").forEach((userNode) -> {
			System.out.println(userNode.get("channel_id").toString() + " with members " + userNode.get("members").toPrettyString());
		});
	}
}
