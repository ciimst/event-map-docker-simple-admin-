package com.imst.event.map.admin.vo.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLastPageItem{
  

	private Integer userId;
    private String url;
    private String username;
    private String updateDate;
    public UserLastPageItem(Integer userId, String url, String username , String updateDate) {
		this.userId = userId;
		this.url = url;
		this.username = username;
		this.updateDate = updateDate;
		
	}
}