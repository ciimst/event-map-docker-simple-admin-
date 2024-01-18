package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.projections.mobile.LayerProjectionMobile;
import com.imst.event.map.admin.vo.LayerItem;
import com.imst.event.map.hibernate.entity.Layer;

public interface LayerRepository extends ProjectionRepository<Layer, Integer> {
	
	
	List<Layer> findAllByStateIsTrue();
	
	long countByStateIsTrue();
	
	List<LayerProjection> findAllProjectedByStateIsTrue();
	
	Page<LayerProjection> findAllProjectedByStateIsTrue(Pageable pagable);
	
	Page<LayerProjection> findAllProjectedByStateIsTrueAndIdIn(Pageable pagable,List<Integer> layerIdList);
	
	LayerProjection findProjectedById(Integer id);
	
	List<LayerProjection> findAllProjectedByOrderByName();
	
	List<LayerProjection> findAllProjectedByIdIn(List<Integer> layerIdList);
	
	List<LayerProjection> findAllProjectedByIdInOrderByName(List<Integer> layerIdList);
	
	Layer findOneByName(String name);

	Layer findAllById(Integer id);
	
	
	List<LayerProjectionMobile> findAllProjectedById(Integer id);
	
	List<LayerProjection> findAllProjectedByIdNotInOrderByName(List<Integer> userLayerPermissionIdList);
	
	List<Layer> findAllByIdIn(List<Integer> idList);
	
	List<Layer> findAll();
}


