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
import com.imst.event.map.admin.db.projections.UserGroupIdProjection;
import com.imst.event.map.admin.db.repositories.UserGroupIdRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiUserGroupIdItem;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/userGroupId")
public class UserGroupIdControllerApi {
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserGroupIdRepository userGroupIdRepository;
	
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_LIST')")
	@Operation(summary = "UserGroupId sayfalama. Örn:/api/userGroupId/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<UserGroupIdProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page< UserGroupIdProjection>  userGroupIdProjections;
		try {
			userGroupIdProjections = userGroupIdRepository.findAllProjectedBy(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return userGroupIdProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public UserGroupIdProjection getById(@PathVariable Integer id) {
		
		UserGroupIdProjection userGroupIdProjections = null;
		try {
			userGroupIdProjections = userGroupIdRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (userGroupIdProjections == null) {
			throw new ApiException("Not found.");
		}
		
		return userGroupIdProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			userGroupIdRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			UserGroupId deleted = new UserGroupId();
			deleted.setId(id);
			logMap.put("deleted", deleted);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.USER_GROUP_ID_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("UserGroupId id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiUserGroupIdItem apiUserGroupIdItem) {
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		UserGroupId userGroupId;
		if (apiUserGroupIdItem.getId() != null) {//update
			
			logTypeE = LogTypeE.USER_GROUP_ID_EDIT;
			
			userGroupId = userGroupIdRepository.findById(apiUserGroupIdItem.getId()).orElse(null);
			if (userGroupId == null) {
				throw new ApiException("UserGroupId not found.");
			}
			
			logMap.put("old", ApiUserGroupIdItem.newInstanceForLog(userGroupId));
			
		} else {
			
			logTypeE = LogTypeE.USER_GROUP_ID_ADD;
			
			userGroupId = new UserGroupId();
		}
		
		
		userGroupId.setGroupId(apiUserGroupIdItem.getGroupId());
		User user = new User();
		user.setId(apiUserGroupIdItem.getFk_userId());
		userGroupId.setUser(user);
		
		UserGroupId saved = userGroupIdRepository.save(userGroupId);
		
		logMap.put("new", ApiUserGroupIdItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/userGroupId/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
}
