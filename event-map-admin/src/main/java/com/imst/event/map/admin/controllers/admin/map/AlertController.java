package com.imst.event.map.admin.controllers.admin.map;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.AlertRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.services.TransactionalAlertService;
import com.imst.event.map.admin.db.specifications.AlertSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.vo.AlertItem;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/alert")
public class AlertController  {
	
	@Autowired
	private AlertRepository alertRepository;
	
	
	@Autowired
	private EventGroupRepository eventGroupRepository;
	
	@Autowired 
	private EventTypeRepository eventTypeRepository;

	@Autowired 
	private LayerRepository layerRepository;

	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@Autowired
	private TransactionalAlertService transactionalAlertService;
	


	
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "Sayfalama")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/alert");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
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
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
						
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
								
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		

				
		List<EventGroup> eventGroupList = eventGroupRepository.findAllByIdInOrderByName(userEventGroupPermissionIdList);
				
		List<EventGroupItem> eventGroupItemList =  new ArrayList<>();
		
		for(EventGroup item : eventGroupList) {

			EventGroupItem eitem = new EventGroupItem();
			eitem.setId(item.getId());
			eitem.setLayerId(item.getLayer().getId());
			eitem.setColor(item.getColor());
			eitem.setDescription(item.getDescription());
			eitem.setName(item.getName());
			eitem.setParentId(item.getParentId());
			
			eventGroupItemList.add(eitem);
		}
		
		List<LayerProjection> layerProjectionList = layerRepository.findAllProjectedByIdInOrderByName(userLayerPermissionIdList);
		
		EventGroupTree eventGroupTree = new EventGroupTree(eventGroupItemList);
		eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
				
		List<EventTypeItem> sortedEventTypeItemList = eventTypeItemList.stream().sorted(Comparator.comparing(EventTypeItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	


		modelAndView.addObject("eventTypes", sortedEventTypeItemList);
		modelAndView.addObject("eventGroups", eventGroupItemList);
		modelAndView.addObject("layers", layerProjectionList);	
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<AlertItem> data(AlertItem alerItem, @DatatablesParams DatatablesCriterias criteria) {
		
		
		PageRequest pageRequest = criteria.getPageRequest(EventItem.class);	
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		 
				
		AlertSpecification alertSpecification = new AlertSpecification(alerItem,userEventGroupPermissionIdList);
		Page<AlertItem> alertItems = masterDao.findAll(alertSpecification, pageRequest);
					
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		
		for(AlertItem alert : alertItems) {
			
			String name = ApplicationContextUtils.getMessage("icons." + alert.getEventTypeCode(), locale);
			name = name.equals("icons." + alert.getEventTypeCode()) ? alert.getEventTypeName() : name;
			alert.setEventTypeName(name);
		}
		
		DataSet<AlertItem> dataSet = new DataSet<>(alertItems.getContent(), 0L, alertItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criteria);
	}
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/deleted")
	public GenericResponseItem delete(Integer id) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		try {
			if (Optional.ofNullable(id).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.alert.not.found"));
				return genericResponseItem;
			}
			
			Alert alert = alertRepository.findById(id).orElse(null);
			
			if (alert == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.alert.not.found"));
				return genericResponseItem;
			}
						
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			if (!userEventGroupPermissionIdList.contains(alert.getEventGroup().getId())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.alert.delete.no.permission"));
				return genericResponseItem;
			}
			
			transactionalAlertService.deleteAlert(id);
						
			Map<String, Object> logMap = new TreeMap<>();
			logMap.put("deleted", AlertItem.newInstanceForLog(alert));
			
			try {
				dbLogger.log(new Gson().toJson(logMap), LogTypeE.ALERT_DELETE);
			} catch (Exception e) {
				log.error(e);
			}
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.alert.delete.error"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}

}