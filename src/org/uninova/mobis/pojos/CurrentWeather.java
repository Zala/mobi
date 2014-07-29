package org.uninova.mobis.pojos;

public class CurrentWeather {

	private String observationTime = "" ;
	private int temperature = 0 ;
	private int windSpeed = 0 ;
	private String windDirection = "" ;
	private int weatherCode = 0 ;
	private String description = "" ;
	private String weatherIcon = "" ;
	private int precipitation = 0 ;
	private double humidityPercentage = 0.0 ;
	private int visibility = 0 ;
	private int pressure = 0 ;
	private int cloudCover = 0 ;
	
	public CurrentWeather() {}

	public String getObservationTime() {
		return observationTime;
	}

	public void setObservationTime(String observationTime) {
		this.observationTime = observationTime;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public int getWeatherCode() {
		return weatherCode;
	}

	public void setWeatherCode(int weatherCode) {
		this.weatherCode = weatherCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWeatherIcon() {
		return weatherIcon;
	}

	public void setWeatherIcon(String weatherIcon) {
		this.weatherIcon = weatherIcon;
	}

	public int getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(int precipitation) {
		this.precipitation = precipitation;
	}

	public double getHumidityPercentage() {
		return humidityPercentage;
	}

	public void setHumidityPercentage(double humidityPercentage) {
		this.humidityPercentage = humidityPercentage;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public int getPressure() {
		return pressure;
	}

	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	public int getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(int cloudCover) {
		this.cloudCover = cloudCover;
	}
	
}
