package com.imst.event.map.admin.controllers.admin.map;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.AlertRepository;
import com.imst.event.map.admin.db.repositories.BlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.UserEventGroupPermissionRepository;
import com.imst.event.map.admin.db.services.TransactionalPermissionService;
import com.imst.event.map.admin.db.specifications.EventGroupSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/eventgroup")
public class EventGroupController {
	
	@Autowired
	private EventGroupRepository eventGroupRepository;		
	@Autowired
	private LayerRepository layerRepository;
	@Autowired 
	private UserEventGroupPermissionRepository userEventGroupPermissionRepository;
	@Autowired
	private BlackListRepository blackListRepository;
	@Autowired
	private TransactionalPermissionService transactionalPermissionService;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired 
	private UserPermissionService userPermissionService;
	
	@Autowired 
	private AlertRepository alertRepository;
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_LIST')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/event_group");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
						
		List<EventGroupProjection> eventGroupsFiltered = eventGroupRepository.findAllProjectedByIdInOrderByName(userEventGroupPermissionIdList);
		
		EventGroupTree eventGroupTree = new EventGroupTree(eventGroupsFiltered, true);
		List<EventGroupItem> eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
						
		List<LayerProjection> layersFiltered = layerRepository.findAllProjectedByIdInOrderByName(userLayerPermissionIdList);
		
//		List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layersFiltered);
				
//		List<EventGroupProjection> eventGroups = eventGroupRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("eventGroups", eventGroupItemList);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<EventGroupItem> data(EventGroupItem eventGroupItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(EventGroupItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
						
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
//		List <Integer> allGroups = eventGroupRepository.findAll().stream().map(EventGroup::getId).collect(Collectors.toList()); //for test		
		
		EventGroupSpecification eventGroupSpecification = new EventGroupSpecification(eventGroupItem, userEventGroupPermissionIdList);
		Page<EventGroupItem> eventGroupItems = masterDao.findAll(eventGroupSpecification, pageRequest);
		
		List<EventGroupItem> eventGroupItemList = eventGroupItems.getContent();
		
		List<Integer> eventGroupParentIdList = eventGroupItemList.stream().map(EventGroupItem::getParentId).collect(Collectors.toList());
		
		List<EventGroupProjection> eventGroupParentList = eventGroupRepository.findAllProjectedByIdIn(eventGroupParentIdList);
		
		for (EventGroupItem eventGroup : eventGroupItemList) {
			
			
			List<EventGroupProjection> parentName =  eventGroupParentList.stream().filter(item -> item.getId().equals(eventGroup.getParentId())).collect(Collectors.toList());
			
			if(parentName.size() > 0) {
				
				 eventGroup.setParentName(parentName.get(0).getName());
			}
							    
		   				
		}
		
