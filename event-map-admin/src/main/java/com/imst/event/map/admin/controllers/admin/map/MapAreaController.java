package com.imst.event.map.admin.controllers.admin.map;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
import com.imst.event.map.admin.db.projections.MapAreaGroupProjection;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.db.repositories.MapAreaRepository;
import com.imst.event.map.admin.db.repositories.TileServerRepository;
import com.imst.event.map.admin.db.specifications.MapAreaSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.MapAreaItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.TileServerItem;
import com.imst.event.map.hibernate.entity.MapArea;
import com.imst.event.map.hibernate.entity.MapAreaGroup;
import com.imst.event.map.hibernate.entity.TileServer;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@RequestMapping("/admin/map/map-area")
public class MapAreaController extends CrudControllerAbs<MapAreaItem> {
	
	private static final String EDIT_LINK = "admin/map/map-area/edit/";
	
	@Autowired
	private TileServerRepository tileServerRepository;
	@Autowired
	private MapAreaGroupRepository mapAreaGroupRepository;
	@Autowired
	private MapAreaRepository mapAreaRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/mapArea/page?page=0&size=10&sort=name,desc")
	@Override
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/map_area");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		List<MapAreaGroupProjection> mapAreaGroupProjectionsFiltered = mapAreaGroupRepository.findAllProjectedByLayerIdInOrderByName(userLayerFullPermissionIdList);
		
//		List<MapAreaGroupProjection> mapAreaGroupProjections = mapAreaGroupRepository.findAllProjectedByOrderByName();
		
		modelAndView.addObject("mapAreaGroups", mapAreaGroupProjectionsFiltered);
	
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_LIST')")
	@Operation(summary = "")
	@Override
	public DatatablesResponse<MapAreaItem> data(MapAreaItem mapAreaItem, DatatablesCriterias criteria) {
		
		PageRequest pageRequest = criteria.getPageRequest(MapAreaItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
//		List<Integer> allLayersIdList = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList()); // for test
				
		MapAreaSpecification mapAreaSpecification = new MapAreaSpecification(mapAreaItem, userLayerFullPermissionIdList);
		Page<MapAreaItem> mapAreaItems = masterDao.findAll(mapAreaSpecification, pageRequest);
		
		DataSet<MapAreaItem> dataSet = new DataSet<>(mapAreaItems.getContent(), 0L, mapAreaItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criteria);
	}
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_MANAGE')")
	@Operation(summary = "Güncelleme")
	@Override
	public ModelAndView edit(Integer id) {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/map_area_edit");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
				
		MapAreaItem mapAreaItem;
		if (id == null) {
			mapAreaItem = new MapAreaItem();
		} else {
			mapAreaItem = mapAreaRepository.findOneProjectedById(id);
			if (mapAreaItem == null) {
				mapAreaItem = new MapAreaItem();
				modelAndView.addObject("notFound", true);
			}
			
			Optional<MapAreaGroup> mapAreaGroup = mapAreaGroupRepository.findById(mapAreaItem.getMapAreaGroupId());
			
			if (mapAreaGroup.isPresent()) {
				//Katman izin kontrolu
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroup.get().getLayer().getId()))) {
					modelAndView = new ModelAndView("error");
					modelAndView.setStatus(HttpStatus.FORBIDDEN);
					return modelAndView;
				}
			}

		}
		
		List<MapAreaGroupProjection> mapAreaGroupProjectionsFiltered = mapAreaGroupRepository.findAllProjectedByLayerIdInOrderByName(userLayerFullPermissionIdList);
		
