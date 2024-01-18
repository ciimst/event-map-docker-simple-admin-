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
import com.imst.event.map.admin.vo.AlertItem;
import com.imst.event.map.hibernate.entity.Alert;

public class AlertSpecification extends CustomSpecificationAbs<Alert, AlertItem> {
	
	private static final long serialVersionUID = -1680943579044626506L;

	private AlertItem alertItem;
	private List<Integer> eventGroupIdList;
	
	public AlertSpecification(AlertItem alertItem, List<Integer> eventGroupIdList) {
		
		this.alertItem = alertItem;
		this.eventGroupIdList = eventGroupIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<Alert> root, CriteriaQuery<AlertItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();

		if(!StringUtils.isBlank(alertItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), alertItem.getName()));
		}

		
		if (Optional.ofNullable(alertItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), alertItem.getLayerId()));
		}
		
		if (Optional.ofNullable(alertItem.getEventTypeId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), alertItem.getEventTypeId()));
		}
		
		if (Optional.ofNullable(alertItem.getEventGroupId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), alertItem.getEventGroupId()));
		}
		
		predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));	

							
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Alert> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Alert> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("user").get("id"),
				root.get("query"),
				root.get("reservedKey"),
				root.get("reservedType"),
				root.get("reservedId"),
				root.get("reservedLink"),
				root.get("eventType").get("id"),
				root.get("eventGroup").get("id"),
				root.get("layer").get("id"),
				root.get("eventType").get("name"),
				root.get("eventGroup").get("name"),
				root.get("layer").get("name"),
				root.get("eventType").get("code"),
				root.get("user").get("name")

				
		};
	}
	
}
