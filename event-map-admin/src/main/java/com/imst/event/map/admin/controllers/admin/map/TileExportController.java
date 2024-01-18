package com.imst.event.map.admin.controllers.admin.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.imst.event.map.admin.constants.SettingsE;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.repositories.TileExportRepository;
import com.imst.event.map.admin.db.repositories.TileServerRepository;
import com.imst.event.map.admin.db.specifications.mobile.TileExportSpecification;
import com.imst.event.map.admin.services.TileService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.TileXYItem;
import com.imst.event.map.admin.vo.mobile.LayerExportItem;
import com.imst.event.map.admin.vo.mobile.MobileTileItem;
import com.imst.event.map.admin.vo.mobile.TileExportItem;
import com.imst.event.map.hibernate.entity.TileExport;
import com.imst.event.map.hibernate.entity.TileServer;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/admin/map/tile-export")
public class TileExportController {
	
	@Autowired MasterDao masterDao;
	@Autowired private TileServerRepository tileServerRepository;
	@Autowired private TileExportRepository tileExportRepository;
	@Autowired private TileService tileService;

	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_LIST')")
	@RequestMapping({""})
	public ModelAndView getPage(){

		ModelAndView modelAndView = new ModelAndView("page/admin/map/tile_export");
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<TileExportItem> data(TileExportItem tileExportItem, @DatatablesParams DatatablesCriterias criterias){
		
		PageRequest pageRequest = criterias.getPageRequest(LayerExportItem.class);
		
		TileExportSpecification tileExportSpecification = new TileExportSpecification(tileExportItem);
		Page<TileExportItem> tileExportItems = masterDao.findAll(tileExportSpecification, pageRequest);
		
		DataSet<TileExportItem> dataSet = new DataSet<>(tileExportItems.getContent(), 0L, tileExportItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping({"/tile"})
	public ModelAndView getPageEdit(Integer id) {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/tile_export_edit");
		TileExportItem tileExportItem = tileExportRepository.findAllProjectedById(id);
		if(tileExportItem != null) {
			
			modelAndView.addObject("tileExportItem", tileExportItem);
			
			Map<String, Integer> map =  fileNameReturn (new File(SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH) + id), id);
			Map<String, Integer> fileList = new TreeMap<>(map);
			modelAndView.addObject("tileFileList", fileList);
		}
		
		return modelAndView;
	}
	
	
	private Map<String, Integer> fileNameReturn(File fileDirectory, Integer tileId) {
	   		
		Map<String, Integer> fileList = new HashMap<>();
		   
	    if (fileDirectory.exists() && fileDirectory.isDirectory()) { 
	        for (File chDir : fileDirectory.listFiles()) {
	        	if(!chDir.isDirectory())
	        	fileList.put(chDir.getName(), tileId);	            		            	
	        }	                
	    }

	    return fileList;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping(value="/tileFileDownload/{fileName}/{tileExportId}")
	public ResponseEntity<Object>  downloadFile(@PathVariable("fileName") String fileName, @PathVariable("tileExportId") Integer tileExportId) throws IOException{
		
		String filename = SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH) + tileExportId + "/" + fileName;
		
		File file = new File(filename);
		InputStreamResource resource =  new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Disposition",
				String.format("attachment; filename=\"%s\"", file.getName()));
		headers.add("Cache-Control", "no-cache, nstore, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		
		ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers)
				.contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/txt")).body(resource);
		
		return responseEntity;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping("/getCoordinate")
	public TileExportItem getCoordinate(Integer tileExportId) {
		
		TileExportItem tileExportItem = tileExportRepository.findAllProjectedById(tileExportId);
		
		return tileExportItem;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping("/save")
	public GenericResponseItem save(double lat1, double long1, double lat2, double long2, Integer minZoom, Integer maxZoom, String name, Integer tileExportId, Integer tileServerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		
		if(minZoom == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.min.zoom.level.enter"));
			return genericResponseItem;
		}
		
		if(maxZoom == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.max.zoom.level.enter"));
			return genericResponseItem;
		}

		
		Timestamp nowT = DateUtils.nowT();	
		TileExport tileExport;
		
		if(tileExportId != null) {
			
			tileExport = tileExportRepository.findAllById(tileExportId);
			
			File fileCreateLayerIdTile = new File(SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH) + tileExportId);
			if(fileCreateLayerIdTile.exists()) {		
				deleteFolder(fileCreateLayerIdTile);
			}
			 
			
		}else {
			
			tileExport = new TileExport();
		}
		

		tileExport.setMinZ(minZoom);
		tileExport.setMaxZ(maxZoom);
		tileExport.setName(name);
		tileExport.setCreateDate(nowT);
		tileExport.setMaxLat(lat1);
		tileExport.setMinLat(lat2);
		tileExport.setMaxLong(long1);
		tileExport.setMinLong(long2);
		
		TileServer tileServer = new TileServer();
		tileServer.setId(tileServerId);
		tileExport.setTileServer(tileServer);
		
		TileExport saveTileExport = tileExportRepository.save(tileExport);
		
		genericResponseItem.setData(saveTileExport.getId());
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping("/delete")
	public GenericResponseItem delete(Integer id) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			TileExport tileExport = tileExportRepository.findById(id).orElse(null);
			if (tileExport == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.tile.export.not.found"));
				return genericResponseItem;
			}

			tileExportRepository.deleteById(id);
			
			File deleteFile = new File(SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH) + id);
			if(deleteFile.exists()) {
				deleteFolder(deleteFile);
			}

		} catch (Exception e) {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.error.operation.failed"));
			return genericResponseItem;
		}
		
		return genericResponseItem;
	}
	
