package com.imst.event.map.admin.controllers.admin.map;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.ActionStateE;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.BlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.specifications.BlackListSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.vo.BlackListItem;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/blacklist")
public class BlackListController {

	@Autowired
	private BlackListRepository blackListRepository;
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired 
	private EventTypeRepository eventTypeRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_LIST')")  
	@Operation(summary = "")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/black_list");
		
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
		
        List<EventTypeProjection> eventTypes = eventTypeRepository.findAllProjectedBy();
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		List<EventTypeItem> eventTypeItemList = new ArrayList<>();
		eventTypes.forEach(item->{

			EventTypeItem eventTypeItem = new EventTypeItem(item);
			String name = ApplicationContextUtils.getMessage("icons." + item.getCode(), locale);
			name = name.equals("icons." + item.getCode()) ? item.getName() : name;
			eventTypeItem.setName(name);
			eventTypeItemList.add(eventTypeItem);

		});

		List<EventTypeItem> sortedEventTypeItemList = eventTypeItemList.stream().sorted(Comparator.comparing(EventTypeItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	 
		modelAndView.addObject("eventTypes", sortedEventTypeItemList);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_LIST')")   
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<BlackListItem> data(BlackListItem blackListItem, @DatatablesParams DatatablesCriterias criteria) {
		
		PageRequest pageRequest = criteria.getPageRequest(BlackListItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
//		List <Integer> allGroups = eventGroupRepository.findAll().stream().map(EventGroup::getId).collect(Collectors.toList()); //for test		
		
//		List <Integer> allLayers = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList()); //for test		

		BlackListSpecification blackListSpecification = new BlackListSpecification(blackListItem, userLayerFullPermissionIdList, userEventGroupPermissionIdList);
		Page<BlackListItem> blackListItems = masterDao.findAll(blackListSpecification, pageRequest);

		
		List<Integer> eventGroupIdList = blackListItems.stream().map(BlackListItem::getEventGroupId).collect(Collectors.toList());
        List<EventGroupProjection> eventGroupList = eventGroupRepository.findAllProjectedByIdIn(eventGroupIdList);
        
        List<Integer> eventTypeIdList = blackListItems.stream().map(BlackListItem::getEventTypeId).collect(Collectors.toList());
        List<EventTypeProjection> eventTypeList = eventTypeRepository.findAllProjectedByIdIn(eventTypeIdList);
			
        String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		
        for (BlackListItem blackListItemTemp : blackListItems) {
			
			//Foreign key alanı olarak null gelebildiği için name alanaı sonradan atanamaktadır
        	Optional<EventGroupProjection> eventGroupOptional = eventGroupList.stream().filter(item -> item.getId().equals(blackListItemTemp.getEventGroupId())).findFirst();
			
			if(eventGroupOptional.isPresent()) {
				
				blackListItemTemp.setEventGroupName(eventGroupOptional.get().getName());
			}
			
			//Foreign key alanı olarak null gelebildiği için  name alanaı sonradan atanamaktadır
			Optional<EventTypeProjection> eventTypeOptional = eventTypeList.stream().filter(item -> item.getId().equals(blackListItemTemp.getEventTypeId())).findFirst();
			
			if(eventTypeOptional.isPresent()) {
				
				String name = ApplicationContextUtils.getMessage("icons." + eventTypeOptional.get().getCode(), locale);
				name = name.equals("icons." + eventTypeOptional.get().getCode()) ? blackListItemTemp.getEventTypeName() : name;
				blackListItemTemp.setEventTypeName(name);
			}
				
		}
 
		DataSet<BlackListItem> dataSet = new DataSet<>(blackListItems.getContent(), 0L, blackListItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criteria);
	}
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_MANAGE')")   
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer blackListId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (Optional.ofNullable(blackListId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.blackList.not.found"));  
			return genericResponseItem;
		}
		
		BlackList blackList = blackListRepository.findByIdAndStateIdIn(blackListId, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
		if (blackList == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.blackList.not.found"));
			return genericResponseItem;
		}
		
		if(blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.no.action.can.be.taken.on.the.blacklist"));
			return genericResponseItem;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
		
		
		if (blackList.getEventGroup() != null) {
			
			if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getEventGroup().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));  
				return genericResponseItem;
			}
			
		}	
		else {
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getLayer().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));  
				return genericResponseItem;
			}
			
		}	
		

