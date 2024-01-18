package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.AlertEventProjection;
import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.User;

@Transactional
public interface AlertEventRepository extends ProjectionRepository<AlertEvent, Integer> {
	
	List<AlertEventProjection> findAllProjectedBy();
	
	long countByUserAndReadStateIsFalse(User user);
	
	List<AlertEventProjection> findAllProjectedByUser(User user);
	
	long countByUser(User user);
	
	
	@Modifying
	@Query("delete from AlertEvent alertEvent where alertEvent.alert.id = :alertId")
	void alertEventByAlertIdDeleted(@Param("alertId") Integer alertId);
	
}


