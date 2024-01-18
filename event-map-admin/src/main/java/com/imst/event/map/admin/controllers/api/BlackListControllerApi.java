package com.imst.event.map.admin.controllers.api;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.ActionStateE;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.projections.BlackListProjection;
import com.imst.event.map.admin.db.repositories.BlackListRepository;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.BlackListItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiBlackListItem;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/blackList")
public class BlackListControllerApi {
	
	@Autowired
	private BlackListRepository blackListRepository;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_LIST')")
	@Operation(summary = "Sayfalama")
	@GetMapping(value = "/page")
	public Page<BlackListProjection> getBlackListPages(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		
		Page<BlackListProjection> allProjectedByStateIsTrue;
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
		
		try {
			allProjectedByStateIsTrue = blackListRepository.findAllProjectedByStateIdInAndEventGroupIdInOrStateIdInAndEventGroupLayerIdIn(pageable, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()), userEventGroupPermissionIdList, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()), userLayerFullPermissionIdList);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return allProjectedByStateIsTrue;
	}
		
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public BlackListProjection getBlackListById(@PathVariable Integer id) {
		
		BlackListProjection blackListProjection = blackListRepository.findProjectedByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
		
		if (blackListProjection == null) {
			throw new ApiException("Not found");
		}
		
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
		
		
		if (blackListProjection.getEventGroupId() != null) {
			
			if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackListProjection.getEventGroupId()))) {
				throw new ApiException("eventGroup no permission");
			}
			
		}	
		else {
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackListProjection.getLayerId()))) {
				throw new ApiException("layer no permission");
			}
			
		}	
		
		
		return blackListProjection;
	}
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdateEvent(@RequestBody ApiBlackListItem apiBlackListItem) {
		
		
		if (StringUtils.isBlank(apiBlackListItem.getName())) {
			throw new ApiException("blacklist name not null.");
		}
		
		if (StringUtils.isBlank(apiBlackListItem.getTag())) {
			throw new ApiException("blacklist tag not null.");
		}
		
		if (apiBlackListItem.getLayerId() == null) {
			throw new ApiException("blacklist layer id not null.");
		}
		
		
		Timestamp nowT = DateUtils.nowT();
		
		Map<String, Object> blackListsForLog = new TreeMap<>();
		
		LogTypeE logTypeE;
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
	
		BlackList blackList;
		
		if (Optional.ofNullable(apiBlackListItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.BLACK_LIST_EDIT;   
			
			blackList = blackListRepository.findByIdAndStateIdIn(apiBlackListItem.getId(), Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
			
			if (blackList == null) {
				throw new ApiException("blacklist not found.");
			}
			
			if(blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue())) {
				throw new ApiException("You cannot make any changes to this blacklist for a while.");
			}
			
			if (blackList.getEventGroup() != null) {
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getEventGroup().getId()))) {
					throw new ApiException("event group no permission.");
				}
				
			}	
			else {
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getLayer().getId()))) {
					throw new ApiException("layer no permission");
				}
				
			}
			
			if (apiBlackListItem.getEventGroupId() != null) {
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(apiBlackListItem.getEventGroupId()))) {
					throw new ApiException("event group no permission.");
				}
				
			}	
			else {
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(apiBlackListItem.getLayerId()))) {
					throw new ApiException("layer no permission");
				}
				
			}
			
			
			Boolean stateStatus = apiBlackListItem.getState() != null && apiBlackListItem.getState() == true ? true : false;
			if(!stateStatus.equals(StateE.getIntegerStateToBoolean(blackList.getState().getId()))) {
				blackList.setActionState(ActionStateE.PENDING.getActionState());
				blackList.setActionDate(nowT);
			}
				
			
			
			blackListsForLog.put("old", BlackListItem.newInstanceForLog(blackList));
			
		} else {//add
			
			logTypeE = LogTypeE.BLACK_LIST_ADD;
			
			
			if (apiBlackListItem.getEventGroupId() != null) {
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(apiBlackListItem.getEventGroupId()))) {
					throw new ApiException("event group no permission.");
				}
				
			}	
			else {
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(apiBlackListItem.getLayerId()))) {
					throw new ApiException("layer no permission");
				}
				
			}
			
			
			blackList = new BlackList();
			blackList.setCreateDate(nowT);
			blackList.setTag(apiBlackListItem.getTag().trim());
			
			Layer layer = new Layer();
			layer.setId(apiBlackListItem.getLayerId());
			blackList.setLayer(layer);
			
			EventGroup eventGroup = new EventGroup();
			if (apiBlackListItem.getEventGroupId() != null) {
				eventGroup.setId(apiBlackListItem.getEventGroupId());
				blackList.setEventGroup(eventGroup);
			}
			else {
				blackList.setEventGroup(null);
			}
			
			EventType eventType = new EventType();
			if (apiBlackListItem.getEventTypeId() != null) {
				eventType.setId(apiBlackListItem.getEventTypeId());
				blackList.setEventType(eventType);
			}
			
			blackList.setActionState(ActionStateE.PENDING.getActionState());
			blackList.setActionDate(nowT);
			
		}
		
		blackList.setName(apiBlackListItem.getName());
	
		
		blackList.setCreateUser(ApplicationContextUtils.getUser().getUsername());
		blackList.setState(StateE.getBooleanState(apiBlackListItem.getState()));
		
		
		
		
		blackList.setUpdateDate(nowT);
		
		BlackList apiBlackListItemResponse = blackListRepository.save(blackList);
		
		blackListsForLog.put("new", apiBlackListItemResponse);
		dbLogger.log(new Gson().toJson(blackListsForLog), logTypeE);
		
		String location = "/api/blackList/" + apiBlackListItemResponse.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", apiBlackListItemResponse.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	@PreAuthorize("hasRole('ROLE_BLACK_LIST_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			if (Optional.ofNullable(id).orElse(0) < 1) {
				
				throw new ApiException("blackList not found");
			}
			
			BlackList blackList = blackListRepository.findByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
			if (blackList == null) {
				
				throw new ApiException("blackList not found");
				
			}
			
			if(blackList.getActionState().getId().equals(ActionStateE.PENDING.getValue()) || blackList.getActionState().getId().equals(ActionStateE.RUNNING.getValue())) {
				throw new ApiException("You cannot make any changes to this blacklist for a while.");
			}
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();	
			
			if (blackList.getEventGroup() != null) {
				
				
				if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getEventGroup().getId()))) {
					throw new ApiException("eventGroup no permission");
				}
				
			}	
			else {
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(blackList.getLayer().getId()))) {
					throw new ApiException("layer no permission");
				}
				
			}	
			
//			boolean deletedStatus = blackListService.updatingEventsAfterBlackListDeleted(blackList);
//			
//			if(!deletedStatus){
//				
//				throw new ApiException("blackList deleted error");
//			}
			blackList.setState(StateE.DELETED.getState());
			blackList.setActionState(ActionStateE.PENDING.getActionState());
			blackListRepository.save(blackList);
			
			Map<String, Object> blackListsForLog = new TreeMap<>();
			BlackList newBlackList = new BlackList();
			newBlackList.setId(id);
			blackListsForLog.put("delete", newBlackList);
			
			dbLogger.log(new Gson().toJson(blackListsForLog), LogTypeE.BLACK_LIST_DELETE);
			
		} catch (ApiException e) {
			
			log.error(e);
			throw e;
		}
		
		catch (Exception e) {
			
			ApiException apiException = new ApiException("BlackList id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
}
