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
import com.imst.event.map.admin.vo.LayerItem;
import com.imst.event.map.hibernate.entity.Layer;

public class LayerSpecification extends CustomSpecificationAbs<Layer, LayerItem> {
	
	private static final long serialVersionUID = -4645922025305311339L;
	
	private LayerItem layerItem;
	List<Integer> userLayerPermissionIdList;
	
	public LayerSpecification(LayerItem layerItem, List<Integer> userLayerPermissionIdList) {
		
		this.layerItem = layerItem;
		this.userLayerPermissionIdList = userLayerPermissionIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<Layer> root, CriteriaQuery<LayerItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();

		if(!StringUtils.isBlank(layerItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), layerItem.getName()));
		}
		
		if (null != layerItem.getState()) {
			predicates.add(criteriaBuilder.equal(root.get("state"), layerItem.getState()));
		}
		
		if (layerItem.getIsTemp() != null) {
			predicates.add(criteriaBuilder.equal(root.get("isTemp"), layerItem.getIsTemp()));
		}
		
    	predicates.add( root.get("id").in(userLayerPermissionIdList));

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Layer> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Layer> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("createDate"),
				root.get("updateDate"),
				root.get("state"),
				root.get("isTemp"),
				root.get("guid")
		};
	}
	
}
