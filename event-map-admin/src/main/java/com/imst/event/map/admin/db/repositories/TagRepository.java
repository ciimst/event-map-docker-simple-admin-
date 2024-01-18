package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.TagProjection;
import com.imst.event.map.hibernate.entity.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends ProjectionRepository<Tag, Integer> {

	Optional<Tag> findOneByName(String name);
	
	Page<TagProjection> findAllProjectedBy(Pageable pagable);
	TagProjection findProjectedById(Integer id);
	
	List<TagProjection> findAllProjectedByOrderByName();
}
