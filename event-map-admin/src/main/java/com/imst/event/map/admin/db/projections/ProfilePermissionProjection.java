package com.imst.event.map.admin.db.projections;

public interface ProfilePermissionProjection {
	
	Integer getId();
	String getProfileName();
	String getProfileDescription();
	String getPermissionDescription();
	String getPermissionGroupName();
}
