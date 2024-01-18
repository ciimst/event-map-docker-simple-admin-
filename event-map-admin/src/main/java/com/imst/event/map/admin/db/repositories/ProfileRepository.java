package com.imst.event.map.admin.db.repositories;

import java.util.List;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.ProfileProjection;
import com.imst.event.map.hibernate.entity.Profile;

public interface ProfileRepository extends ProjectionRepository<Profile, Integer> {
	
	List<ProfileProjection> findAllProjectedByOrderByName();
	List<Profile> findByIsDefault(Boolean isDefault);
	List<Profile> findAllByIsDefaultIsTrue();

	
}
