package com.imst.event.map.admin.controllers.admin;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.ProfileProjection;
import com.imst.event.map.admin.db.repositories.ProfileRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.specifications.UserSpecification;
import com.imst.event.map.admin.security.ldap.LdapConnectionProvider;
import com.imst.event.map.admin.security.ldap.UserSearchItem;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.UserItem;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private DBLogger dbLogger;
	
	@Autowired
	private MasterDao masterDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "")
	@RequestMapping(value = "/ldapInfo", method = RequestMethod.GET)
	@ResponseBody
	public List<String> ldapInfo( @RequestParam(name="user") String user) throws NamingException, IOException {
		
		List<UserSearchItem> attributesList = LdapConnectionProvider.searchUser(user);
		return attributesList.stream().map(m -> m.getValue()).collect(Collectors.toList());
	}
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "")
	@RequestMapping(value = "/getFullName", method = RequestMethod.GET)
	@ResponseBody
	public String getFullName( @RequestParam(name="user") String user) throws NamingException, IOException {
		
		List<UserSearchItem> attributesList = LdapConnectionProvider.getFullName(user);
		return attributesList.stream().map(m -> m.getFullname()).collect(Collectors.toList()).get(0);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LIST')")
	@Operation(summary = "Sayfalama")
	@RequestMapping({""})
	public ModelAndView getUserPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/user");
		
		List<ProfileProjection> profiles = profileRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("profiles", profiles);
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<UserItem> data(UserItem userItem, @DatatablesParams DatatablesCriterias criterias) {
		
		PageRequest pageRequest = criterias.getPageRequest(UserItem.class);
		
		UserSpecification userSpecification = new UserSpecification(userItem);
		Page<UserItem> userItems = masterDao.findAll(userSpecification, pageRequest);
		
		DataSet<UserItem> dataSet = new DataSet<>(userItems.getContent(),  0L, userItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "Güncelleme")
	@RequestMapping(value = "/edit")
	public GenericResponseItem userData(Integer userId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (Optional.ofNullable(userId).orElse(0) < 1) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.not.found"));
			return genericResponseItem;
		}
		
		User userToEdit = userRepository.findById(userId).orElse(null);
		if (userToEdit == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.not.found"));
			return genericResponseItem;
		}
		
		genericResponseItem.setData(new UserItem(userToEdit));
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(UserItem userItem) {
		
		GenericResponseItem genericResponseItem = isUserValid(userItem);
		
		if (!genericResponseItem.isState()) {
			return genericResponseItem;
		}
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> usersForLog = new TreeMap<>();
		LogTypeE logTypeE;
		User user;
		
		if (Optional.ofNullable(userItem.getId()).orElse(0) > 0) {//edit
			
			logTypeE = LogTypeE.USER_EDIT;
			
			user = userRepository.findById(userItem.getId()).orElse(null);
			
			if (user == null) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			if (!user.getUsername().equals(userItem.getUsername())) {
				
				if (checkIfUserExist(userItem.getUsername())) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.being.used"));//TODO:lang
					return genericResponseItem;
				}
			}
			
			usersForLog.put("old", UserItem.newInstanceForLog(user));
			
			if (!StringUtils.isBlank(userItem.getPassword())) {
				
				user.setPassword(passwordEncoder.encode(userItem.getPassword()));
			}
			
			
		} else {//add
			
			logTypeE = LogTypeE.USER_ADD;
			
			if (checkIfUserExist(userItem.getUsername())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.being.used"));//TODO:lang
				return genericResponseItem;
			}
			
			if (StringUtils.isBlank(userItem.getPassword()) && !userItem.getIsDbUser()) {
				
				genericResponseItem.setState(false);
				//TODO:lang
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.new.user.password"));
				return genericResponseItem;
			}
			
			user = new User();
			user.setPassword(passwordEncoder.encode(userItem.getPassword()));
			user.setCreateDate(nowT);
		}
		
		Profile profile = profileRepository.findById(userItem.getProfileId()).orElse(null);
		if (profile == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.please.profile.select"));//TODO:lang
			return genericResponseItem;
		}
		
		user.setUsername(userItem.getUsername());
		user.setName(userItem.getName());
		user.setProfile(profile);
		user.setState(userItem.getState() == null ? false : userItem.getState());
		user.setUpdateDate(nowT);
		user.setProviderUserId(userItem.getProviderUserId());
		
		user.setIsDbUser(userItem.getIsDbUser() == null ? false : userItem.getIsDbUser());
		
		User saved = userRepository.save(user);
		
		usersForLog.put("new", UserItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(usersForLog), logTypeE);
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer userId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(userId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			User user = userRepository.findById(userId).orElse(null);
			if (user == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.user.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			userRepository.deleteById(userId);
			
			Map<String, Object> usersForLog = new TreeMap<>();
			usersForLog.put("deleted", UserItem.newInstanceForLog(user));
			
			dbLogger.log(new Gson().toJson(usersForLog), LogTypeE.USER_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.delete.user.error"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	
	private boolean checkIfUserExist(String username) {
		User userByUsername = userRepository.findByUsername(username);
		return userByUsername != null;
	}
	
	private GenericResponseItem isUserValid(UserItem userItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(userItem.getUsername())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.username.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if (Optional.ofNullable(userItem.getProfileId()).orElse(0) < 1) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.please.profile.select"));//TODO:lang
			return genericResponseItem;
		}
		
		if (StringUtils.isBlank(userItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.name.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		
		return genericResponseItem;
	}
	
}
