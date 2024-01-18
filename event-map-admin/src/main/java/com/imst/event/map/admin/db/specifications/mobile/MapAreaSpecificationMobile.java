package com.imst.event.map.admin.db.specifications.mobile;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.mobile.MapAreaItemMobile;
import com.imst.event.map.hibernate.entity.MapArea;

public class MapAreaSpecificationMobile extends CustomSpecificationAbs<MapArea, MapAreaItemMobile> {
	
	private static final long serialVersionUID = 7724965515229776322L;

	private Integer layerId;
	
	public MapAreaSpecificationMobile(Integer layerId) {
		
		this.layerId = layerId;
	}
	
	@Override
	public Predicate toPredicate(Root<MapArea> root, CriteriaQuery<MapAreaItemMobile> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		predicates.add(criteriaBuilder.equal(root.get("mapAreaGroup").get("layer").get("id"), layerId));
		
		predicates.add(criteriaBuilder.equal(root.get("state"), true));
		
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<MapArea> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<MapArea> root) {
		
		return new Selection[] {
				root.get("id"),
				root.get("mapAreaGroup").get("id"),
				root.get("title"),
				root.get("coordinateInfo"),
		};
	}
	
}
