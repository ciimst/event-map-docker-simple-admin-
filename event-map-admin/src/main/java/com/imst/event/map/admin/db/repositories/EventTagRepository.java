package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.EventTagProjection;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventTag;

import java.util.List;

public interface EventTagRepository extends ProjectionRepository<EventTag, Integer> {
	
	List<EventTag> findAllByEventId(Integer id);
	
	List<EventTagProjection> findAllProjectedByEvent(Event event);
	
	List<EventTag> findAllByEventIdAndTagId(Integer eventId, Integer tagId);

}
