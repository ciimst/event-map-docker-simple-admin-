package com.imst.event.map.admin.db.specifications.mobile;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.mobile.EventMediaItemMobile;
import com.imst.event.map.hibernate.entity.EventMedia;

public class EventMediaSpecificationMobile extends CustomSpecificationAbs<EventMedia, EventMediaItemMobile> {
	
	private static final long serialVersionUID = -3997687404355977671L;

	private List<Integer> eventIdList;
	
	public EventMediaSpecificationMobile(List<Integer> eventIdList) {
		
		this.eventIdList = eventIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<EventMedia> root, CriteriaQuery<EventMediaItemMobile> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		predicates.add( root.get("event").get("id").in(eventIdList));
						
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<EventMedia> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<EventMedia> root) {
		
		return new Selection[] {
				
				root.get("id"),
				root.get("event").get("id"),
				root.get("path"),
				root.get("isVideo")
		};
	}
	
}
