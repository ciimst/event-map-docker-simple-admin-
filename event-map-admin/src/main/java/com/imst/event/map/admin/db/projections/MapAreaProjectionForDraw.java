package com.imst.event.map.admin.db.projections;

public interface MapAreaProjectionForDraw {
	
	Integer getId();
	String getTitle();
	String getCoordinateInfo();
	Integer getMapAreaGroupId();
	Integer getLayerId();
	Boolean getState();
	
}
