package com.imst.event.map.admin.db.projections;

import com.imst.event.map.hibernate.entity.EventTag;

import org.springframework.data.rest.core.config.Projection;

@Projection(types = EventTag.class)
public interface EventTagProjection {
	
	
	TagProjection getTag();
}
