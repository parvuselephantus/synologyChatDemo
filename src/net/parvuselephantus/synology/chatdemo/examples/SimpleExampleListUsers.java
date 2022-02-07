package net.parvuselephantus.synology.chatdemo.examples;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

import net.parvuselephantus.synology.chatdemo.ConfigProperties;
import net.parvuselephantus.synology.chatdemo.SynologyChatConnector;

public class SimpleExampleListUsers {
	private SynologyChatConnector connector;
	
	public static void main(String[] args) throws Exception {
		SimpleExampleListUsers example = new SimpleExampleListUsers();
		example.listAvailableUsers();
	}
	
	public SimpleExampleListUsers() throws IOException {
		//Find your best way to store Chat URL and bot Token
		ConfigProperties props = new ConfigProperties();
		props.load();
		
		//You need to create and pass chat URL and bot token to connector
		connector = new SynologyChatConnector();
		connector.initiate(props.getChatURL(), props.getChatToken());
	}

	public void listAvailableUsers() {
		JsonNode reply = connector.listUsers();
		
		System.out.println("Following users are available:");
		reply.get("data").get("users").forEach((userNode) -> {
			System.out.println(userNode.get("username").toString() + ": " + userNode.get("user_id").asInt());
		});
	}
}
