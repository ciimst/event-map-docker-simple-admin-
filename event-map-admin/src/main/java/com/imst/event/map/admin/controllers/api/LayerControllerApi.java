package com.imst.event.map.admin.controllers.api;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

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
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiLayerItem;
import com.imst.event.map.hibernate.entity.Layer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/layers")
public class LayerControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private UserPermissionService userPermissionService;
	
	
	@PreAuthorize("hasRole('ROLE_LAYER_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/layers/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<LayerProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<LayerProjection> layers;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		try {
			
//			layers = layerRepository.findAllProjectedByStateIsTrue(pageable);
			layers = layerRepository.findAllProjectedByStateIsTrueAndIdIn(pageable, userLayerFullPermissionIdList); //TODO Sıkıntılı
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return layers;
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public LayerProjection getById(@PathVariable Integer id) {
		
		LayerProjection layerProjection = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		try {
			layerProjection = layerRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (layerProjection == null) {
			throw new ApiException("Not found.");
		}
		
		if (!userLayerFullPermissionIdList.contains(layerProjection.getId())) {
			throw new ApiException("No layer permission.");
		}
		
		return layerProjection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (!userLayerFullPermissionIdList.contains(id)) {
			throw new ApiException("No layer permission.");
		}
		
		try {
					
			layerRepository.deleteById(id);
			
			Map<String, Object> layersForLog = new TreeMap<>();
			Layer layer = new Layer();
			layer.setId(id);
			layersForLog.put("deleted", layer);
			
			dbLogger.log(new Gson().toJson(layersForLog), LogTypeE.LAYER_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("Layer id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiLayerItem apiLayerItem) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> layersForLog = new TreeMap<>();
		LogTypeE logTypeE;
		Layer layer;
		if (apiLayerItem.getId() != null) {//update
			
			logTypeE = LogTypeE.LAYER_EDIT;
			
			layer = layerRepository.findById(apiLayerItem.getId()).orElse(null);
			if (layer == null) {
				throw new ApiException("Layer not found.");
			}
			
			if (!userLayerFullPermissionIdList.contains(layer.getId())) {
				throw new ApiException("No layer permission.");
			}
			
			layersForLog.put("old", ApiLayerItem.newInstanceForLog(layer));
			
		} else {

			logTypeE = LogTypeE.LAYER_ADD;
			
			layer = new Layer();
			layer.setCreateDate(nowT);
			layer.setGuid(UUID.randomUUID().toString());
		}
		
		
		if (StringUtils.isBlank(apiLayerItem.getName())) {
			
			throw new ApiException("Name is missing.");
		}
		
		
		boolean state = apiLayerItem.getState() == null ? true : apiLayerItem.getState();
		
		layer.setName(apiLayerItem.getName());
		layer.setState(state);
		layer.setUpdateDate(nowT);
		layer.setIsTemp(apiLayerItem.getIsTemp() == null ? false : apiLayerItem.getIsTemp());
		
		Layer saved = layerRepository.save(layer);
		
		layersForLog.put("new", ApiLayerItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(layersForLog), logTypeE);
		
		String location = "/api/layers/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		responseBody.put("url", "/region/" + saved.getGuid());
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Kısmi güncelleme")
	@PatchMapping(value = {"/{id}"})
	public ResponseEntity<?> update(@RequestBody ApiLayerItem apiLayerItem, @PathVariable Integer id) {
		
		
		Layer layer = layerRepository.findById(apiLayerItem.getId()).orElse(null);
		if (layer == null) {
			throw new ApiException("Layer not found.");
		}
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (!userLayerFullPermissionIdList.contains(layer.getId())) {
			throw new ApiException("No layer permission.");
		}
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> layersForLog = new TreeMap<>();
		layersForLog.put("old", ApiLayerItem.newInstanceForLog(layer));
		
		
		if (!StringUtils.isBlank(apiLayerItem.getName())) {
			
			layer.setName(apiLayerItem.getName());
		}
		
		if (apiLayerItem.getState() != null) {
			
			layer.setState(apiLayerItem.getState());
		}
		
		
		layer.setUpdateDate(nowT);
		Layer saved = layerRepository.save(layer);
		
		layersForLog.put("new", ApiLayerItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(layersForLog), LogTypeE.LAYER_EDIT);
		
		String location = "/api/layers/" + id;
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", id);
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
}
