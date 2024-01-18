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
import com.imst.event.map.admin.vo.UserUserIdItem;
import com.imst.event.map.hibernate.entity.UserUserId;

public class UserUserIdSpecification extends CustomSpecificationAbs<UserUserId, UserUserIdItem>{

	private static final long serialVersionUID = 5302383126100374474L;

	private UserUserIdItem userUserIdItem;
	
	public UserUserIdSpecification(UserUserIdItem userUserIdItem) {
		this.userUserIdItem = userUserIdItem;
	}
	
	
	@Override
	public Predicate toPredicate(Root<UserUserId> root, CriteriaQuery<UserUserIdItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
					
		if(userUserIdItem.getUserId()!=null){
			predicates.add(criteriaBuilder.equal(root.get("userId"), userUserIdItem.getUserId()));
		}
		
		if (Optional.ofNullable(userUserIdItem.getFk_userId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userUserIdItem.getFk_userId()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<UserUserId> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<UserUserId> root) {
		
		return new Selection[] {root.get("id"),
				root.get("userId"),								
				root.get("user").get("id"),
				root.get("user").get("username")
		};
	}
}
