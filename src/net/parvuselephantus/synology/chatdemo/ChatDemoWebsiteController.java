package net.parvuselephantus.synology.chatdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.parvuselephantus.synology.chatdemo.msgTemplate.BotRequest;

/**
 * Here is an example on how you can handle message from Bot/server
 * @author Krzysztof Mroczek
 */
@CrossOrigin(maxAge = 1, allowCredentials = "true")
@RestController
public class ChatDemoWebsiteController {
	
	@Value("${chatdemo.chat.url}")
	private String chatURL;
	
	@Value("${chatdemo.chat.token}")
	private String chatToken;
	
	@Autowired
	private SynologyChatConnector connector;
	
	@RequestMapping(method=RequestMethod.POST, value="/onSynologyChatRequest")
	public String getSynologyChatRequest(@RequestBody String payload) throws Exception {
		if (!connector.isInitiated()) connector.initiate(chatURL, chatToken);
		try {
			BotRequest request = connector.parseBotRequest(payload);
			
			//It's up to you to make logic on user's request
			if (request != null) {
				connector.sendPostToBot(request.getUser_id(), "!@#$%^&*()\"'{}¿æ¿œó³`~I got: " + request.getText());
			}
			return "{\"success\":true}";
		} catch (Exception e) {
			return "{\"success\":false}";
		}
	}

}
