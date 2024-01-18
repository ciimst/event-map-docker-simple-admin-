package com.imst.event.map.admin.db.specifications.mobile;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.mobile.TileExportItem;
import com.imst.event.map.hibernate.entity.TileExport;

public class TileExportSpecification  extends CustomSpecificationAbs<TileExport, TileExportItem>{

	private static final long serialVersionUID = -3144051392090986385L;

	private TileExportItem tileExportItem;
	
	public TileExportSpecification(TileExportItem tileExportItem) {
		this.tileExportItem = tileExportItem;
	}
	
	@Override
	public Predicate toPredicate(Root<TileExport> root, CriteriaQuery<TileExportItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(tileExportItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), tileExportItem.getName()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<TileExport> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<TileExport> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("minZ"),
				root.get("maxZ"),
				root.get("createDate"),
				root.get("maxLat"),
				root.get("minLat"),
				root.get("maxLong"),
				root.get("minLong"),
				root.get("tileServer").get("id"),
				root.get("tileServer").get("name"),
				root.get("tileServer").get("url")
	
		};
	}
}
