package com.imst.event.map.admin.vo;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBlackListItem {

	private Integer id;
	private Integer eventId;
	
	@Column(name="event.state.id")
	private Integer eventStateId;
	private Integer blackListId;
	
	public EventBlackListItem() {
		
	}
	
	public EventBlackListItem(Integer id, Integer eventId, Integer blackListId, Integer eventStateId) {
		this.id = id;
		this.eventId = eventId;
		this.blackListId = blackListId;
		this.eventStateId = eventStateId;
	
	}
	

}
