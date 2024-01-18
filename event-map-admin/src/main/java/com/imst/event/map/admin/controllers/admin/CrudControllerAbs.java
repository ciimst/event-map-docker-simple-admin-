package com.imst.event.map.admin.controllers.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.vo.GenericResponseItem;

public abstract class CrudControllerAbs<T> {
	
	@RequestMapping({""})
	public abstract ModelAndView getPage();
	
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<T> dataFront(T item, @DatatablesParams DatatablesCriterias criteria) {
		return data(item, criteria);
	}
	
	public abstract  DatatablesResponse<T> data(T item, DatatablesCriterias criteria);
	
	
	@GetMapping(value = "/edit/{id}")
	@ResponseBody
	public ModelAndView editFront(@PathVariable("id") Integer id) {
		return edit(id);
	}
	
	public abstract ModelAndView edit(Integer id);
	
	@GetMapping(value = "/add")
	@ResponseBody
	public ModelAndView add() {
		return edit(null);
	}
	
	@RequestMapping(value = "/save")
	public abstract GenericResponseItem save(T item);
	
	@RequestMapping(value = "/delete")
	public abstract GenericResponseItem delete(Integer id);
	
}