		DataSet<EventGroupItem> dataSet = new DataSet<>(eventGroupItemList, 0L, eventGroupItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_MANAGE')")
	@Operation(summary = "güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer eventGroupId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());

		if (Optional.ofNullable(eventGroupId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.not.found"));
			return genericResponseItem;
		}
		
		EventGroup eventGroup = eventGroupRepository.findById(eventGroupId).orElse(null);
		if (eventGroup == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.not.found"));
			return genericResponseItem;
		}
		
//		List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(eventGroupId))) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
			return genericResponseItem;
		}		
		
		genericResponseItem.setData(new EventGroupItem(eventGroup, false));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(EventGroupItem eventGroupItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(eventGroupItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.group.name.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(eventGroupItem.getColor())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.group.color.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (eventGroupItem.getLayerId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
				
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		Layer layer = new Layer();
		layer.setId(eventGroupItem.getLayerId());	
		
		List<EventGroupProjection> eventGroupList = eventGroupRepository.findAllProjectedByLayerAndName(layer, eventGroupItem.getName());
    	List<EventGroupProjection> eventGroups =  eventGroupList.stream().filter(item -> !item.getId().equals(eventGroupItem.getId())).collect(Collectors.toList());
	     
		if(!eventGroups.isEmpty()) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.group.with.same.name.already.exists"));//TODO:lang
			return genericResponseItem;                                     
		}
						
//		List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());		
		
//		List<Integer> userLayerPermissionIdList = sessionUser.getUserLayerPermissionList().stream().map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
		
		Map<String, Object> eventGroupsForLog = new TreeMap<>();
		LogTypeE logTypeE;
//		LogTypeE logTypePermission; 
		EventGroup eventGroup;
		
		if (Optional.ofNullable(eventGroupItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.EVENT_GROUP_EDIT;
//			logTypePermission = LogTypeE.USER_EVENT_GROUP_PERMISSION_EDIT;
			
			eventGroup = eventGroupRepository.findById(eventGroupItem.getId()).orElse(null);		
			
			if (eventGroup == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.group.not.found"));
				return genericResponseItem;
			}
			
			if(eventGroupItem.getParentId() != null && eventGroupItem.getId().equals(eventGroupItem.getParentId())) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.cannot.add.event.group.itself.parent.group"));
				return genericResponseItem;
			}
				
			List<EventGroupProjection> evenGroupsUnderLayer = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);		
			EventGroupTree eventGroupTree = new EventGroupTree(evenGroupsUnderLayer, true);
			//bu listede seçebileceği gruplar var. seçilen üst grup listede var ise ekleyebilir. yok ise ekleyemez.
			boolean eventGroupParentCircleControl = eventGroupTree.eventGroupListThatCanBeAddedAsParent(eventGroup.getId()).stream().anyMatch(f -> f.getId().equals(eventGroupItem.getParentId()));

			if(eventGroupItem.getParentId() != null && !eventGroupParentCircleControl) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.cannot.select.event.group.as.parent.group"));
				return genericResponseItem;
			}
			
			// eventGroupRepository sorgusunun altında olmalı
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
						
			List<Integer> userNoFullLayerPermissionList = permissionWrapperItem.getUserLayerNoFullPermissionItemIds();
			
			List<Integer> userLayerPermissionList = permissionWrapperItem.getUserLayerPermissionItemIds();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			
			// Editlenen olay grubuna izin var mı diye bakılır
			if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
				return genericResponseItem;
			}	
			
			// Editlenen olay grubunun eklenecegi katmana izin var mı diye bakılır
			if (!userLayerPermissionList.stream().anyMatch(n -> n.equals(layer.getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}	
			
			// Full permission layerdaki event group, full permissionlu olmayan bir layera yazılamaz
			if (userLayerFullPermissionIdList.contains(eventGroup.getLayer().getId()) && !userLayerFullPermissionIdList.contains(layer.getId()) ) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}	
			
			// layer'ında full permission olmayan event grouplarina kendi layerlari ve full izinli olunan dısındaki layerlar atanamaz
			if (userNoFullLayerPermissionList.stream().anyMatch(n -> n.equals(eventGroup.getLayer().getId())) && (!layer.getId().equals(eventGroup.getLayer().getId()) && !userLayerFullPermissionIdList.contains(layer.getId()))) { 
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			// event group'a kendi parenti veya izinli oldugu event grouplari disinda parent atanamaz
			if (eventGroupItem.getParentId() != null) {
				if(eventGroup.getParentId() != null) {
					if (!eventGroupItem.getParentId().equals(eventGroup.getParentId()) && !userEventGroupPermissionIdList.contains(eventGroupItem.getParentId())) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.parent.no.permission"));
						return genericResponseItem;
					}
				}
				else {
					if (!userEventGroupPermissionIdList.contains(eventGroupItem.getParentId())) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.parent.no.permission"));
						return genericResponseItem;
					}
				}
			}
	
			eventGroupsForLog.put("old", EventGroupItem.newInstanceForLog(eventGroup));
			
		} else {//add
			
			logTypeE = LogTypeE.EVENT_GROUP_ADD;
//			logTypePermission = LogTypeE.USER_EVENT_GROUP_PERMISSION_ADD;
			
			eventGroup = new EventGroup();	
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);

			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			// Eklenen olay grubunun eklenecegi katmana full izin var mı diye bakılır
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layer.getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}	
			
			// event group'a kendi parenti veya izinli oldugu event grouplari disinda parent atanamaz
			if (eventGroupItem.getParentId() != null) {
				if (!userEventGroupPermissionIdList.contains(eventGroupItem.getParentId())) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.parent.no.permission"));
					return genericResponseItem;
				}
			} 
		}

		eventGroup.setName(eventGroupItem.getName());
		eventGroup.setColor(eventGroupItem.getColor());
		eventGroup.setDescription(eventGroupItem.getDescription());

		eventGroup.setLayer(layer);
		eventGroup.setParentId(eventGroupItem.getParentId());
		
		EventGroup saved = eventGroupRepository.save(eventGroup);
		
		eventGroupsForLog.put("new", EventGroupItem.newInstanceForLog(saved));
		
		// Eklenen olay grubu, ekleyen kullanıcıya izin olarak verilir.
        //Optional<UserEventGroupPermission> userEventGroupPermissionOptional = Optional.ofNullable(userEventGroupPermissionRepository.findByUserIdAndEventGroupId(sessionUser.getUserId(), eventGroup.getId()));
        
        //Map<String, Object> userEventGroupPermissionForLog = new TreeMap<>();
