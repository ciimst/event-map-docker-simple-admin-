package com.imst.event.map.admin.controllers.api;


import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.UserProjection;
import com.imst.event.map.admin.db.repositories.ProfileRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.specifications.api.ApiUserSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.UserItem;
import com.imst.event.map.admin.vo.api.ApiUserItem;
import com.imst.event.map.admin.vo.api.ApiUserSearchItem;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/users")
public class UserControllerApi {
	
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
	@Operation(summary = "Arama")
	@GetMapping(value = "/search")
	public Page<ApiUserSearchItem> getUsers(ApiUserSearchItem userItem, @PageableDefault Pageable pageable) {
		
		Page<ApiUserSearchItem> userItems;
		try {
			ApiUserSpecification userSpecification = new ApiUserSpecification(userItem);
			
			userItems = masterDao.findAll(userSpecification, pageable);
		} catch (Exception e) {
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return userItems;
	}
	
	@Operation(summary = "Sayfalama. Örn:/api/users/page?page=0&size=10&sort=name,desc")
	@PreAuthorize("hasRole('ROLE_USER_LIST')")
	@GetMapping(value = "/page")
	public Page<UserProjection> getPage(@PageableDefault Pageable pageable) {
		
		
		Page<UserProjection> users;
		try {
			
			users = userRepository.findAllProjectedByOrderByName(pageable);
			
		} catch (Exception e) {
			
			log.error(e);
			throw new ApiException("Query cannot be executed. Please try again. If the error persists, please contact system administrator.");
		}
		
		return users;
	}
	
	
	@PreAuthorize("hasRole('ROLE_USER_LIST')")
	@Operation(summary = "Tekil.")
	@GetMapping(value = "/{id}")
	public UserProjection getById(@PathVariable Integer id) {
		
		UserProjection userProjection = null;
		try {
			userProjection = userRepository.findProjectedById(id);
		} catch (Exception e) {
			log.error(e);
		}
		
		if (userProjection == null) {
			throw new ApiException("Not found.");
		}
		
		return userProjection;
	}
	
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "Sil")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		
		try {
			
			userRepository.deleteById(id);
			
			Map<String, Object> usersForLog = new TreeMap<>();
			User user = new User();
			user.setId(id);
			usersForLog.put("deleted", user);
			
			dbLogger.log(new Gson().toJson(usersForLog), LogTypeE.USER_DELETE);
			
		} catch (Exception e) {
			
			ApiException apiException = new ApiException("User id not found: " + id, HttpStatus.NO_CONTENT);
			log.error(apiException);
			throw apiException;
		}
		
		return ResponseEntity.ok().build();
	}
	
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "Yeni kaydet / güncelle")
	@PostMapping(value = {""})
	public ResponseEntity<?> saveOrUpdate(@RequestBody ApiUserItem apiUserItem) {
		
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> usersForLog = new TreeMap<>();
		LogTypeE logTypeE;
		User user;
		if (apiUserItem.getId() != null) {//update
			
			logTypeE = LogTypeE.USER_EDIT;
			
			user = userRepository.findById(apiUserItem.getId()).orElse(null);
			if (user == null) {
				throw new ApiException("User not found.");
			}
			
			if (!user.getUsername().equals(apiUserItem.getUsername())) {
				
				if (checkIfUserExist(apiUserItem.getUsername())) {
					
					throw new ApiException("Username is in use.");
				}
			}
			
			usersForLog.put("old", UserItem.newInstanceForLog(user));
			
		} else {
			
			logTypeE = LogTypeE.USER_ADD;
			
			if (checkIfUserExist(apiUserItem.getUsername())) {
				throw new ApiException("Username is in use.");
			}
			
			String password = apiUserItem.getPassword();
			if (StringUtils.isBlank(password)) {
				
				throw new ApiException("Password is missing.");
			}
			
			user = new User();
			user.setPassword(passwordEncoder.encode(password));
			user.setCreateDate(nowT);
		}
		
		if (StringUtils.isBlank(apiUserItem.getUsername())) {
			
			throw new ApiException("Username is missing.");
		}
		
		if (StringUtils.isBlank(apiUserItem.getName())) {
			
			throw new ApiException("Name is missing.");
		}
		
		Profile profile = profileRepository.findById(apiUserItem.getProfileId()).orElse(null);
		if (profile == null) {
			throw new ApiException("Profile not found.");
		}
		
		boolean state = apiUserItem.getState() == null ? true : apiUserItem.getState();
		boolean isDbUser = apiUserItem.getIsDbUser() == null ? false : apiUserItem.getIsDbUser();
		
		user.setProfile(profile);
		user.setUsername(apiUserItem.getUsername());
		user.setName(apiUserItem.getName());
		user.setState(state);
		user.setUpdateDate(nowT);
		user.setIsDbUser(isDbUser);
		user.setProviderUserId(apiUserItem.getProviderUserId());
		
		User saved = userRepository.save(user);
		
		usersForLog.put("new", UserItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(usersForLog), logTypeE);
		
		String location = "/api/users/" + saved.getId();
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", saved.getId());
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	@PreAuthorize("hasRole('ROLE_USER_MANAGE')")
	@Operation(summary = "Kısmi güncelleme")
	@PatchMapping(value = {"/{id}"})
	public ResponseEntity<?> update(@RequestBody ApiUserItem apiUserItem, @PathVariable Integer id) {
		
		
		User user = userRepository.findById(apiUserItem.getId()).orElse(null);
		if (user == null) {
			throw new ApiException("User not found.");
		}
		
		if (!user.getUsername().equals(apiUserItem.getUsername())) {
			
			if (checkIfUserExist(apiUserItem.getUsername())) {
				
				throw new ApiException("Username is in use.");
			}
		}
		
		
		Timestamp nowT = DateUtils.nowT();
		Map<String, Object> usersForLog = new TreeMap<>();
		usersForLog.put("old", UserItem.newInstanceForLog(user));
		
		if (!StringUtils.isBlank(apiUserItem.getPassword())) {
			
			user.setPassword(passwordEncoder.encode(apiUserItem.getPassword()));
		}
		
		if (!StringUtils.isBlank(apiUserItem.getUsername())) {
			
			user.setUsername(apiUserItem.getUsername());
		}
		
		if (!StringUtils.isBlank(apiUserItem.getName())) {
			
			user.setName(apiUserItem.getName());
		}
		
		if (apiUserItem.getState() != null) {
			
			user.setState(apiUserItem.getState());
		}
		
		if (apiUserItem.getProfileId() != null) {
			
			Profile profile = profileRepository.findById(apiUserItem.getProfileId()).orElse(null);
			if (profile == null) {
				throw new ApiException("Profile not found.");
			}
			
			user.setProfile(profile);
		}
			
		user.setProviderUserId(apiUserItem.getProviderUserId());
		user.setUpdateDate(nowT);
		User saved = userRepository.save(user);
		
		usersForLog.put("new", UserItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(usersForLog), LogTypeE.USER_EDIT);
		
		String location = "/api/users/" + id;
		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("id", id);
		responseBody.put("location", location);
		return ResponseEntity.ok().header("Location", location).body(responseBody);
	}
	
	
	private boolean checkIfUserExist(String username) {
		User userByUsername = userRepository.findByUsername(username);
		return userByUsername != null;
	}
	
}
