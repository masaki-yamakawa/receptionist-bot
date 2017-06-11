package hackathon.receptionist.cognitive;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import hackathon.receptionist.AppConfig;
import hackathon.receptionist.cognitive.Luis.LuisResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
public class LuisTest {

	@Autowired
	private Luis luis;

	@Value("${luis.appid}")
	private String appId;

	@Value("${luis.subscription}")
	private String subscription;

	@Test
	public void testLuis() throws Exception {
		LuisResult result = luis.getIntent(appId, subscription, "ラーメン食べたい");

		if (result == null)
			fail("LuisResult is null.");
		assertThat(result.topScoringIntent.intent, is("めし屋の提案"));
		assertThat(result.entities.get(0).entity, is("ラーメン"));
	}

	@Test
	public void testLuis2() throws Exception {
		LuisResult result = luis.getIntent(appId, subscription, "明後日は何曜日なんだろう");

		if (result == null)
			fail("LuisResult is null.");
		assertThat(result.topScoringIntent.intent, is("曜日応答"));
		assertThat(result.entities.get(0).entity, is("ラーメン"));
	}
}
