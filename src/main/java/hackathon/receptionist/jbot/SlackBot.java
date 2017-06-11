package hackathon.receptionist.jbot;

import java.io.IOException;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import hackathon.receptionist.cognitive.Luis;
import hackathon.receptionist.cognitive.Luis.LuisResult;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;

@Component
public class SlackBot extends Bot {

//	private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    @Autowired
	private Luis luis;

	@Value("${luis.appid}")
	private String appId;

	@Value("${luis.subscription}")
	private String subscription;

	@Value("${slackBotToken}")
	private String slackToken;

	@Override
	public String getSlackToken() {
		return slackToken;
	}

	@Override
	public Bot getSlackBot() {
		return this;
	}

	@Controller(events = { EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE })
	public void onReceiveDM(WebSocketSession session, Event event) {
		reply(session, event, new Message("こんにちは、ぼくは " + slackService.getCurrentUser().getName() + " です。あなたをお手伝いします。"));
	}

// @Controller(events = EventType.MESSAGE, pattern = "^([a-z ]{2})(\\d+)([a-z ]{2})$")
	@Controller(events = EventType.MESSAGE)
	public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
		LuisResult res = null;
		try {
			res = luis.getIntent(appId, subscription, "ラーメン食べたい");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		reply(session, event, new Message("LUISに問い合わせします:" + res.toString()));
	}

	@Controller(events = EventType.PIN_ADDED)
	public void onPinAdded(WebSocketSession session, Event event) {
		reply(session, event, new Message("ピン留めありがとう！"));
	}
}