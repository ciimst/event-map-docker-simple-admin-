package com.imst.event.map.admin.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.BlackListItem;
import com.imst.event.map.admin.vo.EventBlackListItem;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.admin.vo.EventItemForBlackList;
import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventBlackList;

public class EventSpecificationForBlackList extends CustomSpecificationAbs<Event, EventItem> {
	

	private static final long serialVersionUID = 7151046389517645223L;
	
	private EventItemForBlackList eventItem;
	
	public EventSpecificationForBlackList(EventItemForBlackList eventItem) {
		
		this.eventItem = eventItem;
	}
	
	@Override
	public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();

		if (eventItem.getEventGroupIdList() != null) {
			
			predicates.add( root.get("eventGroup").get("id").in(eventItem.getEventGroupIdList()));
		}		
		
		if (eventItem.getEventTypeId() != null) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventItem.getEventTypeId()));
		}
		

		if(eventItem.getBlackListId() != null) {
			Join<EventBlackList, Event> blackListEvent = root.join("eventBlackLists");	
			predicates.add(criteriaBuilder.equal(blackListEvent.get("blackList").get("id"), eventItem.getBlackListId()));
		}
		
		predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), eventItem.getLayerId()));
		predicates.add(criteriaBuilder.equal(root.get("blackListTag"), eventItem.getBlackListTag().trim() ));
		
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Event> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Event> root) {
		
		return new Selection[] {root.get("id"),
				root.get("state").get("id")
				
		};
	}
	
}
