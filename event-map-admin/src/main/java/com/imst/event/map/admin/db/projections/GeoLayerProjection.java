package com.imst.event.map.admin.db.projections;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public interface GeoLayerProjection {
	
	Integer getId();
	String getName();
	String getData();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getCreateDate();
	
	Integer getLayerId();
}
