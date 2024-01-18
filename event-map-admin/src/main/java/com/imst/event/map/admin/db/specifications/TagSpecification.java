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
import com.imst.event.map.admin.vo.TagItem;
import com.imst.event.map.hibernate.entity.Tag;

public class TagSpecification extends CustomSpecificationAbs<Tag, TagItem> {
	
	private static final long serialVersionUID = -9159312566220039849L;

	private TagItem tagItem;
	
	public TagSpecification(TagItem tagItem) {
		
		this.tagItem = tagItem;
	}
	
	@Override
	public Predicate toPredicate(Root<Tag> root, CriteriaQuery<TagItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isBlank(tagItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), tagItem.getName()));
		}
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
	
	@Override
	public Predicate toPredicateCount(Root<Tag> root, CriteriaBuilder criteriaBuilder) {
		
		return toPredicate(root, null, criteriaBuilder);
	}
	
	@Override
	public Selection<?>[] getConstructorParams(Root<Tag> root) {
		
		return new Selection[] {root.get("id"),
				root.get("name")
		};
	}
	
}
