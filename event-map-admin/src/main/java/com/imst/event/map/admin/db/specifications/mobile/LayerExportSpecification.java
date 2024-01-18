package com.imst.event.map.admin.db.specifications.mobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.mobile.LayerExportItem;
import com.imst.event.map.hibernate.entity.LayerExport;

public class LayerExportSpecification extends CustomSpecificationAbs<LayerExport, LayerExportItem>{

	private static final long serialVersionUID = -17046995493720823L;

	private LayerExportItem layerExportItem;
	private List<Integer> userLayerPermissionIdList;
	
	public LayerExportSpecification(LayerExportItem layerExportItem, List<Integer> userLayerPermissionIdList) {
		this.layerExportItem = layerExportItem;
		this.userLayerPermissionIdList = userLayerPermissionIdList;
	}
	
	@Override
	public Predicate toPredicate(Root<LayerExport> root, CriteriaQuery<LayerExportItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
					
		if (Optional.ofNullable(layerExportItem.getLayerId()).orElse(0) > 0) {
			
			predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), layerExportItem.getLayerId()));
		}
		
		predicates.add(root.get("layer").get("id").in(userLayerPermissionIdList));
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<LayerExport> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<LayerExport> root) {
		
		return new Selection[] {root.get("id"),
				root.get("minZ"),
				root.get("maxZ"),
				root.get("createDate"),
				root.get("startDate"),
				root.get("finishDate"),
				root.get("layer").get("id"),
				root.get("layer").get("name"),
				root.get("eventCreateDate"),
				root.get("tileCreateDate"),
				root.get("name"),
				root.get("tileServer").get("id"),
				root.get("tileServer").get("name"),
				root.get("tileServer").get("url"),
				root.get("eventExportCount")
			
				
		};
	}
}
