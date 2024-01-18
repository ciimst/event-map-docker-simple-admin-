package com.imst.event.map.admin.controllers.admin.map;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

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
import com.imst.event.map.admin.db.repositories.FakeLayerIdRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.admin.db.services.TransactionalPermissionService;
import com.imst.event.map.admin.db.specifications.LayerSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.FakeLayerIdItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.LayerItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.UserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.FakeLayerId;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.LayerExport;
import com.imst.event.map.hibernate.entity.MapAreaGroup;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/layer")
public class LayerController {
	
	@Autowired
	private LayerRepository layerRepository;
	
	@Autowired
	private FakeLayerIdRepository fakeLayerIdRepository;
	
	@Autowired 
	private UserLayerPermissionRepository userLayerPermissionRepository;
	
	@Autowired
	private TransactionalPermissionService transactionalPermissionService;
	
	@Autowired
	private UserPermissionService userPermissionService;
	
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	
	public static int saveLayerExportId = 0;
	
	@PreAuthorize("hasRole('ROLE_LAYER_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/layer/page?page=0&size=10&sort=name,desc")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/layer");
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<LayerItem> data(LayerItem layerItem, @DatatablesParams DatatablesCriterias criterias) {
		
		PageRequest pageRequest = criterias.getPageRequest(LayerItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
						
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		//List<Integer> allLayersIdList = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList());
		
		LayerSpecification layerSpecification = new LayerSpecification(layerItem, userLayerFullPermissionIdList);
		Page<LayerItem> layerItems = masterDao.findAll(layerSpecification, pageRequest);
		
		DataSet<LayerItem> dataSet = new DataSet<>(layerItems.getContent(), 0L, layerItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem edit(Integer layerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		if (Optional.ofNullable(layerId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));
			return genericResponseItem;
		}
		
		Layer layer = layerRepository.findById(layerId).orElse(null);
		if (layer == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));
			return genericResponseItem;
		}
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();

		
		if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new LayerItem(layer));
		
		return genericResponseItem;
	}
	

	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/fakeLayerIdAdd")
	public GenericResponseItem fakeLayerIdSave(Integer layerId, String roleId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));

		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		Map<String, Object> fakeLayerIdForLog = new TreeMap<>();
		
		
		if(Optional.ofNullable(layerId).orElse(0) <= 0) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
			return genericResponseItem;
		}
		
		if(StringUtils.isBlank(roleId)) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
			return genericResponseItem;
		}
		
			
		LogTypeE logTypeE = LogTypeE.FAKE_LAYER_ID_ADD;
		
		Layer layer = layerRepository.findById(layerId).orElse(null);			
		
		if (layer == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
			return genericResponseItem;
		}
		
		
		FakeLayerId fakeLayerId = new FakeLayerId(layer, roleId);			
		fakeLayerIdRepository.save(fakeLayerId);
		
		fakeLayerIdForLog.put("new", FakeLayerIdItem.newInstanceForLog(fakeLayerId));
		dbLogger.log(new Gson().toJson(fakeLayerIdForLog), logTypeE);
		
		
		return genericResponseItem;
	}
	
	
	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(LayerItem layerItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(layerItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.name.correctly"));//TODO:lang
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
		Map<String, Object> layersForLog = new TreeMap<>();
		Map<String, Object> userLayerPermissionsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		LogTypeE logTypePermission;
		Layer layer;
		UserLayerPermission userLayerPermission;
		
		if (Optional.ofNullable(layerItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.LAYER_EDIT;
			logTypePermission = LogTypeE.USER_LAYER_PERMISSION_EDIT;
			
			layer = layerRepository.findById(layerItem.getId()).orElse(null);			
			
			if (layer == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			// Editlenecek layera izin var mı diye kontrol edilir.
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layer.getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			layersForLog.put("old", LayerItem.newInstanceForLog(layer));
			
		} else {//add
			
			logTypeE = LogTypeE.LAYER_ADD;
			logTypePermission = LogTypeE.USER_LAYER_PERMISSION_ADD;
			
			layer = new Layer();
			layer.setCreateDate(nowT);
			layer.setIsTemp(false);
			layer.setGuid(UUID.randomUUID().toString());			
		}
		
		
		layer.setName(layerItem.getName());
		layer.setState(layerItem.getState() == null ? false : layerItem.getState());
		layer.setUpdateDate(nowT);
		
        Layer saved = layerRepository.save(layer);

		layersForLog.put("new", LayerItem.newInstanceForLog(saved));
		
        Optional<Integer> userLayerPermissionOptional = userLayerPermissionRepository.findLayerIdByUserIdAndLayerId(sessionUser.getUserId(), layer.getId());
		
		userLayerPermission = new UserLayerPermission();
		
		if(!userLayerPermissionOptional.isPresent()) {
			
			userLayerPermission.setLayer(layer);
			userLayerPermission.setUser(user);
			
			UserLayerPermission userLayerPermissionSaved = userLayerPermissionRepository.save(userLayerPermission);
			userLayerPermissionsForLog.put("new", UserLayerPermissionItem.newInstanceForLog(userLayerPermissionSaved));
		}

		dbLogger.log(new Gson().toJson(layersForLog), logTypeE);
		dbLogger.log(new Gson().toJson(userLayerPermissionsForLog), logTypePermission);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer layerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		try {
			
			if (Optional.ofNullable(layerId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			Layer layer = layerRepository.findById(layerId).orElse(null);
			if (layer == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			Set<EventGroup> events = layer.getEventGroups();
			if (!events.isEmpty()) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.primarily.event.group.delete"));//TODO:lang
				return genericResponseItem;
			}
			
			Set<MapAreaGroup> mapAreas = layer.getMapAreaGroups();
			if (!mapAreas.isEmpty()) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.primarily.map.area.delete"));//TODO:lang
				return genericResponseItem;
			}
						
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
						
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layer.getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			Set<Alert> alerts = layer.getAlerts();
			if (!alerts.isEmpty()) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription("Lütfen önce olay ile ilişkili alarm kriterlerini siliniz");//TODO:lang
				return genericResponseItem;
			}
			
			Set<LayerExport> layerExports = layer.getLayerExports();
			if (!layerExports.isEmpty()) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription("Lütfen önce olay ile ilişkili katman dışa aktarım işlemlerini siliniz");//TODO:lang
				return genericResponseItem;
			}
			
			List<UserLayerPermission> userLayerPermission = userLayerPermissionRepository.findByLayerId(layerId);
			
			//deleting permissions and layer
			transactionalPermissionService.deleteLayer(layerId);	
			
			if (userLayerPermission != null) {
				
				for (UserLayerPermission tempuserLayerPermission: userLayerPermission) {
					
					Map<String, Object> userLayerPermissionsForLog = new TreeMap<>();
					userLayerPermissionsForLog.put("deleted", UserLayerPermissionItem.newInstanceForLog(tempuserLayerPermission));
					
					dbLogger.log(new Gson().toJson(userLayerPermissionsForLog), LogTypeE.USER_LAYER_PERMISSION_DELETE);
				}
			}
			
			Map<String, Object> layerForLog = new TreeMap<>();
			layerForLog.put("deleted", LayerItem.newInstanceForLog(layer));
			
			dbLogger.log(new Gson().toJson(layerForLog), LogTypeE.LAYER_DELETE);
			
		} catch (Exception e) {
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.delete.layer.error"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
}
