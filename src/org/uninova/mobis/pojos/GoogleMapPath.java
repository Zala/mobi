package org.uninova.mobis.pojos;

import java.util.ArrayList;

public class GoogleMapPath {

	private int weight ;
	private String color ;
	private ArrayList<Coordinate> coords ;
	
	public GoogleMapPath() {}
	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public ArrayList<Coordinate> getCoords() {
		return coords;
	}
	public void setCoords(ArrayList<Coordinate> coords) {
		this.coords = coords;
	}
}
