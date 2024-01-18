package com.imst.event.map.admin.controllers.admin.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.admin.constants.SettingsE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.datatables.ajax.DataSet;
import com.imst.event.map.admin.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.admin.datatables.ajax.DatatablesResponse;
import com.imst.event.map.admin.datatables.spring3.DatatablesParams;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.LayerProjection;
import com.imst.event.map.admin.db.projections.mobile.EventTypeProjectionMobile;
import com.imst.event.map.admin.db.projections.mobile.LayerProjectionMobile;
import com.imst.event.map.admin.db.projections.mobile.MapAreaGroupProjectionMobile;
import com.imst.event.map.admin.db.repositories.LayerExportRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.TileServerRepository;
import com.imst.event.map.admin.db.specifications.mobile.LayerExportSpecification;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.CallableEventGroupService;
import com.imst.event.map.admin.services.CallableTileExportEventService;
import com.imst.event.map.admin.services.EventService;
import com.imst.event.map.admin.services.TileService;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.ExportItemEncryptUtils;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.vo.DataSourceInfo;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.PermissionWrapperItem;
import com.imst.event.map.admin.vo.TileXYItem;
import com.imst.event.map.admin.vo.mobile.EventGroupItemMobile;
import com.imst.event.map.admin.vo.mobile.EventItemMobile;
import com.imst.event.map.admin.vo.mobile.ExportEncryptItem;
import com.imst.event.map.admin.vo.mobile.LayerExportItem;
import com.imst.event.map.admin.vo.mobile.MapAreaItemMobile;
import com.imst.event.map.admin.vo.mobile.MobileTileItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.LayerExport;
import com.imst.event.map.hibernate.entity.TileServer;
import com.imst.event.map.hibernate.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/map/layer-export")
public class LayerExportController {

	@Autowired 
	private LayerRepository layerRepository;
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private LayerExportRepository layerExportRepository;
	@Autowired
	private EventService eventService;
	@Autowired 
	private TileServerRepository tileServerRepository;
	@Autowired 
	private TileService  tileService;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_LIST')")
	@RequestMapping({""})
	public ModelAndView getPage(Integer id){

		ModelAndView modelAndView = new ModelAndView("page/admin/map/layer_export");
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
												
		List<LayerProjection> layersFiltered = layerRepository.findAllProjectedByIdInOrderByName(userLayerFullPermissionIdList);
		
//		List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layersFiltered);
		
		modelAndView.addObject("layerId", id);

		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public DatatablesResponse<LayerExportItem> data(LayerExportItem layerExportItem, @DatatablesParams DatatablesCriterias criterias){
		
		PageRequest pageRequest = criterias.getPageRequest(LayerExportItem.class);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
//		List<Integer> allLayersIdList = layerRepository.findAll().stream().map(Layer::getId).collect(Collectors.toList()); // for test
		
		LayerExportSpecification layerExportSpecification = new LayerExportSpecification(layerExportItem, userLayerFullPermissionIdList);
		Page<LayerExportItem> layerExportItems = masterDao.findAll(layerExportSpecification, pageRequest);
		
		DataSet<LayerExportItem> dataSet = new DataSet<>(layerExportItems.getContent(), 0L, layerExportItems.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping(value = "/delete")
	public GenericResponseItem delete(Integer layerExportId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.delete"));
		
		try {
			
			if (Optional.ofNullable(layerExportId).orElse(0) < 1) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.export.not.found"));
				return genericResponseItem;
			}
			
			LayerExport layerExport = layerExportRepository.findById(layerExportId).orElse(null);
			if (layerExport == null) {
				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.export.not.found"));
				return genericResponseItem;
			}
			
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
			User user = new User();
			user.setId(sessionUser.getUserId());
			
			PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
					
			List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
			
			//Katman izin kontrolu
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerExport.getLayer().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}

			layerExportRepository.deleteById(layerExportId);
			
			File deleteFile = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId);
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
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping(value="/zoom")
	public ModelAndView zoomCreate(Integer id){

		ModelAndView modelAndView = new ModelAndView("page/admin/map/layer_zoom_level");
		
		LayerExportItem layerExportInfo = layerExportRepository.findAllProjectedById(id);
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		
		if(layerExportInfo != null) {
			
			int layerId = layerExportInfo.getLayerId();
			
			modelAndView.addObject("layerId",layerId);
			
			LayerProjection layerName = layerRepository.findProjectedById(layerId);
			modelAndView.addObject("layerName", layerName.getName());	
			
			Layer layer = new Layer();
			layer.setId(layerId);
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
				modelAndView = new ModelAndView("error");
				modelAndView.setStatus(HttpStatus.FORBIDDEN);
				return modelAndView;
			}
			
			Map<String, Integer> map =  fileNameReturn (new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + id + "/tile"), id);
			Map<String, Integer> fileList = new TreeMap<>(map);			
			modelAndView.addObject("tileList", fileList);
			
		
			modelAndView.addObject("layerExport", layerExportInfo);
			modelAndView.addObject("layerExportId", id);
			
			Map<String, Integer> eventFileList =  fileNameReturn(new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + id + "/event"),  id);
			modelAndView.addObject("eventFileList", eventFileList);
			
		}
		
			
		List<LayerProjection> layersFiltered = layerRepository.findAllProjectedByIdInOrderByName(userLayerFullPermissionIdList);
		
//		List<LayerProjection> layers = layerRepository.findAllProjectedByOrderByName();
		modelAndView.addObject("layers", layersFiltered);
		
