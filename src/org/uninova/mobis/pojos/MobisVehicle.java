package org.uninova.mobis.pojos;

public class MobisVehicle {

	private String vehicleType ;
	private double vehicleEngine ;
	private int carType ;
	private int fuelType ;
	
	public MobisVehicle () {}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public double getVehicleEngine() {
		return vehicleEngine;
	}

	public void setVehicleEngine(double vehicleEngine) {
		this.vehicleEngine = vehicleEngine;
	}

	public int getCarType() {
		return carType;
	}

	public void setCarType(int carType) {
		this.carType = carType;
	}

	public int getFuelType() {
		return fuelType;
	}

	public void setFuelType(int fuelType) {
		this.fuelType = fuelType;
	}
	
}
