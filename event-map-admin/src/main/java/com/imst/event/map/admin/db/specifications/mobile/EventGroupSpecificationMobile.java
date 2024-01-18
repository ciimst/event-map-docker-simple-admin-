package com.imst.event.map.admin.db.specifications.mobile;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.mobile.EventGroupItemMobile;
import com.imst.event.map.hibernate.entity.EventGroup;

public class EventGroupSpecificationMobile extends CustomSpecificationAbs<EventGroup, EventGroupItemMobile>{
	
	private static final long serialVersionUID = -8009606918667926520L;

	private Integer layerId;
	
	public EventGroupSpecificationMobile(Integer layerId) {
		this.layerId = layerId;
	}
	
	
	@Override
	public Predicate toPredicate(Root<EventGroup> root, CriteriaQuery<EventGroupItemMobile> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), layerId));
		
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<EventGroup> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<EventGroup> root) {
		
		return new Selection[] {
				
				root.get("id"),			
				root.get("name"),
				root.get("color"),
				root.get("layer").get("id")

		};
	}

}
