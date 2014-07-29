package org.uninova.mobis.apis.osm;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.OSMNode;
import org.uninova.mobis.utils.HTTPUtilsImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OpenStreetMapsAPIImpl implements OpenStreetMapsAPI {
	
	public OpenStreetMapsAPIImpl() {}
	
	public void getOSMElementById(String elementType, String elementId) {}
	
	public void getWaysWithNode(String nodeId) {}
	
	public void getElementFullInfo(String elementType, String elementId) {}
	
	public ArrayList<OSMNode> getNodesInWay(String wayId) {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.OSM_API_URL + "way/" + wayId + "/full" ;
		ArrayList<OSMNode> nodes ;
		DocumentBuilderFactory dbFactory ;
		DocumentBuilder dBuilder ;
		Document doc ;
		String result ;
		NodeList nList ;
		Node nNode ;
		Element eNode ;
		OSMNode node ;
		try {
			result = httpUtils.requestURLConnection(url) ;
			dbFactory = DocumentBuilderFactory.newInstance() ;
			dBuilder = dbFactory.newDocumentBuilder() ;
			doc = dBuilder.parse(new InputSource(new StringReader(result))) ;
			doc.getDocumentElement().normalize();
			nList = doc.getElementsByTagName("node");
			nodes = new ArrayList<>() ;
			for (int i = 0; i < nList.getLength(); i++) {
				nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {	 
					eNode = (Element) nNode;
					node = new OSMNode(
							Long.parseLong(eNode.getAttribute("id")), 
							new Coordinate(
									Double.parseDouble(eNode.getAttribute("lat")), 
									Double.parseDouble(eNode.getAttribute("lon")))) ;
					nodes.add(node) ;
				}
			}
			return nodes ;
		} catch (IOException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
}
