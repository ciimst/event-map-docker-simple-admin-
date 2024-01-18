package com.imst.event.map.admin.db.specifications;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.TileServerItem;
import com.imst.event.map.hibernate.entity.TileServer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class TileServerSpecification extends CustomSpecificationAbs<TileServer, TileServerItem> {
	
	private static final long serialVersionUID = 5866651704215252755L;

	private TileServerItem tileServerItem;
	
	public TileServerSpecification(TileServerItem tileServerItem) {
		
		this.tileServerItem = tileServerItem;
	}
	
	@Override
	public Predicate toPredicate(Root<TileServer> root, CriteriaQuery<TileServerItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(tileServerItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), tileServerItem.getName()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<TileServer> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<TileServer> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("url"),
				root.get("createDate"),
				root.get("updateDate"),
				root.get("sortOrder"),
				root.get("state")
		};
	}
	
}
