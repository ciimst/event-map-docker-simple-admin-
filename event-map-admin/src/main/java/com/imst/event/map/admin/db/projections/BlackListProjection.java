package com.imst.event.map.admin.db.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imst.event.map.hibernate.entity.BlackList;

import java.sql.Timestamp;

import org.springframework.data.rest.core.config.Projection;

@Projection(types = BlackList.class)
public interface BlackListProjection {
	
	Integer getId();
	String getName();
	String getTag();
	
	String getCreateUser();
	
	StateProjectionBasic getState();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getCreateDate();
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getUpdateDate();
	
	Integer getLayerId();
	
	Integer getEventGroupId();
	Integer getEventTypeId();
}
