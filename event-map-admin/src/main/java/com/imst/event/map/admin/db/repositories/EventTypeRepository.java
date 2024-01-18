package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.admin.db.projections.mobile.EventTypeProjectionMobile;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.hibernate.entity.EventType;

public interface EventTypeRepository extends ProjectionRepository<EventType, Integer> {
	
	List<EventTypeProjection> findAllProjectedBy();
	
	Page<EventTypeProjection> findAllProjectedBy(Pageable pageable);
	
	EventTypeProjection findProjectedById(Integer id);
	
	EventTypeItem findOneProjectedById(Integer id);
	
	List<EventTypeItem> findByIdIn(List<Integer> idList);
	
	
	List<EventTypeProjectionMobile> findAllMobileProjectedBy();
	
	List<EventTypeProjection> findAllProjectedByIdIn(List<Integer> eventTypeItemIdList);
}
