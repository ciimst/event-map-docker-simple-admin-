package com.imst.event.map.admin.db.projections;

import com.imst.event.map.hibernate.entity.EventGroup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = EventGroup.class)
public interface FakeLayerIdProjection {
	
	Integer getId();
	String getRoleId();

	@Value("#{target.layer.id}")
	Integer getLayerId();
	
	@Value("#{target.layer.name}")
	String getLayerName();

}
