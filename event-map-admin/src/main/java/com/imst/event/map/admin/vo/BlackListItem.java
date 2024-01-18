package com.imst.event.map.admin.vo;

import java.util.Date;

import javax.persistence.Column;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.datatables.EntitySortKey;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.BlackList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlackListItem {

	private Integer id;
	private String name;
	private String tag;
	private String createUser;
	private Integer layerId;
	private String layerName;
	private Integer eventGroupId;
	@EntitySortKey("eventGroupId")
	private String eventGroupName;
	private Integer eventTypeId;
	@EntitySortKey("eventTypeId")
	private String eventTypeName;
	private Date createDate;
	private Date updateDate;
	private Boolean state;//datatable aramaları için
	private Integer stateId;//değer kontrolü için 
	
	@Column(name = "actionState.state_type")
	private String actionStateType;
	@Column(name="actionState.id")
	private Integer actionStateId;
	
	private String createDateStr;
	private String updateDateStr;
	
	public BlackListItem() {
		
	}
	
	public BlackListItem(Integer id,String name,String tag,String createUser,Date createDate,Date updateDate,Integer stateId,Integer layerId,String layerName,Integer eventGroupId, Integer eventTypeId, Integer actionStateId, String actionStateType) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.createUser = createUser;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.stateId = stateId;
		this.layerId = layerId;
		this.layerName = layerName;
		this.eventGroupId = eventGroupId;
		this.eventTypeId = eventTypeId;
		this.actionStateId = actionStateId;
		this.actionStateType = actionStateType;
		
		this.state = StateE.getIntegerStateToBoolean(stateId); 

		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
		
	}
	

	
	public BlackListItem(BlackList blackList) {
		this.id = blackList.getId();
		this.name = blackList.getName();
		this.tag =  blackList.getTag();
		this.createUser = blackList.getCreateUser();
		this.createDate = blackList.getCreateDate();
		this.updateDate = blackList.getUpdateDate();
		
		this.stateId = blackList.getState().getId();
		
		this.state = StateE.getIntegerStateToBoolean(blackList.getState().getId());
		
		this.layerId = blackList.getLayer().getId();
		this.layerName = blackList.getLayer().getName();
		this.eventGroupId = blackList.getEventGroup() != null ? blackList.getEventGroup().getId() : null;
		this.eventGroupName = blackList.getEventGroup() != null ? blackList.getEventGroup().getName() : null;
		this.eventTypeId = blackList.getEventType() != null ? blackList.getEventType().getId() : null;
		this.eventTypeName = blackList.getEventType() != null ? blackList.getEventType().getName() : null;
		this.actionStateId = blackList.getActionState().getId();
		this.actionStateType = blackList.getActionState().getStateType();
		
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
		
	}
	
	public static BlackListItem newInstanceForLog(BlackList blackList) {
		BlackListItem blackListItem = new BlackListItem(blackList);
		return blackListItem;
	}
	
}
