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
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.repositories.GeoLayerRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.specifications.GeoLayerSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.GeoLayerItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.hibernate.entity.GeoLayer;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/geolayer")
public class GeoLayerController {

	@Autowired
	private GeoLayerRepository geoLayerRepository;
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_LIST')")
	@Operation(summary = "")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/geo_layer");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		List<LayerProjection> layerProjectionList = layerRepository.findAllProjectedByIdInOrderByName(userLayerPermissionIdList);
		
//		List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layerProjectionList);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<GeoLayerItem> data(GeoLayerItem geoLayerItem, @DatatablesParams DatatablesCriterias criterias) {
		
		PageRequest pageRequest = criterias.getPageRequest(GeoLayerItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
//		List<Integer> allLayersIdList = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList()); // for test
		
		GeoLayerSpecification geoLayerSpecification = new GeoLayerSpecification(geoLayerItem, userLayerPermissionIdList);
		Page<GeoLayerItem> geoLayerItems = masterDao.findAll(geoLayerSpecification, pageRequest);
		
		DataSet<GeoLayerItem> dataSet = new DataSet<>(geoLayerItems.getContent(), 0L, geoLayerItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_MANAGE')")
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer geoLayerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (Optional.ofNullable(geoLayerId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geoLayer.not.found"));
			return genericResponseItem;
		}
		
		GeoLayer geoLayer = geoLayerRepository.findById(geoLayerId).orElse(null);
		if (geoLayer == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geoLayer.not.found"));
			return genericResponseItem;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		//Katman izin kontrolu
		if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(geoLayer.getLayer().getId()))) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new GeoLayerItem(geoLayer));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(GeoLayerItem geoLayerItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(geoLayerItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geo.layer.name.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(geoLayerItem.getData())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geo.layer.data.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (geoLayerItem.getLayerId() == null) {
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
		
				
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> geoLayersForLog = new TreeMap<>();
		LogTypeE logTypeE;
		GeoLayer geoLayer;
		
		if (Optional.ofNullable(geoLayerItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.GEO_LAYER_EDIT;
			
			geoLayer = geoLayerRepository.findById(geoLayerItem.getId()).orElse(null);
			
			if (geoLayer == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geo.layer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(geoLayer.getLayer().getId()))) { // Editlenecek geolayerin layer id'sine ve Gonderilen itemin layer id'sine bakar
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(geoLayerItem.getLayerId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			geoLayersForLog.put("old", GeoLayerItem.newInstanceForLog(geoLayer));
			
		} else {//add
			
			logTypeE = LogTypeE.GEO_LAYER_ADD;
			
			geoLayer = new GeoLayer();
			geoLayer.setCreateDate(nowT);
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(geoLayerItem.getLayerId()))) { //Gonderilen itemin layer id'sine bakar
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
		}
		
		geoLayer.setName(geoLayerItem.getName());
		geoLayer.setData(geoLayerItem.getData());
		geoLayer.setState(geoLayerItem.getState() == null ? false : geoLayerItem.getState());
		
		Layer layer = new Layer();
		layer.setId(geoLayerItem.getLayerId());
		geoLayer.setLayer(layer);
		
		GeoLayer saved = geoLayerRepository.save(geoLayer);
		
		geoLayersForLog.put("new", GeoLayerItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(geoLayersForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_GEO_LAYER_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer geoLayerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(geoLayerId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geoLayer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			GeoLayer geoLayer = geoLayerRepository.findById(geoLayerId).orElse(null);
			if (geoLayer == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.geoLayer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			//Katman izin kontrolu
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(geoLayer.getLayer().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			
			geoLayerRepository.deleteById(geoLayerId);
			
			Map<String, Object> geoLayersForLog = new TreeMap<>();
			geoLayersForLog.put("deleted", GeoLayerItem.newInstanceForLog(geoLayer));
			
			dbLogger.log(new Gson().toJson(geoLayersForLog), LogTypeE.GEO_LAYER_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
}
