package com.imst.event.map.admin.db.projections;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imst.event.map.hibernate.entity.Event;

@Projection(types = Event.class)
public interface EventProjectionMobile {
	
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
	
	
	String getReservedKey();
	String getReservedType();
	String getReservedId();
	String getReservedLink();
	
	
	@Value("#{target.eventType.id}")
	Integer getEventTypeId();
	

	@Value("#{target.eventGroup.id}")	
	Integer getEventGroupId();
	
	List<EventMediaProjection> getEventMedias();
	
}
