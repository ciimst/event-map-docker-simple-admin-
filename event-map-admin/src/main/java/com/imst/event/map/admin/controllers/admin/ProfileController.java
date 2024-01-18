package com.imst.event.map.admin.controllers.admin;


import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.repositories.ProfilePermissionRepository;
import com.imst.event.map.admin.db.repositories.ProfileRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.services.TransactionalProfileService;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionItem;
import com.imst.event.map.admin.vo.ProfileItem;
import com.imst.event.map.hibernate.entity.Permission;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.ProfilePermission;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/profile")
public class ProfileController {
	
	
	@Autowired
	private ProfilePermissionRepository profilePermissionRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TransactionalProfileService transactionalProfileService;
	@Autowired
	private DBLogger dbLogger;
	
	
	@PreAuthorize("hasRole('ROLE_PROFILE_LIST')")
	@Operation(summary = "")
	@RequestMapping({""})
	public ModelAndView getProfilePage() {
		
		ModelAndView model = new ModelAndView("page/admin/user_profile");
		
		List<Profile> defaultProfileList = profileRepository.findAllByIsDefaultIsTrue();
		Profile defaultProfile = defaultProfileList.size() > 0 ? defaultProfileList.get(0) :  new Profile();
		
		model.addObject("defaultProfileId", defaultProfile.getId());

		return model;
	}
	
