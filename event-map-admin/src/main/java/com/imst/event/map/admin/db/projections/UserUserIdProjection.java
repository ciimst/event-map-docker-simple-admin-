package com.imst.event.map.admin.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserUserIdProjection {

	Integer getId();
	Integer getUserId();
	@Value("#{target.user.id}")
	Integer getFk_userId();
}
