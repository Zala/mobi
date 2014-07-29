package org.uninova.mobis.pojos;

import java.sql.Date;
import java.sql.Time;

public class MobisPrometEvent {

	private long id ;
	private Coordinate point ;
	private String category ;
	private String road ;
	private String type ;
	private String description ;
	private Date validFromDate ;
	private Time validFromTime ;
	private Date validToDate ;
	private Time validToTime ;
	private String section ;
	private String distance ;
	private int priority ;
	private int roadPriority ;
	private boolean isBorder ;
	private String icon ;
	private Date versionDate ;
	private Time versionTime ;
	
	public MobisPrometEvent() {}

	public Coordinate getPoint() {
		return point;
	}

	public void setPoint(Coordinate point) {
		this.point = point;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getValidFromDate() {
		return validFromDate;
	}

	public void setValidFromDate(Date validFromDate) {
		this.validFromDate = validFromDate;
	}

	public Time getValidFromTime() {
		return validFromTime;
	}

	public void setValidFromTime(Time validFromTime) {
		this.validFromTime = validFromTime;
	}

	public Date getValidToDate() {
		return validToDate;
	}

	public void setValidToDate(Date validToDate) {
		this.validToDate = validToDate;
	}

	public Time getValidToTime() {
		return validToTime;
	}

	public void setValidToTime(Time validToTime) {
		this.validToTime = validToTime;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getRoadPriority() {
		return roadPriority;
	}

	public void setRoadPriority(int roadPriority) {
		this.roadPriority = roadPriority;
	}

	public boolean isBorder() {
		return isBorder;
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public Time getVersionTime() {
		return versionTime;
	}

	public void setVersionTime(Time versionTime) {
		this.versionTime = versionTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
