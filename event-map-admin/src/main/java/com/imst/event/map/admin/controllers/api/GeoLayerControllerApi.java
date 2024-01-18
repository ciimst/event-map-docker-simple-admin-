package com.imst.event.map.admin.controllers.api;

import java.sql.Timestamp;
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
import com.imst.event.map.admin.db.projections.GeoLayerProjection;
import com.imst.event.map.admin.db.repositories.GeoLayerRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiGeoLayerItem;
import com.imst.event.map.hibernate.entity.GeoLayer;
import com.imst.event.map.hibernate.entity.Layer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/geo")
public class GeoLayerControllerApi {

	@Autowired
	private GeoLayerRepository geoLayerRepository;
	@Autowired
	private DBLogger dbLogger;
	@Autowired 
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/eventTypes/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<GeoLayerProjection> getPage(@PageableDefault Pageable pageable) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		try {
			 return geoLayerRepository.findAllProjectedByLayerIdIn(pageable, userLayerFullPermissionIdList); // TODO SIKINTILI
//			 return geoLayerRepository.findAllProjectedBy(pageable);
		}
		catch(Exception ex) {
			log.error(ex);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
	}
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public GeoLayerProjection getById(@PathVariable Integer id) {
		try {
			GeoLayerProjection geoLayerProjection;
			geoLayerProjection = geoLayerRepository.findProjectedById(id);
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
						
			if(geoLayerProjection == null) {
				throw new ApiException("Not found.");
			}
			
			if (!userLayerFullPermissionIdList.contains(geoLayerProjection.getLayerId())) {
				throw new ApiException("No layer permission");
			}
			
			return geoLayerProjection;
			
		} catch (Exception ex) {			
			log.error(ex);
			throw ex;
		}
	}
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_MANAGE')")
	@Operation(summary = "")
	@PostMapping(value = {""})	
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiGeoLayerItem apiGeoLayerItem) {
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> geoForLog = new TreeMap<>();
		LogTypeE logTypeE;
		GeoLayer geoLayer = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (apiGeoLayerItem.getId() != null) {
						
			logTypeE = LogTypeE.GEO_LAYER_EDIT;
			
			geoLayer = geoLayerRepository.findById(apiGeoLayerItem.getId()).orElse(null);
			if (geoLayer == null) {
				throw new ApiException("GeoLayer not found.");
			}
			
			if (!userLayerFullPermissionIdList.contains(geoLayer.getLayer().getId())) {
				throw new ApiException("No layer permission");
			}
			
			if (!userLayerFullPermissionIdList.contains(apiGeoLayerItem.getLayerId())) {
				throw new ApiException("No layer permission");
			}
								
			geoForLog.put("old", geoLayer);
			
		} else {	
			
			if (!userLayerFullPermissionIdList.contains(apiGeoLayerItem.getLayerId())) {
				throw new ApiException("No layer permission");
			}
			
			logTypeE = LogTypeE.GEO_LAYER_ADD;
			geoLayer = new GeoLayer();	
			geoLayer.setState(true);
			geoLayer.setCreateDate(nowT);
		}
				
		geoLayer.setName(apiGeoLayerItem.getName());
		geoLayer.setData(apiGeoLayerItem.getData());		
		geoLayer.setState(apiGeoLayerItem.getState() == null ? true : apiGeoLayerItem.getState());
		
		Layer layer = new Layer();
		layer.setId(apiGeoLayerItem.getLayerId());
		geoLayer.setLayer(layer);
		
		GeoLayer saved = geoLayerRepository.save(geoLayer);
		
		geoForLog.put("new", saved);
		
		dbLogger.log(new Gson().toJson(geoForLog), logTypeE);
		
		String location = "/api/geo/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_MANAGE')")
	@Operation(summary = "")
	@PatchMapping(value = {"/{id}"})
	public ResponseEntity<?> update(@RequestBody ApiGeoLayerItem apiGeoLayerItem, @PathVariable Integer id) {
		
		GeoLayer geoLayer = geoLayerRepository.findById(apiGeoLayerItem.getId()).orElse(null);
		if (geoLayer == null) {
			throw new ApiException("Geo Layer not found.");
		}
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (!userLayerFullPermissionIdList.contains(geoLayer.getLayer().getId())) {
			throw new ApiException("No layer permission");
		}
		
		if (apiGeoLayerItem.getLayerId() != null) {
			if (!userLayerFullPermissionIdList.contains(apiGeoLayerItem.getLayerId())) {
				throw new ApiException("No layer permission");
			}
		}
				
		Map<String, Object> geoLayerForLog = new TreeMap<>();
		geoLayerForLog.put("old", geoLayer);
				
		if (!StringUtils.isBlank(apiGeoLayerItem.getName())) {
			
			geoLayer.setName(apiGeoLayerItem.getName());
		}
		
		if (!StringUtils.isBlank(apiGeoLayerItem.getData())) {
			
			geoLayer.setData(apiGeoLayerItem.getData());
		}
		
		if (apiGeoLayerItem.getLayerId() != null) {
			
			Layer layer = new Layer();
			layer.setId(apiGeoLayerItem.getLayerId());
			geoLayer.setLayer(layer);
		}
		
		if (apiGeoLayerItem.getState() != null) {
			
			geoLayer.setState(apiGeoLayerItem.getState());
		}
					
		GeoLayer saved = geoLayerRepository.save(geoLayer);
		
		geoLayerForLog.put("new", saved);
		
		dbLogger.log(new Gson().toJson(geoLayerForLog), LogTypeE.GEO_LAYER_EDIT);
		
		String location = "/api/geo/" + id;
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", id);
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		GeoLayer geolayer = geoLayerRepository.findById(id).orElse(null);
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (geolayer != null) {
			if (!userLayerFullPermissionIdList.contains(geolayer.getLayer().getId())) {
				throw new ApiException("No layer permission");
			}
		}
		
		try {
					
			geoLayerRepository.deleteById(id);
			
			Map<String, Object> geoForLog = new TreeMap<>();
			GeoLayer geoLayer = new GeoLayer();
			geoLayer.setId(id);
			geoForLog.put("deleted", geoLayer);
			
			dbLogger.log(new Gson().toJson(geoForLog), LogTypeE.GEO_LAYER_DELETE);
			
			return ResponseEntity.ok().build();
			
		} catch (Exception ex) {			
			ApiException apiException = new ApiException("GeoLayer id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
	}
}
