package com.imst.event.map.admin.db.projections;

import com.imst.event.map.hibernate.entity.EventGroup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = EventGroup.class)
public interface EventGroupProjectionBasic {
	
	Integer getId();
	String getName();
	String getColor();
	@Value("#{target.layer.id}")
	Integer getLayerId();
	String getDescription();
	Integer getParentId();
}
