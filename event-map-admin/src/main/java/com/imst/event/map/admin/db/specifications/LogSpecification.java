package com.imst.event.map.admin.db.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.admin.db.support.CustomSpecificationAbs;
import com.imst.event.map.admin.vo.LogItem;
import com.imst.event.map.hibernate.entity.Log;

public class LogSpecification extends CustomSpecificationAbs<Log, LogItem> {
	
	private static final long serialVersionUID = -7291735867201764392L;
	
	private LogItem logItem;
	
	public LogSpecification(LogItem logItem) {
		
		this.logItem = logItem;
	}
	
	@Override
	public Predicate toPredicate(Root<Log> root, CriteriaQuery<LogItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		
		if(!StringUtils.isBlank(logItem.getUsername())){
			predicates.add(ilike(criteriaBuilder, root.get("username"), logItem.getUsername()));
		}
		
		if(Optional.ofNullable(logItem.getFkLogTypeId()).orElse(0) > 0){
			predicates.add(criteriaBuilder.equal(root.get("fkLogTypeId"), logItem.getFkLogTypeId()));
		}
		
		if(!StringUtils.isBlank(logItem.getSearchableDescription())){
			predicates.add(ilike(criteriaBuilder, root.get("searchableDescription"), logItem.getSearchableDescription()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Log> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Log> root) {
		
		return new Selection[] {root.get("id"),
				root.get("username"),
				root.get("userId"),
				root.get("ip"),
				root.get("description"),
				root.get("createDate"),
				root.get("fkLogTypeId"),
				root.get("uniqueId")
		};
	}
	
}
