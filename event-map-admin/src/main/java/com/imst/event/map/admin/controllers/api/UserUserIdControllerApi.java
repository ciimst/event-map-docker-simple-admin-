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
import com.imst.event.map.admin.db.projections.UserUserIdProjection;
import com.imst.event.map.admin.db.repositories.UserUserIdRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiUserUserIdItem;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserUserId;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/userUserId")
public class UserUserIdControllerApi {
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserUserIdRepository userUserIdRepository;

	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_LIST')")
	@Operation(summary = "UserUserId sayfalama. Örn:/api/userGroupId/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<UserUserIdProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page< UserUserIdProjection>  userUserIdProjections;
		try {
			userUserIdProjections = userUserIdRepository.findAllProjectedBy(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return userUserIdProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public UserUserIdProjection getById(@PathVariable Integer id) {
		
		UserUserIdProjection userUserIdProjections = null;
		try {
			userUserIdProjections = userUserIdRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (userUserIdProjections == null) {
			throw new ApiException("Not found.");
		}
		
		return userUserIdProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			userUserIdRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			UserUserId deleted = new UserUserId();
			deleted.setId(id);
			logMap.put("deleted", deleted);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.USER_USER_ID_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("UserUserId id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	

	@PreAuthorize("hasRole('ROLE_USER_USER_ID_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiUserUserIdItem apiUserUserIdItem) {
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		UserUserId userUserId;
		if (apiUserUserIdItem.getId() != null) {//update
			
			logTypeE = LogTypeE.USER_USER_ID_EDIT;
			
			userUserId = userUserIdRepository.findById(apiUserUserIdItem.getId()).orElse(null);
			if (userUserId == null) {
				throw new ApiException("UserUserId not found.");
			}
			
			logMap.put("old", ApiUserUserIdItem.newInstanceForLog(userUserId));
			
		} else {
			
			logTypeE = LogTypeE.USER_USER_ID_ADD;
			
			userUserId = new UserUserId();
		}
		
		
		userUserId.setUserId(apiUserUserIdItem.getUserId());
		User user = new User();
		user.setId(apiUserUserIdItem.getFk_userId());
		userUserId.setUser(user);
		
		UserUserId saved = userUserIdRepository.save(userUserId);
		
		logMap.put("new", ApiUserUserIdItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/userUserId/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
}
