package com.imst.event.map.admin.vo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.imst.event.map.admin.datatables.EntitySortKey;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.ProfilePermission;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfileItem {
	
	private Integer id;
	@EntitySortKey("name")
	private String name;
	private String description;
	private String createDate;
	private String updateDate;
	private Boolean isDefault;
	private List<PermissionItem> permissionItemList;
	@EntitySortKey(sortable = false)
	private Long userCount;
	
	public ProfileItem() {
	
	
	}
	
	
	public ProfileItem(Profile profile) {
		this.id = profile.getId();
		this.name = profile.getName();
		this.description = profile.getDescription();
		this.createDate = DateUtils.formatWithCurrentLocale(profile.getCreateDate());
		this.updateDate = DateUtils.formatWithCurrentLocale(profile.getUpdateDate());
		this.isDefault = profile.getIsDefault();
	}
	
	
	public static ProfileItem newInstanceForLog(Profile profile) {
		
		Set<ProfilePermission> profilePermissions = profile.getProfilePermissions();
		
		return newInstanceForLog(profile, profilePermissions);
	}
	
	public static ProfileItem newInstanceForLog(Profile profile, Set<ProfilePermission> profilePermissions) {
		
		ProfileItem userItem = new ProfileItem(profile);
		
		List<PermissionItem> collect = profilePermissions.parallelStream()
				.map(profilePermission -> {
					Integer id = profilePermission.getPermission().getId();
					PermissionItem permissionItem = new PermissionItem();
					permissionItem.setId(id);
					return permissionItem;
				})
				.collect(Collectors.toList());
		
		userItem.setPermissionItemList(collect);
		
		return userItem;
	}
}
