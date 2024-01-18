package com.imst.event.map.admin.db.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;



public class UserEventGroupPermissionSpecification  extends CustomSpecificationAbs<UserEventGroupPermission,UserEventGroupPermissionItem>{
	
	private static final long serialVersionUID = -7848805080136298855L;

	private UserEventGroupPermissionItem userEventGroupPermissionItem;
	
	public UserEventGroupPermissionSpecification(UserEventGroupPermissionItem userEventGroupPermissionItem) {
		this.userEventGroupPermissionItem = userEventGroupPermissionItem;
	}
	
	@Override
	public Predicate toPredicate(Root<UserEventGroupPermission> root, CriteriaQuery<UserEventGroupPermissionItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
					
		if (Optional.ofNullable(userEventGroupPermissionItem.getUserId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userEventGroupPermissionItem.getUserId()));
		}
		
	    if (Optional.ofNullable(userEventGroupPermissionItem.getLayerId()).orElse(0) > 0) {
			
	    	predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), userEventGroupPermissionItem.getLayerId()));
			
		}
		
		if (Optional.ofNullable(userEventGroupPermissionItem.getEventGroupId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), userEventGroupPermissionItem.getEventGroupId()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<UserEventGroupPermission> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<UserEventGroupPermission> root) {
		
		return new Selection[] {root.get("id"),
				root.get("eventGroup").get("layer").get("id"),
				root.get("eventGroup").get("layer").get("name"),
				root.get("eventGroup").get("id"),
				root.get("eventGroup").get("name"),
				root.get("user").get("id"),
				root.get("user").get("username")
				
		};
	}
}
