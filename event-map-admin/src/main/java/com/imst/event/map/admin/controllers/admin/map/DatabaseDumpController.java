package com.imst.event.map.admin.controllers.admin.map;


import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.repositories.DatabaseDumpRepository;
import com.imst.event.map.admin.db.specifications.DatabaseDumpSpecification;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.services.DatabaseBackupService;
import com.imst.event.map.admin.services.S3Service;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.DatabaseDumpItem;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.hibernate.entity.DatabaseDump;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/dumps")
public class DatabaseDumpController {
	
	@Autowired
	private DatabaseDumpRepository databaseDumpRepository;	
	
	@Autowired
	private DatabaseBackupService databaseBackupService;
	
	@Autowired private S3Service s3Service;

	@Autowired
	private MasterDao masterDao;
	@Autowired
	private DBLogger dbLogger;
	
	@PreAuthorize("hasRole('ROLE_DATATABASE_DUMP_MANAGE')")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/databasedumps");
			
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_DATATABASE_DUMP_MANAGE')")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<DatabaseDumpItem> data(DatabaseDumpItem databaseDumpItem, @DatatablesParams DatatablesCriterias criterias) {

		PageRequest pageRequest = criterias.getPageRequest(DatabaseDumpItem.class);
		
		DatabaseDumpSpecification databaseDumpSpecification = new DatabaseDumpSpecification(databaseDumpItem);
		Page<DatabaseDumpItem> dumpItems = masterDao.findAll(databaseDumpSpecification, pageRequest);
		DataSet<DatabaseDumpItem> dataSet = new DataSet<>(dumpItems.getContent(), 0L, dumpItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}	
	
	@PreAuthorize("hasRole('ROLE_DATATABASE_DUMP_MANAGE')")
	@RequestMapping(value = "/restore")
	public GenericResponseItem restore(Integer dumpId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("Başarılı"));
		
		if (Optional.ofNullable(dumpId).orElse(0) < 1) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Dump bulunamadı"));//TODO:lang
			return genericResponseItem;
		}
		
		DatabaseDump databaseDump = databaseDumpRepository.findById(dumpId).orElse(null);
		if (databaseDump == null) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Dump bulunamadı"));//TODO:lang
			return genericResponseItem;
		}
		
		if (!s3Service.checkDump(databaseDump.getName())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Dump s3'de bulunamadı"));//TODO:lang
			return genericResponseItem;
		}
		
		boolean restoreStatus = databaseBackupService.restore(databaseDump.getName());
		
		if (!restoreStatus) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Geri yükleme sırasında hata"));//TODO:lang
			return genericResponseItem;
		}

		return genericResponseItem;
	}	
	
	@PreAuthorize("hasRole('ROLE_DATATABASE_DUMP_MANAGE')")
	@Operation(summary = "Sil")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer dumpId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(dumpId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Dump bulunamadı"));//TODO:lang
				return genericResponseItem;
			}
			
			DatabaseDump databaseDump = databaseDumpRepository.findById(dumpId).orElse(null);
			if (databaseDump == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Dump bulunamadı"));//TODO:lang
				return genericResponseItem;
			}			
			
			databaseDumpRepository.deleteById(dumpId);
			
			Map<String, Object> dumpsForLog = new TreeMap<>();
			dumpsForLog.put("deleted", DatabaseDumpItem.newInstanceForLog(databaseDump));
			
			dbLogger.log(new Gson().toJson(dumpsForLog), LogTypeE.LAYER_DELETE);
			
			if (!s3Service.checkDump(databaseDump.getName())) {
				genericResponseItem.setState(true);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Dump s3'de bulunamadı. Yerel veritabanındaki dump kaydı silindi"));//TODO:lang
				return genericResponseItem;
			}
			
			s3Service.delete("event-map/dumps/" + databaseDump.getName());	
			
			log.info("Database dump " + databaseDump.getName() + " S3'den silindi");
			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_DATATABASE_DUMP_MANAGE')")
	@Operation(summary = "Backup")
	@RequestMapping(value = "/backup")
	public GenericResponseItem backup() {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("Başarılı"));
		
		try {
						
			boolean backupStatus = databaseBackupService.backup(true);
			
			if (!backupStatus) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("Yedekleme sırasında hata"));//TODO:lang
				return genericResponseItem;
			}

			
		} catch (Exception e) {
			
			log.error(e);
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	
}
