package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.MapAreaGroupProjection;
import com.imst.event.map.admin.db.projections.mobile.MapAreaGroupProjectionMobile;
import com.imst.event.map.hibernate.entity.MapAreaGroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MapAreaGroupRepository extends ProjectionRepository<MapAreaGroup, Integer> {
	
	Page<MapAreaGroupProjection> findAllProjectedBy(Pageable pageable);
	
	Page<MapAreaGroupProjection> findAllProjectedByLayerIdIn(Pageable pageable, List<Integer> layerIdList);
	
	MapAreaGroupProjection findProjectedById(Integer id);
	
	List<MapAreaGroupProjection> findAllProjectedByOrderByName();
	
	List<MapAreaGroupProjection> findAllProjectedByLayerIdInOrderByName(List<Integer> layedIds);
	
	List<MapAreaGroupProjectionMobile> findAllProjectedByLayerId(Integer layerId);
}
