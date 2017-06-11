package hackathon.receptionist.cognitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class Luis {

	private static final Logger logger = LoggerFactory.getLogger(Luis.class);

	private static final Moshi MOSHI = new Moshi.Builder().build();
	private static final JsonAdapter<LuisResult> ADAPTER = MOSHI.adapter(LuisResult.class);

	@Value("${luis.endpoint}")
	private String endpoint;

	public LuisResult getIntent(String appId, String subscription, String query) throws IOException {
		Request request = new Request.Builder()
				.url(String.format("%s%s?subscription-key=%s&verbose=true&timezoneOffset=0&q=%s", endpoint, appId, subscription, query))
				.get()
				.build();

		LuisResult result = null;
		Response response = null;
		try {
			OkHttpClient client = new OkHttpClient();
			response = client.newCall(request).execute();
			if (response.isSuccessful())
				result = ADAPTER.fromJson(response.body().source());
			else
				logger.warn(response.body().string());
		} catch (IOException e) {
			throw e;
		} finally {
			if (response != null)
				response.close();
		}
		return result;
	}

	public static class LuisResult {
		String query;
		Intent topScoringIntent;
		List<Intent> intents = new ArrayList<>();
		List<Entity> entities = new ArrayList<>();

		@Override
		public String toString() {
			return "LuisResult [query=" + query + ", topScoringIntent=" + topScoringIntent + ", intents=" + intents + ", entities="
					+ entities + "]";
		}
	}

	public static class Intent {
		String intent;
		double score;

		@Override
		public String toString() {
			return "Intent [intent=" + intent + ", score=" + score + "]";
		}
	}

	public static class Entity {
		String entity;
		String type;
		int startIndex;
		int endIndex;
		double score;
	}
}
