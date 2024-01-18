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
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.repositories.UserUserIdRepository;
import com.imst.event.map.admin.db.specifications.UserUserIdSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.UserUserIdItem;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserUserId;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/useruserid")
public class UserUserIdController {

	@Autowired
	private UserUserIdRepository userUserIdRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_LIST')")
	@Operation(summary = "UserUserId sayfalama. Örn:/api/userGroupId/page?page=0&size=10&sort=name,desc")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/user_user_id");
		
		List<UserProjection> users = userRepository.findAllProjectedByOrderByUsername();
		modelAndView.addObject("users", users);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<UserUserIdItem> data(UserUserIdItem userUserIdItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(UserUserIdItem.class);
		
		UserUserIdSpecification userUserIdSpecification = new UserUserIdSpecification(userUserIdItem);
		Page<UserUserIdItem> userUserIdItems = masterDao.findAll(userUserIdSpecification, pageRequest);
		
		DataSet<UserUserIdItem> dataSet = new DataSet<>(userUserIdItems.getContent(), 0L, userUserIdItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(UserUserIdItem userUserIdItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
				
		if (userUserIdItem.getUserId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.id.not.null"));//TODO:lang
			return genericResponseItem;
		}

		
		if (userUserIdItem.getFk_userId() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.select"));//TODO:lang
			return genericResponseItem;
		}
		
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Map<String, Object> userUserIdsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		UserUserId userUserId;
					
		logTypeE = LogTypeE.USER_USER_ID_ADD;
			
		userUserId = new UserUserId();					
	
		userUserId.setUserId(userUserIdItem.getUserId());
				
		User user = new User();
		user.setId(userUserIdItem.getFk_userId());
		userUserId.setUser(user);
		

		if (checkIfUserExist(userUserId.getUser(), userUserIdItem.getUserId())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.previous.save"));//TODO:lang
			return genericResponseItem;
		}
		
		UserUserId saved = userUserIdRepository.save(userUserId);
		
		userUserIdsForLog.put("new", UserUserIdItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(userUserIdsForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_USER_ID_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer userUserIdId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(userUserIdId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userUserId.not.found"));
				return genericResponseItem;
			}
			
			UserUserId userUserId = userUserIdRepository.findById(userUserIdId).orElse(null);
			if (userUserId == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.userUserId.not.found"));
				return genericResponseItem;
			}
			
			
			userUserIdRepository.deleteById(userUserIdId);
			
			Map<String, Object> userUserIdsForLog = new TreeMap<>();
			userUserIdsForLog.put("deleted", UserUserIdItem.newInstanceForLog(userUserId));
			
			dbLogger.log(new Gson().toJson(userUserIdsForLog), LogTypeE.USER_USER_ID_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	private boolean checkIfUserExist(User fk_userId, Integer userId) {

		UserUserId userIdAndUserUserId = userUserIdRepository.findByUserAndUserId(fk_userId, userId);
		return userIdAndUserUserId != null;
	}
}
