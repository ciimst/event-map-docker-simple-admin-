package com.imst.event.map.admin.db.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.EventExcelItem;
import com.imst.event.map.hibernate.entity.Event;

public class EventExcelSpecification extends CustomSpecificationAbs<Event, EventExcelItem> {
	
	private static final long serialVersionUID = -1680943579044626506L;

	private EventExcelItem eventExcelItem;
	List<Integer> groupIdList;
	List<Integer> userIdList;
	List<Integer> userEventGroupPermissionIdList;
	
	public EventExcelSpecification(EventExcelItem eventExcelItem, List<Integer> groupIdList, List<Integer> userIdList, List<Integer> userEventGroupPermissionIdList) {
		
		this.eventExcelItem = eventExcelItem;
		this.groupIdList = groupIdList;
		this.userIdList = userIdList;
		this.userEventGroupPermissionIdList = userEventGroupPermissionIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventExcelItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		List<Predicate> userOrGroupIdList = new ArrayList<>();
		
		if(!StringUtils.isBlank(eventExcelItem.getTitle())){
			predicates.add(ilike(criteriaBuilder, root.get("title"), eventExcelItem.getTitle()));
		}
		
		if(!StringUtils.isBlank(eventExcelItem.getSpot())){
			predicates.add(ilike(criteriaBuilder, root.get("spot"), eventExcelItem.getSpot()));
		}
		
		if(!StringUtils.isBlank(eventExcelItem.getDescription())){
			predicates.add(ilike(criteriaBuilder, root.get("description"), eventExcelItem.getDescription()));
		}
		
		if(!StringUtils.isBlank(eventExcelItem.getCity())){
			predicates.add(ilike(criteriaBuilder, root.get("city"), eventExcelItem.getCity()));
		}
		
		if(!StringUtils.isBlank(eventExcelItem.getCountry())){
			predicates.add(ilike(criteriaBuilder, root.get("country"), eventExcelItem.getCountry()));
		}
		
		if(!StringUtils.isBlank(eventExcelItem.getBlackListTag())){
			predicates.add(ilike(criteriaBuilder, root.get("blackListTag"), eventExcelItem.getBlackListTag()));
		}
		
		
		
		if (Optional.ofNullable(eventExcelItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), eventExcelItem.getLayerId()));
			
		}
		
		if (eventExcelItem.getStartDate() != null) {
			predicates.add(criteriaBuilder.greaterThan( root.get("eventDate"), eventExcelItem.getStartDate() ));
		}
		
		if (eventExcelItem.getEndDate() != null) {
			predicates.add(criteriaBuilder.lessThan( root.get("eventDate"), eventExcelItem.getEndDate()));
		}

		
		if (Optional.ofNullable(eventExcelItem.getEventTypeId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventExcelItem.getEventTypeId()));
		}
		
		if (Optional.ofNullable(eventExcelItem.getEventGroupId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), eventExcelItem.getEventGroupId()));
		}

			
		predicates.add(root.get("eventGroup").get("id").in(userEventGroupPermissionIdList));
		
		if(groupIdList != null && !groupIdList.isEmpty()){	
			
			userOrGroupIdList.add( root.get("groupId").in(groupIdList));
		}
		
		if(userIdList != null && !userIdList.isEmpty()){
			
			userOrGroupIdList.add( root.get("userId").in(userIdList));
		}				
		
		if (eventExcelItem.getState() != null) {
			Integer stateId = StateE.getBooleanState(eventExcelItem.getState()).getId();
			
			predicates.add(criteriaBuilder.equal(root.get("state").get("id"), stateId));
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
				root.get("eventGroup").get("layer").get("name"),
				root.get("country"),
				root.get("city"),
				root.get("latitude"),
				root.get("longitude"),
				root.get("blackListTag"),
				root.get("createUser"),
				root.get("createDate"),
       			root.get("updateDate"),
				root.get("eventGroup").get("id"),
				root.get("eventGroup").get("name"),
				root.get("state").get("id"),
				root.get("reservedKey"),
				root.get("reservedType"),
				root.get("reservedId"),
				root.get("reservedLink"),
				root.get("userId"),
				root.get("groupId"),
				root.get("eventGroup").get("color"),
				root.get("eventGroup").get("layer").get("id"),
				root.get("eventType").get("code"),
				root.get("reserved1"),
				root.get("reserved2"),
				root.get("reserved3"),
				root.get("reserved4"),
				root.get("reserved5")
				
		};
	}
	
}
