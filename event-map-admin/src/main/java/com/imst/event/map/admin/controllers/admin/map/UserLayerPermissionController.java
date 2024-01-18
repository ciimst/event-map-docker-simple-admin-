package com.imst.event.map.admin.controllers.admin.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import com.imst.event.map.admin.db.projections.UserLayerPermissionProjection2;
import com.imst.event.map.admin.db.projections.UserProjection;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.specifications.UserLayerPermissionSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.UserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/userlayerpermission")
public class UserLayerPermissionController {

	@Autowired 
	private UserLayerPermissionRepository userLayerPermissionRepository;
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_LIST')")
	@Operation(summary = "")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/user_layer_permission");
		
		List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layers);
		
		List<UserProjection> users = userRepository.findAllProjectedByOrderByUsername();     
		modelAndView.addObject("users", users);
		
		return modelAndView;
	}
	
	@RequestMapping(value="/layerFilter")
	public GenericResponseItem layerFilter(Integer userId){
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		
		if(userId != null) {
			User user = new User();
			user.setId(userId);		
									
				
		     	List<UserLayerPermissionProjection2> userLayerPermissionItemList = userLayerPermissionRepository.findAllProjectedByUser(user);
				
		     	List<Integer> userLayerPermissionIdList = userLayerPermissionItemList.stream().map(UserLayerPermissionProjection2::getLayerId).collect(Collectors.toList());
		     	userLayerPermissionIdList.add(0); // liste bos iken listenin tamamnini getirmesi icin
		     	
				List<LayerProjection> layerList = layerRepository.findAllProjectedByIdNotInOrderByName(userLayerPermissionIdList);    
				
				genericResponseItem.setData(layerList);
				
				return genericResponseItem;
			}
			
		return null;
		
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<UserLayerPermissionItem> data(UserLayerPermissionItem userLayerPermissionItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(UserLayerPermissionItem.class);
		
		UserLayerPermissionSpecification userLayerPermissionSpecification = new UserLayerPermissionSpecification(userLayerPermissionItem);
		Page<UserLayerPermissionItem> userLayerPermissionItems = masterDao.findAll(userLayerPermissionSpecification, pageRequest);
		 
	  //List<UserLayerPermissionItem> userLayerPermissionItems3 = userLayerPermissionRepository.findAllByLayer(userLayerPermissionItem.getLayerId(),userLayerPermissionItem.getUserId());
		
/*
	    List<UserLayerPermissionProjection> userLayerPermissionProjectionList = userLayerPermissionRepository.deneme(userLayerPermissionItem.getLayerId(),userLayerPermissionItem.getUserId());
	    
	    List<UserLayerPermissionItem> list = new ArrayList<>();
	    
	    UserLayerPermissionItem newUserLayerPermissionItem2 = new UserLayerPermissionItem();
	    
	    newUserLayerPermissionItem2.setId(1);
	    newUserLayerPermissionItem2.setLayerId(5);
	    newUserLayerPermissionItem2.setUserId(15);
	    
	    
	    userLayerPermissionProjectionList.forEach(item -> {
	    	
	    	System.out.println(item.getFk_userId());
	    	UserLayerPermissionItem newUserLayerPermissionItem = new UserLayerPermissionItem();
	    	newUserLayerPermissionItem.setId(item.getId());
	    	newUserLayerPermissionItem.setLayerId(item.getFk_layerId());
	    	newUserLayerPermissionItem.setUserId(item.getFk_userId());
	    	
	    	list.add(newUserLayerPermissionItem);
	    });
	    */
		
		
	  //Page<UserLayerPermissionItem> userLayerPermissionItems3 = userLayerPermissionRepository.findAllProjectedBy3(45,15);
		
//		DataSet<UserLayerPermissionItem> dataSet = new DataSet<>(list, 0L, (long)list.size());
		
		DataSet<UserLayerPermissionItem> dataSet = new DataSet<>(userLayerPermissionItems.getContent(), 0L, userLayerPermissionItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(UserLayerPermissionItem userLayerPermissionItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
	
		if (userLayerPermissionItem.getUserId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.select"));//TODO:lang
			return genericResponseItem;
		}
		
		if (userLayerPermissionItem.getLayerId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.correctly"));//TODO:lang
			return genericResponseItem;
		}

		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Map<String, Object> userLayerPermissionsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		UserLayerPermission userLayerPermission;
					
			logTypeE = LogTypeE.USER_LAYER_PERMISSION_ADD;
			
			userLayerPermission = new UserLayerPermission();			

		
		if (checkIfUserExist(userLayerPermissionItem.getUserId(), userLayerPermissionItem.getLayerId())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.previous.save"));//TODO:lang
			return genericResponseItem;
		}
		Layer layer = new Layer();
		layer.setId(userLayerPermissionItem.getLayerId());
		userLayerPermission.setLayer(layer);
			
		User user = new User();
		user.setId(userLayerPermissionItem.getUserId());
		userLayerPermission.setUser(user);
	
							
		UserLayerPermission saved = userLayerPermissionRepository.save(userLayerPermission);
		
		userLayerPermissionsForLog.put("new", UserLayerPermissionItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(userLayerPermissionsForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LAYER_PERMISSION_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer userLayerPermissionId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(userLayerPermissionId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userLayerPermission.not.found"));
				return genericResponseItem;
			}
			
			UserLayerPermission userLayerPermission = userLayerPermissionRepository.findById(userLayerPermissionId).orElse(null);
			if (userLayerPermission == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userLayerPermission.not.found"));
				return genericResponseItem;
			}
			
			
			userLayerPermissionRepository.deleteById(userLayerPermissionId);
			
			Map<String, Object> userLayerPermissionsForLog = new TreeMap<>();
			userLayerPermissionsForLog.put("deleted", UserLayerPermissionItem.newInstanceForLog(userLayerPermission));
			
			dbLogger.log(new Gson().toJson(userLayerPermissionsForLog), LogTypeE.USER_LAYER_PERMISSION_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	

	private boolean checkIfUserExist(Integer userId, Integer layerId) {
		UserLayerPermission userLayerId = userLayerPermissionRepository.findByUserIdAndLayerId(userId, layerId);
		return userLayerId != null;
	}
	
	
}
