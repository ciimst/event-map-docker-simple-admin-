package com.imst.event.map.admin.db.repositories;


import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.MapAreaProjection;
import com.imst.event.map.admin.db.projections.MapAreaProjectionForDraw;
import com.imst.event.map.admin.vo.MapAreaItem;
import com.imst.event.map.hibernate.entity.MapArea;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MapAreaRepository extends ProjectionRepository<MapArea, Integer> {
	
	Page<MapAreaProjection> findAllProjectedBy(Pageable pageable);
	
	Page<MapAreaProjection> findAllProjectedByMapAreaGroupIdIn(Pageable pageable, List<Integer> mapAreaGroupIdList);
	
	MapAreaProjection findProjectedById(Integer id);
	
	MapAreaItem findOneProjectedById(Integer id);
	
	@Query("select m.id as id," +
			" m.mapAreaGroup.id as mapAreaGroupId, m.mapAreaGroup.layer.id as layerId," +
			" m.title as title, m.coordinateInfo as coordinateInfo, m.state as state" +
			" from MapArea m where m.state = true")
	List<MapAreaProjectionForDraw>  findAllForDraw();
	
	List<MapAreaProjection> findAllProjectedBy();
}
