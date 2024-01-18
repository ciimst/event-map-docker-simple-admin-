package com.imst.event.map.admin.db.projections;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imst.event.map.hibernate.entity.Event;

import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = Event.class)
public interface EventProjection {
	
	Integer getId();
	Double getLatitude();
	Double getLongitude();
	String getTitle();
	String getSpot();
	String getDescription();
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getEventDate();
	String getCreateUser();
	
	String getCity();
	String getCountry();
	
	StateProjectionBasic getState();
	
	String getReservedKey();
	String getReservedType();
	String getReservedId();
	String getReservedLink();
	
	String getReserved1();
	String getReserved2();
	String getReserved3();
	String getReserved4();
	String getReserved5();
	
	Integer getGroupId();
	Integer getUserId();
	
	EventTypeProjection getEventType();
	

	EventGroupProjectionBasic getEventGroup();
	List<EventMediaProjection> getEventMedias();
	List<EventTagProjection> getEventTags();
}
