package com.imst.event.map.admin.db.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.EventProjection;
import com.imst.event.map.admin.db.projections.EventProjectionMobile;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.hibernate.entity.Event;

@Transactional
public interface EventRepository extends ProjectionRepository<Event, Integer> {
	
//	List<Event> findAllByStateIsTrue();
	
//	long countByStateIsTrue();
	
	Event findByIdAndStateIdIn(Integer id, List<Integer> stateIds);
	EventProjection findProjectedById(Integer id);
	
	EventProjection findProjectedByIdAndStateIdIn(Integer id, List<Integer> stateIds);
	
	//EventItem(Event event) constructorı kullanılıyor
//	EventItem findOneProjectedById(Integer id);
	EventItem findOneProjectedByIdAndStateIdIn(Integer id, List<Integer> stateIds);
			
	Page<EventProjection> findAllProjectedByStateIdInAndEventGroupIdIn(Pageable pageable, List<Integer> stateIds, List<Integer> eventGroupIdList);
			
	List<EventProjection> findAllProjectedByStateIdInAndEventGroupIdIn(List<Integer> stateIds, List<Integer> eventGroupId);
			
	long countByStateIdAndEventGroupIdInAndUserIdInAndGroupIdIn(Integer stateId ,List<Integer> eventGroupId, List<Integer> userId, List<Integer> groupId);

	
//	long countByStateIdAndEventGroupIdInAndUserIdInAndGroupIdIn(Integer stateId, List<Integer> eventGroupId, List<Integer> userId, List<Integer> groupId);
	
	long countByEventType(Integer id);
	
//	@Query("SELECT event.eventType.id, count(*) AS cnt FROM Event event WHERE event.eventGroup.id in :eventGroupIdList GROUP BY event.eventType.id ORDER BY cnt DESC")
//	List<Object[]> findPermissionedEventTypeIdAndEventTypeNameAndCountBy(Pageable pageable, List<Integer> eventGroupIdList);
	
	@Query("SELECT event.eventType.id, count(*) AS cnt FROM Event event WHERE event.eventGroup.id in :eventGroupIdList AND event.userId in :userIdList AND event.groupId in :groupIdList AND event.state.id = :stateId GROUP BY event.eventType.id ORDER BY cnt DESC")
	List<Object[]> findPermissionedEventTypeIdAndEventTypeNameAndCountByAndStateId(Pageable pageable, List<Integer> eventGroupIdList, List<Integer> userIdList, List<Integer> groupIdList, Integer stateId);
	
	EventProjection findFirstByEventGroupIdIn(List<Integer> eventGroupId, Sort sort);
	
	List<EventProjection> findFirstByEventDateBetweenAndEventGroupIdIn(Date startEventDate, Date finishEventDate, List<Integer> eventGroupIdList, Sort sort);
	
	
	@Modifying
	@Query("update Event u set u.state.id = :a where u.id in :b")
	void updateBatchOperationsEventState(@Param("b") List<Integer> eventIdList, @Param("a") Integer stateId);
	
	
	@Modifying
	@Query("update Event u set u.state.id = :stateId where u.id in :idList")
	void batchOperationsUpdateEventBlackList(@Param("stateId") Integer stateId, @Param("idList") List<Integer> eventIdList);
	
	long deleteByEventGroupIdAndStateId(Integer eventGroupId, Integer stateId);
}
