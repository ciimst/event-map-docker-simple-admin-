package com.imst.event.map.admin.controllers.admin.map;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.SettingsE;
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.controllers.admin.CrudControllerAbs;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.EventTagProjection;
import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.projections.TagProjection;
import com.imst.event.map.admin.db.repositories.EventBlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventMediaRepository;
import com.imst.event.map.admin.db.repositories.EventRepository;
import com.imst.event.map.admin.db.repositories.EventTagRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.TagRepository;
import com.imst.event.map.admin.db.repositories.TileServerRepository;
import com.imst.event.map.admin.db.specifications.BlackListCheckedSpecification;
import com.imst.event.map.admin.db.specifications.EventExcelSpecification;
import com.imst.event.map.admin.db.specifications.EventSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.S3Service;
import com.imst.event.map.admin.services.SettingsService;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.utils.MyStringUtils;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.vo.BlackListItem;
import com.imst.event.map.admin.vo.EventExcelItem;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.admin.vo.EventItemBatchOperationsForLog;
import com.imst.event.map.admin.vo.EventMediaItem;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.TileServerItem;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventBlackList;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventMedia;
import com.imst.event.map.hibernate.entity.EventTag;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.Tag;
import com.imst.event.map.hibernate.entity.TileServer;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/event-basic")
public class EventBasicController extends CrudControllerAbs<EventItem> {
	
	private static final String EDIT_LINK = "admin/map/event-basic/edit/";
	
	@Autowired
	private TileServerRepository tileServerRepository;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventMediaRepository eventMediaRepository;
	@Autowired 
	private EventTypeRepository eventTypeRepository;
	@Autowired
	private TagRepository tagRepository;
	@Autowired 
	private LayerRepository layerRepository;
	@Autowired
	private EventTagRepository eventTagRepository;	
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@Autowired 
	private EventBlackListRepository eventBlackListRepository;
	
	@Autowired private S3Service s3Service;
	@Autowired
	private SettingsService settingsService;
	
	@Value("${event.reserved1}")
    private String eventReserved1; 
	
	@Value("${event.reserved2}")
    private String eventReserved2;
	
	@Value("${event.reserved3}")
    private String eventReserved3;
	
	@Value("${event.reserved4}")
    private String eventReserved4;
	
	@Value("${event.reserved5}")
    private String eventReserved5;
	
	@Value("${using.helm.config}")
    private Boolean usingHelmConfig;
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "Sayfalama")
	@Override
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/event_basic");
		
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
		
