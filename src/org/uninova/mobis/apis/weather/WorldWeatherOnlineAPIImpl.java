package org.uninova.mobis.apis.weather;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.CurrentWeather;
import org.uninova.mobis.pojos.MobisWeatherEvent;
import org.uninova.mobis.pojos.PredictedWeather;
import org.uninova.mobis.utils.HTTPUtils;
import org.uninova.mobis.utils.HTTPUtilsImpl;

public class WorldWeatherOnlineAPIImpl {

	public WorldWeatherOnlineAPIImpl() {}
	
	public MobisWeatherEvent getLocalWeather(boolean isCoord, Coordinate coord, String locale, int numberOfDays, String date) throws IOException {
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
		MobisWeatherEvent weather = new MobisWeatherEvent() ;
		CurrentWeather currWeather = new CurrentWeather() ;
		PredictedWeather predWeather = new PredictedWeather() ;
		String query = "", url = "", res = "" ;
		JSONObject obj, request, curr, pred ;
		Object aux ;
		
		if (isCoord) {
			query = coord.getLat() + "," + coord.getLng() ;
		}
		else {
			query = locale ;
		}
		
		url = StringConstants.WORLD_WEATHER_URL + "q=" + query + "&num_of_days=" + numberOfDays + "&date=" + date + "&includeLocation=yes&format=json&key=" + StringConstants.WORLD_WEATHER_KEY ;
		res = httpUtils.requestURLConnection(url) ;
		
		obj = JSONObject.fromObject(res) ;
		obj = obj.getJSONObject("data") ;
		request = obj.getJSONObject("request") ;
		weather.setQuery(request.getString("query")) ;
		weather.setQueryType(request.getString("type")) ;
		
		curr = obj.getJSONObject("current_condition") ;
		currWeather.setTemperature(curr.getInt("temp_C")) ;
		currWeather.setObservationTime(curr.getString("observation_time")) ;
		currWeather.setWindSpeed(curr.getInt("windspeedKmph")) ;
		currWeather.setWindDirection(curr.getString("winddir16Point")) ;
		currWeather.setWeatherCode(curr.getInt("weatherCode")) ;
		currWeather.setDescription(curr.getString("weatherDesc")) ;
		currWeather.setWeatherIcon(curr.getString("weatherIconUrl")) ;
		currWeather.setPrecipitation(curr.getInt("precipMM")) ;
		currWeather.setHumidityPercentage(curr.getDouble("humidity")) ;
		currWeather.setVisibility(curr.getInt("visibility")) ;
		currWeather.setPressure(curr.getInt("pressure")) ;
		currWeather.setCloudCover(curr.getInt("cloudcover")) ;
		
		aux = obj.get("weather") ;
		
		if (aux instanceof JSONObject) {
			pred = obj.getJSONObject("weather") ;
			predWeather.setDate(pred.getString("date")) ;
			predWeather.setMaxTemperature(pred.getInt("tempMaxC")) ;
			predWeather.setMinTemperature(pred.getInt("tempMinC")) ;
			predWeather.setWindSpeed(pred.getInt("windspeedKmph")) ;
			predWeather.setWindDirection(pred.getString("winddir16Point")) ;
			predWeather.setWeatherCode(pred.getInt("weatherCode")) ;
			predWeather.setDescription(pred.getString("weatherDesc")) ;
			predWeather.setWeatherIcon(pred.getString("weatherIconUrl")) ;
			predWeather.setPrecipitation(pred.getInt("precipMM")) ;
		}
		
		weather.setCurr(currWeather) ;
		weather.setForecast(predWeather) ;
		
		return weather ;
	}
}
