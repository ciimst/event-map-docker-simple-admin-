package com.imst.event.map.admin.db.projections;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public interface MapAreaProjection {
	
	Integer getId();
	String getTitle();
	String getCoordinateInfo();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getCreateDate();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getUpdateDate();
	
	Integer getMapAreaGroupId();
}
