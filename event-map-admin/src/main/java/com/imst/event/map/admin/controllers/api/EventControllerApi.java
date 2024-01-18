package com.imst.event.map.admin.controllers.api;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.EventProjection;
import com.imst.event.map.admin.db.repositories.EventBlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventRepository;
import com.imst.event.map.admin.db.services.TransactionalEventService;
import com.imst.event.map.admin.db.specifications.BlackListCheckedSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.utils.exceptions.BlackListApiException;
import com.imst.event.map.admin.utils.exceptions.PermissionApiException;
import com.imst.event.map.admin.vo.BlackListItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiEventGroupItem;
import com.imst.event.map.admin.vo.api.ApiEventItem;
import com.imst.event.map.admin.vo.api.ApiEventTypeItem;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventBlackList;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/events")
public class EventControllerApi {
	
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private TransactionalEventService transactionalEventService;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private MasterDao masterDao;
	@Autowired 
	private UserPermissionService userPermissionService;
	@Autowired
	private EventBlackListRepository eventBlackListRepository;
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "Sayfalama")
	@GetMapping(value = "/page")
	public Page<EventProjection> getEventPages(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		
		Page<EventProjection> allProjectedByStateIsTrue;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		try {
			
//			allProjectedByStateIsTrue = eventRepository.findAllProjectedByStateIsTrue(pageable);
			allProjectedByStateIsTrue = eventRepository.findAllProjectedByStateIdInAndEventGroupIdIn(pageable, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()), userEventGroupPermissionIdList);

			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return allProjectedByStateIsTrue;
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "Liste")
	@GetMapping(value = "/list/{layerId}")
	public List<EventProjection> getEventLists(@PathVariable Integer layerId) {
		
		List<EventProjection> allProjectedByStateIsTrue;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();

		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		if (!userLayerPermissionIdList.contains(layerId)) {
			throw new ApiException("Layer no permission");
		}
		
		try {
			
			List<Integer> eventGroupIdListByLayer = eventGroupRepository.findIdByLayerId(layerId);
			
			eventGroupIdListByLayer = eventGroupIdListByLayer.stream().filter(o -> userEventGroupPermissionIdList.contains(o)).collect(Collectors.toList());
						
			allProjectedByStateIsTrue = eventRepository.findAllProjectedByStateIdInAndEventGroupIdIn(Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()), eventGroupIdListByLayer);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return allProjectedByStateIsTrue;
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public EventProjection getEventById(@PathVariable Integer id) {
		
		EventProjection eventProjection = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();

		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		try {
			eventProjection = eventRepository.findProjectedByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()));
		} catch (Exception e) {
			log.error(e);
		}
			
		if (eventProjection == null) {
			throw new ApiException("Not found");
		}
		
		if (!userEventGroupPermissionIdList.contains(eventProjection.getEventGroup().getId())) {
			throw new ApiException("EventGroup no permission");
		}
	
		return eventProjection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdateEvent(@RequestBody ApiEventItem apiEventItem, @RequestParam(required = false) List<Integer> userEventGroupPermissionIdList) {
		
		Timestamp nowT = DateUtils.nowT();
		
		ApiEventGroupItem eventGroupItem = apiEventItem.getEventGroupItem();
		if (eventGroupItem == null) {
			throw new ApiException("EventGroupItem not found.");
		}
		
		ApiEventTypeItem eventTypeItem = apiEventItem.getEventTypeItem();
		if (eventTypeItem == null) {
			throw new ApiException("EventTypeItem not found.");
		}
		
		if (apiEventItem.getLatitude() == null) {
			throw new ApiException("Latitude not found.");
		}
		
		if (apiEventItem.getLongitude() == null) {
			throw new ApiException("Longitude not found.");
		}
		
		
		if (userEventGroupPermissionIdList == null) {
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
			
			userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
		}
		
		Event event = eventRepository.findByIdAndStateIdIn(apiEventItem.getId(), Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()));

		List<BlackListItem> blackLists = new ArrayList<>();
		EventGroup eventGroup;
		if (apiEventItem.getBlackListTag() != null) {
			
			Layer tempLayer = new Layer();
			tempLayer.setId(eventGroupItem.getLayerItem().getId());
			
			List<EventGroupProjection> allEventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(tempLayer);
			EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList, true);
			List<Integer> permissionEventGroupIdParentList = eventGroupTree.getPermissionEventGroupParent(Arrays.asList(eventGroupItem.getId()));
			
			BlackListItem blackListItem=new BlackListItem();
	    	blackListItem.setTag(apiEventItem.getBlackListTag() != null ? apiEventItem.getBlackListTag().trim() : null); 
	    	eventGroup = eventGroupRepository.findById(apiEventItem.getEventGroupItem().getId()).orElse(null);
	    	blackListItem.setLayerId(eventGroup.getLayer().getId());  
			blackListItem.setEventGroupId(apiEventItem.getEventGroupItem().getId()); 
			blackListItem.setEventTypeId(apiEventItem.getEventTypeItem().getId()); 
			
			BlackListCheckedSpecification blackListCheckedSpecification = new BlackListCheckedSpecification(blackListItem, permissionEventGroupIdParentList);
			blackLists = masterDao.findAll(blackListCheckedSpecification, Sort.by(Direction.DESC, "id"));
	
		}
	
		Map<String, Object> eventsForLog = new TreeMap<>();
		
		LogTypeE logTypeE;
		
		boolean isAdd = false;
		if (event == null) {//add
			
			if (!userEventGroupPermissionIdList.stream().anyMatch(o -> o.equals(apiEventItem.getEventGroupItem().getId()))) {
				throw new PermissionApiException("EventGroup no permission");  
			}
			
			logTypeE = LogTypeE.EVENT_ADD;
			event = new Event();
			event.setCreateDate(nowT);
			event.setCreateUser(ApplicationContextUtils.getUser().getUsername());
			
			isAdd = true;
			
			
			
			
		} else {//update
			
			if (!userEventGroupPermissionIdList.stream().anyMatch(o -> o.equals(apiEventItem.getEventGroupItem().getId()))) {
				throw new PermissionApiException("EventGroup no permission");  
			}
			
			if (!userEventGroupPermissionIdList.contains(event.getEventGroup().getId())) {
				throw new PermissionApiException("EventGroup no permission");  
			}
			
			if (!event.getState().getId().equals(StateE.TRUE.getValue()) && !event.getState().getId().equals(StateE.FALSE.getValue() ) && !event.getState().getId().equals(StateE.BLACKLISTED.getValue())) {
				throw new ApiException("Event not found.");
			}
			
			logTypeE = LogTypeE.EVENT_EDIT;
			eventsForLog.put("old", ApiEventItem.newInstanceForLog(event));
			
			
			if(!blackLists.isEmpty()) {
				
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
	
		}
		
		event.setCity(apiEventItem.getCity());
		event.setCountry(apiEventItem.getCountry());
		event.setGroupId(apiEventItem.getGroupId());
		event.setUserId(apiEventItem.getUserId());
		event.setTitle(apiEventItem.getTitle());
		event.setSpot(apiEventItem.getSpot());
		event.setDescription(apiEventItem.getDescription());
		event.setLatitude(apiEventItem.getLatitude());
		event.setLongitude(apiEventItem.getLongitude());
		if(apiEventItem.getEventDate() != null) {
			event.setEventDate(new Timestamp(apiEventItem.getEventDate()));
		}
		else {
			event.setEventDate(nowT);
		}
		event.setUpdateDate(nowT);
		event.setState(!blackLists.isEmpty() ? StateE.BLACKLISTED.getState() : StateE.getBooleanState(apiEventItem.isState()));
		
		
		event.setReservedId(apiEventItem.getReservedId());
		event.setReservedKey(apiEventItem.getReservedKey());
		event.setReservedLink(apiEventItem.getReservedLink());
		event.setReservedType(apiEventItem.getReservedType());
		
		event.setReserved1(apiEventItem.getReserved1());
		event.setReserved2(apiEventItem.getReserved2());
		event.setReserved3(apiEventItem.getReserved3());
		event.setReserved4(apiEventItem.getReserved4());
		event.setReserved5(apiEventItem.getReserved5());
		
		event.setBlackListTag(apiEventItem.getBlackListTag());
		
		ApiEventItem apiEventItemResponse;
		try {
			
			apiEventItemResponse = transactionalEventService.saveEvent(event, apiEventItem, nowT);
			
			if(isAdd) {
				List<EventBlackList> saveEventBlackList = new ArrayList<>();
				for(BlackListItem item : blackLists) {
					
					//#region olmayanları tabloya ekleme kısmı
						EventBlackList eventBlackList = new EventBlackList();
						
//						Event newEvent  = new Event();
//						newEvent.setId(event.getId());
										
						BlackList blackList = new BlackList();
						blackList.setId(item.getId());	
						
						event.setId(apiEventItemResponse.getId());
						eventBlackList.setEvent(event);		
						eventBlackList.setBlackList(blackList);	
						
						saveEventBlackList.add(eventBlackList);
					
					//#endregion olmayanları tabloya ekleme kısmı
				}
				eventBlackListRepository.saveAll(saveEventBlackList);
			}
		

			
		} catch (ApiException e) {
			log.error(e);
			throw e;
		}
		catch (Exception e) {
			log.error(e);
			throw new ApiException("Record cannot be saved. Please try again. If the error persists, please contact system administrator.");
		}
		
		try {
			
			eventsForLog.put("new", apiEventItemResponse);
			dbLogger.log(new Gson().toJson(eventsForLog), logTypeE);
			
		} catch (Exception e) {
			log.error(e);
		}
		
		String location = "/api/events/" + apiEventItemResponse.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", apiEventItemResponse.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@Operation(summary = "Yeni liste kaydet / güncelle")
	@PostMapping(value = {"/all"})
	public ResponseEntity<?> saveOrUpdateEventAll(@RequestBody List<ApiEventItem> apiEventItemList)   {
		
		List<Object> responseList = new ArrayList<>();
		BlackListApiException exceptionToReturn = null;
		PermissionApiException exceptionPermission = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
	
		
		int blackListCount = 0;
		int permissionExceptionCount = 0;
		for (ApiEventItem apiEventItem : apiEventItemList) {
			
			try {
				
				ResponseEntity<?> saveOrUpdateEvent = saveOrUpdateEvent(apiEventItem, userEventGroupPermissionIdList);
				responseList.add(saveOrUpdateEvent.getBody());	
			} catch (BlackListApiException e) {

				blackListCount++;
				exceptionToReturn = new BlackListApiException(String.format("Blacklist content found and could not be added. [count : %s]", blackListCount));
			} catch (PermissionApiException ex) {

				permissionExceptionCount++;
				exceptionPermission = new PermissionApiException(String.format("Event with no permission found and could not be added. [count : %s]", permissionExceptionCount));
			}
			
		}
		
		if(exceptionToReturn != null || exceptionPermission != null) {
			//throw exceptionToReturn;
			
			Map<String, Object> responseBody = new LinkedHashMap<>();
			if (exceptionToReturn != null) {
				responseBody.put("blacklist_count", blackListCount);
				responseBody.put("exception_message", exceptionToReturn.getMessage());
			}
			if (exceptionPermission != null) {
				responseBody.put("permission_deny_count", permissionExceptionCount);
				responseBody.put("permission_deny_message", exceptionPermission.getMessage());
			}
			responseBody.put("successful_save", responseList);
			
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseBody);
		}
		
		return ResponseEntity.ok().body(responseList);
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		Event eventToBeDeleted = eventRepository.findByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.BLACKLISTED.getValue()));
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		if (!userEventGroupPermissionIdList.stream().anyMatch(o -> o.equals(eventToBeDeleted.getEventGroup().getId()))) {
			throw new ApiException("EventGroup no permission.");
		}
		
		
		try {
						
			eventToBeDeleted.setState(StateE.DELETED.getState());
			eventRepository.save(eventToBeDeleted);
			
			Map<String, Object> eventsForLog = new TreeMap<>();
			Event event = new Event();
			event.setId(id);
			eventsForLog.put("delete", event);
			
			dbLogger.log(new Gson().toJson(eventsForLog), LogTypeE.EVENT_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("Event id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	

}
