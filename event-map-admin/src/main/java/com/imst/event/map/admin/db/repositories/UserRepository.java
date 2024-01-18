package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.UserPermissionProjection;
import com.imst.event.map.admin.db.projections.UserProjection;
import com.imst.event.map.hibernate.entity.User;

public interface UserRepository extends ProjectionRepository<User, Integer> {
	
	User findByUsername(String username);
	User findByUsernameAndIsDbUser(String username, boolean isDbUser);
	
	User findByUsernameAndIsDbUserAndState(String username, boolean isDbUser, boolean state);
	
	Page<UserProjection> findAllProjectedByOrderByName(Pageable pageable);
	
	UserProjection findProjectedById(Integer id);
	
	List<UserProjection> findAllProjectedByOrderByUsername();
	List<UserProjection> findAllProjectedByOrderByName();
	
	long countBy();
	
	@Query("select pp.permission.name as name from User u join u.profile.profilePermissions pp where u.id = ?1")
	List<UserPermissionProjection> findUserPermissions(Integer userId);
	
}
