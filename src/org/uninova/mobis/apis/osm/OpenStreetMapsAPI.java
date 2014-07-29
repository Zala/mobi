package org.uninova.mobis.apis.osm;

import java.util.ArrayList;

import org.uninova.mobis.pojos.OSMNode;

public interface OpenStreetMapsAPI {
	
	public ArrayList<OSMNode> getNodesInWay(String wayId) ;
	
	
}