//		List<LayerProjection> layerProjectionList = layerRepository.findAllProjectedByOrderByName();
//
//		List<EventGroupItem> eventGroupItemList = eventGroupRepository.findAllByOrderByName();
		EventGroupTree eventGroupTree = new EventGroupTree(eventGroupItemList);
		eventGroupItemList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
				
		List<EventTypeItem> sortedEventTypeItemList = eventTypeItemList.stream().sorted(Comparator.comparing(EventTypeItem::getName, Statics.sortedCollator())).collect(Collectors.toList());	


		modelAndView.addObject("eventTypes", sortedEventTypeItemList);
		modelAndView.addObject("eventGroups", eventGroupItemList);
		modelAndView.addObject("layers", layerProjectionList);	
		
		
		int maxCountEventsExcel = SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL);
		modelAndView.addObject("maxCountEventsExcel", maxCountEventsExcel);
		
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "")
	@Override
	public DatatablesResponse<EventItem> data(EventItem eventItem, DatatablesCriterias criteria) {
		
//		criteria.getSortedColumnDefs().get(0).setName("createDate");
		
		PageRequest pageRequest = criteria.getPageRequest(EventItem.class);	
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
//		List <Integer> allGroups = eventGroupRepository.findAll().stream().map(EventGroup::getId).collect(Collectors.toList()); //for test		
		 
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
				
		EventSpecification eventSpecification = new EventSpecification(eventItem,groupIdList,userIdList, userEventGroupPermissionIdList);
		Page<EventItem> eventItems = masterDao.findAll(eventSpecification, pageRequest);
					
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		
		for(EventItem event : eventItems) {
			
			String name = ApplicationContextUtils.getMessage("icons." + event.getEventTypeCode(), locale);
			name = name.equals("icons." + event.getEventTypeCode()) ? event.getEventTypeName() : name;
			event.setEventTypeName(name);
		}
		
		DataSet<EventItem> dataSet = new DataSet<>(eventItems.getContent(), 0L, eventItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criteria);
	}
	
	
	
	@RequestMapping(value="/batchOperations/{bacthState}")
	public GenericResponseItem eventGroupFilter(@PathVariable("bacthState") Boolean bacthState, EventItem eventItem){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		try {
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			List<Integer> groupIdList = sessionUser.getGroupIdList();
			List<Integer> userIdList = sessionUser.getUserIdList();
			
			EventSpecification eventSpecification = new EventSpecification(eventItem,groupIdList,userIdList, userEventGroupPermissionIdList);
			List<EventItem> eventItems = masterDao.findAll(eventSpecification, Sort.by(Direction.DESC, "id"));
			List<Integer> eventIdList = eventItems.stream().map(EventItem::getId).collect(Collectors.toList());
			
/////////////////////////////////////////////////////////
			
			user.setId(sessionUser.getUserId());	
			
			for(EventItem event : eventItems) {
				
				if (bacthState &&  !userEventGroupPermissionIdList.contains(event.getEventGroupId())) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.no.permission"));
					return genericResponseItem;
				}
				
				if (event.getBlackListTag() != null) {
					
					Layer tempLayer = new Layer();
					tempLayer.setId(event.getLayerId());
					
					List<EventGroupProjection> allEventGroupProjectionList = eventGroupRepository.findAllProjectedByLayerOrderByName(tempLayer);
					
					List<EventGroupItem> allEventGroupList = new ArrayList<>();
					
					allEventGroupProjectionList.forEach(item-> {
						
						EventGroupItem eventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getLayerId(), item.getLayerName(), item.getParentId(), item.getDescription());
						
						allEventGroupList.add(eventGroupItem);
					});
					
					EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList);
					List<Integer> permissionEventGroupIdParentList = eventGroupTree.getPermissionEventGroupParent(Arrays.asList(event.getEventGroupId()));
					
					BlackListItem blackListItem = new BlackListItem();
			    	blackListItem.setTag(event.getBlackListTag() != null ? event.getBlackListTag().trim() : null); 
			    	blackListItem.setLayerId(event.getLayerId());  
					blackListItem.setEventGroupId(event.getEventGroupId()); 
					blackListItem.setEventTypeId(event.getEventTypeId()); 
					
					BlackListCheckedSpecification blackListCheckedSpecification = new BlackListCheckedSpecification(blackListItem, permissionEventGroupIdParentList);
					List<BlackListItem> blackLists = masterDao.findAll(blackListCheckedSpecification, Sort.by(Direction.DESC, "id"));
					
					if(!blackLists.isEmpty()) {
						
						BlackListItem blackListItemDb = blackLists.get(0);
						
						log.warn(event.getTitle() + " olayı; Black List'e ait Id : " + blackListItemDb.getId() + ", Name : " + blackListItemDb.getName() + ", Etiket : " + blackListItemDb.getTag() + ", Katman Id : " + blackListItemDb.getLayerId() + ", Katman Adı : " + blackListItemDb.getLayerName() + ", Olay Grubu Id : " + blackListItemDb.getEventGroupId() + ", Olay Türü Id : " + blackListItemDb.getEventTypeId() + " bilgilerini içerdiği için ekleme yapılamamaktadır.");
						genericResponseItem.setState(false);
						genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.editing.cannot.be.done.because.event.content.contains.fields.belonging.blacklist") + " Olay Adı: " + event.getTitle() + " Olay grubu:" + event.getEventGroupName() + " Olay Türü: " + event.getEventTypeName()); 

						return genericResponseItem;
					}
				}
			}
			
			
			
			/////////////////////////////////////////////////////////
			
			Integer stateId = StateE.getBatchOperationStateChange(bacthState).getId();
			eventRepository.updateBatchOperationsEventState(eventIdList, stateId);
			
			EventItemBatchOperationsForLog eventItemBatchOperationsForLog = new EventItemBatchOperationsForLog();
			eventItemBatchOperationsForLog.setEventIdList(eventIdList);
			
			eventItemBatchOperationsForLog.setState(StateE.getBatchOperationStateChange(bacthState));
			
			try {
				Map<String, Object> logMap = new TreeMap<>();
				
				logMap.put("new", eventItemBatchOperationsForLog);
				dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_EDIT);
			} catch (Exception e) {
				log.error(e);
			}
			
		}catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
			
		return genericResponseItem;
		
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@RequestMapping(value = "/stateChange")
	public GenericResponseItem stateChange(Integer id) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		try {
			if (Optional.ofNullable(id).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.not.found"));
				return genericResponseItem;
			}
			
			Event event = eventRepository.findByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
			
			if (event == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.not.found"));
				return genericResponseItem;
			}
			
			
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			if (!userEventGroupPermissionIdList.contains(event.getEventGroup().getId())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.no.permission"));
				return genericResponseItem;
			}
			
			if (event.getBlackListTag() != null) {
				
				Layer tempLayer = new Layer();
				tempLayer.setId(event.getEventGroup().getLayer().getId());
				
				List<EventGroupProjection> allEventGroupProjectionList = eventGroupRepository.findAllProjectedByLayerOrderByName(tempLayer);
				
				List<EventGroupItem> allEventGroupList = new ArrayList<>();
				
				allEventGroupProjectionList.forEach(item-> {
					
					EventGroupItem eventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getLayerId(), item.getLayerName(), item.getParentId(), item.getDescription());
					
					allEventGroupList.add(eventGroupItem);
				});
				
				EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList);
				List<Integer> permissionEventGroupIdParentList = eventGroupTree.getPermissionEventGroupParent(Arrays.asList(event.getEventGroup().getId()));
				
				BlackListItem blackListItem = new BlackListItem();
		    	blackListItem.setTag(event.getBlackListTag() != null ? event.getBlackListTag().trim() : null); 
		    	blackListItem.setLayerId(event.getEventGroup().getLayer().getId());  
				blackListItem.setEventGroupId(event.getEventGroup().getId()); 
				blackListItem.setEventTypeId(event.getEventType().getId()); 
				
				BlackListCheckedSpecification blackListCheckedSpecification = new BlackListCheckedSpecification(blackListItem, permissionEventGroupIdParentList);
				List<BlackListItem> blackLists = masterDao.findAll(blackListCheckedSpecification, Sort.by(Direction.DESC, "id"));
				
				if(!blackLists.isEmpty()) {
					
					BlackListItem blackListItemDb = blackLists.get(0);
					
					log.warn(event.getTitle() + " olayı; Black List'e ait Id : " + blackListItemDb.getId() + ", Name : " + blackListItemDb.getName() + ", Etiket : " + blackListItemDb.getTag() + ", Katman Id : " + blackListItemDb.getLayerId() + ", Katman Adı : " + blackListItemDb.getLayerName() + ", Olay Grubu Id : " + blackListItemDb.getEventGroupId() + ", Olay Türü Id : " + blackListItemDb.getEventTypeId() + " bilgilerini içerdiği için ekleme yapılamamaktadır.");
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.editing.cannot.be.done.because.event.content.contains.fields.belonging.blacklist")); 

					return genericResponseItem;
				}
			}


			event.setState(StateE.getToggleStateChange(event.getState().getId()));
			
			
			eventRepository.save(event);
			
			List<Integer> idList = Arrays.asList(event.getId());
			EventItemBatchOperationsForLog eventItemBatchOperationsForLog = new EventItemBatchOperationsForLog();
			eventItemBatchOperationsForLog.setEventIdList(idList);
			eventItemBatchOperationsForLog.setState(event.getState());
			
			try {
				Map<String, Object> logMap = new TreeMap<>();
				
				logMap.put("new", eventItemBatchOperationsForLog);
				dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_EDIT);
			} catch (Exception e) {
				log.error(e);
			}
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.unknown.error"));
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
	
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@Operation(summary = "güncelle")
	@Override
	public ModelAndView edit(Integer id) {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/event_basic_edit");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
						
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
								
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		List<LayerProjection> layerList = layerRepository.findAllProjectedByIdInOrderByName(userLayerPermissionIdList);
		
//		List<LayerProjection> layerList = layerRepository.findAllProjectedByOrderByName();
		
		modelAndView.addObject("layers", layerList);
		
		EventItem eventItem;
		
		List<EventGroupProjection> eventGroups = new ArrayList<>();
		if(id == null) {
			eventItem = new EventItem();
			
			eventGroups = eventGroupRepository.findAllProjectedByIdInOrderByName(userEventGroupPermissionIdList);
			
//			eventGroups = eventGroupRepository.findAllProjectedByOrderByName();

		} else {
			eventItem = eventRepository.findOneProjectedByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()));
			
			// eventItem bulunamazsa hata sayfası açılır
			Layer layer = new Layer();
			layer.setId(eventItem.getLayerId());
			
			
						
			int selectedGroupId = eventItem.getEventGroupId();
			EventGroupProjection eventGroupProjectionBasic = eventGroupRepository.findProjectedById(selectedGroupId);
			int selectedLayerId = eventGroupProjectionBasic.getLayerId();
			
			if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(selectedGroupId))) {
				modelAndView = new ModelAndView("error");
				modelAndView.setStatus(HttpStatus.FORBIDDEN);
				return modelAndView;
			}
			
			modelAndView.addObject("selectedLayerId", selectedLayerId);
			
			eventGroups = eventGroupRepository.findAllProjectedByLayerAndIdInOrderByName(layer, userEventGroupPermissionIdList);

