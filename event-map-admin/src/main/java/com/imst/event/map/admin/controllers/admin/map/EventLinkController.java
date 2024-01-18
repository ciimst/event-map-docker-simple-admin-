package com.imst.event.map.admin.controllers.admin.map;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.imst.event.map.admin.constants.EventTableColumnE;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.repositories.EventLinkRepository;
import com.imst.event.map.admin.db.specifications.EventLinkSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.EventColumnItem;
import com.imst.event.map.admin.vo.EventLinkItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.hibernate.entity.EventColumn;
import com.imst.event.map.hibernate.entity.EventLink;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/event-link")
public class EventLinkController {
	
	@Autowired
	private EventLinkRepository eventLinkRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;

	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_LIST')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/event_link");
		
		List<EventColumnItem> list = EventTableColumnE.getEventColumnList();		
		modelAndView.addObject("eventColumns", list);		
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<EventLinkItem> data(EventLinkItem eventLinkItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(EventLinkItem.class);
		
		EventLinkSpecification eventGroupSpecification = new EventLinkSpecification(eventLinkItem);
		Page<EventLinkItem> eventGroupItems = masterDao.findAll(eventGroupSpecification, pageRequest);
		
		List<EventLinkItem> eventGroupItemList = eventGroupItems.getContent();
		DataSet<EventLinkItem> dataSet = new DataSet<>(eventGroupItemList, 0L, eventGroupItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_MANAGE')")
	@Operation(summary = "güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer eventLinkId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		if (Optional.ofNullable(eventLinkId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.link.not.found"));
			return genericResponseItem;
		}
		
		EventLink eventLink = eventLinkRepository.findById(eventLinkId).orElse(null);
		if (eventLink == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.link.not.found"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new EventLinkItem(eventLink));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(EventLinkItem eventLinkItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(eventLinkItem.getLink())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.link.correctly"));
			return genericResponseItem;
		}
		
		
		if (eventLinkItem.getEventColumnId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.column.correctly"));
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(eventLinkItem.getDisplayName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.name.correctly"));
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(eventLinkItem.getColor())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.color.select"));
			return genericResponseItem;
		}
		
			

		Map<String, Object> eventLinksForLog = new TreeMap<>();
		LogTypeE logTypeE;
		EventLink eventLink;
		Timestamp nowT = DateUtils.nowT();
		
		if (Optional.ofNullable(eventLinkItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.EVENT_LINK_EDIT;
			
			eventLink = eventLinkRepository.findById(eventLinkItem.getId()).orElse(null);		
			
			if (eventLink == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.link.not.found"));
				return genericResponseItem;
			}
	
			eventLinksForLog.put("old", EventLinkItem.newInstanceForLog(eventLink));
			
		} else {//add
			
			logTypeE = LogTypeE.EVENT_LINK_ADD;
			eventLink = new EventLink();	
			eventLink.setCreateDate(nowT);

		}

		
		eventLink.setLink(eventLinkItem.getLink());
		
		EventColumn eventColumn = new EventColumn();
		eventColumn.setId(eventLinkItem.getEventColumnId());
		eventLink.setEventColumn(eventColumn);
		eventLink.setDisplayName(eventLinkItem.getDisplayName());
		eventLink.setColor(eventLinkItem.getColor());
		eventLink.setUpdateDate(nowT);
		
		
		EventLink saved = eventLinkRepository.save(eventLink);		
		eventLinksForLog.put("new", EventLinkItem.newInstanceForLog(saved));

				
		dbLogger.log(new Gson().toJson(eventLinksForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_LINK_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer eventLinkId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(eventLinkId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.link.not.found"));
				return genericResponseItem;
			}
			
			EventLink eventLink = eventLinkRepository.findById(eventLinkId).orElse(null);
			if (eventLink == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.link.not.found"));
				return genericResponseItem;
			}

			eventLinkRepository.delete(eventLink);
			Map<String, Object> eventLinksForLog = new TreeMap<>();
			eventLinksForLog.put("deleted", EventLinkItem.newInstanceForLog(eventLink));
			
			dbLogger.log(new Gson().toJson(eventLinksForLog), LogTypeE.EVENT_LINK_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.link.delete.error"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}

	
}
