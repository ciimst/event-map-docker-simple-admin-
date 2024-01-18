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

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.EventGroupItem;
import com.imst.event.map.hibernate.entity.EventGroup;

public class EventGroupSpecification extends CustomSpecificationAbs<EventGroup, EventGroupItem> {
	
	private static final long serialVersionUID = 9168735089510687737L;

	private EventGroupItem eventGroupItem;
	List<Integer> userLayerPermissionIdList;
	List<Integer> userEventGroupIdList;
	
	public EventGroupSpecification(EventGroupItem eventGroupItem, List<Integer> userEventGroupIdList) {
		
		this.eventGroupItem = eventGroupItem;
		this.userEventGroupIdList = userEventGroupIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<EventGroup> root, CriteriaQuery<EventGroupItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(eventGroupItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), eventGroupItem.getName()));
		}
		
        if (Optional.ofNullable(eventGroupItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), eventGroupItem.getLayerId()));
		}
        
        if (Optional.ofNullable(eventGroupItem.getParentId()).orElse(0) > 0) {
			
	    	predicates.add(criteriaBuilder.equal(root.get("parentId"), eventGroupItem.getParentId()));
			
		}
        
    	predicates.add( root.get("id").in(userEventGroupIdList));   
		
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<EventGroup> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<EventGroup> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),				
				root.get("color"),
				root.get("layer").get("id"),
				root.get("layer").get("name"),
				root.get("parentId"),
				root.get("description")
		};
	}
	
}
