package org.uninova.mobis.database;

import java.sql.Connection;
import java.util.ArrayList;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisTrafficSituation;
import org.uninova.mobis.utils.DBUtils;

/**
 * INTERFACE MobisTrafficDBConnector
 * @author PAF@UNINOVA
 */
public interface MobisTrafficDBConnector {

	/**
	 * Fetches all situations from the traffic event database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the traffic event database
	 * @return			The {@link java.util.ArrayList} object containing all traffic situations
	 */
	public ArrayList<MobisTrafficSituation> getAllSituations(DBUtils dbUtils, Connection con, String country) ;
	
	/**
	 * Fetches all situations of a given category from the traffic event database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the traffic event database
	 * @param types			A {@link java.lang.String} object containing the traffic events' category types, separated by a comma (,)
	 * @param getRelated	If true, also fetches the related situations to the ones of the categories given in <code>types</code>
	 * @return				The {@link java.util.ArrayList} object containing all traffic situations 
	 */
	public ArrayList<MobisTrafficSituation> getSituationsByCategory(DBUtils dbUtils, Connection con, String types, boolean getRelated, String country) ;
	
	/**
	 * Fetches all situations of a given category from the traffic event database, which are located on the given coordinates and comprise the given trip time
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the traffic event database
	 * @param types			A {@link java.lang.String} object containing the traffic events' category types, separated by a comma (,)
	 * @param getRelated	If true, also fetches the related situations to the ones of the categories given in <code>types</code>
	 * @param coords		The {@link java.util.ArrayList} object with the coordinates to locate the situations
	 * @param startDateTime	The time for the trip
	 * @return				The {@link java.util.ArrayList} object containing all traffic situations 
	 */
	public ArrayList<MobisTrafficSituation> getLocalizedSituationsByCategory(DBUtils dbUtils, Connection con, String types, boolean getRelated, ArrayList<Coordinate> coords, String startDateTime) ;
}
