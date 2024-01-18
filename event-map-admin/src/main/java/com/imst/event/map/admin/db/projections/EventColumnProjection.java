package com.imst.event.map.admin.db.projections;

import org.springframework.data.rest.core.config.Projection;

import com.imst.event.map.hibernate.entity.EventColumn;

@Projection(types = EventColumn.class)
public interface EventColumnProjection {
	
	Integer getId();
	String getName();
	

}
