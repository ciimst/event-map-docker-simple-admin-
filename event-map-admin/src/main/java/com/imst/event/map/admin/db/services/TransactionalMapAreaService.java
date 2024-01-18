package com.imst.event.map.admin.db.services;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.db.repositories.MapAreaRepository;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiMapAreaGroupItem;
import com.imst.event.map.admin.vo.api.ApiMapAreaItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.MapArea;
import com.imst.event.map.hibernate.entity.MapAreaGroup;

@Service
public class TransactionalMapAreaService {
	
	@Autowired
	private MapAreaRepository mapAreaRepository;
	@Autowired
	private MapAreaGroupRepository mapAreaGroupRepository;
	@Autowired
	private LayerRepository layerRepository;

	@Transactional(transactionManager = "masterTransactionManager")
	public ApiMapAreaItem saveEvent(MapArea mapArea, ApiMapAreaItem apiMapAreaItem, Timestamp nowT) {
	
		
		MapAreaGroup mapAreaGroup = handleMapAraeGroup(apiMapAreaItem.getApiMapAreaGroupItem());
		
		mapArea.setMapAreaGroup(mapAreaGroup);
		
		MapArea saved = mapAreaRepository.save(mapArea);
			
		ApiMapAreaItem apiMapAreaItemResponse = new ApiMapAreaItem();
		apiMapAreaItemResponse.setId(saved.getId());
		apiMapAreaItemResponse.setTitle(saved.getTitle());
		apiMapAreaItemResponse.setCoordinateInfo(saved.getCoordinateInfo());
		apiMapAreaItemResponse.setState(saved.getState());
		apiMapAreaItemResponse.setCreateDate(saved.getCreateDate());
		
		ApiMapAreaGroupItem apiMapAreaGroupItem = new ApiMapAreaGroupItem();
		apiMapAreaGroupItem.setId(saved.getMapAreaGroup().getId());
		apiMapAreaGroupItem.setName(saved.getMapAreaGroup().getName());
		apiMapAreaGroupItem.setColor(saved.getMapAreaGroup().getColor());
		apiMapAreaGroupItem.setLayerId(saved.getMapAreaGroup().getLayer().getId());
		
		apiMapAreaItemResponse.setApiMapAreaGroupItem(apiMapAreaGroupItem);
		
		return apiMapAreaItemResponse;
	}
	
	private MapAreaGroup handleMapAraeGroup(ApiMapAreaGroupItem mapAreaGroupItem) {
		
		MapAreaGroup mapAreaGroup;
		
		if (mapAreaGroupItem.getId() == null) {
			
			if (StringUtils.isBlank(mapAreaGroupItem.getName())) {
				throw new ApiException("MapAreaGroup name is null or empty. You need to define either eventGroupItem.id or eventGroupItem.name.");
			}
			
			if (StringUtils.isBlank(mapAreaGroupItem.getColor())) {
				
				throw new ApiException("MapAreaGroup color is null or empty. You need to define either eventGroupItem.id or eventGroupItem.color.");
			}
			
			MapAreaGroup newMapAreaGroup = new MapAreaGroup();
			newMapAreaGroup.setName(mapAreaGroupItem.getName());
			newMapAreaGroup.setColor(mapAreaGroupItem.getColor());
			
			Layer layer = layerRepository.findById(mapAreaGroupItem.getLayerId()).orElse(null);
			
			if (layer == null) {
				throw new ApiException("Layer not found. Please be sure that the layerItem.id exists.");
			}
			
			newMapAreaGroup.setLayer(layer);
			
			mapAreaGroup = mapAreaGroupRepository.save(newMapAreaGroup);
			
		} else {
			
			mapAreaGroup = mapAreaGroupRepository.findById(mapAreaGroupItem.getId()).orElse(null);
		}
		
		if (mapAreaGroup == null) {
			throw new ApiException("MapAreaGroup not found. Please be sure mapAreaGroupItem.id exists.");
		}
		
		return mapAreaGroup;
	}
	
}
