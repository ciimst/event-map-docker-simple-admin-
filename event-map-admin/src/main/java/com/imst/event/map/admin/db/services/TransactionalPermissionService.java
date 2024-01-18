package com.imst.event.map.admin.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.repositories.BlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventMediaRepository;
import com.imst.event.map.admin.db.repositories.EventRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.UserEventGroupPermissionRepository;
import com.imst.event.map.admin.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;
import com.imst.event.map.hibernate.entity.UserLayerPermission;

@Service
public class TransactionalPermissionService {
	
	@Autowired
	private EventGroupRepository eventGroupRepository;	
	@Autowired 
	private UserEventGroupPermissionRepository userEventGroupPermissionRepository;
	@Autowired
	private LayerRepository layerRepository;	
	@Autowired 
	private UserLayerPermissionRepository userLayerPermissionRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventMediaRepository eventMediaRepository;
	@Autowired
	private BlackListRepository blackListRepository;
	
	@Transactional(transactionManager = "masterTransactionManager")
	public void deleteEventGroup(Integer eventGroupId) {
			
		eventMediaRepository.deleteByEventGroupIdAndStateId(eventGroupId, StateE.DELETED.getValue());
		eventRepository.deleteByEventGroupIdAndStateId(eventGroupId, StateE.DELETED.getValue());
		blackListRepository.deleteByEventGroupIdAndStateId(eventGroupId, StateE.DELETED.getValue());
		
		List<UserEventGroupPermission> soonTBDUserEventGroupPermissionList = userEventGroupPermissionRepository.findByEventGroupId(eventGroupId);
		
		if (soonTBDUserEventGroupPermissionList != null) {

			userEventGroupPermissionRepository.deleteAll(soonTBDUserEventGroupPermissionList);
			
		}	

		eventGroupRepository.deleteById(eventGroupId);
		
	}
	
	@Transactional(transactionManager = "masterTransactionManager")
	public void deleteLayer(Integer layerId) {
		
		List<UserLayerPermission> soonTBDUserLayerPermissionList = userLayerPermissionRepository.findByLayerId(layerId);
		
		if (soonTBDUserLayerPermissionList != null) {

			userLayerPermissionRepository.deleteAll(soonTBDUserLayerPermissionList);
			
		}
		
		layerRepository.deleteById(layerId);
		
	}

}
