package com.imst.event.map.admin.controllers.api;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import com.imst.event.map.admin.db.projections.MapAreaGroupProjection;
import com.imst.event.map.admin.db.projections.MapAreaProjection;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.db.repositories.MapAreaRepository;
import com.imst.event.map.admin.db.services.TransactionalMapAreaService;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiMapAreaGroupItem;
import com.imst.event.map.admin.vo.api.ApiMapAreaItem;
import com.imst.event.map.hibernate.entity.MapArea;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/mapArea")
public class MapAreaControllerApi {

	@Autowired
	private MapAreaRepository mapAreaRepository;
	@Autowired
	private MapAreaGroupRepository mapAreaGroupRepository;
	@Autowired
	private TransactionalMapAreaService transactionalMapAreaService;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/mapArea/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<MapAreaProjection> getPage(@PageableDefault Pageable pageable) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		List<Integer> mapAreaGroupIdList = mapAreaGroupRepository.findAllProjectedByLayerIdInOrderByName(userLayerFullPermissionIdList).stream().map(MapAreaGroupProjection::getLayerId).collect(Collectors.toList());
		
		try {
			return mapAreaRepository.findAllProjectedByMapAreaGroupIdIn(pageable, mapAreaGroupIdList); // TODO Sıkıntılı
//			return mapAreaRepository.findAllProjectedBy(pageable);	
		}
		catch(Exception ex) {
			log.error(ex);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public MapAreaProjection getById(@PathVariable Integer id) {
		try {
			MapAreaProjection mapAreaProjection;
			mapAreaProjection = mapAreaRepository.findProjectedById(id);
			
			MapArea mapAreaToGet = mapAreaRepository.findById(id).orElse(null);
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
					
			if(mapAreaProjection == null) {
				throw new ApiException("Map Area Not found.");
			}
			
			if (!userLayerFullPermissionIdList.contains(mapAreaToGet.getMapAreaGroup().getLayer().getId())) {
				throw new ApiException("Layer no permission");
			}
			
			return mapAreaProjection;
			
		} catch (Exception ex) {			
			log.error(ex);
			throw ex;
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_MANAGE')")
	@Operation(summary = "")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdateEvent(@RequestBody ApiMapAreaItem apiMapAreaItem) {
		
		Timestamp nowT = DateUtils.nowT();
		
		ApiMapAreaGroupItem apiMapAreaGroupItem = apiMapAreaItem.getApiMapAreaGroupItem();
		if (apiMapAreaGroupItem == null) {
			throw new ApiException("MapAreaGroupItem not found.");
		}
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		Map<String, Object> mapAreaForLog = new TreeMap<>();
		
		LogTypeE logTypeE;
		
		MapArea mapArea = null;
		if(apiMapAreaItem.getId() != null) {
			mapArea = mapAreaRepository.findById(apiMapAreaItem.getId()).orElse(null);
		}
		
		if (mapArea == null) {//add
			
			if (!userLayerFullPermissionIdList.contains(apiMapAreaGroupItem.getLayerId())) {
				throw new ApiException("Layer no permission");
			}
			
			logTypeE = LogTypeE.MAP_AREA_ADD;
			mapArea = new MapArea();
			mapArea.setCreateDate(nowT);
			mapArea.setState(true);
			
		} else {//update
			
			if (!userLayerFullPermissionIdList.contains(apiMapAreaGroupItem.getLayerId())) {
				throw new ApiException("Layer no permission");
			}
			
			if (!userLayerFullPermissionIdList.contains(mapArea.getMapAreaGroup().getLayer().getId())) {
				throw new ApiException("Layer no permission");
			}
			
			logTypeE = LogTypeE.MAP_AREA_EDIT;
			mapAreaForLog.put("old", mapArea);
		}
			
		mapArea.setTitle(apiMapAreaItem.getTitle());
		mapArea.setCoordinateInfo(apiMapAreaItem.getCoordinateInfo());
		
		mapArea.setUpdateDate(nowT);
		mapArea.setState(apiMapAreaItem.isState());
		
		ApiMapAreaItem apiMapAreaItemResponse;
		try {
			
			apiMapAreaItemResponse = transactionalMapAreaService.saveEvent(mapArea, apiMapAreaItem, nowT);
			
		} catch (ApiException e) {
			log.error(e);
			throw e;
		}
		catch (Exception e) {
			log.error(e);
			throw new ApiException("Record cannot be saved. Please try again. If the error persists, please contact system administrator.");
		}
		
		try {
			
			mapAreaForLog.put("new", apiMapAreaItemResponse);
			dbLogger.log(new Gson().toJson(mapAreaForLog), logTypeE);
			
		} catch (Exception e) {
			log.error(e);
		}
		
		String location = "/api/mapArea/" + apiMapAreaItemResponse.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", apiMapAreaItemResponse.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		MapArea mapAreaToBeDeleted = mapAreaRepository.findById(id).orElse(null);

		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if (!userLayerFullPermissionIdList.contains(mapAreaToBeDeleted.getMapAreaGroup().getLayer().getId())) {
			throw new ApiException("Layer no permission");
		}
		
		try {
					
			mapAreaRepository.deleteById(id);
			
			Map<String, Object> mapAreaForLog = new TreeMap<>();
			MapArea mapArea = new MapArea();
			mapArea.setId(id);
			mapAreaForLog.put("deleted", mapArea);
			
			dbLogger.log(new Gson().toJson(mapAreaForLog), LogTypeE.MAP_AREA_DELETE);
			
			return ResponseEntity.ok().build();
			
		} catch (Exception ex) {			
			ApiException apiException = new ApiException("Map Area id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
	}
}
