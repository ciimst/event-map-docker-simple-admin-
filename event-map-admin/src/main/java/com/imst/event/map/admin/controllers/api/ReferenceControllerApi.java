package com.imst.event.map.admin.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.projections.EventTypeProjection;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;

@RestController
@RequestMapping("/api/references")
public class ReferenceControllerApi {
	
	//Bura basit düzeydeki referanslar için, değişebilir.
	
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private EventTypeRepository eventTypeRepository;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_LIST')")
	@GetMapping(value = "/eventTypes")
	public List<EventTypeProjection> getEventTypes() {
		
		return eventTypeRepository.findAllProjectedBy();
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_LIST')")
	@GetMapping(value = "/eventGroups")
	public List<EventGroupProjection> getEventGroups() {
		
		return eventGroupRepository.findAllProjectedByOrderByName();
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_LIST')")
	@GetMapping(value = "/layers")
	public List<LayerProjection> getLayers() {
		
		return layerRepository.findAllProjectedByStateIsTrue();
	}
	

}

