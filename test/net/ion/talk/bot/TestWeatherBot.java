package net.ion.talk.bot;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import junit.framework.TestCase;

public class TestWeatherBot extends TestCase {

	public void testParse() throws Exception {
		// {"coord":{"lon":126.98,"lat":37.56},"sys":{"message":0.2146,"country":"KR","sunrise":1401912665,"sunset":1401965406},
		// "weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"base":"cmc stations",
		// "main":{"temp":300.1,"pressure":1004,"humidity":50,"temp_min":297.15,"temp_max":308.15},"wind":{"speed":2.1,"deg":10},"rain":{"3h":0},"clouds":{"all":75},"dt":1401951600,"id":1837055,"name":"Yongsan","cod":200}
		NewClient nc = NewClient.create();
		WeatherResult wresult = new Weather(nc, "37.565", "71.977").execute() ;


		Debug.line(wresult.icon(), wresult.weather(), wresult.description(), wresult.temp(), wresult.tempRange(), wresult.humidity(), wresult.cityName());

		nc.close();

	}
}

