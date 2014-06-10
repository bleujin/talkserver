package net.ion.talk.bot;

import net.ion.framework.parse.gson.JsonObject;

public class WeatherResult {
	private JsonObject jso;

	WeatherResult(JsonObject jso) {
		this.jso = jso;
	}

	public String icon() {
		return jso.asJsonArray("weather").get(0).getAsJsonObject().asString("icon");
	}

	public String weather() {
		return jso.asJsonArray("weather").get(0).getAsJsonObject().asString("main");
	}

	public String description() {
		return jso.asJsonArray("weather").get(0).getAsJsonObject().asString("description");
	}

	public String temp() {
		return jso.asJsonObject("main").asString("temp");
	}

	public String tempRange() {
		return jso.asJsonObject("main").asString("temp_min") + "-" + jso.asJsonObject("main").asString("temp_max");
	}

	public String humidity() {
		return jso.asJsonObject("main").asString("humidity");
	}

	
	public String cityName() {
		return jso.asString("name") ;
	}


}