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
import com.imst.event.map.admin.vo.BlackListItem;
import com.imst.event.map.hibernate.entity.BlackList;

public class BlackListSpecification extends CustomSpecificationAbs<BlackList, BlackListItem>{

	private static final long serialVersionUID = 4975009281373390532L;
	
	private BlackListItem blackListItem;
	private List<Integer> userLayerPermissionIdList;
	private List<Integer> userEventGroupPermissionIdList;
	
	public BlackListSpecification(BlackListItem blackListItem, List<Integer> userLayerPermissionIdList, List<Integer> userEventGroupPermissionIdList) {
		
		this.blackListItem = blackListItem;
		this.userLayerPermissionIdList = userLayerPermissionIdList;
		this.userEventGroupPermissionIdList = userEventGroupPermissionIdList;		
		
	}
	
	@Override
	public Predicate toPredicate(Root<BlackList> root, CriteriaQuery<BlackListItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		List<Predicate> layerOrEventGroupIdList = new ArrayList<>();
		
		if(!StringUtils.isBlank(blackListItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), blackListItem.getName()));
		}
		
		if(!StringUtils.isBlank(blackListItem.getTag())){
			predicates.add(ilike(criteriaBuilder, root.get("tag"), blackListItem.getTag()));
		}
		   	
        if (Optional.ofNullable(blackListItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), blackListItem.getLayerId()));
		}
        
	    if (Optional.ofNullable(blackListItem.getEventGroupId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), blackListItem.getEventGroupId()));
		}
        
        if (Optional.ofNullable(blackListItem.getEventTypeId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), blackListItem.getEventTypeId()));
		}
        
        if (blackListItem.getState() != null) {
        	
        	Integer stateId = StateE.getBooleanState(blackListItem.getState()).getId();
        	
			predicates.add(criteriaBuilder.equal(root.get("state").get("id"), stateId));
		} else {
			
			predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue())));
		}
        

    	layerOrEventGroupIdList.add(root.get("eventGroup").get("id").in(userEventGroupPermissionIdList));
        
        
    	layerOrEventGroupIdList.add(root.get("layer").get("id").in(userLayerPermissionIdList));
              

        predicates.add(criteriaBuilder.or(layerOrEventGroupIdList.toArray(new Predicate[0])));
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<BlackList> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<BlackList> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("tag"),
				root.get("createUser"),
				root.get("createDate"),
				root.get("updateDate"),
				root.get("state").get("id"),
				root.get("layer").get("id"),
				root.get("layer").get("name"),
				root.get("eventGroup").get("id"),
				root.get("eventType").get("id"),
				root.get("actionState").get("id"),
				root.get("actionState").get("stateType")
		};
	}

}
