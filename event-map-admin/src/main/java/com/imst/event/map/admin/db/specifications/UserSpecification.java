package com.imst.event.map.admin.db.specifications;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.UserItem;
import com.imst.event.map.hibernate.entity.User;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class UserSpecification extends CustomSpecificationAbs<User, UserItem> {
	
	private static final long serialVersionUID = 5137034799433046024L;

	private UserItem userItem;
	
	public UserSpecification(UserItem userItem) {
		
		this.userItem = userItem;
	}
	
	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<UserItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(userItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), userItem.getName()));
		}
		
		if(!StringUtils.isBlank(userItem.getUsername())){
			predicates.add(ilike(criteriaBuilder, root.get("username"), userItem.getUsername()));
		}
		
		if (userItem.getProfileId() != null && userItem.getProfileId() > 0) {
			predicates.add(criteriaBuilder.equal(root.get("profile").get("id"), userItem.getProfileId()));
		}
		
		if (null != userItem.getState()) {
			predicates.add(criteriaBuilder.equal(root.get("state"), userItem.getState()));
		}
	
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<User> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<User> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("username"),
				root.get("profile").get("id"),
				root.get("profile").get("name"),
				root.get("state"),
				root.get("isDbUser"),
				root.get("providerUserId"),};
	}
	
}
