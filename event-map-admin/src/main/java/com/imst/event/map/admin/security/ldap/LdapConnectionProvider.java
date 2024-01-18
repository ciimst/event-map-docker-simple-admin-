package com.imst.event.map.admin.security.ldap;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;


public class LdapConnectionProvider {
	
	private static final SearchControls searchControls = new SearchControls();
	private static SpringSecurityLdapTemplate template;
	private static String base;
	
	
	
	public static void init(LdapContextSource contextSource, String searchBase) {
		
		base = searchBase;

		template = new SpringSecurityLdapTemplate(contextSource);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(2000);
		template.setSearchControls(searchControls);
	}
	
	
	public static List<UserSearchItem> getFullName(String user) throws NamingException, IOException {

		String filter = String.format("(& (objectCategory=person) (sAMAccountName=%s))", user);
		return searchUser(user, filter);
	}
	
	

	public static List<UserSearchItem> searchUser(String user) throws NamingException, IOException {

		String filter = String.format("(& (objectCategory=person) (sAMAccountName=%s*))", user);
		return searchUser(user, filter);
	}
	
	private static List<UserSearchItem> searchUser(String user, String filter) throws NamingException, IOException {
		
		List<UserSearchItem> attributesList = template.search(base, filter, new AttributesMapper<UserSearchItem>() {

			@Override
			public UserSearchItem mapFromAttributes(Attributes attributes) throws NamingException {
				Attribute samaccountname = attributes.get("samaccountname");
				if (samaccountname == null) {
					return null;
				}
				
								
				String username = samaccountname.get() + "";
				String displayName = attributes.get("displayname") == null ? "" : attributes.get("displayname").get() + "";
				String fullName = (attributes.get("givenname") == null ? "" : attributes.get("givenname").get() + "") +
						(attributes.get("sn") == null ? "" : " " + attributes.get("sn").get() + "");
				String mail = attributes.get("mail") == null ? "" : attributes.get("mail").get() + "";
				String phone = attributes.get("mobile") == null ? "" : attributes.get("mobile").get() + "";
				
				UserSearchItem userSearchItem = new UserSearchItem();
				userSearchItem.setDisplayName(displayName);
				userSearchItem.setValue(username);
				userSearchItem.setFullname(fullName);
				userSearchItem.setMail(mail);
				userSearchItem.setPhone(phone);
				
				return userSearchItem;
			}
			
		});
		
		return attributesList;
	}
	
//	public static ArrayList<String> getAllUsers() throws NamingException, IOException {
//
//////	connection = (LdapContext) contextSource.getContext(user_dn, password);
//	ArrayList<String> lst = new ArrayList<String>();
//	String searchFilter = "(&(objectClass=user))";
//	String[] reqAtt = {"displayName"};
//	SearchControls controls = new SearchControls();
//	controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//	//controls.setCountLimit(0);
//	controls.setReturningAttributes(reqAtt);
//
//	connection.setRequestControls(new Control[]{ new PagedResultsControl(countLimit, Control.CRITICAL) });
//	NamingEnumeration<SearchResult> users = connection.search("CN=Users", searchFilter, controls);
//
//	SearchResult result = null;
//	
//	while (users.hasMore()) {
//		
//		result = (SearchResult) users.next();
//		Attributes attr = result.getAttributes();
//		lst.add(attr.get("displayName").toString());
//	}
//	
//	return lst;
//
//}
//
//public static ArrayList<String> getAllGroups() throws NamingException, IOException {
//
////	connection = (LdapContext) contextSource.getContext(user_dn, password);
//	ArrayList<String> lst = new ArrayList<String>();
//	String searchFilter = "(&(objectClass=group))";
//	String[] reqAtt = {"cn"};
//	SearchControls controls = new SearchControls();
//	controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//	//controls.setCountLimit(0);
//	controls.setReturningAttributes(reqAtt);
//	connection.setRequestControls(new Control[]{ new PagedResultsControl(countLimit, Control.CRITICAL) });
//	NamingEnumeration<SearchResult> groups = connection.search("CN=Users", searchFilter, controls);
//
//	SearchResult result = null;
//	
//	while (groups.hasMore()) {
//		
//		result = (SearchResult) groups.next();
//		Attributes attr = result.getAttributes();
//		lst.add(attr.get("cn").toString());
//	}
//
//	return lst;
//}
//	
//	public static ArrayList<String> getMemberships(String user) throws NamingException, IOException {
//
////		connection = (LdapContext) contextSource.getContext(user_dn, password);
//		ArrayList<String> lst = new ArrayList<String>();
//		String searchFilter = "(objectClass=group)";
//		String[] reqAtt = {"cn"};
//		SearchControls controls = new SearchControls();
//		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//		//controls.setCountLimit(0);
//		controls.setReturningAttributes(reqAtt);
//		ArrayList<String> groupNames = new ArrayList<String>();
//		connection.setRequestControls(new Control[]{ new PagedResultsControl(countLimit, Control.CRITICAL) });
//		NamingEnumeration<SearchResult> groups = connection.search("CN=Users", searchFilter, controls);
//
//		SearchResult result = null;
//		
//		while (groups.hasMore()) {
//			
//			result = (SearchResult) groups.next();
//			Attributes attr = result.getAttributes();
//			groupNames.add(attr.get("cn").toString().substring(4));
//		}
//		
//		searchFilter = "(&(objectClass=user)";
//
//		for (String s : groupNames) {
//			
//			NamingEnumeration<SearchResult> membership = connection.search("CN=Users", searchFilter + "(sAMAccountName=" + user + ")(memberof=CN=" + s + ",CN=Users,DC=imst,DC=local))", controls);
//
//			result = null;
//			
//			while (membership.hasMore()) {
//				
//				result = (SearchResult) membership.next();
//				Attributes attr = result.getAttributes();
//				lst.add(s);
//			}
//		}
//
//		return lst;
//	}
	
}
