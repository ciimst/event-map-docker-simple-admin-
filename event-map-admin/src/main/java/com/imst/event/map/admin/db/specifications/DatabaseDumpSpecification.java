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
import com.imst.event.map.admin.vo.DatabaseDumpItem;
import com.imst.event.map.hibernate.entity.DatabaseDump;

public class DatabaseDumpSpecification extends CustomSpecificationAbs<DatabaseDump, DatabaseDumpItem> {
	
	private static final long serialVersionUID = -9159312566220039849L;

	private DatabaseDumpItem databaseDumpItem;
	
	public DatabaseDumpSpecification(DatabaseDumpItem databaseDumpItem) {
		
		this.databaseDumpItem = databaseDumpItem;
	}
	
	@Override
	public Predicate toPredicate(Root<DatabaseDump> root, CriteriaQuery<DatabaseDumpItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(databaseDumpItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), databaseDumpItem.getName()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<DatabaseDump> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<DatabaseDump> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name"),
				root.get("createDate"),
				root.get("dumpSize")
		};
	}
	
}