//		List<MapAreaGroupProjection> mapAreaGroupProjections = mapAreaGroupRepository.findAllProjectedByOrderByName();
		
		List<TileServer> all = tileServerRepository.findAll();
		List<TileServerItem> tileServerItems = all.stream().map(TileServerItem::new).collect(Collectors.toList());
		
		modelAndView.addObject("mapAreaGroups", mapAreaGroupProjectionsFiltered);
		modelAndView.addObject("mapAreaItem", mapAreaItem);
		modelAndView.addObject("tileServers", tileServerItems);
		
		return modelAndView;
	}
	
	//Add abstract içinde edit(null) çağırıyor
	@PreAuthorize("hasRole('ROLE_MAP_AREA_MANAGE')")
	@Operation(summary = "Kaydet")
	@Override
	public GenericResponseItem save(MapAreaItem mapAreaItem) {
		
		GenericResponseItem genericResponseItem = validate(mapAreaItem);
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		try {
			
			MapAreaGroup mapAreaGroup = mapAreaGroupRepository.findById(mapAreaItem.getMapAreaGroupId()).orElse(null);
			
			if (mapAreaGroup == null) {
				log.error("MapAreaGroup bulunamadı: " + mapAreaItem.getMapAreaGroupId());
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));
				return genericResponseItem;
			}
			
			mapAreaItem.setState(mapAreaItem.getState() == null ? false : mapAreaItem.getState());
			
			LogTypeE logTypeE;
			Timestamp nowT = DateUtils.nowT();
			Map<String, Object> mapAreaForLog = new TreeMap<>();
			MapArea mapArea;
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();			
			
			if (Optional.ofNullable(mapAreaItem.getId()).orElse(0) > 0) {//edit
				
				logTypeE = LogTypeE.MAP_AREA_EDIT;
				mapArea = mapAreaRepository.findById(mapAreaItem.getId()).orElse(null);
				
				if (mapArea == null) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapArea.not.found"));
					return genericResponseItem;
				}
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapArea.getMapAreaGroup().getLayer().getId()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.no.permission"));
					return genericResponseItem;
				}
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroup.getLayer().getId()))) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.no.permission"));
					return genericResponseItem;
				}
				
				mapAreaForLog.put("old", MapAreaItem.newInstanceForLog(mapArea));
				
			} else {
				
				logTypeE = LogTypeE.MAP_AREA_ADD;
				mapArea = new MapArea();
				mapArea.setCreateDate(nowT);
				
				if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapAreaGroup.getLayer().getId()))) { // Gönderilen itemin layer id sine bakar. Grupların katmanına izin yoksa kayıt edilemez
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.no.permission"));
					return genericResponseItem;
				}
				
			}
			
			mapArea.setUpdateDate(nowT);
			mapArea.setMapAreaGroup(mapAreaGroup);
			mapArea.setCoordinateInfo(mapAreaItem.getCoordinateInfo());
			mapArea.setTitle(mapAreaItem.getTitle());
			mapArea.setState(mapAreaItem.getState());
			
			MapArea saved = mapAreaRepository.save(mapArea);
			
			try {
				mapAreaForLog.put("new", MapAreaItem.newInstanceForLog(saved));
				dbLogger.log(new Gson().toJson(mapAreaForLog), logTypeE);
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
	
	@PreAuthorize("hasRole('ROLE_MAP_AREA_MANAGE')")
	@Operation(summary = "Sil")
	@Override
	public GenericResponseItem delete(Integer mapAreaId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		try {
			
			if (Optional.ofNullable(mapAreaId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapArea.not.found"));
				return genericResponseItem;
			}
			
			MapArea mapArea = mapAreaRepository.findById(mapAreaId).orElse(null);
			if (mapArea == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapArea.not.found"));
				return genericResponseItem;
			}
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			//Katman izin kontrolu
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(mapArea.getMapAreaGroup().getLayer().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.no.permission"));
				return genericResponseItem;
			}
			
			mapAreaRepository.deleteById(mapAreaId);
			
			Map<String, Object> mapAreaForLog = new TreeMap<>();
			mapAreaForLog.put("deleted", MapAreaItem.newInstanceForLog(mapArea));
			
			try {
				dbLogger.log(new Gson().toJson(mapAreaForLog), LogTypeE.MAP_AREA_DELETE);
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
	
	private GenericResponseItem validate(MapAreaItem mapAreaItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(mapAreaItem.getTitle())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.title.not.null")); //TODO:lang
			return genericResponseItem;
		}
		
		if (Optional.ofNullable(mapAreaItem.getMapAreaGroupId()).orElse(0) < 1){
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.mapAreaGroup.not.found"));
			return genericResponseItem;
		}
		
	
		return genericResponseItem;
	}
	
	
}
