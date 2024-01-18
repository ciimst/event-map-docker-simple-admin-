package com.imst.event.map.admin.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.FakeLayerIdProjection;
import com.imst.event.map.hibernate.entity.FakeLayerId;

@Transactional
public interface FakeLayerIdRepository extends ProjectionRepository<FakeLayerId, Integer> {
	
	FakeLayerId findByLayerId(Integer layerId);
	FakeLayerIdProjection findProjectedById(Integer id);
	List<FakeLayerId> findAllByLayerId(Integer layerId);	
	List<FakeLayerId> findAllByLayerIdAndRoleId(Integer layerId, String roleId);	
	
	
	@Modifying
	@Query("delete from FakeLayerId where id in :idList")
	void deleteByIdList(List<Integer> idList);
	
	
	
//	List<EventGroupProjection> findAllProjectedByOrderByName();
//	
//	
//	List<FakeLayerIdProjection> findAllProjectedByLayerIdIn(List<Integer> layerIdList);
//	
//
	Page<FakeLayerIdProjection> findAllPageableProjectedByIdIn(Pageable pageable, List<Integer> layerIdList);
//	
	FakeLayerIdProjection findProjectedByLayerId(Integer layerId);
//	
//	EventGroup findOneByLayerAndName(Layer layer, String name);
//	
//	List<EventGroup> findAllByLayer(Layer layer);
//	
//	List<EventGroupProjectionMobile> findAllProjectedByLayerId(Integer layerId);
//		
//
//	
//	List<EventGroupProjection> findAllProjectedByIdNotIn(List<Integer> userEventGroupPermissionIdList);
//	
//	List<EventGroupProjection> findAllProjectedByIdIn(List<Integer> eventGroupItemIdList);
//	
//	List<EventGroupProjection> findAllProjectedByIdInOrderByName(List<Integer> eventGroupItemIdList);
//	
//	List<EventGroupProjection> findAllProjectedByLayerAndIdInOrderByName(Layer layer, List<Integer> eventGroupItemIdList);
//	
//	List<EventGroupProjection> findAllProjectedByLayerAndName(Layer layerId, String name);
//	
//	List<EventGroupProjection> findAllProjectedByLayerOrderByName(Layer layerId);
//	
//	List<EventGroup> findAllByLayerIdIn(List<Integer> layerIdList);
//	
//	List<EventGroup> findAllByIdIn(List<Integer> eventGroupIdList);
//	
//	List<EventGroup> findAllByIdInOrderByName(List<Integer> eventGroupIdList);
//	
//	Long countByParentId(Integer parentId);

	
	
 }
