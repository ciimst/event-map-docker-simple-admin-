package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.UserEventGroupPermissionProjection;
import com.imst.event.map.admin.db.projections.UserEventGroupPermissionProjection2;
import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;

public interface UserEventGroupPermissionRepository extends ProjectionRepository<UserEventGroupPermission, Integer> {
	
	Page<UserEventGroupPermissionProjection> findAllProjectedBy(Pageable pageable);	
	UserEventGroupPermissionProjection findProjectedById(Integer id);
	

	UserEventGroupPermission findByUserIdAndEventGroupId(Integer userId, Integer eventGroupId);
	
	List<UserEventGroupPermission> findByEventGroupId(Integer eventGroupId);
	
	
	List<UserEventGroupPermissionProjection2> findAllProjectedByUser(User user);
	
}