		genericResponseItem.setData(new BlackListItem(blackList));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_MANAGE')")   
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(BlackListItem blackListItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(blackListItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.black.list.name.correctly"));  
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(blackListItem.getTag())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.black.list.tag.correctly")); 
			return genericResponseItem;
		}
		
		if (blackListItem.getLayerId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.correctly"));
			return genericResponseItem;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
					
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> blackListsForLog = new TreeMap<>();
		List<Object> newBlackListForLog = new ArrayList<>();
		LogTypeE logTypeE = LogTypeE.BLACK_LIST_ADD;
		BlackList blackList;
		
		 String[] arraytags = blackListItem.getTag().split("\n");
		 List<BlackList> blackListList = new ArrayList<>();
		 
		 for(String tag : arraytags) {
			 
			if (tag.trim().equals("")) {
				continue;
			}
		
			if (Optional.ofNullable(blackListItem.getId()).orElse(0) > 0) {//edit
				
				logTypeE = LogTypeE.BLACK_LIST_EDIT;   
				
				blackList = blackListRepository.findByIdAndStateIdIn(blackListItem.getId(), Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
				
				if (blackList == null) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.black.list.not.found"));  
					return genericResponseItem;
				}
				
				
				if (blackList.getEventGroup() != null) {
					
					Integer blackListEventGroupId = blackList.getEventGroup().getId();
					if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackListEventGroupId))) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
						return genericResponseItem;
					}
					
				}	
				else {
					
					Integer blackListLayerId = blackList.getLayer().getId();
					if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackListLayerId))) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
						return genericResponseItem;
					}
					
				}
				
				if (blackListItem.getEventGroupId() != null) {
					
					if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackListItem.getEventGroupId()))) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission")); 
						return genericResponseItem;
					}
					
				}	
				else {
					
					if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackListItem.getLayerId()))) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission")); 
						return genericResponseItem;
					}
					
				}
				
				if(blackList.getActionState() != null && (blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.no.action.can.be.taken.on.the.blacklist"));
					return genericResponseItem;
				}
				
				
				
				Boolean stateStatus = blackListItem.getState() != null && blackListItem.getState() == true ? true : false;
				if(!stateStatus.equals(StateE.getIntegerStateToBoolean(blackList.getState().getId()))) {
					blackList.setActionState(ActionStateE.PENDING.getActionState());
					blackList.setActionDate(nowT);
				}
					
				blackListsForLog.put("old", BlackListItem.newInstanceForLog(blackList));
				
			} else {//add

				if (blackListItem.getEventGroupId() != null) {
					
					if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackListItem.getEventGroupId()))) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
						return genericResponseItem;
					}
					
				}	
				else {
					
					if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackListItem.getLayerId()))) {
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission")); 
						return genericResponseItem;
					}
					
				}
				
				blackList = new BlackList();
				blackList.setCreateDate(nowT);
				blackList.setActionState(ActionStateE.PENDING.getActionState());
				blackList.setActionDate(nowT);
				blackList.setTag(tag.trim());
				
				Layer layer = new Layer();
				layer.setId(blackListItem.getLayerId());
				blackList.setLayer(layer);
				
				
				EventGroup eventGroup = new EventGroup();
				if (blackListItem.getEventGroupId() != null) {
					eventGroup.setId(blackListItem.getEventGroupId());
					blackList.setEventGroup(eventGroup);
				}
				else {
					blackList.setEventGroup(null);
				}
				
				EventType eventType = new EventType();
				if (blackListItem.getEventTypeId() != null) {
					eventType.setId(blackListItem.getEventTypeId());
					blackList.setEventType(eventType);
				}
					
			}
			
			
			blackList.setName(blackListItem.getName());				
			blackList.setCreateUser(ApplicationContextUtils.getUser().getUsername());
			blackList.setState(StateE.getBooleanState(blackListItem.getState()));
			

			blackList.setUpdateDate(nowT);
			blackListList.add(blackList);
			newBlackListForLog.add(BlackListItem.newInstanceForLog(blackList));
		}	
		 
		 blackListRepository.saveAll(blackListList);
			
			
		blackListsForLog.put("new", newBlackListForLog);
		dbLogger.log(new Gson().toJson(blackListsForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_MANAGE')")   
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer blackListId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		try {
			
			if (Optional.ofNullable(blackListId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.blackList.not.found"));  
				return genericResponseItem;
			}
			
			BlackList blackList = blackListRepository.findByIdAndStateIdIn(blackListId, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
			if (blackList == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.blackList.not.found"));  
				return genericResponseItem;
			}
			
			if(blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.no.action.can.be.taken.on.the.blacklist"));
				return genericResponseItem;
			}
			
			
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
			
			
			if (blackList.getEventGroup() != null) {
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getEventGroup().getId()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission")); 
					return genericResponseItem;
				}
				
			}	
			else {
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getLayer().getId()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission")); 
					return genericResponseItem;
				}
				
			}	
			
//			boolean deletetedState = blackListService.updatingEventsAfterBlackListDeleted(blackList);
//			
//			if(!deletetedState) {
//				genericResponseItem.setState(false);
//				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
//				return genericResponseItem;
//			}
			
			blackList.setState(StateE.DELETED.getState());
			blackList.setActionState(ActionStateE.PENDING.getActionState());
			blackListRepository.save(blackList);
			
			Map<String, Object> blackListsForLog = new TreeMap<>();
			blackListsForLog.put("deleted", BlackListItem.newInstanceForLog(blackList));
			
			dbLogger.log(new Gson().toJson(blackListsForLog), LogTypeE.BLACK_LIST_DELETE);  
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	@RequestMapping(value="/eventGroupFilter")
	public GenericResponseItem eventGroupFilter(Integer layerId){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		if(layerId != null) {
			Layer layer = new Layer();
			layer.setId(layerId);	
			
			List<EventGroupProjection> eventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);
			
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			eventGroupList = eventGroupList.stream().filter(o -> userEventGroupPermissionIdList.contains(o.getId())).collect(Collectors.toList());
			
			EventGroupTree eventGroupTree = new EventGroupTree(eventGroupList, true);
			List<EventGroupItem> eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);

			genericResponseItem.setData(eventGroupItemList);
			
			return genericResponseItem;
		}
		return null;
		
	}
}
