package net.parvuselephantus.synology.chatdemo.examples;

import java.io.IOException;

import net.parvuselephantus.synology.chatdemo.ConfigProperties;
import net.parvuselephantus.synology.chatdemo.SynologyChatConnector;

/**
 * Note webhook and bot will have different tokens!
 */
public class SimpleExampleSendMessage {
	private SynologyChatConnector connector;
	
	public static void main(String[] args) throws Exception {
		SimpleExampleSendMessage example = new SimpleExampleSendMessage();
		example.sendPost();
	}
	
	public SimpleExampleSendMessage() throws IOException {
		//Find your best way to store Chat URL and bot Token
		ConfigProperties props = new ConfigProperties();
		props.load();
		
		//You need to create and pass chat URL and bot token to connector
		connector = new SynologyChatConnector();
		connector.initiate(props.getChatURL(), props.getChatToken());		
	}
	
	//Send message to user
	public void sendPost() throws Exception {
		String msgToSend = "No need to escape these: \"'=?&{}$#";
		
		connector.sendWebhookPost(msgToSend);
		
//		int userID = 4; //You need to find somehow what is id of your user - eg see SimpleExampleListUsers
//		connector.sendPostToBot(userID, msgToSend);
	}
}
