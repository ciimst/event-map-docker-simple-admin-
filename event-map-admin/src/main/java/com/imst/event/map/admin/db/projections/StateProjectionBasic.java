package com.imst.event.map.admin.db.projections;

import org.springframework.data.rest.core.config.Projection;

import com.imst.event.map.hibernate.entity.State;

@Projection(types = State.class)
public interface StateProjectionBasic {
	
	Integer getId();
	String getStateType();
	

}
