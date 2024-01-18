package com.imst.event.map.admin.db.projections;

import java.sql.Timestamp;

import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imst.event.map.hibernate.entity.EventLink;

@Projection(types = EventLink.class)
public interface EventLinkProjection {
	
	Integer getId();
	String getDisplayName();
	String getColor();
	String getLink();
	
	EventColumnProjection getEventColumn();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getCreateDate();
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getUpdateDate();
	
}
