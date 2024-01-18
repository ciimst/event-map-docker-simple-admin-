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
import com.imst.event.map.admin.vo.EventLinkItem;
import com.imst.event.map.hibernate.entity.EventLink;

public class EventLinkSpecification extends CustomSpecificationAbs<EventLink, EventLinkItem> {
	
	private static final long serialVersionUID = 9168735089510687737L;

	private EventLinkItem eventLinkItem;
	public EventLinkSpecification(EventLinkItem eventLinkItem) {
		
		this.eventLinkItem = eventLinkItem;
	}
	
	@Override
	public Predicate toPredicate(Root<EventLink> root, CriteriaQuery<EventLinkItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(Optional.ofNullable(eventLinkItem.getEventColumnId()).orElse(0) > 0) {
			predicates.add(criteriaBuilder.equal(root.get("eventColumn").get("id"), eventLinkItem.getEventColumnId()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<EventLink> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<EventLink> root) {
		
		return new Selection[] {root.get("id"),
				root.get("link"),
				root.get("eventColumn").get("id"),				
				root.get("eventColumn").get("name"),
				root.get("displayName"),
				root.get("color")
				
		};
	}
	
}
