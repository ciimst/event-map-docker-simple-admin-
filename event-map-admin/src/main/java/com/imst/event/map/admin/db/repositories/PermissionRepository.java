package com.imst.event.map.admin.db.repositories;

import java.util.List;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.hibernate.entity.Permission;


public interface PermissionRepository extends ProjectionRepository<Permission, Integer> {
	
	List<Permission> findAllByState(Boolean state);
}
