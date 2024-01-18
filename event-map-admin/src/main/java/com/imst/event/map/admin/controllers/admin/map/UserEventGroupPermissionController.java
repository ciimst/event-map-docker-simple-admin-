package com.imst.event.map.admin.controllers.admin.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.projections.UserEventGroupPermissionProjection2;
import com.imst.event.map.admin.db.projections.UserLayerPermissionProjection2;
import com.imst.event.map.admin.db.projections.UserProjection;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.UserEventGroupPermissionRepository;
import com.imst.event.map.admin.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.specifications.UserEventGroupPermissionSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/usereventgrouppermission")
public class UserEventGroupPermissionController {

	@Autowired
	private LayerRepository layerRepository;
	@Autowired 
	private UserLayerPermissionRepository userLayerPermissionRepository;
	@Autowired 
	private UserEventGroupPermissionRepository userEventGroupPermissionRepository;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_LIST')")
	@Operation(summary = "")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/user_event_group_permission");
		
	    List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layers);
		
		List<EventGroupProjection> eventGroups = eventGroupRepository.findAllProjectedByOrderByName();
		
		EventGroupTree eventGroupTree = new EventGroupTree(eventGroups, true);
		List<EventGroupItem> eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
		modelAndView.addObject("eventGroups", eventGroupItemList);
		
		List<UserProjection> users = userRepository.findAllProjectedByOrderByUsername();
		modelAndView.addObject("users", users);
		
		return modelAndView;
	}
	
	@RequestMapping(value="/eventGroupFormFilter")
	public GenericResponseItem eventGroupFilter(Integer layerId){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		if(layerId != null) {
			Layer layer = new Layer();
			layer.setId(layerId);			
			
			List<EventGroupProjection> eventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);
			
			EventGroupTree eventGroupTree = new EventGroupTree(eventGroupList, true);
			List<EventGroupItem> eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
			
		
			genericResponseItem.setData(eventGroupItemList);
			
			return genericResponseItem;
		}
		return null;
		
	}
	
	@RequestMapping(value="/layerFilter")
	public GenericResponseItem layerFilter(Integer userId){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		
		if(userId != null) {
			User user = new User();
			user.setId(userId);		
									
				
		     	List<UserLayerPermissionProjection2> userLayerPermissionItemList = userLayerPermissionRepository.findAllProjectedByUser(user);
				
		     	List<Integer> userLayerPermissionIdList = userLayerPermissionItemList.stream().map(UserLayerPermissionProjection2::getLayerId).collect(Collectors.toList());
		     	userLayerPermissionIdList.add(0);// liste bos iken listenin tamamnini getirmesi icin
		     	
				List<LayerProjection> layerList = layerRepository.findAllProjectedByIdNotInOrderByName(userLayerPermissionIdList);    
				
				genericResponseItem.setData(layerList);
				
				return genericResponseItem;
			}
			
		return null;
		
	}
	
	@RequestMapping(value="/eventGroupFilter")
	public GenericResponseItem eventGroupFilter(Integer userId, Integer layerId){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		
		if(userId != null) {
			User user = new User();
			user.setId(userId);		
			
			if(layerId != null) {
				Layer layer = new Layer();
				layer.setId(layerId);			
				
				List<EventGroupProjection> eventGroupItems = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);   
	
				List<UserEventGroupPermissionProjection2> userEventGroupPermissionItemList = userEventGroupPermissionRepository.findAllProjectedByUser(user);  
				
				List<Integer> userEventGroupPermissionIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionProjection2::getEventGroupId).collect(Collectors.toList()); 
				
				//List<EventGroupProjection> notAllowedEventGroupList = eventGroupRepository.findAllProjectedByIdNotIn(userEventGroupPermissionIdList);    
				
				EventGroupTree eventGroupTree = new EventGroupTree(eventGroupItems, true);
				List<EventGroupItem >eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
				
				List<EventGroupItem> eventGroupList = eventGroupItemList.stream().filter(item -> !userEventGroupPermissionIdList.contains(item.getId()) ).collect(Collectors.toList());
				
				genericResponseItem.setData(eventGroupList);
				
				return genericResponseItem;
			}
		}
			
		return null;
		
	}
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<UserEventGroupPermissionItem> data(UserEventGroupPermissionItem userEventGroupPermissionItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(UserEventGroupPermissionItem.class);
		
		UserEventGroupPermissionSpecification userEventGroupPermissionSpecification = new UserEventGroupPermissionSpecification(userEventGroupPermissionItem);
		Page<UserEventGroupPermissionItem> userEventGroupPermissionItems = masterDao.findAll(userEventGroupPermissionSpecification, pageRequest);
		
		DataSet<UserEventGroupPermissionItem> dataSet = new DataSet<>(userEventGroupPermissionItems.getContent(), 0L, userEventGroupPermissionItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(UserEventGroupPermissionItem userEventGroupPermissionItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
	
		if (userEventGroupPermissionItem.getUserId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.select"));
			return genericResponseItem;
		}
		
		if (userEventGroupPermissionItem.getLayerId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.correctly"));
			return genericResponseItem;
		}
		
		if (userEventGroupPermissionItem.getEventGroupId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.group.correctly"));
			return genericResponseItem;
		}

		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Map<String, Object> userEventGroupPermissionsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		UserEventGroupPermission userEventGroupPermission;
					
			logTypeE = LogTypeE.USER_EVENT_GROUP_PERMISSION_ADD;
			
			userEventGroupPermission = new UserEventGroupPermission();			

		
		if (checkIfUserExist(userEventGroupPermissionItem.getUserId(), userEventGroupPermissionItem.getEventGroupId())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.previous.save"));
			return genericResponseItem;
		}
		
		Layer layer = new Layer();
		layer.setId(userEventGroupPermissionItem.getLayerId());
//		userEventGroupPermission.setLayer(layer); 
		
		EventGroup eventGroup = new EventGroup();
		eventGroup.setId(userEventGroupPermissionItem.getEventGroupId());
		eventGroup.setLayer(layer); 
		userEventGroupPermission.setEventGroup(eventGroup);
			
		User user = new User();
		user.setId(userEventGroupPermissionItem.getUserId());
		userEventGroupPermission.setUser(user);
	
							
		UserEventGroupPermission saved = userEventGroupPermissionRepository.save(userEventGroupPermission);
		
		userEventGroupPermissionsForLog.put("new", UserEventGroupPermissionItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(userEventGroupPermissionsForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer userEventGroupPermissionId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(userEventGroupPermissionId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userEventGroupPermission.not.found"));
				return genericResponseItem;
			}
			
			UserEventGroupPermission userEventGroupPermission = userEventGroupPermissionRepository.findById(userEventGroupPermissionId).orElse(null);
			if (userEventGroupPermission == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userEventGroupPermission.not.found"));
				return genericResponseItem;
			}
			
			
			userEventGroupPermissionRepository.deleteById(userEventGroupPermissionId);
			
			Map<String, Object> userEventGroupPermissionsForLog = new TreeMap<>();
			userEventGroupPermissionsForLog.put("deleted", UserEventGroupPermissionItem.newInstanceForLog(userEventGroupPermission));
			
			dbLogger.log(new Gson().toJson(userEventGroupPermissionsForLog), LogTypeE.USER_EVENT_GROUP_PERMISSION_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	

	private boolean checkIfUserExist(Integer userId, Integer eventGroupId) {
		UserEventGroupPermission userEventGroupId = userEventGroupPermissionRepository.findByUserIdAndEventGroupId(userId, eventGroupId);
		return userEventGroupId != null;
	}
	
	
}
