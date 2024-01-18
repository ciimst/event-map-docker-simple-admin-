package com.imst.event.map.admin.controllers.admin.map;


import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.controllers.admin.CrudControllerAbs;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.specifications.EventTypeSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventType;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/event-type")
public class EventTypeController extends CrudControllerAbs<EventTypeItem> {
	
	private static final String EDIT_LINK = "admin/map/event-type/edit/";
	
	@Autowired
	private EventTypeRepository eventTypeRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_LIST')")
	@Operation(summary = "")
	@Override
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/event_type");
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_LIST')")
	@Operation(summary = "")
	@Override
	public DatatablesResponse<EventTypeItem> data(EventTypeItem item, DatatablesCriterias criteria) {
		
		PageRequest pageRequest = criteria.getPageRequest(EventTypeItem.class);
		
		EventTypeSpecification eventTypeSpecification = new EventTypeSpecification(item);
		Page<EventTypeItem> eventTypeItems = masterDao.findAll(eventTypeSpecification, pageRequest);
		
		DataSet<EventTypeItem> dataSet = new DataSet<>(eventTypeItems.getContent(), 0L, eventTypeItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criteria);
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_MANAGE')")
	@Operation(summary = "Güncelleme")
	@Override
	public ModelAndView edit(Integer id) {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/event_type_edit");
		
		EventTypeItem eventTypeItem;
		if (id == null) {
			eventTypeItem = new EventTypeItem();
		} else {
			eventTypeItem = eventTypeRepository.findOneProjectedById(id);
			if (eventTypeItem == null) {
				eventTypeItem = new EventTypeItem();
				modelAndView.addObject("notFound", true);
			}
		}
		
		modelAndView.addObject("eventTypeItem", eventTypeItem);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_MANAGE')")
	@Operation(summary = "Kaydet")
	@Override
	public GenericResponseItem save(EventTypeItem item) {
		
		GenericResponseItem genericResponseItem = validate(item);
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		try {
			
			LogTypeE logTypeE;
			Map<String, Object> mapForLog = new TreeMap<>();
			EventType eventType;
			
			if (Optional.ofNullable(item.getId()).orElse(0) > 0) {//edit
				
				logTypeE = LogTypeE.EVENT_TYPE_EDIT;
				eventType = eventTypeRepository.findById(item.getId()).orElse(null);
				if (eventType == null) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventType.not.found"));
					return genericResponseItem;
				}
				
				mapForLog.put("old", EventTypeItem.newInstanceForLog(eventType));
				
			} else {
				
				logTypeE = LogTypeE.EVENT_TYPE_ADD;
				eventType = new EventType();
			}
			
			eventType.setCode(item.getCode());
			eventType.setName(item.getName());
			eventType.setImage(item.getImage());
			eventType.setPathData(item.getPathData());
			
			EventType saved = eventTypeRepository.save(eventType);
			
			try {
				mapForLog.put("new", EventTypeItem.newInstanceForLog(saved));
				dbLogger.log(new Gson().toJson(mapForLog), logTypeE);
			} catch (Exception e) {
				log.error(e);
			}
			
			genericResponseItem.setRedirectUrl(EDIT_LINK + saved.getId());
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TYPE_MANAGE')")
	@Operation(summary = "Sil")
	@Override
	public GenericResponseItem delete(Integer id) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			if (Optional.ofNullable(id).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventType.not.found"));
				return genericResponseItem;
			}
			
			EventType eventType = eventTypeRepository.findById(id).orElse(null);
			if (eventType == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventType.not.found"));
				return genericResponseItem;
			}
			
			Set<Event> events = eventType.getEvents();
			if (events != null && !events.isEmpty()) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.eventType.delete.event.related"));
				return genericResponseItem;
			}
			
			eventTypeRepository.deleteById(id);
			
			Map<String, Object> mapForLog = new TreeMap<>();
			mapForLog.put("deleted", EventTypeItem.newInstanceForLog(eventType));
			
			try {
				dbLogger.log(new Gson().toJson(mapForLog), LogTypeE.EVENT_TYPE_DELETE);
			} catch (Exception e) {
				log.error(e);
			}
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		
		return genericResponseItem;
	}
	
	private GenericResponseItem validate(EventTypeItem item) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(item.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.name.not.null")); //TODO:lang
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(item.getImage())) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.icon.not.null")); //TODO:lang
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
}
