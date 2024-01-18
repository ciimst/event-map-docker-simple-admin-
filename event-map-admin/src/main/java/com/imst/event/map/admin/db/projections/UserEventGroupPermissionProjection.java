package com.imst.event.map.admin.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserEventGroupPermissionProjection {

	Integer getId();
	
	@Value("#{target.user.id}")
	Integer getFk_userId();
	
	@Value("#{target.eventGroup.id}")
	Integer getFk_eventGroupId();
}
