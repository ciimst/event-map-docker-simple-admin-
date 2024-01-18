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
import com.imst.event.map.admin.vo.FakeLayerIdItem;
import com.imst.event.map.admin.vo.LayerItem;
import com.imst.event.map.hibernate.entity.FakeLayerId;
import com.imst.event.map.hibernate.entity.Layer;

public class FakeLayerIdSpecification extends CustomSpecificationAbs<FakeLayerId, FakeLayerIdItem> {
	
	private static final long serialVersionUID = -4645922025305311339L;
	
	private FakeLayerIdItem fakeLayerIdItem;
	
	public FakeLayerIdSpecification(FakeLayerIdItem fakeLayerIdItem) {
		
		this.fakeLayerIdItem = fakeLayerIdItem;
	}
	
	@Override
	public Predicate toPredicate(Root<FakeLayerId> root, CriteriaQuery<FakeLayerIdItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();

		if(fakeLayerIdItem.getLayerId() != null){
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), fakeLayerIdItem.getLayerId()));
		}
		
		if (!StringUtils.isBlank(fakeLayerIdItem.getRoleId())) {
			predicates.add(criteriaBuilder.equal(root.get("roleId"), fakeLayerIdItem.getRoleId()));
		}
		
		if (!StringUtils.isBlank(fakeLayerIdItem.getLayerName())) {
			predicates.add(ilike(criteriaBuilder, root.get("layer").get("name"), fakeLayerIdItem.getLayerName()));
		}
		
		

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<FakeLayerId> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<FakeLayerId> root) {
		
		return new Selection[] {root.get("id"),
				root.get("roleId"),
				root.get("layer").get("id"),
				root.get("layer").get("name")
				
		};
	}


	
}
