package com.imst.event.map.admin.db.specifications;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.hibernate.entity.Event;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class EventSpecification extends CustomSpecificationAbs<Event, EventItem> {
	
	private static final long serialVersionUID = -1680943579044626506L;

	private EventItem eventItem;
	List<Integer> groupIdList;
	List<Integer> userIdList;
	List<Integer> eventGroupIdList;
	
	public EventSpecification(EventItem eventItem, List<Integer> groupIdList, List<Integer> userIdList, List<Integer> eventGroupIdList) {
		
		this.eventItem = eventItem;
		this.groupIdList = groupIdList;
		this.userIdList = userIdList;
		this.eventGroupIdList = eventGroupIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		List<Predicate> userOrGroupIdList = new ArrayList<>();
		
		if(!StringUtils.isBlank(eventItem.getTitle())){
			predicates.add(ilike(criteriaBuilder, root.get("title"), eventItem.getTitle()));
		}
		
		if(!StringUtils.isBlank(eventItem.getSpot())){
			predicates.add(ilike(criteriaBuilder, root.get("spot"), eventItem.getSpot()));
		}
		
		if(!StringUtils.isBlank(eventItem.getDescription())){
			predicates.add(ilike(criteriaBuilder, root.get("description"), eventItem.getDescription()));
		}
		
		if(!StringUtils.isBlank(eventItem.getCity())){
			predicates.add(ilike(criteriaBuilder, root.get("city"), eventItem.getCity()));
		}
		
		if(!StringUtils.isBlank(eventItem.getCountry())){
			predicates.add(ilike(criteriaBuilder, root.get("country"), eventItem.getCountry()));
		}
		
		if(!StringUtils.isBlank(eventItem.getBlackListTag())){
			predicates.add(ilike(criteriaBuilder, root.get("blackListTag"), eventItem.getBlackListTag()));
		}
		
		
		
		if (Optional.ofNullable(eventItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), eventItem.getLayerId()));
			
		}
		
		if(!StringUtils.isBlank(eventItem.getStartDateStr())){
			
			Date startDate = null;
			try {
				startDate = DateUtils.convertToDate(eventItem.getStartDateStr(), DateUtils.TURKISH);  
			}catch(Exception e) {
				
			}
			
			predicates.add(criteriaBuilder.greaterThan( root.get("eventDate"), startDate));
		}
		
		if(!StringUtils.isBlank(eventItem.getEndDateStr())){
			Date endDate = null;
			try {
				endDate = DateUtils.convertToDate(eventItem.getEndDateStr(), DateUtils.TURKISH);
			}catch(Exception e) {
				
			}
			  
			predicates.add(criteriaBuilder.lessThan( root.get("eventDate"), endDate));
		}

		
		if (Optional.ofNullable(eventItem.getEventTypeId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventItem.getEventTypeId()));
		}
		
		if (Optional.ofNullable(eventItem.getEventGroupId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), eventItem.getEventGroupId()));
		}
		
			
		predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));	
		
		if(groupIdList != null && !groupIdList.isEmpty()){	
			
			userOrGroupIdList.add( root.get("groupId").in(groupIdList));
		}
		
		if(userIdList != null && !userIdList.isEmpty()){
			
			userOrGroupIdList.add( root.get("userId").in(userIdList));
		}				
		
		if (eventItem.getStateId() != null) {
			
			predicates.add(criteriaBuilder.equal(root.get("state").get("id"), eventItem.getStateId()));
		}else{
			predicates.add( criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.BLACKLISTED.getValue())));
			
		}
		
		predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
							
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Event> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Event> root) {
		
		return new Selection[] {root.get("id"),
				root.get("title"),
				root.get("spot"),
				root.get("description"),
				root.get("eventDate"),
				root.get("eventType").get("id"),
				root.get("eventType").get("name"),
				root.get("eventType").get("image"),
				root.get("eventType").get("code"),
				root.get("country"),
				root.get("city"),
				root.get("latitude"),
				root.get("longitude"),
				root.get("blackListTag"),
				root.get("eventGroup").get("id"),
				root.get("eventGroup").get("name"),
				root.get("state").get("id"),
				root.get("reservedKey"),
				root.get("reservedType"),
				root.get("reservedId"),
				root.get("reservedLink"),
				root.get("userId"),
				root.get("groupId"),
				root.get("eventGroup").get("layer").get("id"),
				root.get("reserved1"),
				root.get("reserved2"),
				root.get("reserved3"),
				root.get("reserved4"),
				root.get("reserved5")
				
		};
	}
	
}
