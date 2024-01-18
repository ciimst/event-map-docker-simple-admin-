package com.imst.event.map.admin.vo.api;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiMapAreaItem {
	
	private Integer id;
	private Timestamp createDate;
	private Timestamp updateDate;
	private boolean state = true;
	
	private String title;
	private String coordinateInfo;
		
	private ApiMapAreaGroupItem apiMapAreaGroupItem;

	public ApiMapAreaItem() {
	
	}	
}
