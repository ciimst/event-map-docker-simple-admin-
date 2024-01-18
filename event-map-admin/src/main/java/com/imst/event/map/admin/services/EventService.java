package com.imst.event.map.admin.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.admin.constants.SettingsE;
import com.imst.event.map.admin.constants.Statics;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.mobile.EventTypeProjectionMobile;
import com.imst.event.map.admin.db.projections.mobile.LayerProjectionMobile;
import com.imst.event.map.admin.db.projections.mobile.MapAreaGroupProjectionMobile;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.db.specifications.mobile.EventGroupSpecificationMobile;
import com.imst.event.map.admin.db.specifications.mobile.EventMediaSpecificationMobile;
import com.imst.event.map.admin.db.specifications.mobile.EventSpecificationMobile;
import com.imst.event.map.admin.db.specifications.mobile.MapAreaSpecificationMobile;
import com.imst.event.map.admin.utils.ExportItemEncryptUtils;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.vo.DataSourceInfo;
import com.imst.event.map.admin.vo.mobile.EventGroupItemMobile;
import com.imst.event.map.admin.vo.mobile.EventItemMobile;
import com.imst.event.map.admin.vo.mobile.EventMediaItemMobile;
import com.imst.event.map.admin.vo.mobile.ExportEncryptItem;
import com.imst.event.map.admin.vo.mobile.MapAreaItemMobile;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class EventService {
	
	@Autowired public MasterDao masterDao;
	
	@Autowired public EventGroupRepository eventGroupRepository;
	@Autowired public EventTypeRepository eventTypeRepository;
	@Autowired public LayerRepository layerRepository;
	@Autowired public MapAreaGroupRepository mapAreaGroupRepository;
	
	
	public List<EventItemMobile> getEventLatLong(MasterDao masterDao, Date startDate, Date finishDate, Sort sort, Integer layerId){
		
		PageRequest pageRequest = PageRequest.of(0, 1, sort);
		
		EventSpecificationMobile eventSpecificationMobile = new EventSpecificationMobile(layerId, startDate, finishDate, null);
		Page<EventItemMobile> eventItemMobilePage = masterDao.findAll(eventSpecificationMobile, pageRequest);
		
		List<EventItemMobile> eventItemMobileList = eventItemMobilePage.getContent();
		
		return eventItemMobileList;
	}
	
	public List<EventGroupItemMobile> generateEventGroupData(MasterDao masterDao, Sort sort, Integer layerId) {
		
		EventGroupSpecificationMobile eventGroupSpecificationMobile = new EventGroupSpecificationMobile(layerId);
		List<EventGroupItemMobile> eventGroupList = masterDao.findAll(eventGroupSpecificationMobile, sort);
		
		for(EventGroupItemMobile eventGroupItemMobile : eventGroupList) {
			eventGroupItemMobile.setDbName(Statics.DEFAULT_DB_NAME);
		}
		
		return eventGroupList;
	}
	

	public List<EventTypeProjectionMobile> generateEventTypeData() {
		
		List<EventTypeProjectionMobile> eventTypeList = eventTypeRepository.findAllMobileProjectedBy();
		return eventTypeList;
	}
	
	public List<LayerProjectionMobile> generateLayerData(Integer layerId) {
		
		// Aslında bir tane gelir ama liste ihtiyacı olduğu için list kullanılmıştır
		List<LayerProjectionMobile> layerList = layerRepository.findAllProjectedById(layerId);
		return layerList;
	}
	
	public List<MapAreaItemMobile> generateMapAreaData(Integer layerId) {
		
		MapAreaSpecificationMobile mapAreaSpecificationMobile = new MapAreaSpecificationMobile(layerId);
		List<MapAreaItemMobile> mapAreaList = masterDao.findAll(mapAreaSpecificationMobile, Sort.by("id"));
		return mapAreaList;
	}
	
	public List<MapAreaGroupProjectionMobile> generateMapAreaGroupData(Integer layerId) {
		
		List<MapAreaGroupProjectionMobile> mapAreaGroupList = mapAreaGroupRepository.findAllProjectedByLayerId(layerId);
		return mapAreaGroupList;
	}
	
	public List<EventItemMobile> generateEventData(MasterDao masterDao, Integer layerId, Date startDate, Date finishDate, Integer lastEventId) {
		int eventLoadLimit = Integer.parseInt(SettingsUtil.getString(SettingsE.LAYER_EXPORT_EVENT_LOAD_LIMIT));
		PageRequest pageRequest = PageRequest.of(0, eventLoadLimit, Sort.by("id"));
		
		EventSpecificationMobile eventSpecificationMobile = new EventSpecificationMobile(layerId, startDate, finishDate, lastEventId);
		Page<EventItemMobile> eventItemMobilePage = masterDao.findAll(eventSpecificationMobile, pageRequest);
		
		List<EventItemMobile> eventItemMobileList = eventItemMobilePage.getContent();
		List<Integer> eventIdList = eventItemMobileList.stream().map(EventItemMobile::getId).collect(Collectors.toList());
		
	
		int startSize = 0;
		int endSize = 0;
		int lastSize = 0;	
		if(eventIdList.size() > 0) {
			List<EventMediaItemMobile> eventMediaItemMobileListAll = new ArrayList<>();
			while(endSize < eventIdList.size()) {
							
				startSize = lastSize; 
				endSize = lastSize + Statics.eventMediaListSize;
				
				if(endSize > eventIdList.size()) {
					endSize = eventIdList.size();
				}
				
				EventMediaSpecificationMobile eventMediaSpecificationMobile = new EventMediaSpecificationMobile(eventIdList.subList(startSize, endSize));
				List<EventMediaItemMobile> tempEventMediaItemMobileListAll = masterDao.findAll(eventMediaSpecificationMobile, Sort.by("id").ascending());
				eventMediaItemMobileListAll.addAll(tempEventMediaItemMobileListAll);
				lastSize += Statics.eventMediaListSize;
			}
			
			Map<Integer, List<EventMediaItemMobile>> eventMediaItemMobileGroup = eventMediaItemMobileListAll.stream().collect(Collectors.groupingBy(EventMediaItemMobile::getEventId));
			
			for (EventItemMobile eventItemMobile : eventItemMobileList) {
				eventItemMobile.setDbName(Statics.DEFAULT_DB_NAME);
				List<EventMediaItemMobile> eventMediaItemMobileList = eventMediaItemMobileGroup.get(eventItemMobile.getId());
				if(eventMediaItemMobileList == null) {
					continue;
				}
				eventMediaItemMobileList = eventMediaItemMobileList.stream().map(item -> getImageBase64Data(item)).collect(Collectors.toList());
				
				eventItemMobile.setEventMediaList(eventMediaItemMobileList);
			}
		
		}
		
		return eventItemMobileList;
	}
	

	public EventMediaItemMobile getImageBase64Data(EventMediaItemMobile eventMediaItemMobile) {
		
		String string = SettingsUtil.getString(SettingsE.MEDIA_PATH);
		
		File imageFile = new File(string, eventMediaItemMobile.getData());
			
		if(imageFile.exists()) {
			try {
				byte[] byteData = FileUtils.readFileToByteArray(imageFile);
				String base64ImageData = Base64.getEncoder().encodeToString(byteData);
				eventMediaItemMobile.setData(base64ImageData);
				return eventMediaItemMobile;
			} catch (IOException e) {

				log.error(e);
			}
		}
		
		return null;
	}
		
	public Integer writeTileDataToFile(Date startDate, Date finishDate, Integer layerId, Integer layerExportId)  {
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		Integer eventAllExportCount = 0;
		try {	
			
			String jsonResult = "";
			String rootPath = fileCreateRooth(layerExportId);
			int fileIndex = 1;
			
			for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
								
				Integer lastEventId = null;
				List<EventItemMobile> eventList= eventCallable(startDate, finishDate, layerId, layerExportId, dataSourceInfo, lastEventId);				
			
				while(eventList.size() > 0) {
					eventAllExportCount += eventList.size();
					
					String encryptedItem = null;
					List<ExportEncryptItem> encryptList = new ArrayList<>();
					
					for(EventItemMobile eventItemMobile : eventList) {
						
						encryptedItem = ExportItemEncryptUtils.encryptGeneric(eventItemMobile);
						
						ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
						exportEncrypItem.setData(encryptedItem);
						encryptList.add(exportEncrypItem);
						
					}
					
					jsonResult = objectMapper.writeValueAsString(encryptList);//eventList
					File file = fileCreate(fileIndex, rootPath);				
					FileWriter fileToWrite = new FileWriter(file);
							
					fileToWrite.write(jsonResult);			
					fileToWrite.close();
								
					lastEventId = eventList.get(eventList.size()-1).getId();
					eventList = eventCallable(startDate, finishDate, layerId, layerExportId, dataSourceInfo, lastEventId);
					
					fileIndex++;				
										
				}									
				
			}
			

			List<EventItemMobile> eventList  = generateEventData(masterDao, layerId, startDate, finishDate, null);
			eventList.stream().sorted(Comparator.comparingInt(EventItemMobile::getId)).collect(Collectors.toList());
		
			while(eventList.size() > 0) {
				
				eventAllExportCount += eventList.size();
				String encryptedItem = null;
				
				List<ExportEncryptItem> encryptList = new ArrayList<>();
				
				for(EventItemMobile eventItemMobile : eventList) {
					
					encryptedItem = ExportItemEncryptUtils.encryptGeneric(eventItemMobile);
					
					ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
					exportEncrypItem.setData(encryptedItem);
					encryptList.add(exportEncrypItem);

				}
	
				jsonResult = objectMapper.writeValueAsString(encryptList);
				File file = fileCreate(fileIndex, rootPath);				
				FileWriter fileToWrite = new FileWriter(file);
						
				fileToWrite.write(jsonResult);			
				fileToWrite.close();
				
				Integer lastEventId = eventList.get(eventList.size()-1).getId();
				eventList  = generateEventData(masterDao, layerId, startDate, finishDate, lastEventId);
				

				fileIndex++;
			}

			
		} catch (IOException e) {

			log.error(e);
		}
		
		return eventAllExportCount;
		
	
	}

	public File fileCreate(int tileFileIndex, String filePathRoot) throws IOException {
		
		String filePath = filePathRoot + "/event_"+ tileFileIndex +".json";
		File file = new File(filePath);		
		file.createNewFile();
		return file;
	}
	
	private String fileCreateRooth(Integer layerExportId) {
		
		String rootPath = SettingsUtil.getString(SettingsE.LAYER_EXPORT_FILE_PATH);
		File fileCreate = new File(rootPath);
		fileCreate.mkdirs();		
		
		fileCreate = new File(rootPath+layerExportId + "/event");
		fileCreate.mkdirs();
		
		return rootPath+layerExportId + "/event";
	}
	
	private List<EventItemMobile> eventCallable(Date startDate, Date finishDate, Integer layerId, Integer layerExportId, DataSourceInfo dataSourceInfo, Integer lastEventId) {
		
		//eventCallable
		ExecutorService executorEvent = Executors.newFixedThreadPool(10);
		List<Future<List<EventItemMobile>>> futureOrderEventList = new ArrayList<>();
				
									
		CallableEventService callableEvent = new CallableEventService(startDate, finishDate, layerId, dataSourceInfo, layerExportId, lastEventId);
		futureOrderEventList.add(executorEvent.submit(callableEvent));
				
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
		return totalEventList;	
	}
}
