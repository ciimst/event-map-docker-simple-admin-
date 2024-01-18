package com.imst.event.map.admin.db.specifications.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.api.ApiUserSearchItem;
import com.imst.event.map.hibernate.entity.User;

public class ApiUserSpecification extends CustomSpecificationAbs<User, ApiUserSearchItem> {
	
	private static final long serialVersionUID = 4162111373458501396L;

	private ApiUserSearchItem apiUserSearchItem;
	
	public ApiUserSpecification(ApiUserSearchItem apiUserSearchItem) {
		
		this.apiUserSearchItem = apiUserSearchItem;
	}
	
	@Override
	public Predicate toPredicate(Root<User> root, CriteriaQuery<ApiUserSearchItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(apiUserSearchItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), apiUserSearchItem.getName()));
		}
		
		if(!StringUtils.isBlank(apiUserSearchItem.getUsername())){
			predicates.add(ilike(criteriaBuilder, root.get("username"), apiUserSearchItem.getUsername()));
		}
		
		if (apiUserSearchItem.getProfileId() != null && apiUserSearchItem.getProfileId() > 0) {
			predicates.add(criteriaBuilder.equal(root.get("profile").get("id"), apiUserSearchItem.getProfileId()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<User> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("username"),
				root.get("profile").get("id"),
				root.get("profile").get("name"),
				root.get("providerUserId")};
	}
}
