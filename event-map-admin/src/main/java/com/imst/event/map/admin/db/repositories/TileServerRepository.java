package com.imst.event.map.admin.db.repositories;

import java.util.List;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.TileServerProjection;
import com.imst.event.map.hibernate.entity.TileServer;

public interface TileServerRepository extends ProjectionRepository<TileServer, Integer> {
	List<TileServerProjection> findAllProjectedBy();;
}
