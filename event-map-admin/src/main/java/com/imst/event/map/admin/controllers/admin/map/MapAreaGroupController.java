package com.imst.event.map.admin.controllers.admin.map;


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
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.db.specifications.MapAreaGroupSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.MapAreaGroupItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.MapAreaGroup;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/mapareagroup")
public class MapAreaGroupController {
	
	@Autowired
	private MapAreaGroupRepository mapAreaGroupRepository;	
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/mapArea/page?page=0&size=10&sort=name,desc")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/map_area_group");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		List<LayerProjection> layerProjectionList = layerRepository.findAllProjectedByIdInOrderByName(userLayerFullPermissionIdList);
		
//		List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layerProjectionList);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<MapAreaGroupItem> data(MapAreaGroupItem mapAreaGroupItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(MapAreaGroupItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
//		List<Integer> allLayersIdList = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList()); // for test
		
		MapAreaGroupSpecification mapAreaGroupSpecification = new MapAreaGroupSpecification(mapAreaGroupItem, userLayerFullPermissionIdList);
		Page<MapAreaGroupItem> mapAreaGroupItems = masterDao.findAll(mapAreaGroupSpecification, pageRequest);
		DataSet<MapAreaGroupItem> dataSet = new DataSet<>(mapAreaGroupItems.getContent(), 0L, mapAreaGroupItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_MANAGE')")
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer mapAreaGroupId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (Optional.ofNullable(mapAreaGroupId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));
			return genericResponseItem;
		}
		
		MapAreaGroup mapAreaGroup = mapAreaGroupRepository.findById(mapAreaGroupId).orElse(null);
		if (mapAreaGroup == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));
			return genericResponseItem;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		//Katman izin kontrolu
		if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroup.getLayer().getId()))) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new MapAreaGroupItem(mapAreaGroup));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(MapAreaGroupItem mapAreaGroupItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(mapAreaGroupItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.name.not.null"));//TODO:lang
			return genericResponseItem;
		}
		
		
		if (StringUtils.isBlank(mapAreaGroupItem.getColor())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.map.area.color.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (mapAreaGroupItem.getLayerId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
				
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();

		
		Map<String, Object> mapAreaGroupsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		MapAreaGroup mapAreaGroup;
		
		if (Optional.ofNullable(mapAreaGroupItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.MAP_AREA_GROUP_EDIT;
			
			mapAreaGroup = mapAreaGroupRepository.findById(mapAreaGroupItem.getId()).orElse(null);
			
			if (mapAreaGroup == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			// Editlenecek areagroup'un layer id'sine ve Gonderilen itemin layer id'sine bakar
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroup.getLayer().getId()))) { 
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroupItem.getLayerId()))) { 
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			mapAreaGroupsForLog.put("old", MapAreaGroupItem.newInstanceForLog(mapAreaGroup));
			
		} else {//add
			
			logTypeE = LogTypeE.MAP_AREA_GROUP_ADD;
			
			mapAreaGroup = new MapAreaGroup();	
			
			//Gonderilen itemin layer id'sine bakar
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroupItem.getLayerId()))) { 
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
		}
		
		mapAreaGroup.setName(mapAreaGroupItem.getName());
		mapAreaGroup.setColor(mapAreaGroupItem.getColor());
		
		Layer layer = new Layer();
		layer.setId(mapAreaGroupItem.getLayerId());
		mapAreaGroup.setLayer(layer);
		
		MapAreaGroup saved = mapAreaGroupRepository.save(mapAreaGroup);
		
		mapAreaGroupsForLog.put("new", MapAreaGroupItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(mapAreaGroupsForLog), logTypeE);
		
		return genericResponseItem;
	}
	@PreAuthorize("hasRole('ROLE_MAP_AREA_GROUP_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer mapAreaGroupId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(mapAreaGroupId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			MapAreaGroup mapAreaGroup = mapAreaGroupRepository.findById(mapAreaGroupId).orElse(null);
			if (mapAreaGroup == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			//Katman izin kontrolu
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroup.getLayer().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			mapAreaGroupRepository.deleteById(mapAreaGroupId);
			
			Map<String, Object> mapAreaGroupsForLog = new TreeMap<>();
			mapAreaGroupsForLog.put("deleted", MapAreaGroupItem.newInstanceForLog(mapAreaGroup));
			
			dbLogger.log(new Gson().toJson(mapAreaGroupsForLog), LogTypeE.MAP_AREA_GROUP_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.map.area.gorup.delete.error"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	
}
