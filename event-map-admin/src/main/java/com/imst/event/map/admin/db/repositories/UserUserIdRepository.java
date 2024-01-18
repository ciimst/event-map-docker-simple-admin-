package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.UserUserIdProjection;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserUserId;

public interface UserUserIdRepository extends ProjectionRepository<UserUserId, Integer>{

	Page<UserUserIdProjection> findAllProjectedBy(Pageable pageable);	
	UserUserIdProjection findProjectedById(Integer id);
	
	UserUserId findByUserAndUserId(User fk_userId, Integer userId);
	
	
	List<UserUserId> findAllByUser(User user);
}
