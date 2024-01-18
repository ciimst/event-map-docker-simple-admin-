package com.imst.event.map.admin.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.GeoLayerItem;
import com.imst.event.map.hibernate.entity.GeoLayer;

public class GeoLayerSpecification extends CustomSpecificationAbs<GeoLayer, GeoLayerItem>{

	private static final long serialVersionUID = 4975009281373390532L;
	
	private GeoLayerItem geoLayerItem;
	private List<Integer> userLayerPermissionIdList;
	
	public GeoLayerSpecification(GeoLayerItem geoLayerItem, List<Integer> userLayerPermissionIdList) {
		
		this.geoLayerItem = geoLayerItem;
		this.userLayerPermissionIdList = userLayerPermissionIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<GeoLayer> root, CriteriaQuery<GeoLayerItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(geoLayerItem.getName())){
			predicates.add(criteriaBuilder.equal(root.get("name"), geoLayerItem.getName()));
		}
		
		if (null != geoLayerItem.getState()) {
			predicates.add(criteriaBuilder.equal(root.get("state"), geoLayerItem.getState()));
		}
		
			
		predicates.add(root.get("layer").get("id").in(userLayerPermissionIdList));		
	
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<GeoLayer> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<GeoLayer> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("data"),
				root.get("createDate"),
				root.get("layer").get("id"),
				root.get("layer").get("name"),
				root.get("state")
		};
	}

}
