package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.ProfilePermissionProjection;
import com.imst.event.map.hibernate.entity.ProfilePermission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfilePermissionRepository extends ProjectionRepository<ProfilePermission, Integer> {
	
	
	Page<ProfilePermissionProjection> findAllProjectedBy(Pageable pageable);
	
	List<ProfilePermission> findAllByProfileId(Integer id);
	
	List<ProfilePermission> findAllByProfileIdAndPermissionId(Integer profileId, Integer permissionId);
	
	List<ProfilePermission> findPermissionIdByProfileId(Integer id);
}
