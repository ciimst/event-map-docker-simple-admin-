package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.AlertProjection;
import com.imst.event.map.admin.db.projections.LayerAlertCountProjection;
import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.User;


public interface AlertRepository extends ProjectionRepository<Alert, Integer> {
	
	List<AlertProjection> findAllProjectedBy();
	
	@Query("select alert.layer.id, count(*) as cnt FROM Alert alert group by alert.layer.id order by cnt desc")
	List<Object[]> findLayerIdAndLayerNameAndCountBy(Pageable pageable);
	
	@Query("SELECT alert.layer.id AS layerId, alert.layer.name AS layerName, count(*) AS alertCount FROM Alert alert WHERE alert.layer.id in :layerIdList GROUP BY alert.layer.id, alert.layer.name ORDER BY alertCount DESC")
	List<LayerAlertCountProjection> findPermissionedLayerAlertCountProjectedBy(Pageable pageable, List<Integer> layerIdList);
	
	long countBySharedByNotNull();
	
	long countByUserAndSharedByNotNull(User user);
	
	long countByUser(User user);
	
	List<AlertProjection> findAllProjectedByUser(User user);
	
	long countByEventGroupId(Integer eventGroupId);
}
