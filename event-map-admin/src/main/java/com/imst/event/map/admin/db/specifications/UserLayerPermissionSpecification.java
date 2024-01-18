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
import com.imst.event.map.admin.vo.UserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.UserLayerPermission;

public class UserLayerPermissionSpecification  extends CustomSpecificationAbs<UserLayerPermission,UserLayerPermissionItem>{
	
	private static final long serialVersionUID = -7848805080136298855L;

	private UserLayerPermissionItem userLayerPermissionItem;
	
	public UserLayerPermissionSpecification(UserLayerPermissionItem userLayerPermissionItem) {
		this.userLayerPermissionItem = userLayerPermissionItem;
	}
	
	@Override
	public Predicate toPredicate(Root<UserLayerPermission> root, CriteriaQuery<UserLayerPermissionItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
					
		if (Optional.ofNullable(userLayerPermissionItem.getUserId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userLayerPermissionItem.getUserId()));
		}
		
		if (Optional.ofNullable(userLayerPermissionItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), userLayerPermissionItem.getLayerId()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<UserLayerPermission> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<UserLayerPermission> root) {
		
		return new Selection[] {root.get("id"),
				root.get("layer").get("id"),
				root.get("layer").get("name"),
				root.get("user").get("id"),
				root.get("user").get("username")
				
		};
	}
}
