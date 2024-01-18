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
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.repositories.AlertRepository;
import com.imst.event.map.admin.db.repositories.BlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.services.TransactionalPermissionService;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.api.ApiEventGroupItemBasic;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/eventGroups")
public class EventGroupControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired
	private UserPermissionService userPermissionService;
	@Autowired
	private BlackListRepository blackListRepository;
	@Autowired 
	private TransactionalPermissionService transactionalPermissionService;
	
	@Autowired
	private AlertRepository alertRepository;
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/eventGroups/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<EventGroupProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<EventGroupProjection> projections;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		
		try {
			
//			projections = eventGroupRepository.findAllProjectedBy(pageable);
			projections = eventGroupRepository.findAllPageableProjectedByIdIn(pageable, userEventGroupPermissionIdList); // TODO BAK
			
		} catch (Exception e) {
			
			log.debug(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return projections;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public EventGroupProjection getById(@PathVariable Integer id) {
		
		EventGroupProjection projection = null;
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		try {
			projection = eventGroupRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (projection == null) {
			throw new ApiException("Not found.");
		}
		
		if (!userEventGroupPermissionIdList.contains(id)) {
			throw new ApiException("EventGroup no permission.");
		}
		
		return projection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(id))) {
			throw new ApiException("EventGroup no permission.");
		}
		
		
		long eventGroupRelatedAlertCount = alertRepository.countByEventGroupId(id);
		
		if(eventGroupRelatedAlertCount > 0) {
			throw new ApiException("Please delete the associated alarms first.");
		}
		
		// ALtında event grup var mı
		Long childCount = eventGroupRepository.countByParentId(id);
		if (childCount > 0) {
			throw new ApiException("Please delete the associated subgroups first.");
		}
		
		
		long blackListCount = blackListRepository.countByEventGroupIdAndStateIdNot(id, StateE.DELETED.getValue());
		if (blackListCount > 0) {
			throw new ApiException("Please delete the associated blacklists first.");
			
		}
		
		
		try {
	
			//delete event group and permissions
			transactionalPermissionService.deleteEventGroup(id);
			//eventGroupRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			EventGroup deleted = new EventGroup();
			deleted.setId(id);
			logMap.put("deleted", deleted);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_GROUP_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("EventGroup id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiEventGroupItemBasic apiEventGroupItem) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		List<Integer> userNoFullLayerPermissionIdList = permissionWrapperItem.getUserLayerNoFullPermissionItemIds();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		EventGroup eventGroup;
		if (apiEventGroupItem.getId() != null && apiEventGroupItem.getId() != 0) {//update
			
			logTypeE = LogTypeE.EVENT_GROUP_EDIT;
			
			eventGroup = eventGroupRepository.findById(apiEventGroupItem.getId()).orElse(null);
			if (eventGroup == null) {
				throw new ApiException("EventGroup not found.");
			}
			
			if(apiEventGroupItem.getParentId() != null && eventGroup.getId().equals(apiEventGroupItem.getParentId())) {
				
				throw new ApiException("You cannot add the event group itself as a parent group.");
			}
			
			
			// Editlenen olay grubuna izin var mı diye bakılır
			if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getId()))) {
				throw new ApiException("EventGroup no permission.");
			}	
			
			// Editlenen olay grubunun eklenecegi katmana izin var mı diye bakılır
			if (!userLayerPermissionIdList.stream().anyMatch(n -> n.equals(apiEventGroupItem.getLayerId()))) {
				throw new ApiException("Layer no permission.");
			}	
			
			// Full permission layerdaki event group, full permissionlu olmayan bir layera yazılamaz
			if (userLayerFullPermissionIdList.contains(eventGroup.getLayer().getId()) && !userLayerFullPermissionIdList.contains(apiEventGroupItem.getLayerId()) ) {		
				throw new ApiException("Layer no permission.");
			}
			
			// layer'ında full permission olmayan event grouplarina kendi layerlari ve full izinli olunan dısındaki layerlar atanamaz
			if (userNoFullLayerPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getLayer().getId())) && (!apiEventGroupItem.getLayerId().equals(eventGroup.getLayer().getId()) && !userLayerFullPermissionIdList.contains(apiEventGroupItem.getLayerId()))) { 
				throw new ApiException("Layer no permission.");
			}
			
			// event group'a kendi parenti veya izinli oldugu event grouplari disinda parent atanamaz
			if (apiEventGroupItem.getParentId() != null) {
				if(eventGroup.getParentId() != null) {
					if (!apiEventGroupItem.getParentId().equals(eventGroup.getParentId()) && !userEventGroupPermissionIdList.contains(apiEventGroupItem.getParentId())) {
						throw new ApiException("EventGroup parent no permission.");
					}
				}
				else {
					if (!userEventGroupPermissionIdList.contains(apiEventGroupItem.getParentId())) {
						throw new ApiException("EventGroup parent no permission.");
					}
				}
			}
		
			logMap.put("old", ApiEventGroupItemBasic.newInstanceForLog(eventGroup));
			
		} else {
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(apiEventGroupItem.getLayerId()))) {
				throw new ApiException("EventGroup no permission.");
			}
			
			if (apiEventGroupItem.getParentId() != null) {
				if (!userEventGroupPermissionIdList.contains(apiEventGroupItem.getParentId())) {
					throw new ApiException("EventGroup parent no permission.");
				}
			}
			
			logTypeE = LogTypeE.EVENT_GROUP_ADD;
			
			eventGroup = new EventGroup();
		}
		
		
		if (StringUtils.isBlank(apiEventGroupItem.getName())) {
			
			throw new ApiException("Name is missing.");
		}
		
		if (StringUtils.isBlank(apiEventGroupItem.getColor())) {
			
			throw new ApiException("Color is missing.");
		}
		
		if (apiEventGroupItem.getLayerId() == null) {
			
			throw new ApiException("Layer.id is missing.");
		}
		
		eventGroup.setName(apiEventGroupItem.getName());
		eventGroup.setColor(apiEventGroupItem.getColor());
		eventGroup.setDescription(apiEventGroupItem.getDescription());
		eventGroup.setParentId(apiEventGroupItem.getParentId());
		
		Layer layer = new Layer();
		layer.setId(apiEventGroupItem.getLayerId());
		eventGroup.setLayer(layer);
		
		
		if(apiEventGroupItem.getId() != null && apiEventGroupItem.getId() != 0) {
			
			List<EventGroupProjection> evenGroupsUnderLayer = eventGroupRepository.findAllProjectedByLayerOrderByName(layer);		
			EventGroupTree eventGroupTree = new EventGroupTree(evenGroupsUnderLayer, true);
			//bu listede seçebileceği gruplar var. seçilen üst grup listede var ise ekleyebilir. yok ise ekleyemez.
			boolean eventGroupParentCircleControl = eventGroupTree.eventGroupListThatCanBeAddedAsParent(eventGroup.getId()).stream().anyMatch(f -> f.getId().equals(apiEventGroupItem.getParentId()));

			if(apiEventGroupItem.getParentId() != null && !eventGroupParentCircleControl) {

				throw new ApiException("You cannot select the event group as the parent group because it loops.");
			}
			
		}
		
		EventGroup saved = eventGroupRepository.save(eventGroup);
		
		logMap.put("new", ApiEventGroupItemBasic.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/eventGroups/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_GROUP_MANAGE')")
	@Operation(summary = "Kısmi güncelleme")
	@PatchMapping(value = {"/{id}"})
	public ResponseEntity<?> update(@RequestBody ApiEventGroupItemBasic apiEventGroupItem, @PathVariable Integer id) {
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.getUserPermissions();
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		List<Integer> userNoFullLayerPermissionIdList = permissionWrapperItem.getUserLayerNoFullPermissionItemIds();
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
				
		EventGroup eventGroup = eventGroupRepository.findById(apiEventGroupItem.getId()).orElse(null);
		if (eventGroup == null) {
			throw new ApiException("EventGroup not found.");
		}	
		
		Map<String, Object> logMap = new TreeMap<>();
		logMap.put("old", ApiEventGroupItemBasic.newInstanceForLog(eventGroup));
		
		
		if (!StringUtils.isBlank(apiEventGroupItem.getName())) {
			
			eventGroup.setName(apiEventGroupItem.getName());
		}
		
		if (!StringUtils.isBlank(apiEventGroupItem.getColor())) {
			
			eventGroup.setColor(apiEventGroupItem.getColor());
		}
		
		
		// Editlenen olay grubuna izin var mı diye bakılır
		if (!userEventGroupPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getId()))) {
			throw new ApiException("EventGroup no permission.");
		}	
		
		if (apiEventGroupItem.getLayerId() != null) {
			// Editlenen olay grubunun eklenecegi katmana izin var mı diye bakılır
			if (!userLayerPermissionIdList.stream().anyMatch(n -> n.equals(apiEventGroupItem.getLayerId()))) {
				throw new ApiException("Layer no permission.");
			}	
			
			// Full permission layerdaki event group, full permissionlu olmayan bir layera yazılamaz
			if (userLayerFullPermissionIdList.contains(eventGroup.getLayer().getId()) && !userLayerFullPermissionIdList.contains(apiEventGroupItem.getLayerId()) ) {		
				throw new ApiException("Layer no permission.");
			}
			
			// layer'ında full permission olmayan event grouplarina kendi layerlari ve full izinli olunan dısındaki layerlar atanamaz
			if (userNoFullLayerPermissionIdList.stream().anyMatch(n -> n.equals(eventGroup.getLayer().getId())) && (!apiEventGroupItem.getLayerId().equals(eventGroup.getLayer().getId()) && !userLayerFullPermissionIdList.contains(apiEventGroupItem.getLayerId()))) { 
				throw new ApiException("Layer no permission.");
			}
		}
		
		// event group'a kendi parenti veya izinli oldugu event grouplari disinda parent atanamaz
		if (apiEventGroupItem.getParentId() != null) {
			if(eventGroup.getParentId() != null) {
				if (!apiEventGroupItem.getParentId().equals(eventGroup.getParentId()) && !userEventGroupPermissionIdList.contains(apiEventGroupItem.getParentId())) {
					throw new ApiException("EventGroup parent no permission.");
				}
			}
			else {
				if (!userEventGroupPermissionIdList.contains(apiEventGroupItem.getParentId())) {
					throw new ApiException("EventGroup parent no permission.");
				}
			}
		}
		
		
		if (apiEventGroupItem.getLayerId() != null) {
			
			Layer layer = new Layer();
			layer.setId(apiEventGroupItem.getLayerId());
			eventGroup.setLayer(layer);
		}
			
		
		EventGroup saved = eventGroupRepository.save(eventGroup);
		
		logMap.put("new", ApiEventGroupItemBasic.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_GROUP_EDIT);
		
		String location = "/api/eventGroups/" + id;
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", id);
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
}
