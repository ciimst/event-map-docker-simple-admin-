package com.imst.event.map.admin.db.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j2;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Log4j2
public abstract class CustomSpecificationAbs<D, T>  implements CustomSpecification<D, T>{
	
	private static final long serialVersionUID = -9000613907716161837L;

	private Class<D> domain;
	private Class<T> target;
	private Map<String, List<String>> conParams = new HashMap<>();
	
	@SuppressWarnings({ "unchecked" })
	public CustomSpecificationAbs() {
		
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.domain = (Class<D>) type.getActualTypeArguments()[0];
		this.target = (Class<T>) type.getActualTypeArguments()[1];
		
		Constructor<?>[] constructors = target.getConstructors();
		if (constructors.length == 0) {
			throw new NotImplementedException();
		}
		
		try {
			for (Constructor<?> constructor : constructors) {
				
				Parameter[] parameters = constructor.getParameters();
				for (Parameter parameter : parameters) {
					conParams.put(parameter.getName(), parameterFix(parameter.getName()));
				}
				if (conParams.size() > 0) {
					break;
				}
			}
		} catch (Exception e) {

			log.error(e);
		}
	}
	
	private List<String> parameterFix(String parameterName) {
		
		if (parameterName == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(StringUtils.splitByWholeSeparator(parameterName, "."));
	}
	
	
	@Override
	public Predicate toPredicate(Root<D> root, CriteriaQuery<T> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
		return null;
	}
	
	@Override
	public Predicate toPredicateCount(Root<D> root, CriteriaBuilder criteriaBuilder) {
		return null;
	}
	
	@Override
	public Class<D> getDomain() {
		
		return this.domain;
	}
	
	@Override
	public Class<T> getTarget() {
		
		return this.target;
	}
	
	
	@Override
	public Selection<?>[] getSelections(Root<D> root) {
		
		Selection<?>[] constructorParams = getConstructorParams(root);
		if (constructorParams != null) {
			return constructorParams;
		}
		
		List<Selection<?>> selectionList = new ArrayList<>();
		
		for (Map.Entry<String, List<String>> stringListEntry : conParams.entrySet()) {
			
			List<String> value = stringListEntry.getValue();
			
			Path<?> objectPath = null;
			for (String paramPart : value) {
				
				if (objectPath == null) {
					objectPath = root.get(paramPart);
				} else {
					objectPath = objectPath.get(paramPart);
				}
			}
			selectionList.add(objectPath);
		}
		
		return selectionList.toArray(new Selection<?>[0]);
	}
	
	/**
	 * @return null-> default parameter names from target constructor
	 */
	public abstract Selection<?>[] getConstructorParams(Root<D> root);
	
	public Predicate ilike(CriteriaBuilder criteriaBuilder, Path<String> name, String search) {
		
		return criteriaBuilder.like(
				criteriaBuilder.upper(criteriaBuilder.lower(name)),
				"%" + search.toLowerCase(new Locale("tr")).toUpperCase() + "%");
	}
}
