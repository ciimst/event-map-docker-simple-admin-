package com.imst.event.map.admin.db.specifications;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.MapAreaItem;
import com.imst.event.map.hibernate.entity.MapArea;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class MapAreaSpecification extends CustomSpecificationAbs<MapArea, MapAreaItem> {
	
	private static final long serialVersionUID = -8157318134702447683L;

	private MapAreaItem mapAreaItem;
	private List<Integer> userLayerPermissionIdList;
	
	public MapAreaSpecification(MapAreaItem mapAreaItem, List<Integer> userLayerPermissionIdList) {
		
		this.mapAreaItem = mapAreaItem;
		this.userLayerPermissionIdList = userLayerPermissionIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<MapArea> root, CriteriaQuery<MapAreaItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(mapAreaItem.getTitle())){
			predicates.add(ilike(criteriaBuilder, root.get("title"), mapAreaItem.getTitle()));
		}
		
		if (Optional.ofNullable(mapAreaItem.getMapAreaGroupId()).orElse(0) > 0) {
			predicates.add(criteriaBuilder.equal(root.get("mapAreaGroup").get("id"), mapAreaItem.getMapAreaGroupId()));
		}
		
		predicates.add(root.get("mapAreaGroup").get("layer").get("id").in(userLayerPermissionIdList));
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<MapArea> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<MapArea> root) {
		
		return new Selection[] {root.get("id"),
				root.get("mapAreaGroup").get("id"),
				root.get("mapAreaGroup").get("name"),
				root.get("mapAreaGroup").get("color"),
				root.get("title"),
				root.get("coordinateInfo"),
				root.get("state")
		};
	}
	
}
