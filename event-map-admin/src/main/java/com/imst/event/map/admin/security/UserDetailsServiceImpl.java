package com.imst.event.map.admin.security;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.constants.AuthenticationExceptionE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.db.projections.UserPermissionProjection;
import com.imst.event.map.admin.db.repositories.ProfilePermissionRepository;
import com.imst.event.map.admin.db.repositories.ProfileRepository;
import com.imst.event.map.admin.db.repositories.UserGroupIdRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.repositories.UserUserIdRepository;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import  com.imst.event.map.admin.vo.UserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.ProfilePermission;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;
import com.imst.event.map.hibernate.entity.UserUserId;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupIdRepository userGroupIdRepository;
	
	@Autowired
	private UserUserIdRepository userUserIdRepository;
	
	@Autowired
	private ProfilePermissionRepository profilePermissionRepository;
	
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired 
	private UserPermissionService userPermissionService;


	@Transactional(transactionManager = "masterTransactionManager")
	public UserItemDetails loadUserByUsernameKeycloak(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsernameAndIsDbUserAndState(username, false, true);
		
		if (user == null) {
			
			
			List<Profile> defaultProfileList = profileRepository.findByIsDefault(true);
			if(defaultProfileList.size() == 0) {
				return null;
//				throw new UsernameNotFoundException("Profile not found authenticationFailed");
			}
			
			user = new User();
			user.setCreateDate(DateUtils.nowT());
			user.setIsDbUser(false);
			user.setName(username);
			user.setPassword(UUID.randomUUID().toString());
			user.setProfile(defaultProfileList.get(0));
			user.setState(true);
			user.setUsername(username);
			
			userRepository.save(user);
			
		}
				
		List<ProfilePermission> profilePermissionList = profilePermissionRepository.findAllByProfileIdAndPermissionId(user.getProfile().getId(), Statics.adminUserLoginPermissionId);
		
		if(profilePermissionList == null || profilePermissionList.isEmpty()) {
			throw new UsernameNotFoundException("authenticationFailed");
			
		}
		
		return getUserItemDetails(user);
	}
	
	
	@Override
	@Transactional(readOnly = true, transactionManager = "masterTransactionManager")
	public UserItemDetails loadUserByUsername(String username) throws UsernameNotFoundException {
						
		User user = userRepository.findByUsernameAndIsDbUserAndState(username, true, true);
				
		if (user == null) {
			throw new UsernameNotFoundException("authenticationFailed");
		}
		
			
		List<ProfilePermission> profilePermissionList = profilePermissionRepository.findAllByProfileIdAndPermissionId(user.getProfile().getId(), Statics.adminUserLoginPermissionId);
		
		if(profilePermissionList == null || profilePermissionList.isEmpty()) {
			throw new UsernameNotFoundException("authenticationFailed");
			
		}
		
		return getUserItemDetails(user);
	}

	@Transactional( transactionManager = "masterTransactionManager")
	public UserItemDetails loadUserByUsernameLdap(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsernameAndIsDbUserAndState(username, false, true);
				
		if (user == null) {
			
			List<Profile> defaultProfileList = profileRepository.findByIsDefault(true);
			if(defaultProfileList.size() == 0) {
				throw new UsernameNotFoundException("Profile not found authenticationFailed");
			}
			
			user = new User();
			user.setCreateDate(DateUtils.nowT());
			user.setIsDbUser(false);
			user.setName(username);
			user.setPassword(UUID.randomUUID().toString());
			user.setProfile(defaultProfileList.get(0));
			user.setState(true);
			user.setUsername(username);
			
			userRepository.save(user);
			
			
			//throw new UsernameNotFoundException("authenticationFailed");
		}
		
		
		List<ProfilePermission> profilePermissionList = profilePermissionRepository.findAllByProfileIdAndPermissionId(user.getProfile().getId(), Statics.adminUserLoginPermissionId);
		
		if(profilePermissionList == null || profilePermissionList.isEmpty()) {
			throw new UsernameNotFoundException("authenticationFailed");
			
		}
		
		return getUserItemDetails(user);
	}
	
	public UserItemDetails getUserItemDetails(User user) {
		
		try {
			
			String username = user.getUsername();
			String displayName = user.getName();
			String password = user.getPassword();
			String excelStateInformation = "";
			
			List<UserPermissionProjection> userPermissions = userRepository.findUserPermissions(user.getId());
			List<GrantedAuthority> auths = userPermissions.stream().map(profilePermission -> new SimpleGrantedAuthority(profilePermission.getName()))
					.collect(Collectors.toList());
			
			
			List<UserGroupId> userGroupIdList = userGroupIdRepository.findAllByUser(user);
			List<Integer> groupIdList = userGroupIdList.stream().map(UserGroupId::getGroupId).collect(Collectors.toList());
			groupIdList.add(0);
						
			List<UserUserId> userUserIdList = userUserIdRepository.findAllByUser(user);
			List <Integer> userIdList = userUserIdList.stream().map(UserUserId::getUserId).collect(Collectors.toList());
			userIdList.add(0);
			
			
//			List<UserLayerPermissionItem> userLayerPermissionList = userLayerPermissionRepository.findAllByUser(user); // only permissioned layers
			
//			List<UserEventGroupPermissionItem> userEventGroupPermissionItemList = userEventGroupPermissionRepository.findAllByUser(user);
						
//			List<Integer> userLayerPermissionIdList = userLayerPermissionList.stream().map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());			
			
//			List<Integer> userEventGroupPermissionIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
			
			List<UserEventGroupPermissionItem> userEventGroupPermissionItemList = permissionWrapperItem.getUserEventGroupPermissionItemList();
			
			List<UserLayerPermissionItem> userLayerPermissionItemList = permissionWrapperItem.getUserLayerPermissionItemList();
			
			boolean enabled = true;
			boolean accountNonExpired = true;
			boolean credentialsNonExpired = true;
			boolean accountNonLocked = !auths.isEmpty();
			
			
			
			UserItemDetails userItemDetails = new UserItemDetails(user.getId(), username, displayName, password, auths, 
					accountNonExpired, accountNonLocked, credentialsNonExpired, enabled, user.getIsDbUser(),groupIdList,userIdList, userLayerPermissionItemList, userEventGroupPermissionItemList, excelStateInformation);
			return userItemDetails;
			
		} catch (Exception e) {
			
			log.error(e);
			throw new BadCredentialsException(AuthenticationExceptionE.LOCKED.name());
		}
	}
}
