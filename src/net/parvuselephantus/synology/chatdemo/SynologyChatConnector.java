package net.parvuselephantus.synology.chatdemo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import net.parvuselephantus.synology.chatdemo.msgTemplate.BotRequest;

/**
 * This is most important class of whole demo - here all the communication is placed.
 * It's not state of the art style (eg no error handling/logging) - it's just to show on example of working call
 * @author Krzysztof Mroczek
 */
@Component
public class SynologyChatConnector {
	
	/**
	 * Identifier of token/webook
	 */
	private String token;
	/**
	 * an URL of service starting with "https://..." and ending on ".../direct" eg:
	 * https://thisisexamplenameofsynologyserver.cz2.quickconnect.to/direct
	 */
	private String serverURL;
	
	private boolean initiated = false;
	
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	
	{
		//Don't push lots of debug stuff in console
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));
		for(String log : loggers) { 
		    Logger logger = (Logger) LoggerFactory.getLogger(log);
		    logger.setLevel(Level.INFO);
		    logger.setAdditive(false);
		}
	}
	
	public SynologyChatConnector() {}
	
	public SynologyChatConnector(String serverURL, String token) {
		this.serverURL = serverURL;
		this.token = token;
		initiated = true;
	}
	
	public void initiate(String serverURL, String token) {
		this.serverURL = serverURL;
		this.token = token;
		initiated = true;
	}
	
	public boolean isInitiated() {
		return initiated;
	}
	
	public JsonNode listChannels() {
		return sendToServer(serverURL + "/webapi/entry.cgi?api=SYNO.Chat.External&method=channel_list&version=2&token=%22" + token + "%22");
	}
	
	public JsonNode listUsers() {
		return sendToServer(serverURL + "/webapi/entry.cgi?api=SYNO.Chat.External&method=user_list&version=2&token=%22" + token + "%22");
	}
	
	public JsonNode listPosts(int channel_id, int next_count, int prev_count) {
		return listPosts(channel_id, next_count, prev_count, -1);
		
	}
	public JsonNode listPosts(int channel_id, int next_count, int prev_count, long post_id) {
		String url = serverURL + "/webapi/entry.cgi?api=SYNO.Chat.External&method=post_list&version=2&token=%22" + token + "%22"
				+ "&channel_id=" + channel_id
				+ "&next_count=" + next_count
				+ "&prev_count=" + prev_count;
		if (post_id != -1) {
			url = url + "&post_id=" + post_id;
		}
		return sendToServer(url);
	}
	
	
	private JsonNode sendToServer(String url) {
		try {
			JsonNode resp = sendPost(url, null);
			if (resp.get("success").asBoolean()) {
				return resp;
			} else {
				throw new RuntimeException("Request failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * In case we want to send webhook message (see synology webhook, which is only server->synology chat channel)
	 */
	public void sendWebhookPost(String post) throws Exception {
  	    ObjectMapper mapper = new ObjectMapper();

  	    ObjectNode user = mapper.createObjectNode();
  	    user.put("token", token);
  	    user.put("text", post);

  	    // convert `ObjectNode` to pretty-print JSON
  	    // without pretty-print, use `user.toString()` method
  	    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
  	    sendPost(serverURL + "/webapi/entry.cgi?api=SYNO.Chat.External&method=incoming&version=2&token=%22" + token + "%22", json);
	}
	/**
	 * In case you want to send message 'as a bot' 
	 */
	public void sendPostToBot(int userID, String post) throws Exception {
	    ObjectMapper mapper = new ObjectMapper();

	    ObjectNode user = mapper.createObjectNode();
	    user.put("token", token);
	    user.putArray("user_ids").add(userID);
	    user.put("text", post);

	    // convert `ObjectNode` to pretty-print JSON
	    // without pretty-print, use `user.toString()` method
	    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
	    sendPost(serverURL + "/webapi/entry.cgi?api=SYNO.Chat.External&method=chatbot&version=2&token=%22" + token + "%22", json);
	}
	

	public void close() throws IOException {
		httpClient.close();
	}
	
	private JsonNode sendPost(String url, String payload) throws Exception {
        HttpPost post = new HttpPost(url);

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("payload", payload == null ? "{\"token\": \"" + token + "\"}" : payload));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        
        post.addHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(post))
        {
        	ObjectMapper mapper = new ObjectMapper();
        	String jsonResponse = EntityUtils.toString(response.getEntity());
        	JsonNode jsonObject = mapper.readTree(jsonResponse);
        	if (jsonObject.get("success").asBoolean() == false) {
        		JsonNode error = jsonObject.get("error");
        		if (error == null)
        			throw new IOException("Chat connection failed");
        		else
        			throw new IOException(error.toPrettyString());
        		
        	}
        	return jsonObject;
        }
    }

	public BotRequest parseBotRequest(String payload) {
		BotRequest botRequest = new BotRequest();
				
		for (String val : payload.split("&")) {
			String[] v = val.split("=");
			
			if (v[0].equals("token")) {
				botRequest.setToken(v[1]);
			} else if (v[0].equals("user_id")) {
				try {
					botRequest.setUser_id(Integer.parseInt(v[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			} else if (v[0].equals("username")) {
				botRequest.setUsername(v[1]);
			} else if (v[0].equals("post_id")) {
				try {
					botRequest.setPost_id(Long.parseLong(v[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			} else if (v[0].equals("thread_id")) {
				try {
					botRequest.setThread_id(Integer.parseInt(v[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			} else if (v[0].equals("timestamp")) {
				try {
					botRequest.setTimestamp(Long.parseLong(v[1]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			} else if (v[0].equals("text")) {
				try {
					botRequest.setText(URLDecoder.decode( v[1], "UTF-8" ));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return botRequest;
	}
}
