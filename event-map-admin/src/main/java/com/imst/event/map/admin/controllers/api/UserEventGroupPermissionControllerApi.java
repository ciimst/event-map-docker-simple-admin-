package com.imst.event.map.admin.controllers.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.projections.UserEventGroupPermissionProjection;
import com.imst.event.map.admin.db.repositories.UserEventGroupPermissionRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiUserEventGroupPermissionItem;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/userEventGroupPermission")
public class UserEventGroupPermissionControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired UserEventGroupPermissionRepository userEventGroupPermissionRepository;
	
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_LIST')")
	@Operation(summary = "UserEventGroupPermission sayfalama. Örn:/api/userEventGroupPermission/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<UserEventGroupPermissionProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<UserEventGroupPermissionProjection>  userEventGroupPermissionProjections;
		try {
			userEventGroupPermissionProjections = userEventGroupPermissionRepository.findAllProjectedBy(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return userEventGroupPermissionProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public UserEventGroupPermissionProjection getById(@PathVariable Integer id) {
		
		UserEventGroupPermissionProjection userEventGroupPermissionProjections = null;
		try {
			userEventGroupPermissionProjections = userEventGroupPermissionRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (userEventGroupPermissionProjections == null) {
			throw new ApiException("Not found.");
		}
		
		return userEventGroupPermissionProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			userEventGroupPermissionRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			UserEventGroupPermission deleted = new UserEventGroupPermission();
			deleted.setId(id);
			logMap.put("deleted", deleted);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.USER_EVENT_GROUP_PERMISSION_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("UserEventGroupPermission id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_USER_EVENT_GROUP_PERMISSION_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiUserEventGroupPermissionItem apiUserEventGroupPermissionItem) {
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		UserEventGroupPermission userEventGroupPermission;
		if (apiUserEventGroupPermissionItem.getId() != null) {//update
			
			logTypeE = LogTypeE.USER_EVENT_GROUP_PERMISSION_EDIT;
			
			userEventGroupPermission = userEventGroupPermissionRepository.findById(apiUserEventGroupPermissionItem.getId()).orElse(null);
			if (userEventGroupPermission == null) {
				throw new ApiException("UserEventGroupPermission not found.");
			}
			
			logMap.put("old", ApiUserEventGroupPermissionItem.newInstanceForLog(userEventGroupPermission));
			
		} else {
			
			logTypeE = LogTypeE.USER_EVENT_GROUP_PERMISSION_ADD;
			
			userEventGroupPermission = new UserEventGroupPermission();
		}
		
						
		User user = new User();
		user.setId(apiUserEventGroupPermissionItem.getFk_userId());
		userEventGroupPermission.setUser(user);
		
		EventGroup eventGroup = new EventGroup();
		eventGroup.setId(apiUserEventGroupPermissionItem.getFk_eventGroupId());
		userEventGroupPermission.setEventGroup(eventGroup);
		
		UserEventGroupPermission saved = userEventGroupPermissionRepository.save(userEventGroupPermission);
		
		logMap.put("new", ApiUserEventGroupPermissionItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/userEventGroupPermission/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	

}