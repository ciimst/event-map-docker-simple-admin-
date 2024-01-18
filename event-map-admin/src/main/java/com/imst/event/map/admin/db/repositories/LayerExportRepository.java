package com.imst.event.map.admin.db.repositories;

import java.util.List;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.LayerExport;
import com.imst.event.map.admin.db.projections.LayerExportProjection;
import com.imst.event.map.admin.vo.mobile.LayerExportItem;

public interface LayerExportRepository extends ProjectionRepository<LayerExport, Integer>{

	
	LayerExportProjection findAllByLayerId(Integer layerId);
	
	List<LayerExportProjection> findAllByLayer(Layer layer);
	
	List<LayerExportProjection> findAllProjectedBy();
	
	LayerExport findAllById(Integer id);
	
	LayerExportItem findAllProjectedById(Integer id);
	
}
