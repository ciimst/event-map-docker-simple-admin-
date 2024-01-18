package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.EventGroupProjectionBasic;
import com.imst.event.map.admin.db.projections.mobile.EventGroupProjectionMobile;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.admin.vo.mobile.EventGroupItemMobile;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;

public interface EventGroupRepository extends ProjectionRepository<EventGroup, Integer> {
	
	List<EventGroupProjection> findAllProjectedByOrderByName();
	
	
	List<EventGroupProjection> findAllProjectedByLayerIdIn(List<Integer> layerIdList);
	
	@Query("select eg.id from EventGroup eg where eg.layer.id = ?1")
	List<Integer> findIdByLayerId(Integer layerId);
	
//	Page<EventGroupProjectionBasic> findAllProjectedBy(Pageable pageable);
	
	Page<EventGroupProjection> findAllPageableProjectedByIdIn(Pageable pageable, List<Integer> eventGroupIdList);
	
	EventGroupProjection findProjectedById(Integer id);
	
	EventGroup findOneByLayerAndName(Layer layer, String name);
	
	List<EventGroup> findAllByLayer(Layer layer);
	
	List<EventGroupProjectionMobile> findAllProjectedByLayerId(Integer layerId);
		
//	List<EventGroupItem> findAllByLayerOrderByName(Layer layerId);
	
//	List<EventGroupProjection> findAllProjectedByLayerOrderByName(Layer layer);
	
//	List<EventGroupItemMobile> findAllByLayerId(Integer layer);
	
	//List<EventGroupItem> findAllByLayerIdAndIdNotNull(Integer layerId);
	
	List<EventGroupProjection> findAllProjectedByIdNotIn(List<Integer> userEventGroupPermissionIdList);
	
	List<EventGroupProjection> findAllProjectedByIdIn(List<Integer> eventGroupItemIdList);
	
	List<EventGroupProjection> findAllProjectedByIdInOrderByName(List<Integer> eventGroupItemIdList);
	
	List<EventGroupProjection> findAllProjectedByLayerAndIdInOrderByName(Layer layer, List<Integer> eventGroupItemIdList);
	
	List<EventGroupProjection> findAllProjectedByLayerAndName(Layer layerId, String name);
	
	List<EventGroupProjection> findAllProjectedByLayerOrderByName(Layer layerId);
	
	List<EventGroup> findAllByLayerIdIn(List<Integer> layerIdList);
	
	List<EventGroup> findAllByIdIn(List<Integer> eventGroupIdList);
	
	List<EventGroup> findAllByIdInOrderByName(List<Integer> eventGroupIdList);
	
	Long countByParentId(Integer parentId);

	
	
 }
