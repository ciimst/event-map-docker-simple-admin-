package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.UserGroupIdProjection;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;

public interface UserGroupIdRepository extends ProjectionRepository<UserGroupId, Integer>{
	
	
	Page<UserGroupIdProjection> findAllProjectedBy(Pageable pageable);	
	UserGroupIdProjection findProjectedById(Integer id);
	
	UserGroupId findByUserIdAndGroupId(Integer userId, Integer groupId);
	
	List<UserGroupId> findAllByUser(User user);
}
