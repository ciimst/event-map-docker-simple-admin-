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
import com.imst.event.map.admin.db.projections.UserLayerPermissionProjection;
import com.imst.event.map.admin.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiUserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/userLayerPermission")
public class UserLayerPermissionControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired UserLayerPermissionRepository userLayerPermissionRepository;
	
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_LIST')")
	@Operation(summary = "UserLayerPermission sayfalama. Örn:/api/userLayerPermission/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<UserLayerPermissionProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page< UserLayerPermissionProjection>  userLayerPermissionProjections;
		try {
			userLayerPermissionProjections = userLayerPermissionRepository.findAllProjectedBy(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return userLayerPermissionProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public UserLayerPermissionProjection getById(@PathVariable Integer id) {
		
		UserLayerPermissionProjection userLayerPermissionProjections = null;
		try {
			userLayerPermissionProjections = userLayerPermissionRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (userLayerPermissionProjections == null) {
			throw new ApiException("Not found.");
		}
		
		return userLayerPermissionProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			userLayerPermissionRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			UserLayerPermission deleted = new UserLayerPermission();
			deleted.setId(id);
			logMap.put("deleted", deleted);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.USER_LAYER_PERMISSION_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("UserLayerPermission id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiUserLayerPermissionItem apiUserLayerPermissionItem) {
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		UserLayerPermission userLayerPermission;
		if (apiUserLayerPermissionItem.getId() != null) {//update
			
			logTypeE = LogTypeE.USER_LAYER_PERMISSION_EDIT;
			
			userLayerPermission = userLayerPermissionRepository.findById(apiUserLayerPermissionItem.getId()).orElse(null);
			if (userLayerPermission == null) {
				throw new ApiException("UserLayerPermission not found.");
			}
			
			logMap.put("old", ApiUserLayerPermissionItem.newInstanceForLog(userLayerPermission));
			
		} else {
			
			logTypeE = LogTypeE.USER_LAYER_PERMISSION_ADD;
			
			userLayerPermission = new UserLayerPermission();
		}
		
						
		User user = new User();
		user.setId(apiUserLayerPermissionItem.getFk_userId());
		userLayerPermission.setUser(user);
		
		Layer layer = new Layer();
		layer.setId(apiUserLayerPermissionItem.getFk_layerId());
		userLayerPermission.setLayer(layer);
		
		UserLayerPermission saved = userLayerPermissionRepository.save(userLayerPermission);
		
		logMap.put("new", ApiUserLayerPermissionItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/userLayerPermission/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	

}
