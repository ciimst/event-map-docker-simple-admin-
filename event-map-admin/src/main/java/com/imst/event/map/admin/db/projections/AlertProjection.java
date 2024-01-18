package com.imst.event.map.admin.db.projections;

import org.springframework.data.rest.core.config.Projection;

import com.imst.event.map.hibernate.entity.Alert;

@Projection(types = Alert.class)
public interface AlertProjection {
	
	Integer getId();
	String getName();
	Integer getLayerId();
	String getLayerName();
	
	
}
