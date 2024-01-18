package com.imst.event.map.admin.controllers.api;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiEventTypeItem;
import com.imst.event.map.hibernate.entity.EventType;

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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/eventTypes")
public class EventTypeControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private EventTypeRepository eventTypeRepository;
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/eventTypes/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<EventTypeProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<EventTypeProjection> typeProjections;
		try {
			
			typeProjections = eventTypeRepository.findAllProjectedBy(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return typeProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public EventTypeProjection getById(@PathVariable Integer id) {
		
		EventTypeProjection eventTypeProjection = null;
		try {
			eventTypeProjection = eventTypeRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (eventTypeProjection == null) {
			throw new ApiException("Not found.");
		}
		
		return eventTypeProjection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			eventTypeRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			EventType eventType = new EventType();
			eventType.setId(id);
			logMap.put("deleted", eventType);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_TYPE_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("EventType id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiEventTypeItem apiEventTypeItem) {
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		EventType eventType;
		if (apiEventTypeItem.getId() != null) {//update
			
			logTypeE = LogTypeE.EVENT_TYPE_EDIT;
			
			eventType = eventTypeRepository.findById(apiEventTypeItem.getId()).orElse(null);
			if (eventType == null) {
				throw new ApiException("EventType not found.");
			}
			
			logMap.put("old", ApiEventTypeItem.newInstanceForLog(eventType));
			
		} else {
			
			logTypeE = LogTypeE.EVENT_TYPE_ADD;
			
			eventType = new EventType();
		}
		
		
		if (StringUtils.isBlank(apiEventTypeItem.getName())) {
			
			throw new ApiException("Name is missing.");
		}
		
		if (StringUtils.isBlank(apiEventTypeItem.getImage())) {
			
			throw new ApiException("Image is missing.");
		}
		
		
		eventType.setName(apiEventTypeItem.getName());
		eventType.setImage(apiEventTypeItem.getImage());
		eventType.setCode(apiEventTypeItem.getCode());
		eventType.setPathData(apiEventTypeItem.getPathData());
		
		EventType saved = eventTypeRepository.save(eventType);
		
		logMap.put("new", ApiEventTypeItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/eventTypes/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_MANAGE')")
	@Operation(summary = "Kısmi güncelleme")
	@PatchMapping(value = {"/{id}"})
	public ResponseEntity<?> update(@RequestBody ApiEventTypeItem apiEventTypeItem, @PathVariable Integer id) {
		
		
		EventType eventType = eventTypeRepository.findById(apiEventTypeItem.getId()).orElse(null);
		if (eventType == null) {
			throw new ApiException("EventType not found.");
		}
		
		Map<String, Object> logMap = new TreeMap<>();
		logMap.put("old", ApiEventTypeItem.newInstanceForLog(eventType));
		
		
		if (!StringUtils.isBlank(apiEventTypeItem.getName())) {
			
			eventType.setName(apiEventTypeItem.getName());
		}
		
		if (!StringUtils.isBlank(apiEventTypeItem.getImage())) {
			
			eventType.setImage(apiEventTypeItem.getImage());
		}
		
		if (!StringUtils.isBlank(apiEventTypeItem.getCode())) {
			
			eventType.setCode(apiEventTypeItem.getCode());
		}
		
		
		EventType saved = eventTypeRepository.save(eventType);
		
		logMap.put("new", ApiEventTypeItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_TYPE_EDIT);
		
		String location = "/api/eventTypes/" + id;
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", id);
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
}
