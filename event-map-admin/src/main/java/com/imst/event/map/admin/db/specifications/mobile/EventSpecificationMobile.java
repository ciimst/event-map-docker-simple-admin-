package com.imst.event.map.admin.db.specifications.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.mobile.EventItemMobile;
import com.imst.event.map.hibernate.entity.Event;

public class EventSpecificationMobile extends CustomSpecificationAbs<Event, EventItemMobile> {
	
	private static final long serialVersionUID = 8619174480721915825L;

	private Integer layerId;
	private Date startDate;
	private Date finishDate;
	private Integer lastEventId;
	
	public EventSpecificationMobile(Integer layerId, Date startDate, Date finishDate, Integer lastEventId) {
		
		this.layerId = layerId;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.lastEventId = lastEventId;
	}
	
	@Override
	public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventItemMobile> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), layerId));
		if(startDate != null) {
			predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
		}
		
		if(finishDate != null) {
			predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), finishDate));
		}
		
		if(lastEventId != null) {
			predicates.add(criteriaBuilder.greaterThan(root.get("id"), lastEventId));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Event> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Event> root) {
		
		return new Selection[] {
				
				root.get("id"),
				root.get("eventGroup").get("id"),
				root.get("eventType").get("id"),
				root.get("title"),
				root.get("spot"),
				root.get("description"),
				root.get("eventDate"),
				root.get("country"),
				root.get("city"),
				root.get("latitude"),
				root.get("longitude"),
				root.get("createUser"),
				root.get("reservedKey"),
				root.get("reservedType"),
				root.get("reservedId"),
				root.get("reservedLink")
		};
	}
	
}
