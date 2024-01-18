package com.imst.event.map.admin.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.hibernate.entity.EventType;

public class EventTypeSpecification extends CustomSpecificationAbs<EventType, EventTypeItem> {
	
	private static final long serialVersionUID = -1169783875496279009L;

	private EventTypeItem eventTypeItem;
	
	public EventTypeSpecification(EventTypeItem eventTypeItem) {
		
		this.eventTypeItem = eventTypeItem;
	}
	
	@Override
	public Predicate toPredicate(Root<EventType> root, CriteriaQuery<EventTypeItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(eventTypeItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), eventTypeItem.getName()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<EventType> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<EventType> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("image"),
				root.get("code"),
				root.get("pathData")
		};
	}
	
}
