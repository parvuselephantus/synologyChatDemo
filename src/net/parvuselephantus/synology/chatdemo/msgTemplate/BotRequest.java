package net.parvuselephantus.synology.chatdemo.msgTemplate;

/**
 * Your bot will call your backend with message - this is template to map the message into. 
 */
public class BotRequest {
	private int user_id;
	private String username;
	private long post_id;
	private int thread_id;
	private long timestamp;
	private String text;
	private String token;
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public long getPost_id() {
		return post_id;
	}
	public void setPost_id(long post_id) {
		this.post_id = post_id;
	}
	
	public int getThread_id() {
		return thread_id;
	}
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	public String getToken() {
		return token;
	}
}
