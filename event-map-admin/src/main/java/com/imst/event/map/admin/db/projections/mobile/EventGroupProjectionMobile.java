package com.imst.event.map.admin.db.projections.mobile;

import com.imst.event.map.hibernate.entity.EventGroup;

import org.springframework.data.rest.core.config.Projection;

@Projection(types = EventGroup.class)
public interface EventGroupProjectionMobile {
	
	Integer getId();
	String getName();
	String getColor();
	Integer getLayerId();
}
