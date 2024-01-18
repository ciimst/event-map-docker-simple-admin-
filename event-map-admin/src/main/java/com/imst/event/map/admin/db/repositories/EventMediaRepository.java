package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.EventMediaProjection;
import com.imst.event.map.admin.vo.EventMediaItem;
import com.imst.event.map.hibernate.entity.EventMedia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventMediaRepository extends ProjectionRepository<EventMedia, Integer> {
	
	List<EventMediaProjection> findAllProjectedBy();
	List<EventMediaItem> findAllProjectedByEventId(Integer id);
	List<EventMedia> findAllByEventId(Integer id);
	
	EventMediaItem findAllById(Integer id);
	
	Optional<EventMedia> findOneByIdAndEventId(Integer id, Integer eventId);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "delete from event_media m where m.fk_event_id in (select e.id from event e where e.fk_event_group_id = :eventGroupId  and e.fk_state_id = :stateId);")
    void deleteByEventGroupIdAndStateId(@Param ("eventGroupId") Integer eventGroupId, @Param ("stateId") Integer stateId);
	
}