//			eventGroups = eventGroupRepository.findAllByLayerOrderByName(layer);
			
		}
		
			
		modelAndView.addObject("eventItem", eventItem);
				
		List<EventMediaItem> eventMedias = eventMediaRepository.findAllProjectedByEventId(id);
		List<EventMediaItem> eventMediaList = new ArrayList<>();
		for(EventMediaItem mediaPath : eventMedias) {
			
			String imageRelativePath = new String(Base64.getEncoder().encode(mediaPath.getPath().getBytes()));
			mediaPath.setPath(imageRelativePath);	
			eventMediaList.add(mediaPath);
	
		}
		
		eventMediaList = eventMediaList.stream().sorted(Comparator.comparing(EventMediaItem::getId)).collect(Collectors.toList());
		modelAndView.addObject("eventMedias", eventMediaList);
		
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

		List<EventTypeItem> list = eventTypeItemList.stream().sorted(Comparator.comparing(EventTypeItem::getName, Statics.sortedCollator())).collect(Collectors.toList());
		modelAndView.addObject("eventTypes", list);
		
				
		
		
		List<EventGroupItem> eventGroupList = new ArrayList<>();
		eventGroups.forEach(item -> {
			eventGroupList.add(new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getLayerId(), item.getLayerName(), item.getParentId(), ""));});
		EventGroupTree eventGroupTree = new EventGroupTree(eventGroupList);
		List<EventGroupItem> resultEventGroupList = eventGroupTree.eventGroupListThatCanBeAddedAsParent(null);
		modelAndView.addObject("eventGroups", resultEventGroupList);
		
		List<EventTagProjection> tagList = new ArrayList<>();
		Event event = new Event();
		if(id != null) {			
			event.setId(id);
		    tagList = eventTagRepository.findAllProjectedByEvent(event);
			tagList.stream().map(EventTagProjection::getTag);	
		}		
		modelAndView.addObject("tagIds", tagList);
		
	
		List<TagProjection> tags = tagRepository.findAllProjectedByOrderByName();
		List<TagProjection> tagsList = new ArrayList<>();
		
		boolean control = true;
		for(TagProjection item: tags) {			
			for(EventTagProjection item2 : tagList) {				
				if(item.getId() == item2.getTag().getId()) {					
					control = false;
				}
			}
			if(control == true) {
				tagsList.add(item);
			}
			control = true;
		}
		tagsList = tagsList.stream().sorted(Comparator.comparing(TagProjection::getName, Statics.sortedCollator())).collect(Collectors.toList());
		modelAndView.addObject("tags", tagsList); 
	

		List<TileServer> all = tileServerRepository.findAll();
		List<TileServerItem> tileServerItems = all.stream().map(TileServerItem::new).collect(Collectors.toList());
		modelAndView.addObject("tileServers", tileServerItems);
		
		return modelAndView;
	}
	
	
	
	@Operation(summary = "Excele Aktar")	
	@RequestMapping(value = "/export/{eventExcelItem}", method = RequestMethod.GET)
	public ResponseEntity<?> exportToExcelAndDownload(@PathVariable("eventExcelItem") String eventItemStr) {		
		
		ResponseEntity<?> responseEntity = null;
		UserItemDetails sessionUser = null;
		
		try {
				
			settingsService.updateSettingsCache();
			
			
			sessionUser = ApplicationContextUtils.getUser();
			
			sessionUser.setExcelStateInformation("started");
						
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
				
			List<Integer> groupIdList = sessionUser.getGroupIdList();
			List<Integer> userIdList = sessionUser.getUserIdList();
			
			ObjectMapper objectMapper = new ObjectMapper();
			EventExcelItem eventExcelItem = objectMapper.readValue(eventItemStr, EventExcelItem.class);

			
			Date startDate = DateUtils.convertToDate(eventExcelItem.getStartDateStr(), DateUtils.TURKISH);
			Date endDate = DateUtils.convertToDate(eventExcelItem.getEndDateStr(), DateUtils.TURKISH);
			
			eventExcelItem.setStartDate(startDate);
			eventExcelItem.setEndDate(endDate);
			
		
			Integer page = 0;			
			int pageLoadLimit = SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL);
			PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Direction.DESC, "createDate")); 
			
			EventExcelSpecification eventExcelSpecification = new EventExcelSpecification(eventExcelItem,groupIdList,userIdList,userEventGroupPermissionIdList);
			
		    List<EventExcelItem> eventItems = masterDao.findAll(eventExcelSpecification, pageRequest).getContent();//.stream().limit(1000).collect(Collectors.toList());
				
		    
			String eventReserved1Utf8 = eventReserved1;
			String eventReserved2Utf8 = eventReserved2;
			String eventReserved3Utf8 = eventReserved3;
			String eventReserved4Utf8 = eventReserved4;
			String eventReserved5Utf8 = eventReserved5;
			
			if (!usingHelmConfig) {
				eventReserved1Utf8 = MyStringUtils.toUTF8(eventReserved1);
				eventReserved2Utf8 = MyStringUtils.toUTF8(eventReserved2);
				eventReserved3Utf8 = MyStringUtils.toUTF8(eventReserved3);
				eventReserved4Utf8 = MyStringUtils.toUTF8(eventReserved4);
				eventReserved5Utf8 = MyStringUtils.toUTF8(eventReserved5);
			}
			
		    String[] columns = {"Başlık", "Kısa Açıklama", "Açıklama", "Olay Tarihi", "Olay Türü", "Katman", "Olay Grubu", "Ülke", "Şehir", "Ayrılmış Bağlantı", "Ayrılmış Tür", "Ayrılmış Anahtar", "Ayrılmış Kimlik(ID)", "Enlem", "Boylam", "Black List Tag", 
		    		"Oluşturan Kullanıcı", "Oluşturulma Tarihi", "Güncellenme Tarihi", "Olay Grup Rengi", "Kullanıcı Id", "Grup Id", "Durum", eventReserved1Utf8, eventReserved2Utf8, eventReserved3Utf8, eventReserved4Utf8, eventReserved5Utf8};
		    List<String> columnsList = new ArrayList<String>();
		    boolean[] settings = {
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_TITLE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_SPOT),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_DESCRIPTION),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_DATE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_TYPE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LAYER_NAME),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_NAME),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_COUNTRY),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CITY),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_LINK),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_TYPE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_KEY),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EXCEL_EVENT_RESEERVED_ID),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LATITUDE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LONGITUDE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_BLACK_LIST_TAG),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CREATE_USER),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CREATE_DATE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_UPDATE_DATE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_COLOR),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_USER_ID),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_ID),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_STATE),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_1),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_2),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_3),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_4),
		    		SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_5),
		    		};
		    
		    for (int i = 0; i < settings.length; i++) {
		    	if (settings[i]) {
		    		columnsList.add(columns[i]);
		    	}
		    }

		    Workbook workbook = new XSSFWorkbook();

		    Sheet sheet = workbook.createSheet("events");

		    Font headerFont = workbook.createFont();

		    headerFont.setBold(true);

		    headerFont.setFontHeightInPoints((short) 14);

		    headerFont.setColor(IndexedColors.BLACK.getIndex());

		    CellStyle headerCellStyle = workbook.createCellStyle();

		    headerCellStyle.setFont(headerFont);

		    Row headerRow = sheet.createRow(0);

		    for (int i = 0; i < columnsList.size(); i++) {
		
			   Cell cell = headerRow.createCell(i);
			
			   cell.setCellValue(columnsList.get(i));
			
			   cell.setCellStyle(headerCellStyle);
		    }

		    int rowNum = 1;

		    for (EventExcelItem event : eventItems) {
		    	
		    	
		    	String language = LocaleContextHolder.getLocale().getLanguage();
				Locale locale = new Locale(language);
				
				String eventTypeName = ApplicationContextUtils.getMessage("icons." + event.getEventTypeCode(), locale);
				eventTypeName = eventTypeName.equals("icons." + event.getEventTypeCode()) ? event.getEventTypeName() : eventTypeName;
				
			    Row row = sheet.createRow(rowNum++);
			    int counter = 0;
			    
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_TITLE)) {
			    	row.createCell(counter).setCellValue(event.getTitle()); 
			    	counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_SPOT)) {
				    row.createCell(counter).setCellValue(event.getSpot());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_DESCRIPTION)) {
				    row.createCell(counter).setCellValue(event.getDescription());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_DATE)) {
				    row.createCell(counter).setCellValue(event.getEventDateStr());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_TYPE)) {
				    row.createCell(counter).setCellValue(eventTypeName);
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LAYER_NAME)) {
				    row.createCell(counter).setCellValue(event.getLayerName());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_NAME)) {
				    row.createCell(counter).setCellValue(event.getEventGroupName());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_COUNTRY)) {
				    row.createCell(counter).setCellValue(event.getCountry());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CITY)) {
				    row.createCell(counter).setCellValue(event.getCity());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_LINK)) {
				    row.createCell(counter).setCellValue(event.getReservedLink());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_TYPE)) {
				    row.createCell(counter).setCellValue(event.getReservedType());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_KEY)) {
				    row.createCell(counter).setCellValue(event.getReservedKey());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EXCEL_EVENT_RESEERVED_ID)) {
				    row.createCell(counter).setCellValue(event.getReservedId());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LATITUDE)) {
				    row.createCell(counter).setCellValue(event.getLatitude());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_LONGITUDE)) {
				    row.createCell(counter).setCellValue(event.getLongitude());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_BLACK_LIST_TAG)) {
				    row.createCell(counter).setCellValue(event.getBlackListTag());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CREATE_USER)) {
				    row.createCell(counter).setCellValue(event.getCreateUser());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_CREATE_DATE)) {
				    row.createCell(counter).setCellValue(event.getCreateDateStr());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_UPDATE_DATE)) {
				    row.createCell(counter).setCellValue(event.getUpdateDateStr());  
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_COLOR)) {
				    row.createCell(counter).setCellValue(event.getEventGroupColor());			    
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_USER_ID)) {
				    row.createCell(counter).setCellValue(event.getUserId());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_GROUP_ID)) {
				    row.createCell(counter).setCellValue(event.getGroupId());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_STATE)) {
				    row.createCell(counter).setCellValue(event.getState() != null && event.getStateId().equals(StateE.TRUE.getValue()) ? "Aktif" : "Pasif");
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_1)) {
				    row.createCell(counter).setCellValue(event.getReserved1());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_2)) {
				    row.createCell(counter).setCellValue(event.getReserved2());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_3)) {
				    row.createCell(counter).setCellValue(event.getReserved3());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_4)) {
				    row.createCell(counter).setCellValue(event.getReserved4());
				    counter++;
			    }
			    if(SettingsUtil.getBoolean(SettingsE.EXCEL_EVENT_RESERVED_5)) {
				    row.createCell(counter).setCellValue(event.getReserved5());
				    counter++;
			    }
		    }
		    
		  
		    for (int i = 0; i < columnsList.size(); i++) {
		    
		    	if(i > 2) {
		    	    sheet.autoSizeColumn(i);
		    	}else { 
  	    		    sheet.setColumnWidth(i, 40 * 256);
		    	}
		    }
		   

		    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    workbook.write(stream);
		    workbook.close();
		   		       
		    HttpHeaders headers = new HttpHeaders();
					
			headers.add("Content-Disposition", String.format("attachment; filename=Events.xlsx"));
					
			responseEntity = ResponseEntity.ok()
		      .headers(headers)
		      .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
		      .body(stream.toByteArray());
		
			return responseEntity;
			
		}catch (IOException e) {
			log.error("Excel Oluşturulurken Hata Oluştu");
		}finally {
			
			sessionUser.setExcelStateInformation("finished");
		}
		
		return responseEntity;
 	 }

	
	@Operation(summary = "Excele State Information")	
	@RequestMapping(value = "/excelStateInformation", method = RequestMethod.GET)
	private String excelStateInformation() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
	
		return sessionUser.getExcelStateInformation();
	}

	
		
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@Operation(summary = "Yeni kaydet")
	@Override
	public GenericResponseItem save(EventItem eventItem) {
		
		GenericResponseItem genericResponseItem = validate(eventItem);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		try {
			
			EventGroup eventGroup = eventGroupRepository.findById(eventItem.getEventGroupId()).orElse(null);
			if (eventGroup == null) {
				log.error("TODO: eventGroup bulunamadı");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.not.found"));
				return genericResponseItem;
			}
			
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			EventType eventType = eventTypeRepository.findById(eventItem.getEventTypeId()).orElse(null);
			
			if (eventType == null) {
				log.error("TODO: eventType bulunamadı");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventType.not.found"));
				return genericResponseItem;
			}
			
			if(eventItem.getTitle().length() > 256) {
				
				log.error("Başlık bilgisi 256 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.title.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getSpot().length() > 512) {
				
				log.error("Kısa açıklama bilgisi 512 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.spot.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getDescription().length() > 4096) {
				
				log.error("Açıklama bilgisi 4096 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.description.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getCountry().length() > 64) {
				
				log.error("Ülke bilgisi 64 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.country.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getCity().length() > 64) {
				
				log.error("Şehir bilgisi 64 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.city.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getReservedKey().length() > 256) {
				
				log.error("Ayrılmış Anahtar bilgisi 256 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.reserved.key.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getReservedId().length() > 256) {
				
				log.error("Ayrılmış Kimlik (ID) bilgisi 256 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.reversed.id.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getReservedLink().length() > 4096) {
				
				log.error("Ayrılmış Link bilgisi 4096 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.reversed.link.max.exceed"));
				return genericResponseItem;
			}
			
			if(eventItem.getReservedType().length() > 256) {
				
				log.error("Ayrılmış Tür bilgisi 256 karakterden fazla girilemez.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.reversed.type.max.exceed"));
				return genericResponseItem;
			}
			
//			Boolean isBlackListEvent = false;
//			Integer blackListId = null;
			List<BlackListItem> blackLists = new ArrayList<>();
			if (eventItem.getBlackListTag() != null) {
				
				Layer tempLayer = new Layer();
				tempLayer.setId(eventItem.getLayerId());
				
				List<EventGroupProjection> allEventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(tempLayer);
				EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList, true);
				List<Integer> permissionEventGroupIdParentList = eventGroupTree.getPermissionEventGroupParent(Arrays.asList(eventItem.getEventGroupId()));
				
				BlackListItem blackListItem = new BlackListItem();
		    	blackListItem.setTag(eventItem.getBlackListTag() != null ? eventItem.getBlackListTag().trim() : null); 
		    	blackListItem.setLayerId(eventItem.getLayerId());  
				blackListItem.setEventGroupId(eventItem.getEventGroupId()); 
				blackListItem.setEventTypeId(eventItem.getEventTypeId()); 
				
				BlackListCheckedSpecification blackListCheckedSpecification = new BlackListCheckedSpecification(blackListItem, permissionEventGroupIdParentList);
				blackLists = masterDao.findAll(blackListCheckedSpecification, Sort.by(Direction.DESC, "id"));
				
				if(!blackLists.isEmpty()) {
					
//					isBlackListEvent = true;
//					blackListIds = blackLists.stream().map(BlackListItem::getId).collect(Collectors.toList());
//					blackListId = blackLists.get(0).getId();
//					BlackListItem blackListItemDb = blackLists.get(0);
//					
//					log.warn(eventItem.getTitle() + " olayı; Black List'e ait Id : " + blackListItemDb.getId() + ", Name : " + blackListItemDb.getName() + ", Etiket : " + blackListItemDb.getTag() + ", Katman Id : " + blackListItemDb.getLayerId() + ", Katman Adı : " + blackListItemDb.getLayerName() + ", Olay Grubu Id : " + blackListItemDb.getEventGroupId() + ", Olay Türü Id : " + blackListItemDb.getEventTypeId() + " bilgilerini içerdiği için ekleme yapılamamaktadır.");
//					genericResponseItem.setState(false);
//					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.adding.cannot.be.done.because.event.content.contains.fields.belonging.blacklist")); 
//
//					return genericResponseItem;
				}
			}
		
			Timestamp nowT = DateUtils.nowT();
			Map<String, Object> logMap = new TreeMap<>();
			LogTypeE logTypeE;
			
			Event event;
			
			if (Optional.ofNullable(eventItem.getId()).orElse(0) > 0) {//edit
				
				logTypeE = LogTypeE.EVENT_EDIT;
				event = eventRepository.findByIdAndStateIdIn(eventItem.getId(), Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()));
				
				if (event == null) {
					log.error("TODO: event bulunamadı");
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.not.found"));
					return genericResponseItem;
				}
				
				
				// Hem editlenecek olayın olay grubuna, izin var mı bakılır. Hem de editlenen olayın, kaydedilecegi olay grubuna izin var mı bakılır
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(event.getEventGroup().getId()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
					return genericResponseItem;
				}
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getId()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
					return genericResponseItem;
				}
				
				logMap.put("old", EventItem.newInstanceForLog(event));
				
				
				if(!blackLists.isEmpty()) {
					
					//tüm event-blacklist eşleşmelerini çek. Eşleşenleri brişey yapma. olmayanları tabloya ekle. tablo da fazla olanları sil.
					
					List<EventBlackList> allCurrentEventBlackList = eventBlackListRepository.findAllByEventId(event.getId());			
					List<EventBlackList> saveEventBlackList = new ArrayList<>();
					List<Integer> deleteEventBlackList = new ArrayList<>();
					
					
					for(BlackListItem item : blackLists) {
						
						//#region olmayanları tabloya ekleme kısmı
						List<EventBlackList> eventBlackListItem = allCurrentEventBlackList.stream().filter(f -> f.getBlackList().getId().equals(item.getId()))
								.collect(Collectors.toList());				
						if(eventBlackListItem.isEmpty()) {
							
							EventBlackList eventBlackList = new EventBlackList();
							
							Event newEvent  = new Event();
							newEvent.setId(event.getId());
											
							BlackList blackList = new BlackList();
							blackList.setId(item.getId());	
							
							eventBlackList.setEvent(newEvent);		
							eventBlackList.setBlackList(blackList);	
							
							saveEventBlackList.add(eventBlackList);
						}
						//#endregion olmayanları tabloya ekleme kısmı
					}
					
					
					for(EventBlackList item : allCurrentEventBlackList) {
						
						//#region fazla olanları tablodan silme		
						
						boolean status = blackLists.stream().anyMatch(f -> item.getBlackList().getId().equals(f.getId()));
						
						if(!status) {
							deleteEventBlackList.add(item.getBlackList().getId());
						}
						//#endregion fazla olanları tablodan silme
						
					}
					eventBlackListRepository.eventBlackListDeleted(event.getId(), deleteEventBlackList);			
					eventBlackListRepository.saveAll(saveEventBlackList);
					
				}else {
					//Eğer blacklistTag silinmişse event-blacklist tablosundan da kaydını silmek gerekir.
					eventBlackListRepository.eventBlackListDeletedEventIdIn(event.getId());
				}
				
				
			} else {//add
				
				logTypeE = LogTypeE.EVENT_ADD;
				event = new Event();
				event.setCreateDate(nowT);			
				
				if (!userEventGroupPermissionIdList.contains(eventGroup.getId())) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventGroup.no.permission"));
					return genericResponseItem;
				}
				
				
				
				
			}
			
			event.setUpdateDate(nowT);
			event.setCity(eventItem.getCity());
			event.setCountry(eventItem.getCountry());
			event.setEventType(eventType);
			event.setEventGroup(eventGroup);
			event.setTitle(eventItem.getTitle());
			event.setSpot(eventItem.getSpot());
			event.setDescription(eventItem.getDescription());
			event.setLatitude(eventItem.getLatitude());
			event.setLongitude(eventItem.getLongitude());
			event.setBlackListTag(eventItem.getBlackListTag() != null ? eventItem.getBlackListTag().trim() : null);
			event.setEventDate(DateUtils.convertToTimestamp(eventItem.getEventDateStr(),ApplicationContextUtils.getMessage("label.time.format")));
			event.setCreateUser(ApplicationContextUtils.getUser().getUsername());

			event.setState(!blackLists.isEmpty() ? StateE.BLACKLISTED.getState() : StateE.getBooleanState(eventItem.getState()));

			event.setUserId(eventItem.getUserId());
			event.setGroupId(eventItem.getGroupId());
			
			event.setReservedId(eventItem.getReservedId());
			event.setReservedKey(eventItem.getReservedKey());
			event.setReservedLink(eventItem.getReservedLink());
			event.setReservedType(eventItem.getReservedType());
			
			event.setReserved1(eventItem.getReserved1());
			event.setReserved2(eventItem.getReserved2());
			event.setReserved3(eventItem.getReserved3());
			event.setReserved4(eventItem.getReserved4());
			event.setReserved5(eventItem.getReserved5());

			
			Event savedEvent = eventRepository.save(event);
			
			if(Optional.ofNullable(eventItem.getId()).orElse(0) == 0) {//add
				
				List<EventBlackList> saveEventBlackList = new ArrayList<>();				
				for(BlackListItem item : blackLists) {
					
					//#region olmayanları tabloya ekleme kısmı
						
						EventBlackList eventBlackList = new EventBlackList();					
										
						BlackList blackList = new BlackList();
						blackList.setId(item.getId());	
						
						event.setId(savedEvent.getId());
						eventBlackList.setEvent(event);		
						eventBlackList.setBlackList(blackList);	
						
						saveEventBlackList.add(eventBlackList);
					
					//#endregion olmayanları tabloya ekleme kısmı
				}
				eventBlackListRepository.saveAll(saveEventBlackList);
			}
			
			
			
			
	
			
			
			String tagIdList = eventItem.getTagId();
			
			if(tagIdList != null) {
				String[] tagList = tagIdList.split(",");
				for(String tagId : tagList) {

					Integer IntegerTagId = new Integer(Integer.parseInt(tagId));
					List<EventTag> tagIdQuery =  eventTagRepository.findAllByEventIdAndTagId(event.getId(), IntegerTagId);
					
					if(tagIdQuery.size() == 0) {
						
						EventTag eventTag = new EventTag();
						Tag tag = new Tag();				
						tag.setId(Integer.parseInt(tagId));
						
						eventTag.setTag(tag);
						eventTag.setEvent(event);
						eventTagRepository.save(eventTag);
					}
				}
			}
			
				
			Map<String,String> mediaPathMap = eventMediaFileOperations(eventItem.getMediaPath());	
			
			if(mediaPathMap != null) {
				for (Map.Entry<String, String> entry : mediaPathMap.entrySet()) {
				   
				    String mediaPath = entry.getValue();
				    
				    EventMedia eventMedia = new EventMedia();
					
					if(mediaPath.endsWith(".mp4") || mediaPath.endsWith(".mp4/")) {
						eventMedia.setIsVideo(true);
					}else {
						eventMedia.setIsVideo(false);
					}
					
					
					String pathItem = s3Service.move(entry.getKey(), mediaPath);
					
					eventMedia.setCoverImagePath(pathItem);
					eventMedia.setEvent(event);
					
					eventMedia.setPath(pathItem);
					
					eventMediaRepository.save(eventMedia);
					
					
					
				}
			}
			
			
			//medya silme
			String mediaIds = eventItem.getDeleteImageId();
			
			if(mediaIds != null) {
				String[] mediaIdList = mediaIds.split(",");

				for(String id : mediaIdList) {
					EventMediaItem mediaItem = eventMediaRepository.findAllById(Integer.parseInt(id));
					if(mediaItem != null) {
						
						int index = mediaItem.getPath().indexOf(SettingsUtil.getString(SettingsE.MEDIA_PATH));
						String path = mediaItem.getPath().substring(index, mediaItem.getPath().length()); 
						s3Service.delete(path);
						eventMediaRepository.deleteById(mediaItem.getId());
						
						File deleteFile = new File(SettingsUtil.getString(SettingsE.MEDIA_PATH) + mediaItem.getPath());						
						if(deleteFile.exists()) {	
							
							deleteFile.delete();												 
						}
					}
					
				}
			}
			
			//evet_tag silme
			String deleteTagIds = eventItem.getDeleteTagId();
			
			if(deleteTagIds != null) {
				String[] deleteTagIdList = deleteTagIds.split(",");

				for(String id : deleteTagIdList) {
					List<EventTag> eventTagItem= eventTagRepository.findAllByEventIdAndTagId(eventItem.getId(), Integer.parseInt(id));
					
					if(eventTagItem.size()>0) {
						
						eventTagRepository.deleteById(eventTagItem.get(0).getId());
												
					}					
				}
			}
				
			
			try {
				logMap.put("new", EventItem.newInstanceForLog(savedEvent));
				dbLogger.log(new Gson().toJson(logMap), logTypeE);
			} catch (Exception e) {
				log.error(e);
			}
			
			genericResponseItem.setRedirectUrl(EDIT_LINK + savedEvent.getId());
			
			if(!blackLists.isEmpty()) {
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.recorded.successfully.but.recorded.belonging.to.the.blacklist"));
			}
			
		} catch (Exception e) {
			
			log.debug(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
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
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.not.found"));
				return genericResponseItem;
			}
			
			Event event = eventRepository.findByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()));
			
			if (event == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.not.found"));
				return genericResponseItem;
			}
						
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			if (!userEventGroupPermissionIdList.contains(event.getEventGroup().getId())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.no.permission"));
				return genericResponseItem;
			}
			
			
			// TODO BUNLARIN NASIL YAPILACAGINA KARAR VERILECEK
//			List<EventMedia> eventMediaList = eventMediaRepository.findAllByEventId(event.getId());
//			for(EventMedia item : eventMediaList) {
//				eventMediaRepository.deleteById(item.getId());
//			}
//			
//			
//			List<EventTag> eventTagList =  eventTagRepository.findAllByEventId(id);
//			
//			for(EventTag item : eventTagList) {
//				eventTagRepository.deleteById(item.getId());
//			}
//			
//			
//			eventRepository.deleteById(id);
			
			
			
			event.setState(StateE.DELETED.getState());
			eventRepository.save(event);
						
			Map<String, Object> logMap = new TreeMap<>();
			logMap.put("deleted", EventItem.newInstanceForLog(event));
			
			try {
				dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_DELETE);
			} catch (Exception e) {
				log.error(e);
			}
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.delete.error"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	private GenericResponseItem validate(EventItem eventItem) { 
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (eventItem.getLatitude() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.latitude.not.null")); 
			return genericResponseItem;
		}
		
		if (eventItem.getLongitude() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.longitude.not.null"));
			return genericResponseItem;
		}
	
		return genericResponseItem;
	}
	
	private Map<String,String> eventMediaFileOperations(String mediaPaths) {
		
		if(mediaPaths == null) {
			return null;
		}
		
		String[] paths = mediaPaths.split(",");		
		List<String> mediaPath = Arrays.asList(paths); 
		
		Map<String,String> eventMediaPaths = new HashMap<>();
		for(String key : mediaPath) {					
		
			String filename = "";
			int index = key.lastIndexOf("%24_") + 3;
			if (index <= key.length()) {
				filename = key.substring(index);
			}
			
			String [] filenameSplit = filename.split("/");
			filename = filenameSplit[0];	
		
			try {
	             eventMediaPaths.put("$temp$" + filename, "file"+filename);
	            				           	            
	        }catch (Exception e){
	            log.error("Hata Kopyalama Başarısız");
	            log.error(e);
	        }		
	
		}
		return eventMediaPaths;
	}
}