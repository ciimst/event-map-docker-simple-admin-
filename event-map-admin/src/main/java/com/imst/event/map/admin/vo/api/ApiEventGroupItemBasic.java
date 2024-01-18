package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.EventGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventGroupItemBasic {
	
	private Integer id;
	private String name;
	private String color;
	private Integer layerId;
	private String description;
	private Integer parentId;
	
	public ApiEventGroupItemBasic() {
	
	}
	
	public ApiEventGroupItemBasic(EventGroup eventGroup) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerId = eventGroup.getLayer().getId();
		description = eventGroup.getDescription();
		parentId = eventGroup.getParentId();
	}
	
	public static ApiEventGroupItemBasic newInstanceForLog(EventGroup eventGroup) {
		
		return new ApiEventGroupItemBasic(eventGroup);
	}
}
