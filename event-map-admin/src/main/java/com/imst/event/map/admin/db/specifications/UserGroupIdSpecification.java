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
import com.imst.event.map.admin.vo.UserGroupIdItem;
import com.imst.event.map.hibernate.entity.UserGroupId;

public class UserGroupIdSpecification extends CustomSpecificationAbs<UserGroupId, UserGroupIdItem>{

	private static final long serialVersionUID = 7103403221769696954L;

	private UserGroupIdItem userGroupIdItem;
	
	public UserGroupIdSpecification(UserGroupIdItem userGroupIdItem) {
		this.userGroupIdItem = userGroupIdItem;
	}
	
	
	@Override
	public Predicate toPredicate(Root<UserGroupId> root, CriteriaQuery<UserGroupIdItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
					
		if(userGroupIdItem.getGroupId()!=null){
			predicates.add(criteriaBuilder.equal(root.get("groupId"), userGroupIdItem.getGroupId()));
		}
		
		if (Optional.ofNullable(userGroupIdItem.getUserId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userGroupIdItem.getUserId()));
		}
		
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<UserGroupId> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<UserGroupId> root) {
		
		return new Selection[] {root.get("id"),
				root.get("groupId"),								
				root.get("user").get("id"),
				root.get("user").get("username")
		};
	}
}
