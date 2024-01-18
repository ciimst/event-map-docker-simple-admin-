package com.imst.event.map.admin.db.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.repositories.ProfilePermissionRepository;
import com.imst.event.map.admin.db.repositories.ProfileRepository;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.ProfilePermission;

@Service
public class TransactionalProfileService {
	
	@Autowired
	private ProfilePermissionRepository profilePermissionRepository;
	@Autowired
	private ProfileRepository profileRepository;
	
	
	@Transactional(transactionManager = "masterTransactionManager")
	public Profile saveProfile(Profile profile, Set<ProfilePermission> profilePermissionList) {
		
		
		List<Profile> defaultProfileList = profileRepository.findAllByIsDefaultIsTrue();
		Profile defaultProfile = defaultProfileList.size() > 0 ? defaultProfileList.get(0) :  null;
		
		if(profile.getIsDefault() == true && defaultProfile != null && !defaultProfile.getId().equals(profile.getId())) {
			
			defaultProfile.setIsDefault(false);
			profileRepository.save(defaultProfile);
		}
		
		Profile save = profileRepository.save(profile);
		
		List<ProfilePermission> allByProfileId = profilePermissionRepository.findAllByProfileId(profile.getId());
		
		profilePermissionRepository.deleteAll(allByProfileId);
		
		profilePermissionRepository.saveAll(profilePermissionList);
		
		return save;
	}
	
	@Transactional(transactionManager = "masterTransactionManager")
	public void deleteProfile(Integer profileId) {
		
		List<ProfilePermission> allByProfileId = profilePermissionRepository.findAllByProfileId(profileId);
		
		profilePermissionRepository.deleteAll(allByProfileId);
		
		profileRepository.deleteById(profileId);
		
	}
}
