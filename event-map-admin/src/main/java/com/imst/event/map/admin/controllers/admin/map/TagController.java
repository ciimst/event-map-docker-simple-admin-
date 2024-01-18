package com.imst.event.map.admin.controllers.admin.map;


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
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.repositories.TagRepository;
import com.imst.event.map.admin.db.specifications.TagSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.TagItem;
import com.imst.event.map.hibernate.entity.Tag;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/tag")
public class TagController {
	
	@Autowired
	private TagRepository tagRepository;	

	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_LIST')")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/tag");
			
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_LIST')")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<TagItem> data(TagItem tagItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(TagItem.class);
		
		TagSpecification tagSpecification = new TagSpecification(tagItem);
		Page<TagItem> tagItems = masterDao.findAll(tagSpecification, pageRequest);
		DataSet<TagItem> dataSet = new DataSet<>(tagItems.getContent(), 0L, tagItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_MANAGE')")
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer tagId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (Optional.ofNullable(tagId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tag.not.found"));
			return genericResponseItem;
		}
		
		Tag tag = tagRepository.findById(tagId).orElse(null);
		if (tag == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tag.not.found"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new TagItem(tag));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_MANAGE')")
	@Operation(summary = "Save")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(TagItem tagItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(tagItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.name.not.null"));//TODO:lang
			return genericResponseItem;
		}
				
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Map<String, Object> tagsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		Tag tag;
		
		if (Optional.ofNullable(tagItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.TAG_EDIT;
			
			tag = tagRepository.findById(tagItem.getId()).orElse(null);
			
			if (tag == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tag.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			tagsForLog.put("old", TagItem.newInstanceForLog(tag));
			
		} else {//add
			
			logTypeE = LogTypeE.TAG_ADD;
			
			tag = new Tag();			
		}
		
		tag.setName(tagItem.getName());
		
		Tag saved = tagRepository.save(tag);
		
		tagsForLog.put("new", TagItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(tagsForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer tagId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(tagId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tag.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			Tag tag = tagRepository.findById(tagId).orElse(null);
			if (tag == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tag.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			
			tagRepository.deleteById(tagId);
			
			Map<String, Object> tagsForLog = new TreeMap<>();
			tagsForLog.put("deleted", TagItem.newInstanceForLog(tag));
			
			dbLogger.log(new Gson().toJson(tagsForLog), LogTypeE.TAG_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	
}
