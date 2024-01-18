package com.imst.event.map.admin.vo.api;

import com.imst.event.map.hibernate.entity.EventMedia;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEventMediaItem {
	
	private Integer id;
	private String path;
	private String coverImagePath;
	private boolean isVideo;
	
	public ApiEventMediaItem() {
	
	}
	
	public ApiEventMediaItem(EventMedia eventMedia) {
		
		this.id = eventMedia.getId();
		this.path = eventMedia.getPath();
		this.coverImagePath = eventMedia.getCoverImagePath();
		this.isVideo = eventMedia.getIsVideo();
	}
}
