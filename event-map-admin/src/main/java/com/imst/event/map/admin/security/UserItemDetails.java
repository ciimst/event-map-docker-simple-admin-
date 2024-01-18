package com.imst.event.map.admin.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import com.imst.event.map.admin.vo.UserLayerPermissionItem;

public class UserItemDetails implements UserDetails {

	private static final long serialVersionUID = -587178004402978678L;

	private String password;
    private String displayName;
    private String username;
    private Integer userId;
    private Collection<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean loggedOut;
    private boolean connectionTimedOut;
    private boolean connectionExpired;
    private boolean isDbUser;
    private String excelStateInformation;
    
    private List<Integer> userIdList;
    private List<Integer> groupIdList;
    private List<UserLayerPermissionItem> userLayerPermissionList;
    private List<UserEventGroupPermissionItem> userEventGroupPermissionList;
  
    
   

    public UserItemDetails(Integer userId, String username, String displayName, String password, Collection<GrantedAuthority> authorities,
						   boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired,
						   boolean enabled, boolean isDbUser, List<Integer> groupIdList,List<Integer> userIdList,List<UserLayerPermissionItem> userLayerPermissionList, List<UserEventGroupPermissionItem> userEventGroupPermissionList, String excelStateInformation) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.displayName = displayName;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.isDbUser = isDbUser;
        this.userIdList = userIdList;
        this.groupIdList = groupIdList;
        this.userLayerPermissionList = userLayerPermissionList;
        this.userEventGroupPermissionList = userEventGroupPermissionList;
        this.excelStateInformation = excelStateInformation;
       
    }
    
	public List<UserEventGroupPermissionItem> getUserEventGroupPermissionList() {
		return userEventGroupPermissionList;
	}

	public void setUserEventGroupPermissionList(List<UserEventGroupPermissionItem> userEventGroupPermissionList) {
		this.userEventGroupPermissionList = userEventGroupPermissionList;
	}

	public List<UserLayerPermissionItem> getUserLayerPermissionList() {
		return userLayerPermissionList;
	}

	public void setUserLayerPermissionList(List<UserLayerPermissionItem> userLayerPermissionIdList) {
		this.userLayerPermissionList = userLayerPermissionIdList;
	}


	public List<Integer> getUserIdList() {
		return userIdList;
	}


	public void setUserIdList(List<Integer> userIdList) {
		this.userIdList = userIdList;
	}


	public List<Integer> getGroupIdList() {
		return groupIdList;
	}


	public void setGroupIdList(List<Integer> groupIdList) {
		this.groupIdList = groupIdList;
	}
	
	public String getExcelStateInformation() {
		return excelStateInformation;
	}

	public void setExcelStateInformation(String excelStateInformation) {
		this.excelStateInformation = excelStateInformation;
	}

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    public Integer getUserId() {
        
        return userId;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isLoggedOut() {

        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {

        this.loggedOut = loggedOut;
    }

    public boolean isConnectionTimedOut() {

        return connectionTimedOut;
    }

    public void setConnectionTimedOut(boolean connectionTimedOut) {

        this.connectionTimedOut = connectionTimedOut;
    }

    public boolean isConnectionExpired() {

        return connectionExpired;
    }

    public void setConnectionExpired(boolean connectionExpired) {

        this.connectionExpired = connectionExpired;
    }

    @Override
    public boolean equals(Object obj) {

        return this.getUsername().equals(((UserItemDetails) obj).getUsername());
    }

    @Override
    public int hashCode() {

        return this.getUsername().hashCode();
    }

	public boolean isDbUser() {
		return isDbUser;
	}

	public void setDbUser(boolean isDbUser) {
		this.isDbUser = isDbUser;
	}
	
}