	@PreAuthorize("hasRole('ROLE_PROFILE_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<ProfileItem> data(@DatatablesParams DatatablesCriterias criterias) {
		
		PageRequest pageRequest = criterias.getPageRequest(ProfileItem.class);
		
		Page<Profile> profiles = profileRepository.findAll(pageRequest);
		if (profiles == null || profiles.isEmpty()) {
			
			DataSet<ProfileItem> dataSet = new DataSet<>(new ArrayList<ProfileItem>(), 0L, 0L);
			return DatatablesResponse.build(dataSet, criterias);
		}
		
		List<ProfileItem> profileItems = profiles.stream()
				.map(profile -> {
					ProfileItem profileItem = new ProfileItem(profile);
					
					User user = new User();
					Profile profile1 = new Profile();
					profile1.setId(profile.getId());
					user.setProfile(profile1);
					long count = userRepository.count(Example.of(user));
					profileItem.setUserCount(count);
					
					return profileItem;
				})
				.collect(Collectors.toList());
		
		
		DataSet<ProfileItem> dataSet = new DataSet<>(profileItems, 0L, profiles.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@RequestMapping(value = "/permission/data")
	public GenericResponseItem getProfilePermissions(Integer profileId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "");
		
		if (profileId == null || profileId < 0) {
			
			List<PermissionItem> collect = Statics.permissionList.parallelStream()
					.map(permission -> {
						PermissionItem permissionItem = new PermissionItem(permission);
						permissionItem.setSelected(false);
						return permissionItem;
					})
					.collect(Collectors.toList());
			
			ProfileItem profileItem = new ProfileItem();
			profileItem.setPermissionItemList(collect);
			genericResponseItem.setData(profileItem);
			return genericResponseItem;
		}
		
		Profile profile = profileRepository.findById(profileId).orElse(null);
		if (profile == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.not.found"));//TODO:lang
			return genericResponseItem;
		}
		
		List<ProfilePermission> pPermissions = profilePermissionRepository.findPermissionIdByProfileId(profileId);
		
		List<Integer> permissionIds = pPermissions.stream()
				.map(profilePermission -> profilePermission.getPermission().getId())
				.collect(Collectors.toList());
		
		List<PermissionItem> collect = Statics.permissionList.parallelStream()
				.map(permission -> {
					PermissionItem permissionItem = new PermissionItem(permission);
					permissionItem.setSelected(permissionIds.contains(permission.getId()));
					return permissionItem;
				})
				.collect(Collectors.toList());
		
		collect = collect.stream().sorted(Comparator.comparing(PermissionItem::getDisplayOrder)).collect(Collectors.toList());
		
		ProfileItem profileItem = new ProfileItem(profile);
		profileItem.setPermissionItemList(collect);
		genericResponseItem.setData(profileItem);
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_PROFILE_MANAGE')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(ProfileItem profileItem) {
		
		GenericResponseItem genericResponseItem = isProfileValid(profileItem);
		
		try {
			
			if (!genericResponseItem.isState()) {
				return genericResponseItem;
			}
			
			Timestamp nowT = DateUtils.nowT();
			Map<String, Object> profilesForLog = new TreeMap<>();
			LogTypeE logTypeE;
			Profile profile;
			
			if (Optional.ofNullable(profileItem.getId()).orElse(0) > 0) {//edit
				
				logTypeE = LogTypeE.PROFILE_EDIT;
				
				profile = profileRepository.findById(profileItem.getId()).orElse(null);
				if (profile == null) {
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.not.found"));//TODO:lang
					return genericResponseItem;
				}
				
				profilesForLog.put("old", ProfileItem.newInstanceForLog(profile));
				
			} else {//add
				
				logTypeE = LogTypeE.PROFILE_ADD;
				
				profile = new Profile();
				profile.setCreateDate(nowT);
			}
			
			profile.setName(profileItem.getName());
			profile.setDescription(profileItem.getDescription());
			profile.setUpdateDate(nowT);
			profile.setIsDefault(profileItem.getIsDefault() != null && profileItem.getIsDefault() ? true : false);
			
			Set<ProfilePermission> profilePermissionList = profileItem.getPermissionItemList().stream().map(permissionItem -> {
				ProfilePermission profilePermission = new ProfilePermission();
				
				Permission permission = new Permission();
				permission.setId(permissionItem.getId());
				
				profilePermission.setPermission(permission);
				profilePermission.setProfile(profile);
				return profilePermission;
			}).collect(Collectors.toSet());
		
			
			Profile saved = transactionalProfileService.saveProfile(profile, profilePermissionList);
			
			profilesForLog.put("new", ProfileItem.newInstanceForLog(saved, profilePermissionList));
			
			dbLogger.log(new Gson().toJson(profilesForLog), logTypeE);

		} catch (Exception e) {
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		//Defaul profile var mı kontrolü için.
		//Javascriptde buradaki değere göre uyarı gösteriliyor.
		List<Profile> defaultProfileList = profileRepository.findAllByIsDefaultIsTrue();
		genericResponseItem.setData(defaultProfileList.size()>0);
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_PROFILE_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer profileId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(profileId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			Profile profile = profileRepository.findById(profileId).orElse(null);
			if (profile == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.not.found"));//TODO:lang
				return genericResponseItem;
			}
			
			//silinmeden önce alıyoruz instanceı çünkü lazy işlem var içerde
			//ileride sorun çıkarırsa ayrıca bi select çekilebilir permissionlar için
			ProfileItem profileItem = ProfileItem.newInstanceForLog(profile);
			
			transactionalProfileService.deleteProfile(profileId);
			
			Map<String, Object> profilesForLog = new TreeMap<>();
			profilesForLog.put("deleted", profileItem);
			
			dbLogger.log(new Gson().toJson(profilesForLog), LogTypeE.PROFILE_DELETE);
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.user.have"));
			return genericResponseItem;
		}
		
		//Defaul profile var mı kontrolü için.
		//Javascriptde buradaki değere göre uyarı gösteriliyor.
		List<Profile> defaultProfileList = profileRepository.findAllByIsDefaultIsTrue();
		genericResponseItem.setData(defaultProfileList.size()>0);
		return genericResponseItem;
	}
	
	
	private GenericResponseItem isProfileValid(ProfileItem profileItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if (StringUtils.isBlank(profileItem.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.name.correctly"));//TODO:lang
			return genericResponseItem;
		}
		
		if(profileItem.getPermissionItemList() == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.profile.permission.active"));//TODO:lang
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
}