		List<TileServer> tileServerList = tileServerRepository.findAll();
		tileServerList = tileServerList.stream().sorted(Comparator.comparing(TileServer::getSortOrder)).collect(Collectors.toList());
		modelAndView.addObject("tileServerList", tileServerList);
		
		return modelAndView;
	}

	private Map<String, Integer> fileNameReturn(File fileDirectory, Integer layerId) {
		   		   		
		Map<String, Integer> fileList = new HashMap<>();
		   
	    if (fileDirectory.exists() && fileDirectory.isDirectory()) { 
	        for (File chDir : fileDirectory.listFiles()) {
	        	if(!chDir.isDirectory())
	        	fileList.put(chDir.getName(), layerId);	            		            	
	        }	                
	    }
	    
	    
	    Map<String, Integer> sortedList = fileList.entrySet().stream()
	            .sorted(Map.Entry.comparingByKey())
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
	                    (e1, e2) -> e1, LinkedHashMap::new));

	    return sortedList;
	}	  
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping(value="/save")
	public GenericResponseItem saveLayerExport(@RequestParam(name="id") Integer layerId, Integer minZoom, Integer maxZoom, String startDate, String finishDate, Integer layerExportId, String name, Integer tileServerId) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
				
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		if(layerId == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.please.select.layer"));
			return genericResponseItem;
		}
		
		if(tileServerId == null) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.please.select.tileServer"));
			return genericResponseItem;
		}
		
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
		LayerExport layerExport ;
		
		if(layerExportId != null) {
			 layerExport = layerExportRepository.findAllById(layerExportId);	
			 
			 File fileCreateLayerIdTile = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId+"/tile");
			 if(fileCreateLayerIdTile.exists()) {		
				deleteFolder(fileCreateLayerIdTile);
			 }
			 
			 File fileCreateLayerIdEvent = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId+"/event");
			 if(fileCreateLayerIdEvent.exists()) {		
				deleteFolder(fileCreateLayerIdEvent);
			 }
			 
			//Editlenecek exportun layer id'si ve verilen layer id'sine bakar
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerExport.getLayer().getId()))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
			
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
					
		}else {			
			layerExport = new LayerExport();		
			 
			//Katman izin kontrolu
			if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
				return genericResponseItem;
			}
		}
		
		layerExport.setCreateDate(nowT);
		Layer layer = new Layer();
		layer.setId(layerId);
		layerExport.setLayer(layer);
		layerExport.setMinZ(minZoom);
		layerExport.setMaxZ(maxZoom);
		layerExport.setStartDate(DateUtils.convertToTimestamp(startDate,ApplicationContextUtils.getMessage("label.time.format")));
		layerExport.setFinishDate(DateUtils.convertToTimestamp(finishDate,ApplicationContextUtils.getMessage("label.time.format")));
		layerExport.setName(name);
		layerExport.setEventExportCount(0);
		
		TileServer tileServer = new TileServer();
		tileServer.setId(tileServerId);
		layerExport.setTileServer(tileServer);
		
		LayerExport saveLayerExport = layerExportRepository.save(layerExport);			  
		
	    genericResponseItem.setData(saveLayerExport.getId());
		return genericResponseItem;
		
	}
	
	private void editDateUpdate(Integer layerId, Integer minZoom, Integer maxZoom, String startDate, String finishDate, Integer layerExportId, String name, String exportType) {
		
		Timestamp nowT = DateUtils.nowT();	
		LayerExport layerExport ;
		if(layerExportId != null) {
			 layerExport = layerExportRepository.findAllById(layerExportId);	

			 if(exportType != null) {
				 				 
				 if(exportType.equals("tile")) {					 
					 layerExport.setTileCreateDate(nowT);
				 }
				 if(exportType.equals("event")) {
					 layerExport.setEventCreateDate(nowT);
				 }				  
			 }	
			layerExport.setCreateDate(nowT);
			layerExportRepository.save(layerExport);	
		}
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping(value="/tileFileDownload/{fileName}/{layerExportId}")
	public ResponseEntity<Object>  downloadFile(@PathVariable("fileName") String fileName, @PathVariable("layerExportId") Integer layerId) throws IOException{
		
		String filename = SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerId + "/tile/" + fileName;
		
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
	

	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping("/tileExport")
	public GenericResponseItem mobileTileData(Integer layerId, Integer minZoom, Integer maxZoom, String startDate, String finishDate, Integer layerExportId, String name, Integer tileServerId) throws ParseException {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		File fileCreateLayerIdTile = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId+"/tile");
		 if(fileCreateLayerIdTile.exists()) {		
			deleteFolder(fileCreateLayerIdTile);
		 }
		 
		Layer layer = new Layer();
		layer.setId(layerId);
		
		editDateUpdate(layerId, minZoom, maxZoom, startDate, finishDate, layerExportId, name, "tile");
		
		SimpleDateFormat formatter=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date startEventDate = null;
		Date finishEventDate = null;
		if(!startDate.equals("")) {
			startEventDate = formatter.parse(startDate);
		}
		if(!finishDate.equals("")) {
			finishEventDate =formatter.parse(finishDate);
		}
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		//Katman izin kontrolu
		if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
			return genericResponseItem;
		}
		
		/*****************Diğer veritabanlarından event alma**********/

		ExecutorService executorEvent = Executors.newFixedThreadPool(10);
		List<Future<List<EventItemMobile>>> futureOrderEventList = new ArrayList<>();
		
		for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
							
			CallableTileExportEventService callableEvent = new CallableTileExportEventService(startEventDate, finishEventDate, dataSourceInfo, layerId);
			futureOrderEventList.add(executorEvent.submit(callableEvent));
		}
		
		executorEvent.shutdown();
		
		List<EventItemMobile> totalEventList = new ArrayList<>();
		
		for(Future<List<EventItemMobile>> futureOrderList : futureOrderEventList) {
			
			try {
				
				totalEventList.addAll(futureOrderList.get());
				
			} catch (InterruptedException e) {
				log.debug(e);
			} catch (ExecutionException e) {
				log.debug(e);
			}
			
		}
		
		/***********************************************************/
		
		List<EventItemMobile> maxLat  = eventService.getEventLatLong(masterDao, startEventDate, finishEventDate, Sort.by(Direction.DESC, "latitude") ,layerId);
		List<EventItemMobile> minLat  = eventService.getEventLatLong(masterDao, startEventDate, finishEventDate, Sort.by(Direction.ASC, "latitude") ,layerId);
		List<EventItemMobile> maxLong  = eventService.getEventLatLong(masterDao, startEventDate, finishEventDate, Sort.by(Direction.DESC, "longitude") ,layerId);
		List<EventItemMobile> minLong  = eventService.getEventLatLong(masterDao, startEventDate, finishEventDate, Sort.by(Direction.ASC, "longitude") ,layerId);
		
		totalEventList.addAll(maxLat);
		totalEventList.addAll(minLat);
		totalEventList.addAll(maxLong);
		totalEventList.addAll(minLong);
		
		if(totalEventList.size() > 0) {
			
			EventItemMobile eventMaxLatitude =  totalEventList.stream().max(Comparator.comparing(EventItemMobile::getLatitude)).orElseThrow(NoSuchElementException::new);
			EventItemMobile eventMinLatitude = totalEventList.stream().min(Comparator.comparing(EventItemMobile::getLatitude)) .orElseThrow(NoSuchElementException::new);
			EventItemMobile eventMaxLongitude = totalEventList.stream().max(Comparator.comparing(EventItemMobile::getLongitude)) .orElseThrow(NoSuchElementException::new);
			EventItemMobile eventMinLongitude = totalEventList.stream().min(Comparator.comparing(EventItemMobile::getLongitude)) .orElseThrow(NoSuchElementException::new);
			
			double maxLatitude = eventMaxLatitude.getLatitude();
			double minLatitude = eventMinLatitude.getLatitude();
			double maxLongitude = eventMaxLongitude.getLongitude();
			double minLongitude = eventMinLongitude.getLongitude();
						
			generateMobileDataFromCoordinate(minZoom, maxZoom, maxLatitude, minLongitude, minLatitude, maxLongitude, 1, layerExportId, tileServerId);
		}else {
			
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.events.between.date.not.found"));
			return genericResponseItem;
		}

		return genericResponseItem;
	}

	private void generateMobileDataFromCoordinate(Integer minZ, Integer maxZ, Double lat1, Double lon1, Double lat2, Double lon2, Integer distance, Integer layerExportId, Integer tileServerId) {
		
		Iterable<TileServer> tileServerIterator = tileServerRepository.findAll(Sort.by("sortOrder"));
		TileServer tileServer = null;
		for (TileServer tileServerTemp : tileServerIterator) {
			if(tileServerTemp.getId() == tileServerId) {
				tileServer = tileServerTemp;
				break;
			}
		}
						
		List<MobileTileItem> mobileTileItemList = new ArrayList<>();
			
		for (int z = minZ; z <= maxZ; z++) {
			
			TileXYItem tileXY = tileService.getTileXY(lat1, lon1, z);
			TileXYItem tileXY2 = tileService.getTileXY(lat2, lon2, z);

			String filePath = SettingsUtil.getString(SettingsE.LAYER_TILE_ROOT_PATH );
			List<MobileTileItem> mobileTileItemListForZoomLevel = tileService.prepareTileDataForZoomLevel(tileServer.getUrl(), null, z, tileXY.getX(), tileXY2.getX(), tileXY.getY() , tileXY2.getY(), filePath, tileServer.getId());
			mobileTileItemList.addAll(mobileTileItemListForZoomLevel);
		}
				
		
		File fileCreate = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH));
		fileCreate.mkdirs();
		
		File fileCreateLayerId = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId);
						
		fileCreateLayerId.mkdir();
		
		tileService.writeTileDataToFile(mobileTileItemList, SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId + "/tile/");
		
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping(value="/eventExport")
	public GenericResponseItem generateAllEventData(Integer layerId, Integer minZoom, Integer maxZoom, String startDate, String finishDate, Integer layerExportId, String name, Integer tileServerId) throws ParseException {
		
		 File fileCreateLayerIdEvent = new File(SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerExportId+"/event");
		 if(fileCreateLayerIdEvent.exists()) {		
			deleteFolder(fileCreateLayerIdEvent);
		 }
		 
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
				 
		SimpleDateFormat formatter=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	     
		Date startEventDate = null;
		Date finishEventDate = null;
		if(!startDate.equals("")) {
			startEventDate = formatter.parse(startDate);
		}
		if(!finishDate.equals("")) {
			finishEventDate =formatter.parse(finishDate);  
		}
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));	
		ObjectMapper objectMapper = new ObjectMapper();
		
		editDateUpdate(layerId, minZoom, maxZoom, startDate, finishDate, layerExportId, name, "event");
		
		    	
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userLayerFullPermissionIdList = permissionWrapperItem.getUserLayerHasFullPermissionItemIds();
		
		//Katman izin kontrolu
		if (!userLayerFullPermissionIdList.stream().anyMatch(n -> n.equals(layerId))) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.layer.no.permission"));
			return genericResponseItem;
		}

		
		/******************EventGroup*************************/

		ExecutorService executor = Executors.newFixedThreadPool(10);
		
    	List<Future<List<EventGroupItemMobile>>> futureOrderListList = new ArrayList<>();
    	
    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		
    		CallableEventGroupService callableTest = new CallableEventGroupService(layerId, dataSourceInfo);
			futureOrderListList.add(executor.submit(callableTest));
    		
    	}
    	executor.shutdown();
    	
    	List<EventGroupItemMobile> totalEventGroupList = new ArrayList<>();    	
    	   
    	for (Future<List<EventGroupItemMobile>> futureOrderList : futureOrderListList) {
    		
			try {
				totalEventGroupList.addAll(futureOrderList.get());	
				
			} catch (InterruptedException e) {
				log.debug(e);
			} catch (ExecutionException e) {
				log.debug(e);
			}
					
		}
		
		/**************************************************/  
		
		try {			
			
			Integer eventAllExportCount = eventService.writeTileDataToFile(startEventDate, finishEventDate, layerId, layerExportId);
			
			LayerExport layerExport = new LayerExport();
			layerExport = layerExportRepository.findAllById(layerExportId);	
			layerExport.setEventExportCount(eventAllExportCount);
			layerExportRepository.save(layerExport);
			
			if(eventAllExportCount == 0) {				
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.events.between.date.not.found"));
				return genericResponseItem;
			}
			
			String rootPath  = fileCreate("eventGroup", layerExportId);
			List<EventGroupItemMobile> eventGroupData = eventService.generateEventGroupData(masterDao, Sort.by("id"), layerId);
			totalEventGroupList.addAll(eventGroupData);
			
			String encryptedItem = null;
			List<ExportEncryptItem> encryptEventGroupList = new ArrayList<>();
							    		     
			for(EventGroupItemMobile item :  totalEventGroupList) {
							
				encryptedItem = ExportItemEncryptUtils.encryptGeneric(item);
				
				ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
				exportEncrypItem.setData(encryptedItem);
				encryptEventGroupList.add(exportEncrypItem);				
			}			
			objectMapper.writeValue(new File(rootPath, "eventGroup_0.json"), encryptEventGroupList);
			
			
		    rootPath  = fileCreate("eventType", layerExportId);
			List<EventTypeProjectionMobile> eventTypeData = eventService.generateEventTypeData();
			encryptedItem = null;
			List<ExportEncryptItem> encryptEventTypeList = new ArrayList<>();
			for(EventTypeProjectionMobile item :  eventTypeData) {
							
				encryptedItem = ExportItemEncryptUtils.encryptGeneric(item);
				
				ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
				exportEncrypItem.setData(encryptedItem);
				encryptEventTypeList.add(exportEncrypItem);
				
			}

			objectMapper.writeValue(new File(rootPath, "eventType_0.json"), encryptEventTypeList);
			
			
			encryptedItem = null;
			rootPath  = fileCreate("layer", layerExportId);
			List<LayerProjectionMobile> layerData = eventService.generateLayerData(layerId);
			List<ExportEncryptItem> encryptLayerList = new ArrayList<>();
			for(LayerProjectionMobile item :  layerData) {
								
				encryptedItem = ExportItemEncryptUtils.encryptGeneric(item);
				
				ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
				exportEncrypItem.setData(encryptedItem);
				encryptLayerList.add(exportEncrypItem);
				
			}
			objectMapper.writeValue(new File(rootPath, "layer_0.json"), encryptLayerList);
			
			
			encryptedItem = null;
			rootPath  = fileCreate("mapArea", layerExportId);
			List<MapAreaItemMobile> mapAreaData = eventService.generateMapAreaData(layerId);
			List<ExportEncryptItem> encryptMapAreaList = new ArrayList<>();
			for(MapAreaItemMobile item :  mapAreaData) {				
				
				encryptedItem = ExportItemEncryptUtils.encryptGeneric(item);
				
				ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
				exportEncrypItem.setData(encryptedItem);
				encryptMapAreaList.add(exportEncrypItem);
				
			}
			objectMapper.writeValue(new File(rootPath, "mapArea_0.json"), encryptMapAreaList);
			
			
			encryptedItem = null;
			rootPath  = fileCreate("mapAreaGroup", layerExportId);
			List<MapAreaGroupProjectionMobile> mapAreaGroupData = eventService.generateMapAreaGroupData(layerId);
			List<ExportEncryptItem> encryptMapAreaGroupList = new ArrayList<>();
			for(MapAreaGroupProjectionMobile item :  mapAreaGroupData) {
								
				encryptedItem = ExportItemEncryptUtils.encryptGeneric(item);
				
				ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
				exportEncrypItem.setData(encryptedItem);
				encryptMapAreaGroupList.add(exportEncrypItem);
				
			}
			objectMapper.writeValue(new File(rootPath, "mapAreaGroup_0.json"), encryptMapAreaGroupList);
			
			return genericResponseItem;
			
		} catch (JsonGenerationException e) {
			log.debug(e);
		} catch (JsonMappingException e) {
			log.debug(e);
		} catch (IOException e) {
			log.debug(e);
		}
		genericResponseItem.setState(false);
		genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.unknown.error"));
		return genericResponseItem;
	}
	
	
	private String fileCreate(String filename, Integer layerExportId) {
		
		String rootPath = SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH);
		File fileCreate = new File(rootPath);
		fileCreate.mkdirs();		
		
		fileCreate = new File(rootPath+layerExportId + "/event");
		fileCreate.mkdirs();
		
		return rootPath+layerExportId + "/event";
	}
	
	@PreAuthorize("hasRole('ROLE_LAYER_EXPORT_MANAGE')")
	@RequestMapping(value="/allFileDownload/{fileName}/{layerId}/{directory}")
	public ResponseEntity<Object>  downloadFileAll(@PathVariable("fileName") String fileName, @PathVariable("layerId") Integer layerId, @PathVariable("directory") String directoryName) throws IOException{
		
		String filename = SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH) + layerId + "/" + directoryName + "/" + fileName;
		
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
	
}
