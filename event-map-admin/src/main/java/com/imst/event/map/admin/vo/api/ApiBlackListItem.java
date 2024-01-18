package com.imst.event.map.admin.vo.api;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.hibernate.entity.BlackList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiBlackListItem {

	private Integer id;
	private String name;
	private String tag;
	private Integer layerId;
	private Integer eventGroupId;
	private Integer eventTypeId;
	private Boolean state;

	
	public ApiBlackListItem() {
		
	}
	
	public ApiBlackListItem(Integer id,String name,String tag,Boolean state,Integer layerId,Integer eventGroupId, Integer eventTypeId, Boolean actionState) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.state = state;
		this.layerId = layerId;
		this.eventGroupId = eventGroupId;
		this.eventTypeId = eventTypeId;		
	}
	

	
	public ApiBlackListItem(BlackList blackList) {
		this.id = blackList.getId();
		this.name = blackList.getName();
		this.tag =  blackList.getTag();
		this.state = StateE.getIntegerStateToBoolean(blackList.getState().getId());	
		this.layerId = blackList.getLayer().getId();
		this.eventGroupId = blackList.getEventGroup() != null ? blackList.getEventGroup().getId() : null;
		this.eventTypeId = blackList.getEventType() != null ? blackList.getEventType().getId() : null;

	}
	
	public static ApiBlackListItem newInstanceForLog(BlackList blackList) {
		ApiBlackListItem blackListItem = new ApiBlackListItem(blackList);
		return blackListItem;
	}
	
}
