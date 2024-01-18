package com.imst.event.map.admin.controllers.admin.map;


import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.repositories.TileServerRepository;
import com.imst.event.map.admin.db.specifications.TileServerSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.TileServerItem;
import com.imst.event.map.hibernate.entity.TileServer;

import io.swagger.v3.oas.annotations.Operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/tile")
public class TileServerController {
	
	@Autowired
	private TileServerRepository tileServerRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_TILE_SERVER_LIST')")
	@Operation(summary = "Sayfalama")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/tile");
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_SERVER_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<TileServerItem> data(TileServerItem tileServerItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(TileServerItem.class);
		
		TileServerSpecification tileServerSpecification = new TileServerSpecification(tileServerItem);
		Page<TileServerItem> tileServerItems = masterDao.findAll(tileServerSpecification, pageRequest);
		DataSet<TileServerItem> dataSet = new DataSet<>(tileServerItems.getContent(), 0L, tileServerItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_SERVER_MANAGE')")
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer tileServerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (Optional.ofNullable(tileServerId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.not.found"));
			return genericResponseItem;
		}
		
		TileServer tileServer = tileServerRepository.findById(tileServerId).orElse(null);
		if (tileServer == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.not.found"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new TileServerItem(tileServer));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_SERVER_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(TileServerItem tileServerItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(tileServerItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.name.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(tileServerItem.getUrl())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.url.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> tilesForLog = new TreeMap<>();
		LogTypeE logTypeE;
		TileServer tileServer;
		
		if (Optional.ofNullable(tileServerItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.TILE_SERVER_EDIT;
			
			tileServer = tileServerRepository.findById(tileServerItem.getId()).orElse(null);
			
			if (tileServer == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tileserver.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			tilesForLog.put("old", TileServerItem.newInstanceForLog(tileServer));
			
		} else {//add
			
			logTypeE = LogTypeE.TILE_SERVER_ADD;
			
			tileServer = new TileServer();
			tileServer.setCreateDate(nowT);
		}
		
		tileServer.setName(tileServerItem.getName());
		tileServer.setUrl(tileServerItem.getUrl());
		tileServer.setSortOrder(tileServerItem.getSortOrder());
		tileServer.setState(tileServerItem.getState());
		tileServer.setUpdateDate(nowT);
		
		TileServer saved = tileServerRepository.save(tileServer);
		
		tilesForLog.put("new", TileServerItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(tilesForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_SERVER_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer tileServerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(tileServerId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.not.found"));
				return genericResponseItem;
			}
			
			TileServer tileServer = tileServerRepository.findById(tileServerId).orElse(null);
			if (tileServer == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.not.found"));
				return genericResponseItem;
			}
			
			
			tileServerRepository.deleteById(tileServerId);
			
			Map<String, Object> tileForLog = new TreeMap<>();
			tileForLog.put("deleted", TileServerItem.newInstanceForLog(tileServer));
			
			dbLogger.log(new Gson().toJson(tileForLog), LogTypeE.TILE_SERVER_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	
}
