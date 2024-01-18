package com.imst.event.map.admin.db.projections;

import java.sql.Timestamp;

public interface TileServerProjection {
	
	Integer getId();
	String getName();
	String getUrl();
	Timestamp getCreateDate();
	Timestamp getUpdateDate();
}
