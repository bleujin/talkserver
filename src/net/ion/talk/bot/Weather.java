package net.ion.talk.bot;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;


public class Weather {
	private NewClient nc;
	private String lat;
	private String lon;

	public Weather(NewClient nc, String lat, String lon) {
		this.nc = nc;
		this.lat = lat;
		this.lon = lon;
	}

	public WeatherResult execute() throws InterruptedException, ExecutionException, IOException {
		Response response = nc.prepareGet("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric").execute().get();
		JsonObject jso = JsonObject.fromString(response.getTextBody("UTF-8"));
		return new WeatherResult(jso) ;
	}
}