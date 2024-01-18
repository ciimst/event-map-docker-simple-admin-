package com.imst.event.map.admin.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.EventLinkProjection;
import com.imst.event.map.hibernate.entity.EventLink;

public interface EventLinkRepository extends ProjectionRepository<EventLink, Integer> {
	
	Page<EventLinkProjection> findAllProjectedBy(Pageable pageable);
	
	EventLinkProjection findProjectedById(Integer id);
	
 }
