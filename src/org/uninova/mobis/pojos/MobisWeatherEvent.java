package org.uninova.mobis.pojos;

public class MobisWeatherEvent {

	private String query ;
	private String queryType ;
	private CurrentWeather curr ;
	private PredictedWeather forecast ;
	
	public MobisWeatherEvent() {}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public CurrentWeather getCurr() {
		return curr;
	}

	public void setCurr(CurrentWeather curr) {
		this.curr = curr;
	}

	public PredictedWeather getForecast() {
		return forecast;
	}

	public void setForecast(PredictedWeather forecast) {
		this.forecast = forecast;
	}
	
}
