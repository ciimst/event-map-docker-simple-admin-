package com.imst.event.map.admin.db.repositories;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.vo.mobile.TileExportItem;
import com.imst.event.map.hibernate.entity.TileExport;

public interface TileExportRepository extends ProjectionRepository<TileExport, Integer>{
	
	TileExportItem findAllProjectedById(Integer id);
	
	TileExport findAllById(Integer id);
}
