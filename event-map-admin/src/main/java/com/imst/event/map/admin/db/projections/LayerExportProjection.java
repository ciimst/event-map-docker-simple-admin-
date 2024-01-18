package com.imst.event.map.admin.db.projections;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface LayerExportProjection {
	Integer getId();
	
	Integer getMinZ();
	Integer getMaxZ();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getCreateDate();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getStartDate();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getFinishDate();
	
	LayerProjection getLayer();
}
