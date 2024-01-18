package com.imst.event.map.admin.controllers.api;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.projections.TagProjection;
import com.imst.event.map.admin.db.repositories.TagRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiTagItem;
import com.imst.event.map.hibernate.entity.Tag;

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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/tags")
public class TagControllerApi {
	
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private TagRepository tagRepository;
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_LIST')")
	@Operation(summary = "Etiket sayfalama. Örn:/api/tags/page?page=0&size=10&sort=name,desc")
	@GetMapping(value = "/page")
	public Page<TagProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<TagProjection> tagProjections;
		try {
			
			tagProjections = tagRepository.findAllProjectedBy(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return tagProjections;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_LIST')")
	@Operation(summary = "Tekil tag.")
	@GetMapping(value = "/{id}")
	public TagProjection getById(@PathVariable Integer id) {
		
		TagProjection tagProjection = null;
		try {
			tagProjection = tagRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (tagProjection == null) {
			throw new ApiException("Not found.");
		}
		
		return tagProjection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			tagRepository.deleteById(id);
			
			Map<String, Object> logMap = new TreeMap<>();
			Tag tag = new Tag();
			tag.setId(id);
			logMap.put("deleted", tag);
			
			dbLogger.log(new Gson().toJson(logMap), LogTypeE.EVENT_TAG_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("Tag id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_TAG_MANAGE')")
	@Operation(summary = "Yeni etiket kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiTagItem apiTagItem) {
		
		Map<String, Object> logMap = new TreeMap<>();
		LogTypeE logTypeE;
		Tag tag;
		if (apiTagItem.getId() != null) {//update
			
			logTypeE = LogTypeE.EVENT_TAG_EDIT;
			
			tag = tagRepository.findById(apiTagItem.getId()).orElse(null);
			if (tag == null) {
				throw new ApiException("Tag not found.");
			}
			
			
			if (!tag.getName().equals(apiTagItem.getName())) {
				
				if (checkIfTagExist(apiTagItem.getName())) {
					
					throw new ApiException("Tag name is in use.");
				}
			}
			
			logMap.put("old", ApiTagItem.newInstanceForLog(tag));
			
		} else {
			
			logTypeE = LogTypeE.EVENT_TAG_ADD;
			
			tag = new Tag();
			
			if (checkIfTagExist(apiTagItem.getName())) {
				
				throw new ApiException("Tag name is in use.");
			}
		}
		
		
		if (StringUtils.isBlank(apiTagItem.getName())) {
			
			throw new ApiException("Name is missing.");
		}
		
		
		tag.setName(apiTagItem.getName());
		
		Tag saved = tagRepository.save(tag);
		
		logMap.put("new", ApiTagItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(logMap), logTypeE);
		
		String location = "/api/tags/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	private boolean checkIfTagExist(String username) {
		Tag tag = tagRepository.findOneByName(username).orElse(null);
		return tag != null;
	}
	
}
