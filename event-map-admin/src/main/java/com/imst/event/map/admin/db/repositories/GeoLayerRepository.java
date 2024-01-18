package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.GeoLayerProjection;
import com.imst.event.map.hibernate.entity.GeoLayer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeoLayerRepository extends ProjectionRepository<GeoLayer, Integer> {
	
	Page<GeoLayerProjection> findAllProjectedBy(Pageable pageable);
	
	Page<GeoLayerProjection> findAllProjectedByLayerIdIn(Pageable pageable, List<Integer> layerIdList);
	
	GeoLayerProjection findProjectedById(Integer id);
	
	List<GeoLayerProjection> findAllProjectedBy();
	
}
