package com.imst.event.map.admin.db.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.imst.event.map.hibernate.entity.EventMedia;

@Projection(types = EventMedia.class)
public interface EventMediaProjection {
	
	Integer getId();
	
	@Value("#{T(com.imst.event.map.admin.constants.Statics).settings.get(\"mediaPath\") + target.path}")
	String getPath();
	@Value("#{T(com.imst.event.map.admin.constants.Statics).settings.get(\"mediaPath\") + target.coverImagePath}")
	String getCoverImagePath();
	Boolean getIsVideo();
}
