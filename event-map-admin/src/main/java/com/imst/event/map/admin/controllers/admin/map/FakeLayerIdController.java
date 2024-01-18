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
import com.imst.event.map.admin.db.repositories.FakeLayerIdRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.specifications.FakeLayerIdSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.FakeLayerIdItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.hibernate.entity.FakeLayerId;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/fake-layer-id")
public class FakeLayerIdController {
	
	@Autowired
	private LayerRepository layerRepository;
	

	@Autowired
	private FakeLayerIdRepository fakeLayerIdRepository;
	@Autowired
	private UserPermissionService userPermissionService;
	
	
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	
	public static int saveLayerExportId = 0;
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_LIST')")
	@Operation(summary = "Sayfalama. Örn:/api/layer/page?page=0&size=10&sort=name,desc")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/fake_layer_id");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerPermissionIdList = permissionWrapperItem.getUserLayerPermissionItemIds();
						
						
		List<LayerProjection> layersFiltered = layerRepository.findAllProjectedByIdInOrderByName(userLayerPermissionIdList);
		
		modelAndView.addObject("layers", layersFiltered);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<FakeLayerIdItem> data(FakeLayerIdItem fakeLayerIdItem, @DatatablesParams DatatablesCriterias criterias) {
		
		PageRequest pageRequest = criterias.getPageRequest(FakeLayerIdItem.class);
		
		FakeLayerIdSpecification layerSpecification = new FakeLayerIdSpecification(fakeLayerIdItem);
		Page<FakeLayerIdItem> layerItems = masterDao.findAll(layerSpecification, pageRequest);
		
		DataSet<FakeLayerIdItem> dataSet = new DataSet<>(layerItems.getContent(), 0L, layerItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}

	

	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_MANAGE')")
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
	
	
	
	
	@PreAuthorize("hasRole('ROLE_FAKE_LAYER_ID_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer id) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		try {
			
			if (Optional.ofNullable(id).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			FakeLayerId fakeLayerId = fakeLayerIdRepository.findById(id).orElse(null);
			if (fakeLayerId == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			
			fakeLayerIdRepository.deleteById(id);

			Map<String, Object> layerForLog = new TreeMap<>();
			layerForLog.put("deleted", FakeLayerIdItem.newInstanceForLog(fakeLayerId));
			
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