//        UserEventGroupPermission userEventGroupPermission;
//        
//        userEventGroupPermission = new UserEventGroupPermission();
//        
//        
//		if(!userEventGroupPermissionOptional.isPresent()) {
//			
//			userEventGroupPermission.setEventGroup(eventGroup);
//			userEventGroupPermission.setUser(user);
//			
//			//UserEventGroupPermission userEventGroupPermissionSaved = userEventGroupPermissionRepository.save(userEventGroupPermission);
//			//userEventGroupPermissionForLog.put("new", UserEventGroupPermissionItem.newInstanceForLog(userEventGroupPermissionSaved));
//		}
				
		dbLogger.log(new Gson().toJson(eventGroupsForLog), logTypeE);
		//dbLogger.log(new Gson().toJson(userEventGroupPermissionForLog), logTypePermission);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer eventGroupId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(eventGroupId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.not.found"));
				return genericResponseItem;
			}
			
			EventGroup eventGroup = eventGroupRepository.findById(eventGroupId).orElse(null);
			if (eventGroup == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.not.found"));
				return genericResponseItem;
			}
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
//			List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());			
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			
			if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
				return genericResponseItem;
			}		
			
			// ALtında event grup var mı
			Long childCount = eventGroupRepository.countByParentId(eventGroupId);
			if (childCount > 0) {
				
				genericResponseItem.setState(false); // TODO dil dosyası
				genericResponseItem.setDescription("Lütfen önce ilişkili alt grupları siliniz.");
				return genericResponseItem;
			}
			
			
			long blackListCount = blackListRepository.countByEventGroupIdAndStateIdNot(eventGroupId, StateE.DELETED.getValue());
			if (blackListCount > 0) {
				
				genericResponseItem.setState(false); // TODO dil dosyası
				genericResponseItem.setDescription("Lütfen önce ilişkili blacklist'leri siliniz.");
				return genericResponseItem;
			}
			
			long eventGroupRelatedAlertCount = alertRepository.countByEventGroupId(eventGroupId);
			
			if(eventGroupRelatedAlertCount > 0) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription("Lütfen önce ilişkili alarmları siliniz.");
				return genericResponseItem;
			}
			
			List<UserEventGroupPermission> userEventGroupPermission = userEventGroupPermissionRepository.findByEventGroupId(eventGroupId);
						
			//delete event group and permissions
			transactionalPermissionService.deleteEventGroup(eventGroupId);
			
			if (userEventGroupPermission != null) {
				
				Map<String, Object> userEventGroupPermissionsForLog = new TreeMap<>();
				for (UserEventGroupPermission tempUserEventGroupPermission : userEventGroupPermission) {
					
					userEventGroupPermissionsForLog.put("deleted", UserEventGroupPermissionItem.newInstanceForLog(tempUserEventGroupPermission));
					dbLogger.log(new Gson().toJson(userEventGroupPermissionsForLog), LogTypeE.USER_EVENT_GROUP_PERMISSION_DELETE);
					
				}			
				
			}
	
			Map<String, Object> eventGroupsForLog = new TreeMap<>();
			eventGroupsForLog.put("deleted", EventGroupItem.newInstanceForLog(eventGroup));
			
			dbLogger.log(new Gson().toJson(eventGroupsForLog), LogTypeE.EVENT_GROUP_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventType.delete.event.related"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	@RequestMapping(value="/eventGroupFilter")
	public GenericResponseItem getLayerInEventGroupColor(Integer layerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));		
		try {
			
			Layer layer = layerRepository.findAllById(layerId);
			
			if(layer == null) {
				
				log.error("label.layer.not.found");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));
				return genericResponseItem;
			}
			
			List<String> eventGroupColorList = eventGroupRepository.findAllByLayer(layer).stream().map(EventGroup::getColor).collect(Collectors.toList());
			genericResponseItem.setData(eventGroupColorList);
			genericResponseItem.setState(true);
			
		}catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	
	}
	
	@RequestMapping(value="/parentFilter")
	public GenericResponseItem parentFilter(Integer layerId, Integer currentEventGroupId){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
				
		if(layerId != null) {
			Layer layer = new Layer();
			layer.setId(layerId);	
			
			//tamamına parentlar icin ihtiyacımız var
			 List<EventGroupProjection> eventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);
			
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			List<EventGroupProjection> permissionedEventGroups = eventGroupList.stream().filter(o -> userEventGroupPermissionIdList.contains(o.getId())).collect(Collectors.toList());
			
			List<Integer> parentIdsOfPermissionedEventGroups = permissionedEventGroups.stream().map(EventGroupProjection::getParentId).collect(Collectors.toList());
			
			List<EventGroupProjection> parentsOfPermissionedEventGroups = eventGroupList.stream().filter(o -> parentIdsOfPermissionedEventGroups.contains(o.getId())).collect(Collectors.toList());
			
			for (EventGroupProjection item : parentsOfPermissionedEventGroups) {
				if(!permissionedEventGroups.contains(item)) {
					permissionedEventGroups.add(item);
				}
			}
			
			eventGroupList = permissionedEventGroups;
			
//			eventGroupList = eventGroupList.stream().filter(o -> userEventGroupPermissionIdList.contains(o.getId())).collect(Collectors.toList());
			
			EventGroupTree eventGroupTree = new EventGroupTree(eventGroupList, true);
			List<EventGroupItem> eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(currentEventGroupId);
			
			if(currentEventGroupId != null) {

				Optional<EventGroupItem> currentEventGroupItem = eventGroupItemList.stream().filter(f -> f.getId().equals(currentEventGroupId)).findAny();

				if(currentEventGroupItem.isPresent()) {
					
					EventGroupItem removeItem = currentEventGroupItem.get();
					eventGroupItemList.remove(removeItem);
				}				
			}
		
			genericResponseItem.setData(eventGroupItemList);
			
			return genericResponseItem;
		}
		return null;
		
	}
	
	
}
