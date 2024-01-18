package com.imst.event.map.admin.vo;

import com.imst.event.map.hibernate.entity.EventMedia;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMediaItem {

	private Integer id;
	private Integer eventId;
	private String path;
	private String coverImagePath;
	private Boolean isVideo;
	
	public EventMediaItem() {
		
	}
	
	public EventMediaItem(Integer id, Integer eventId, String path, String coverImagePath,Boolean isVideo) {
		this.id = id;
		this.eventId = eventId;
		this.path = path;
		this.coverImagePath = coverImagePath;
		this.isVideo = isVideo;
	}
	
	public EventMediaItem(EventMedia eventMedia) {
		this.id = eventMedia.getId();
		this.eventId = eventMedia.getEvent().getId();
		this.path = eventMedia.getPath();
		this.coverImagePath = eventMedia.getCoverImagePath();
		this.isVideo = eventMedia.getIsVideo();
	}
	
public static EventMediaItem newInstanceForLog(EventMedia eventMedia) {
	EventMediaItem eventMediaItem = new EventMediaItem(eventMedia);
	return eventMediaItem;
}

}
