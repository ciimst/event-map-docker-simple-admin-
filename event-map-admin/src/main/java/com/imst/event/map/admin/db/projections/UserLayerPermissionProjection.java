package com.imst.event.map.admin.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserLayerPermissionProjection {

	Integer getId();
	
	@Value("#{target.user.id}")
	Integer getFk_userId();
	
	@Value("#{target.layer.id}")
	Integer getFk_layerId();
}
