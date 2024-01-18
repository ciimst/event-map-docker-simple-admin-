package com.imst.event.map.admin.controllers.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.projections.FakeLayerIdProjection;
import com.imst.event.map.admin.db.repositories.FakeLayerIdRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiFakeLayerIdItem;
import com.imst.event.map.hibernate.entity.FakeLayerId;
import com.imst.event.map.hibernate.entity.Layer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/fakeLayerId")
public class FakeLayerIdControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@Autowired
	private FakeLayerIdRepository fakeLayerIdRepository;

	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/eventGroups/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<FakeLayerIdProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<FakeLayerIdProjection> projections;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		try {
			projections = fakeLayerIdRepository.findAllPageableProjectedByIdIn(pageable, userLayerPermissionIdList);
			
		} catch (Exception e) {
			
			log.debug(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return projections;
	}
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{layerId}")
	public FakeLayerIdProjection getByLayerId(@PathVariable Integer layerId) {
		
		FakeLayerIdProjection projection = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		try {
			projection = fakeLayerIdRepository.findProjectedByLayerId(layerId);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (projection == null) {
			throw new ApiException("Not found.");
		}
		
		if (!userEventGroupPermissionIdList.contains(layerId)) {
			throw new ApiException("Layer no permission.");
		}
		
		return projection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{layerId}")
	public ResponseEntity<?> deleteByLayerId(@PathVariable Integer layerId) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		if (!userLayerPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
			throw new ApiException("Layer no permission.");
		}
		
		
		
		try {
			
			List<FakeLayerId> list = fakeLayerIdRepository.findAllByLayerId(layerId);
			List<Integer> fakeLayerIdItemIdList = list.stream().map(FakeLayerId::getId).collect(Collectors.toList());
			
			if(fakeLayerIdItemIdList.size() == 0) {
				throw new ApiException("Fake Layer Id not found.");
			}
			
			fakeLayerIdRepository.deleteByIdList(fakeLayerIdItemIdList);
			
			Map<String, Object> logMap = new TreeMap<>();
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(list);
			logMap.put("deleted", json);
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.FAKE_LAYER_ID_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("Layer id not found: " + layerId, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{layerId}/{roleId}")
	public ResponseEntity<?> deleteByLayerIdAndRoleId(@PathVariable Integer layerId, @PathVariable String roleId) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		if (!userLayerPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
			throw new ApiException("Layer no permission.");
		}
		
		try {
			
			List<FakeLayerId> list = fakeLayerIdRepository.findAllByLayerIdAndRoleId(layerId, roleId);
			List<Integer> fakeLayerIdItemIdList = list.stream().map(FakeLayerId::getId).collect(Collectors.toList());
			
			if(fakeLayerIdItemIdList.size() == 0) {
				throw new ApiException("Fake Layer Id not found.");
			}
			
			fakeLayerIdRepository.deleteByIdList(fakeLayerIdItemIdList);
			
			Map<String, Object> logMap = new TreeMap<>();
			
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(list);
			logMap.put("deleted", json);
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.FAKE_LAYER_ID_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("Layer id not found: " + layerId, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiFakeLayerIdItem apiEventGroupItem) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		FakeLayerId fakeLayerId;
		
		if (!userLayerPermissionIdList.stream().anyMatch(n -> n.equals(apiEventGroupItem.getLayerId()))) {
			throw new ApiException("Layer no permission.");
		}

		logTypeE = LogTypeE.FAKE_LAYER_ID_ADD;
		fakeLayerId = new FakeLayerId();
		
		if (StringUtils.isBlank(apiEventGroupItem.getRoleId())) {
			
			throw new ApiException("Role Id is missing.");
		}
		
		if (apiEventGroupItem.getLayerId() == null) {
			
			throw new ApiException("Layer.id is missing.");
		}
		
		fakeLayerId.setRoleId(apiEventGroupItem.getRoleId());
		
		Layer layer = new Layer();
		layer.setId(apiEventGroupItem.getLayerId());
		fakeLayerId.setLayer(layer);
		
		
		FakeLayerId saved = fakeLayerIdRepository.save(fakeLayerId);
		
		logMap.put("new", ApiFakeLayerIdItem.newInstanceForLog(saved));
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/fakeLayerId/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	
	
}
