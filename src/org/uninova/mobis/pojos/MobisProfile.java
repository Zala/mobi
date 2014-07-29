package org.uninova.mobis.pojos;

import java.util.ArrayList;

public class MobisProfile {

	private long profileId ;
	private boolean gpsDevice ;
	private boolean gpsService ;
	private boolean tripPlanner ;
	private boolean paperMap ;
	private String otherAssistant ;
	private boolean ownsCar ;
	private boolean ownsMotorbike ;
	private boolean ownsBike ;
	private boolean handicapped ;
	private boolean disableCar ;
	private boolean disableMotorbike ;
	private boolean disableBike ;
	private boolean disableTrain ;
	private boolean disableBus ;
	private boolean disableTaxi ;
	private boolean disableSubway ;
	private boolean disableWalk ;
	private int carFrequency ;
	private int motorbikeFrequency ;
	private int bikeFrequency ;
	private int trainFrequency ;
	private int busFrequency ;
	private int taxiFrequency ;
	private int subwayFrequency ;
	private int walkFrequency ;
	private int stressLevel ;
	private int trafficDisturbanceLevel ;
	private ArrayList<MobisVehicle> vehicles ;
	
	public MobisProfile () {}

	public boolean isGpsDevice() {
		return gpsDevice;
	}

	public void setGpsDevice(boolean gpsDevice) {
		this.gpsDevice = gpsDevice;
	}

	public boolean isGpsService() {
		return gpsService;
	}

	public void setGpsService(boolean gpsService) {
		this.gpsService = gpsService;
	}

	public boolean isTripPlanner() {
		return tripPlanner;
	}

	public void setTripPlanner(boolean tripPlanner) {
		this.tripPlanner = tripPlanner;
	}

	public boolean isPaperMap() {
		return paperMap;
	}

	public void setPaperMap(boolean paperMap) {
		this.paperMap = paperMap;
	}

	public String getOtherAssistant() {
		return otherAssistant;
	}

	public void setOtherAssistant(String otherAssistant) {
		this.otherAssistant = otherAssistant;
	}

	public boolean isOwnsCar() {
		return ownsCar;
	}

	public void setOwnsCar(boolean ownsCar) {
		this.ownsCar = ownsCar;
	}

	public boolean isOwnsMotorbike() {
		return ownsMotorbike;
	}

	public void setOwnsMotorbike(boolean ownsMotorbike) {
		this.ownsMotorbike = ownsMotorbike;
	}

	public boolean isOwnsBike() {
		return ownsBike;
	}

	public void setOwnsBike(boolean ownsBike) {
		this.ownsBike = ownsBike;
	}

	public boolean isHandicapped() {
		return handicapped;
	}

	public void setHandicapped(boolean handicapped) {
		this.handicapped = handicapped;
	}

	public boolean isDisableCar() {
		return disableCar;
	}

	public void setDisableCar(boolean disableCar) {
		this.disableCar = disableCar;
	}

	public boolean isDisableMotorbike() {
		return disableMotorbike;
	}

	public void setDisableMotorbike(boolean disableMotorbike) {
		this.disableMotorbike = disableMotorbike;
	}

	public boolean isDisableBike() {
		return disableBike;
	}

	public void setDisableBike(boolean disableBike) {
		this.disableBike = disableBike;
	}

	public boolean isDisableTrain() {
		return disableTrain;
	}

	public void setDisableTrain(boolean disableTrain) {
		this.disableTrain = disableTrain;
	}

	public boolean isDisableBus() {
		return disableBus;
	}

	public void setDisableBus(boolean disableBus) {
		this.disableBus = disableBus;
	}

	public boolean isDisableTaxi() {
		return disableTaxi;
	}

	public void setDisableTaxi(boolean disableTaxi) {
		this.disableTaxi = disableTaxi;
	}

	public boolean isDisableSubway() {
		return disableSubway;
	}

	public void setDisableSubway(boolean disableSubway) {
		this.disableSubway = disableSubway;
	}

	public boolean isDisableWalk() {
		return disableWalk;
	}

	public void setDisableWalk(boolean disableWalk) {
		this.disableWalk = disableWalk;
	}

	public int getCarFrequency() {
		return carFrequency;
	}

	public void setCarFrequency(int carFrequency) {
		this.carFrequency = carFrequency;
	}

	public int getMotorbikeFrequency() {
		return motorbikeFrequency;
	}

	public void setMotorbikeFrequency(int motorbikeFrequency) {
		this.motorbikeFrequency = motorbikeFrequency;
	}

	public int getBikeFrequency() {
		return bikeFrequency;
	}

	public void setBikeFrequency(int bikeFrequency) {
		this.bikeFrequency = bikeFrequency;
	}

	public int getTrainFrequency() {
		return trainFrequency;
	}

	public void setTrainFrequency(int trainFrequency) {
		this.trainFrequency = trainFrequency;
	}

	public int getBusFrequency() {
		return busFrequency;
	}

	public void setBusFrequency(int busFrequency) {
		this.busFrequency = busFrequency;
	}

	public int getTaxiFrequency() {
		return taxiFrequency;
	}

	public void setTaxiFrequency(int taxiFrequency) {
		this.taxiFrequency = taxiFrequency;
	}

	public int getSubwayFrequency() {
		return subwayFrequency;
	}

	public void setSubwayFrequency(int subwayFrequency) {
		this.subwayFrequency = subwayFrequency;
	}

	public int getWalkFrequency() {
		return walkFrequency;
	}

	public void setWalkFrequency(int walkFrequency) {
		this.walkFrequency = walkFrequency;
	}

	public int getStressLevel() {
		return stressLevel;
	}

	public void setStressLevel(int stressLevel) {
		this.stressLevel = stressLevel;
	}

	public int getTrafficDisturbanceLevel() {
		return trafficDisturbanceLevel;
	}

	public void setTrafficDisturbanceLevel(int trafficDisturbanceLevel) {
		this.trafficDisturbanceLevel = trafficDisturbanceLevel;
	}

	public ArrayList<MobisVehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(ArrayList<MobisVehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	
}
