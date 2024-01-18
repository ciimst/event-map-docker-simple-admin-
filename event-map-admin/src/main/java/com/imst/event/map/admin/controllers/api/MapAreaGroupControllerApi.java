package com.imst.event.map.admin.controllers.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.projections.MapAreaGroupProjection;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiMapAreaGroupItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.MapAreaGroup;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/mapAreaGroup")
public class MapAreaGroupControllerApi {

	@Autowired
	private MapAreaGroupRepository mapAreaGroupRepository;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/mapArea/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<MapAreaGroupProjection> getPage(@PageableDefault Pageable pageable) {
		try {
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			return mapAreaGroupRepository.findAllProjectedByLayerIdIn(pageable, userLayerFullPermissionIdList);//TODO SIKINTILI
//			return mapAreaGroupRepository.findAllProjectedBy(pageable);	
		}
		catch(Exception ex) {
			log.error(ex);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
	}
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_LIST')")
	@GetMapping(value = "/{id}")
	public MapAreaGroupProjection getById(@PathVariable Integer id) {
		try {
			MapAreaGroupProjection mapAreaGroupProjection;
			mapAreaGroupProjection = mapAreaGroupRepository.findProjectedById(id);
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
						
			if(mapAreaGroupProjection == null) {
				throw new ApiException("Not found.");
			}
			
			if (!userLayerFullPermissionIdList.contains(mapAreaGroupProjection.getLayerId())) {
				throw new ApiException("No layer permission");
			}
			
			return mapAreaGroupProjection;
			
		} catch (Exception ex) {			
			log.error(ex);
			throw ex;
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_MANAGE')")
	@Operation(summary = "")
	@PostMapping(value = {""})	
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiMapAreaGroupItem apiMapAreaGroupItem) {
		
		Map<String, Object> mapAreaGroupForLog = new TreeMap<>();
		LogTypeE logTypeE;
		MapAreaGroup mapAreaGroup = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (apiMapAreaGroupItem.getId() != null) {
			
			logTypeE = LogTypeE.MAP_AREA_GROUP_EDIT;
			
			mapAreaGroup = mapAreaGroupRepository.findById(apiMapAreaGroupItem.getId()).orElse(null);
			if (mapAreaGroup == null) {
				throw new ApiException("MapAreaGroup not found.");
			}
			
			// Editlenecek areagroup'un layer id'sine ve Gonderilen itemin layer id'sine bakar
			if (!userLayerFullPermissionIdList.contains(mapAreaGroup.getLayer().getId())) { 
				throw new ApiException("Layer no permission.");
			}
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(apiMapAreaGroupItem.getLayerId()))) { 
				throw new ApiException("Layer no permission.");
			}
								
			mapAreaGroupForLog.put("old", mapAreaGroup);
			
		} else {						
			logTypeE = LogTypeE.MAP_AREA_GROUP_ADD;
			mapAreaGroup = new MapAreaGroup();		
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(apiMapAreaGroupItem.getLayerId()))) { 
				throw new ApiException("Layer no permission.");
			}
		}
				
		mapAreaGroup.setName(apiMapAreaGroupItem.getName());
		mapAreaGroup.setColor(apiMapAreaGroupItem.getColor());				
		
		Layer layer = new Layer();
		layer.setId(apiMapAreaGroupItem.getLayerId());
		mapAreaGroup.setLayer(layer);
		
		MapAreaGroup saved = mapAreaGroupRepository.save(mapAreaGroup);
		
		mapAreaGroupForLog.put("new", saved);
		
		dbLogger.log(new Gson().toJson(mapAreaGroupForLog), logTypeE);
		
		String location = "/api/mapAreaGroup/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_MANAGE')")
	@Operation(summary = "")
	@PatchMapping(value = {"/{id}"})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiMapAreaGroupItem apiMapAreaGroupItem, @PathVariable Integer id) {
		
		MapAreaGroup mapAreaGroup = mapAreaGroupRepository.findById(apiMapAreaGroupItem.getId()).orElse(null);
		if (mapAreaGroup == null) {
			throw new ApiException("MapAreaGroup not found.");
		}
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
				
		if (!userLayerFullPermissionIdList.contains(mapAreaGroup.getLayer().getId())) {
			throw new ApiException("No layer permission");
		}
	
				
		Map<String, Object> mapAreaGroupForLog = new TreeMap<>();
		mapAreaGroupForLog.put("old", mapAreaGroup);
				
		if (!StringUtils.isBlank(apiMapAreaGroupItem.getName())) {
			
			mapAreaGroup.setName(apiMapAreaGroupItem.getName());
		}
		
		if (!StringUtils.isBlank(apiMapAreaGroupItem.getColor())) {
			
			mapAreaGroup.setColor(apiMapAreaGroupItem.getColor());
		}
		
		if (apiMapAreaGroupItem.getLayerId() != null) {
			
			Layer layer = new Layer();
			layer.setId(apiMapAreaGroupItem.getLayerId());
			mapAreaGroup.setLayer(layer);
		}
		
		if (apiMapAreaGroupItem.getLayerId() != null) {
			if (!userLayerFullPermissionIdList.contains(apiMapAreaGroupItem.getLayerId())) {
				throw new ApiException("No layer permission");
			}
		}
			
		MapAreaGroup saved = mapAreaGroupRepository.save(mapAreaGroup);
		
		mapAreaGroupForLog.put("new", saved);
		
		dbLogger.log(new Gson().toJson(mapAreaGroupForLog), LogTypeE.MAP_AREA_GROUP_EDIT);
		
		String location = "/api/geo/" + id;
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", id);
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		MapAreaGroup mapAreaGroupToBeDeleted = mapAreaGroupRepository.findById(id).orElse(null);
		
		if (mapAreaGroupToBeDeleted != null) {
			if (!userLayerFullPermissionIdList.contains(mapAreaGroupToBeDeleted.getLayer().getId())) {
				throw new ApiException("No layer permission");
			}
		}
		
		try {
			
			mapAreaGroupRepository.deleteById(id);
			
			Map<String, Object> mapAreaGroupForLog = new TreeMap<>();
			MapAreaGroup mapAreaGroup = new MapAreaGroup();
			mapAreaGroup.setId(id);
			mapAreaGroupForLog.put("deleted", mapAreaGroup);
			
			dbLogger.log(new Gson().toJson(mapAreaGroupForLog), LogTypeE.MAP_AREA_GROUP_DELETE);
			
			return ResponseEntity.ok().build();
			
		} catch (Exception ex) {			
			ApiException apiException = new ApiException("MapAreaGroup id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
	}
}
