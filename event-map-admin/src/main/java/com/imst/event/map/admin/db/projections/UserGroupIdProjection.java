package com.imst.event.map.admin.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserGroupIdProjection {
	
	Integer getId();
	Integer getGroupId();	
	@Value("#{target.user.id}")
	Integer getFk_userId();
}