	private void deleteFolder(File file){
		   for (File subFile : file.listFiles()) {
		      if(subFile.isDirectory()) {
		            deleteFolder(subFile);
		       } else {
		            subFile.delete();
		       }
		    }
		   file.delete();
		}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping("/tileServer")
	public List<TileServer> TileLayerList() {
		
		Iterable<TileServer> tileServerIterator = tileServerRepository.findAll(Sort.by("sortOrder"));
		
		List<TileServer> tileServerList = StreamSupport.stream(tileServerIterator.spliterator(), false)
			    .collect(Collectors.toList());

		return tileServerList;
	}
	
	@PreAuthorize("hasRole('ROLE_TILE_EXPORT_MANAGE')")
	@RequestMapping(value="/tileExport")
	public GenericResponseItem tileExport(double lat1, double long1, double lat2, double long2, Integer minZoom, Integer maxZoom, Integer tileExportId, String tileServerUrl, Integer tileServerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		if(minZoom == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.min.zoom.level.enter"));
			return genericResponseItem;
		}
		
		if(maxZoom == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.max.zoom.level.enter"));
			return genericResponseItem;
		}				
		
		generateMobileDataFromCoordinate(minZoom, maxZoom, lat1, long1, lat2, long2, 1, tileExportId, tileServerUrl, tileServerId);
		
		Timestamp nowT = DateUtils.nowT();
		TileExport tileExport;
	
		if(tileExportId != null) {
			
			tileExport = tileExportRepository.findAllById(tileExportId);
			tileExport.setCreateDate(nowT);
			tileExportRepository.save(tileExport);
		}
		
			
		return genericResponseItem;
	}
	
	
	private void generateMobileDataFromCoordinate(Integer minZ, Integer maxZ, Double lat1, Double lon1, Double lat2, Double lon2, Integer distance, Integer tileExportId, String tileServerUrl, Integer tileServerId) {
					
		List<MobileTileItem> mobileTileItemList = new ArrayList<>();
			
		for (int z = minZ; z <= maxZ; z++) {
			
			TileXYItem tileXY = tileService.getTileXY(lat1, lon1, z);
			TileXYItem tileXY2 = tileService.getTileXY(lat2, lon2, z);
			
			String filePath = SettingsUtil.getString(SettingsE.LAYER_TILE_ROOT_PATH);
			List<MobileTileItem> mobileTileItemListForZoomLevel = tileService.prepareTileDataForZoomLevel(tileServerUrl, null, z, tileXY.getX(), tileXY2.getX(), tileXY.getY() , tileXY2.getY(), filePath, tileServerId);
			mobileTileItemList.addAll(mobileTileItemListForZoomLevel);
		}
				
		
		File fileCreate = new File(SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH));
		fileCreate.mkdirs();
		
		File fileCreateLayerId = new File(SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH) + tileExportId);
						
		fileCreateLayerId.mkdir();
		
		tileService.writeTileDataToFile(mobileTileItemList, SettingsUtil.getString(SettingsE.TILE_EXPORT_FILE_PATH) + tileExportId);
		
	}
}
