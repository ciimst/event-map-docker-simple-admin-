package com.imst.event.map.admin.controllers.admin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.specifications.LogSpecification;
import com.imst.event.map.admin.vo.LogItem;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/admin/logs")
public class LogController {
	
	@Autowired
	private MasterDao masterDao;
	
	@PreAuthorize("hasRole('ROLE_LOG_LIST')")
	@Operation(summary = "")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/logs");
		
		LogTypeE[] values = LogTypeE.values();
		List<LogTypeE> collect = Arrays.stream(values).filter(logTypeE -> logTypeE != LogTypeE.NONE).sorted(Comparator.comparing(LogTypeE::getName, Statics.sortedCollator())).collect(Collectors.toList());
		modelAndView.addObject("logTypes", collect);
		
		return modelAndView;
	}
	@PreAuthorize("hasRole('ROLE_LOG_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<LogItem> data(LogItem userItem, @DatatablesParams DatatablesCriterias criterias) {
		
		PageRequest pageRequest = criterias.getPageRequest(LogItem.class);
		
		LogSpecification logSpecification = new LogSpecification(userItem);
		Page<LogItem> logItems = masterDao.findAll(logSpecification, pageRequest);
		
		DataSet<LogItem> dataSet = new DataSet<>(logItems.getContent(), 0L, logItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
}
