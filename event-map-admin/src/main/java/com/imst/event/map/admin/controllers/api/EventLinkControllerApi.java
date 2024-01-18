package com.imst.event.map.admin.controllers.api;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.imst.event.map.admin.constants.EventTableColumnE;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.projections.EventLinkProjection;
import com.imst.event.map.admin.db.repositories.EventLinkRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.EventColumnItem;
import com.imst.event.map.admin.vo.api.ApiEventLinkItem;
import com.imst.event.map.hibernate.entity.EventColumn;
import com.imst.event.map.hibernate.entity.EventLink;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/eventLink")
public class EventLinkControllerApi {

	@Autowired private EventLinkRepository eventLinkRepository;	
	@Autowired private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_LIST')")
	@Operation(summary = "link sayfalama. Örn:/api/eventLink/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<EventLinkProjection> getPage(@PageableDefault Pageable pageable){
		
		Page<EventLinkProjection> eventLinkProjections;
		try {
			
			eventLinkProjections = eventLinkRepository.findAllProjectedBy(pageable);
			
		}catch (Exception e) {
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return eventLinkProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_LIST')")
	@Operation(summary = "olay kolonlarını değerini görmek için kullanılabilir.")
	@GetMapping(value = "/getEventColumn")
	public List<EventColumnItem> getEventColumn(){
		
		List<EventColumnItem> list = new ArrayList<>();	
		try {		
			list = EventTableColumnE.getEventColumnList();
			
		}catch (Exception e) {
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return list;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_LIST')")
	@Operation(summary = "Tekil")
	@GetMapping(value = "/{id}")
	public EventLinkProjection getEventLinkById(@PathVariable Integer id) {
		
		EventLinkProjection eventLinkProjection = eventLinkRepository.findProjectedById(id);
		if(eventLinkProjection == null) {
			throw new ApiException("Not found");
		}
		
		return eventLinkProjection;
		
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdateEventLink(@RequestBody ApiEventLinkItem apiEventLinkItem){
		
		if (StringUtils.isBlank(apiEventLinkItem.getDisplayName())) {
			throw new ApiException("link display name not null.");
		}
		
		if (StringUtils.isBlank(apiEventLinkItem.getLink())) {
			throw new ApiException("event link not null.");
		}
		
		if (apiEventLinkItem.getEventColumnId() == null) {
			throw new ApiException("event link column id not null.");
		}
		
		if(!EventTableColumnE.getEventColumnList().stream().anyMatch( f -> f.getId().equals(apiEventLinkItem.getEventColumnId()))) {
			throw new ApiException("event column id not found. please make a request to the /api/eventLink/getEventColumn method and check the id information of the columns");
		}
		
		Timestamp nowT = DateUtils.nowT();
		
		Map<String, Object> eventLinksForLog = new TreeMap<>();
		
		LogTypeE logTypeE;
		
		EventLink eventLink;
		
		if(Optional.ofNullable(apiEventLinkItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.EVENT_LINK_EDIT;  
			
			eventLink = null;
			Optional<EventLink> optianalEventLink = eventLinkRepository.findById(apiEventLinkItem.getId());
			
			if(optianalEventLink.isPresent()) {
				eventLink = optianalEventLink.get();
			}
			if(eventLink == null) {
				throw new ApiException("event link not found.");
			}
			
			eventLinksForLog.put("old", eventLink);
		}else {//add
			
			logTypeE = LogTypeE.EVENT_LINK_ADD;
			eventLink = new EventLink();
			eventLink.setCreateDate(nowT);
			
		}
		
		eventLink.setLink(apiEventLinkItem.getLink());
		eventLink.setDisplayName(apiEventLinkItem.getDisplayName());
		eventLink.setColor(apiEventLinkItem.getColor());
		

		
		EventColumn eventColumn = new EventColumn();
		eventColumn.setId(apiEventLinkItem.getEventColumnId());
		
		eventLink.setEventColumn(eventColumn);	
		eventLink.setUpdateDate(nowT);
		
		EventLink saved = eventLinkRepository.save(eventLink);
		
		String location = "/api/eventLink/" + saved.getId();
		eventLinksForLog.put("new", saved);
		dbLogger.log(new Gson().toJson(eventLinksForLog), logTypeE);
		
		
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
		
		
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id){
		
		try {
			
			if (Optional.ofNullable(id).orElse(0) < 1) {
				throw new ApiException("event link not found");
			}
		
			Optional<EventLink> optionalEventLink = eventLinkRepository.findById(id);
			EventLink eventLink = null;
			if(optionalEventLink.isPresent()) {
				eventLink = optionalEventLink.get();
			}
			if (eventLink == null) {
				
				throw new ApiException("event Link not found");
				
			}
			
			eventLinkRepository.delete(eventLink);
			
			Map<String, Object> eventLinksForLog = new TreeMap<>();
			EventLink newEventLink = new EventLink();
			newEventLink.setId(id);
			eventLinksForLog.put("delete", newEventLink);
			
			dbLogger.log(new Gson().toJson(eventLinksForLog), LogTypeE.EVENT_LINK_DELETE);
		
		
		}catch (ApiException e) {
			
			log.error(e);
			throw e;
		}
		
		return ResponseEntity.ok().build();
		
		
	}
	
	
	
	
}
