package org.uninova.mobis.apis.venues;

import java.util.HashMap;
import java.util.Iterator;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.Coordinate;

import com.factual.driver.Circle;
import com.factual.driver.Factual;
import com.factual.driver.Geopulse;
import com.factual.driver.MatchQuery;
import com.factual.driver.Point;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;
import com.factual.driver.ResolveQuery;

public class FactualAPIImpl implements FactualAPI{
	
	public FactualAPIImpl() {}

	public String search(String opt, String filters, String requiredFields, boolean geoFilter, Coordinate coord, int radius, boolean rowCount, int limit, int offset, String query, String sort) {
		Factual factualAPI = new Factual(StringConstants.FACTUAL_KEY, StringConstants.FACTUAL_SECRET) ;
		Query q = new Query() ;
		String[] filterStrings ;
		String filter, field, oper, value ;
		Object[] values ;
		
		if (filters != null && !filters.equals("")) {
			if (filters.contains(";")) {
				filterStrings = filters.split(";") ;
				for (int i = 0; i < filterStrings.length; i++) {
					filter = filterStrings[i] ;
					
					if (filter.indexOf("_") == filter.lastIndexOf("_")) {
						// FILTER WITHOUT VALUE CONSTRAINTS - blank and notBlank
						oper = filter.substring(0, filter.indexOf("_")) ;
						field = filter.substring(filter.indexOf("_") + 1) ;
						if (oper.equals("blank")) {
							q.field(field).blank() ;
						}
						else if (oper.equals("notBlank")) {
							q.field(field).notBlank() ;
						}
					}
					else {
						// FILTER WITH VALUE CONSTRAINTS
						oper = filter.substring(0, filter.indexOf("_")) ;
						filter = filter.substring(filter.indexOf("_") + 1) ;
						field = filter.substring(0, filter.indexOf("_")) ;
						value = filter.substring(filter.indexOf("_") + 1) ;
						
						switch (oper) {
							case "equal": q.field(field).equals(value) ; break ;
							case "notEqual": q.field(field).notEqual(value) ; break ;
							case "search": q.field(field).search(value) ; break ;
							case "in": 
								if (!value.contains(":")) { 
									q.field(field).in(value) ;
								}
								else {
									values = value.split(":") ;
									q.field(field).in(values) ;
								}
								break ;
							case "notIn":
								if (!value.contains(":")) { 
									q.field(field).notIn(value) ;
								}
								else {
									values = value.split(":") ;
									q.field(field).notIn(values) ;
								}
								break ;
							case "beginsWith": q.field(field).beginsWith(value) ; break ;
							case "notBeginsWith": q.field(field).notBeginsWith(value) ; break ;
							case "beginsWithAny":
								values = value.split(":") ;
								q.field(field).beginsWithAny(values) ;
								break ;
							case "notBeginsWithAny":
								values = value.split(":") ;
								q.field(field).notBeginsWithAny(values) ;
								break ;
							case "greaterThan": q.field(field).greaterThan(value) ; break ;
							case "greaterThanOrEqual": q.field(field).greaterThanOrEqual(value) ; break ;
							case "lessThan": q.field(field).lessThan(value) ; break ;
							case "lessThanOrEqual": q.field(field).lessThanOrEqual(value) ; break ;
							case "includes": q.field(field).includes(value) ; break ;
							case "includesAny": 
								values = value.split(":") ;
								q.field(field).includesAny(values) ;
								break ;
						}
					}
				}
			}
		}
		if (requiredFields != null && !requiredFields.equals("")){
			values = requiredFields.split(";") ;
			q.only((String[]) values) ;
		}
		if (rowCount) {
			q.includeRowCount() ;
		}
		if (geoFilter) {
			q.within(new Circle(coord.getLat(), coord.getLng(), radius)) ;
		}
		if (limit > 0) {
			q.limit(limit) ;
		}
		if (offset > 0) {
			q.offset(offset) ;
		}
		if (query != null && !query.equals("")) {
			q.search(query) ;
		}
		if (sort != null && !sort.equals("")) {
			if (sort.contains(";")) {
				values = sort.split(";") ;
				for (int i = 0; i < values.length; i++) {
					field = sort.substring(0, sort.indexOf("_")) ;
					oper = sort.substring(sort.indexOf("_") + 1) ;
					if (oper.equals("asc")) {
						q.sortAsc(field) ;
					}
					else if (oper.equals("desc")) {
						q.sortDesc(field) ;
					}
				}
			}
		}
		
		ReadResponse resp = factualAPI.fetch(opt, q) ;
		return resp.getJson() ;
	}
	
	public String match(String table, HashMap<String, String> matchParams) {
		Factual factualAPI = new Factual(StringConstants.FACTUAL_KEY, StringConstants.FACTUAL_SECRET) ;
		Iterator<String> fieldsIter = matchParams.keySet().iterator() ;
		MatchQuery q = new MatchQuery() ;
		String field ;
		
		while (fieldsIter.hasNext()) {
			field = fieldsIter.next() ;
			q.add(field, matchParams.get(field)) ;
		}
		
		return factualAPI.match(table, q) ;
	}
	
	public String resolve(String table, HashMap<String, String> matchParams) {
		Factual factualAPI = new Factual(StringConstants.FACTUAL_KEY, StringConstants.FACTUAL_SECRET) ;
		Iterator<String> fieldsIter = matchParams.keySet().iterator() ;
		ResolveQuery q = new ResolveQuery() ;
		String field ;
		
		while (fieldsIter.hasNext()) {
			field = fieldsIter.next() ;
			q.add(field, matchParams.get(field)) ;
		}
		
		ReadResponse resp = factualAPI.resolves(table, q) ;
		return resp.getJson() ;
	}
	
	public String geopulse(Coordinate coord, String requiredFields) {
		Factual factualAPI = new Factual(StringConstants.FACTUAL_KEY, StringConstants.FACTUAL_SECRET) ;
		ReadResponse resp = factualAPI.geopulse(new Geopulse(new Point(coord.getLat(), coord.getLng()))) ;
		return resp.getJson() ;
	}
}
