package com.imst.event.map.admin.controllers.admin.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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
import com.imst.event.map.admin.db.projections.UserProjection;
import com.imst.event.map.admin.db.repositories.UserGroupIdRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.specifications.UserGroupIdSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.UserGroupIdItem;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/usergroupid")
public class UserGroupIdController {

	@Autowired
	private UserGroupIdRepository userGroupIdRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_LIST')")
	@Operation(summary = "UserGroupId sayfalama")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/user_group_id");
		
		List<UserProjection> users = userRepository.findAllProjectedByOrderByUsername();
		modelAndView.addObject("users", users);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<UserGroupIdItem> data(UserGroupIdItem userGroupIdItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(UserGroupIdItem.class);
		
		UserGroupIdSpecification userGroupIdSpecification = new UserGroupIdSpecification(userGroupIdItem);
		Page<UserGroupIdItem> userGroupIdItems = masterDao.findAll(userGroupIdSpecification, pageRequest);
		
		DataSet<UserGroupIdItem> dataSet = new DataSet<>(userGroupIdItems.getContent(), 0L, userGroupIdItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(UserGroupIdItem userGroupIdItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
				
		
		if (userGroupIdItem.getGroupId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.group.id.not.null"));//TODO:lang
			return genericResponseItem;
		}

		
		if (userGroupIdItem.getUserId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.select"));//TODO:lang
			return genericResponseItem;
		}
		
		
		
		
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Map<String, Object> userGroupIdsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		UserGroupId userGroupId;
					
			logTypeE = LogTypeE.USER_GROUP_ID_ADD;
			
			userGroupId = new UserGroupId();
		
		if (checkIfUserExist(userGroupIdItem.getUserId(), userGroupIdItem.getGroupId())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.previous.save"));//TODO:lang
			return genericResponseItem;
		}
		
		userGroupId.setGroupId(userGroupIdItem.getGroupId());
		
		
		User user = new User();
		user.setId(userGroupIdItem.getUserId());
		userGroupId.setUser(user);
		
		UserGroupId saved = userGroupIdRepository.save(userGroupId);
		
		userGroupIdsForLog.put("new", UserGroupIdItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(userGroupIdsForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_GROUP_ID_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer userGroupIdId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(userGroupIdId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userGroupId.not.found"));
				return genericResponseItem;
			}
			
			UserGroupId userGroupId = userGroupIdRepository.findById(userGroupIdId).orElse(null);
			if (userGroupId == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userGroupId.not.found"));
				return genericResponseItem;
			}
			
			
			userGroupIdRepository.deleteById(userGroupIdId);
			
			Map<String, Object> userGroupIdsForLog = new TreeMap<>();
			userGroupIdsForLog.put("deleted", UserGroupIdItem.newInstanceForLog(userGroupId));
			
			dbLogger.log(new Gson().toJson(userGroupIdsForLog), LogTypeE.USER_GROUP_ID_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	private boolean checkIfUserExist(Integer userId, Integer groupId) {
		UserGroupId userGroupId = userGroupIdRepository.findByUserIdAndGroupId(userId, groupId);
		return userGroupId != null;
	}
}
