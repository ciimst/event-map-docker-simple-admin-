package com.imst.event.map.admin.security.ldap;


import java.util.HashSet;
import java.util.Set;

//import com.aa.arge.socialmedia.db.jpa.repositories.LdapGroupRepository;
//import com.aa.arge.socialmedia.db.jpa.repositories.ProfileRepository;
//import com.aa.arge.socialmedia.db.jpa.repositories.UserRepository;
//import com.aa.arge.socialmedia.helper.DateUtils;
//import com.aa.arge.socialmedia.helper.Logger;
//import com.aa.arge.socialmedia.helper.Statics;
//import com.aa.arge.socialmedia.hibernate.entity.LdapGroup;
//import com.aa.arge.socialmedia.hibernate.entity.Permission;
//import com.aa.arge.socialmedia.hibernate.entity.Profile;
//import com.aa.arge.socialmedia.hibernate.entity.User;
//import com.aa.arge.socialmedia.hibernate.entity.UserExtraPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="ldap.user-search")
public class UserLdapAuthoritiesPopulator {
	
	
	public static final String DEFAULT_USER_ROLE = "DEFAULT_ROLE_USED_FOR_NOTHING";
	
	@Autowired
	private LdapContextSource contextSource;
	
	private String base;
	private String filter;
	private String defaultRole;
	private Boolean searchSubtree;
	private Boolean resultException;
	
	public Populator build() {
		
		Populator populator = new Populator(contextSource, base);
		populator.setDefaultRole(DEFAULT_USER_ROLE);
		populator.setSearchSubtree(searchSubtree);
		populator.setIgnorePartialResultException(resultException);
		return populator;
	}
	
	public class Populator  extends DefaultLdapAuthoritiesPopulator {
	
		public Populator(ContextSource contextSource, String groupSearchBase) {
			super(contextSource, groupSearchBase);
		}
		
		
		@Override
		public Set<GrantedAuthority> getGroupMembershipRoles(String userDn, String username) {

			Set<GrantedAuthority> groupMembershipRoles = super.getGroupMembershipRoles(userDn, username);
			return groupMembershipRoles;
		}
		
		@Override
		protected Set<GrantedAuthority> getAdditionalRoles(DirContextOperations userData, String username) {
			
			Set<GrantedAuthority> roles = new HashSet<>();
			
			return roles;
		}
		
	}
	
	
	public String getBase() {
		return base;
	}
	
	public void setBase(String base) {
		this.base = base;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getDefaultRole() {
		return defaultRole;
	}
	
	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}
	
	public Boolean getSearchSubtree() {
		return searchSubtree;
	}
	
	public void setSearchSubtree(Boolean searchSubtree) {
		this.searchSubtree = searchSubtree;
	}
	
	public Boolean getResultException() {
		return resultException;
	}
	
	public void setResultException(Boolean resultException) {
		this.resultException = resultException;
	}
}
