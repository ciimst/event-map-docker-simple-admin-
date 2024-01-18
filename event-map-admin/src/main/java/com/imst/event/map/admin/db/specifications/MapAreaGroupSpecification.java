package com.imst.event.map.admin.db.specifications;

import com.imst.event.map.hibernate.entity.MapAreaGroup;
import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.MapAreaGroupItem;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class MapAreaGroupSpecification extends CustomSpecificationAbs<MapAreaGroup, MapAreaGroupItem> {
	
	private static final long serialVersionUID = -2632623495127781667L;

	private MapAreaGroupItem mapAreaGroupItem;
	private List<Integer> userLayerPermissionIdList;
	
	public MapAreaGroupSpecification(MapAreaGroupItem mapAreaGroupItem, List<Integer> userLayerPermissionIdList) {
		
		this.mapAreaGroupItem = mapAreaGroupItem;
		this.userLayerPermissionIdList = userLayerPermissionIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<MapAreaGroup> root, CriteriaQuery<MapAreaGroupItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(mapAreaGroupItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), mapAreaGroupItem.getName()));
		}
		
		if (Optional.ofNullable(mapAreaGroupItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), mapAreaGroupItem.getLayerId()));
		}
		
		predicates.add(root.get("layer").get("id").in(userLayerPermissionIdList));

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<MapAreaGroup> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<MapAreaGroup> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),				
				root.get("color"),
				root.get("layer").get("id"),
				root.get("layer").get("name")
		};
	}
	
}
