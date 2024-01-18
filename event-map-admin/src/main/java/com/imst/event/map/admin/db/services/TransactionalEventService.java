package com.imst.event.map.admin.db.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventMediaRepository;
import com.imst.event.map.admin.db.repositories.EventRepository;
import com.imst.event.map.admin.db.repositories.EventTagRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.LayerRepository;
import com.imst.event.map.admin.db.repositories.TagRepository;
import com.imst.event.map.admin.services.S3Service;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.utils.exceptions.ApiException;
import com.imst.event.map.admin.vo.api.ApiEventGroupItem;
import com.imst.event.map.admin.vo.api.ApiEventItem;
import com.imst.event.map.admin.vo.api.ApiEventMediaItem;
import com.imst.event.map.admin.vo.api.ApiEventTagItem;
import com.imst.event.map.admin.vo.api.ApiEventTypeItem;
import com.imst.event.map.admin.vo.api.ApiLayerItem;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventMedia;
import com.imst.event.map.hibernate.entity.EventTag;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.Tag;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransactionalEventService {
	

	@Autowired
	private EventMediaRepository eventMediaRepository;
	@Autowired
	private EventTagRepository eventTagRepository;
	@Autowired
	private LayerRepository layerRepository;
	@Autowired
	private EventGroupRepository eventGroupRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventTypeRepository eventTypeRepository;
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private S3Service s3Service;
	private static final String tempPrefixRegex = "\\$temp\\$_";
	
	private Timestamp mNowT;
	
	@Transactional(transactionManager = "masterTransactionManager")
	public ApiEventItem saveEvent(Event event, ApiEventItem apiEventItem, Timestamp nowT) {
	
		this.mNowT = nowT;
		
		EventGroup eventGroup = handleEventGroup(apiEventItem.getEventGroupItem());
		EventType eventType = handleEventType(apiEventItem.getEventTypeItem());
		
		event.setEventGroup(eventGroup);
		event.setEventType(eventType);
		
		Event saved = eventRepository.save(event);
		
	
		List<EventTag> eventTags = eventTagRepository.findAllByEventId(saved.getId());
		
		/*one to manyleri siliyoruz(eventMedia hariç file işlemi olduğu için)*/
		
		eventTagRepository.deleteAll(eventTags);
			
		
		List<ApiEventTagItem> apiEventTagItems = handleEventTags(apiEventItem.getEventTags(), saved);
		List<ApiEventMediaItem> apiEventMediaItems = handleEventMedias(apiEventItem.getEventMedias(), saved);
		
		apiEventMediaItems.parallelStream().forEach(apiEventMediaItem -> {//path fixes
			String path = new File(SettingsUtil.settings.get("mediaLinkPrefix"), apiEventMediaItem.getPath()).getPath();
			String cover = new File(SettingsUtil.settings.get("mediaLinkPrefix"), apiEventMediaItem.getCoverImagePath()).getPath();
			apiEventMediaItem.setCoverImagePath(cover);
			apiEventMediaItem.setPath(path);
		});
		
		ApiEventItem apiEventItemResponse = new ApiEventItem(saved);

		apiEventItemResponse.setEventTags(apiEventTagItems);
		apiEventItemResponse.setEventMedias(apiEventMediaItems);
		
		return apiEventItemResponse;
	}
	
	
	private List<ApiEventTagItem> handleEventTags(List<ApiEventTagItem> apiEventTags, Event save) {
		
		if (apiEventTags != null) {
			
			List<EventTag> collect = apiEventTags.stream().map(apiEventTagItem -> {
				EventTag eventTag = new EventTag();
				eventTag.setEvent(save);
				
				Tag tag;
				if (apiEventTagItem.getTagId() != null) {
					tag = tagRepository.findById(apiEventTagItem.getTagId()).orElse(null);
					if (tag == null) {
						throw new ApiException("Unknown tagId.");
					}
				} else {
					
					if (StringUtils.isBlank(apiEventTagItem.getTagName())) {
						throw new ApiException("Tag name is null or empty. You need to define either tag.id or tag.name.");
					}
					
					String tagName = apiEventTagItem.getTagName().trim();
					//ismin tekilliği
					tag = tagRepository.findOneByName(tagName).orElse(null);
					
					if (tag == null) {
						
						tag = new Tag(tagName);
						tagRepository.save(tag);
					}
				}
				
				eventTag.setTag(tag);
				
				return eventTag;
			}).collect(Collectors.toList());
			
			Iterable<EventTag> eventTags = eventTagRepository.saveAll(collect);
			return StreamSupport.stream(eventTags.spliterator(), false)
					.map(eventTag -> new ApiEventTagItem(eventTag.getTag()))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}
	
	private List<ApiEventMediaItem> handleEventMedias(List<ApiEventMediaItem> apiEventMedias, Event event) {
		
		List<EventMedia> dbEventMedias = eventMediaRepository.findAllByEventId(event.getId());
		List<Integer> idListToDelete = dbEventMedias.stream().map(EventMedia::getId).collect(Collectors.toList());
		
		List<EventMedia> collect = new ArrayList<>();
		
		if (apiEventMedias != null) {
			
			collect = apiEventMedias.stream()
					.map(apiEventMediaItem -> {
						
						EventMedia eventMedia;
						
						String path = apiEventMediaItem.getPath();
						String coverPath = apiEventMediaItem.getCoverImagePath();
						
						if (apiEventMediaItem.getId() != null) { //update yok direk id kullanıyoruz
							
							eventMedia = dbEventMedias.stream()
									.filter(eventMedia1 -> eventMedia1.getId().equals(apiEventMediaItem.getId()))
									.findFirst().orElse(null);
							
							if (eventMedia == null) {
								throw new ApiException("ApiEventMediaItem.id not found.");
							}
							
							idListToDelete.removeIf(id -> id.equals(eventMedia.getId()));
							
						} else {//yeni
							
							eventMedia = new EventMedia();
							eventMedia.setEvent(event);
							
							if (isValidUrl(coverPath)) {//url ise download //can be null
								
								coverPath = downloadFile(coverPath);
								
							} else {
								
								coverPath = writeBase64ToFile(coverPath);
							}
							
							eventMedia.setCoverImagePath(s3Service.saveTodo(coverPath).getUrl());
							
							if (isValidUrl(path)) {//url ise download
								
								path = downloadFile(path);
								
							} else {
								
								path = writeBase64ToFile(path);
							}
							
							eventMedia.setPath(s3Service.saveTodo(path).getUrl());
							
							if (!StringUtils.isBlank(coverPath)) {
								
								File pathFileToDelete = new File(getBaseDir(),path);
								deleteFile(pathFileToDelete.getAbsolutePath());
							}
							
							if (!StringUtils.isBlank(path)) {
								
								File coverFileToDelete = new File(getBaseDir(),coverPath);
								deleteFile(coverFileToDelete.getAbsolutePath());
							}
							
						}
						
						if (StringUtils.isBlank(eventMedia.getPath())) {
							throw new ApiException("Path cannot be empty.");
						}
						
						eventMedia.setIsVideo(apiEventMediaItem.isVideo());
						return eventMedia;
					}).collect(Collectors.toList());
		}
		
		if (!idListToDelete.isEmpty()) {
			
			File baseDir = getBaseDir();
			List<EventMedia> deleteList = dbEventMedias.stream().filter(eventMedia -> idListToDelete.contains(eventMedia.getId())).collect(Collectors.toList());
			for (EventMedia eventMedia : deleteList) {
				
				if (StringUtils.isBlank(eventMedia.getPath())) {
					
					File pathFileToDelete = new File(baseDir, eventMedia.getPath());
					deleteFile(pathFileToDelete.getAbsolutePath());
				}
				
				if (StringUtils.isBlank(eventMedia.getCoverImagePath())) {
					
					File coverFileToDelete = new File(baseDir, eventMedia.getCoverImagePath());
					deleteFile(coverFileToDelete.getAbsolutePath());
				}
			}
			
			eventMediaRepository.deleteAll(deleteList);
		}
		
		if (!collect.isEmpty()) {
			
			Iterable<EventMedia> eventMedias = eventMediaRepository.saveAll(collect);
			
			return StreamSupport.stream(eventMedias.spliterator(), false)
					.map(ApiEventMediaItem::new)
					.collect(Collectors.toList());
		}
		
		
		return Collections.emptyList();
	}
	
	
	private EventType handleEventType(ApiEventTypeItem eventTypeItem) {
		
		EventType eventType;
		if (eventTypeItem.getId() == null) {
			
			EventType newEventType = new EventType();
			newEventType.setName(eventTypeItem.getName());
			newEventType.setImage(eventTypeItem.getImage());
			newEventType.setCode(eventTypeItem.getCode());
			
			if (StringUtils.isAnyBlank(newEventType.getName(), newEventType.getImage())) {
				
				throw new ApiException("Couldn't find eventTypeName or eventTypeImage for new EventType.");
			}
			
			eventType = eventTypeRepository.save(newEventType);
			
		} else {
			
			eventType = eventTypeRepository.findById(eventTypeItem.getId()).orElse(null);
		}
		
		if (eventType == null) {
			throw new ApiException("EventType not found. Please be sure eventTypeItem.id exists.");
		}
		
		return eventType;
	}
	
	private EventGroup handleEventGroup(ApiEventGroupItem eventGroupItem) {
		
		EventGroup eventGroup;
		
		if (eventGroupItem.getId() == null) {
			
			if (StringUtils.isBlank(eventGroupItem.getName())) {
				throw new ApiException("EventGroup name is null or empty. You need to define either eventGroupItem.id or eventGroupItem.name.");
			}
			
			if (StringUtils.isBlank(eventGroupItem.getColor())) {
				
				throw new ApiException("EventGroup color is null or empty. You need to define either eventGroupItem.id or eventGroupItem.color.");
			}
			
			EventGroup newEventGroup = new EventGroup();
			newEventGroup.setName(eventGroupItem.getName());
			newEventGroup.setColor(eventGroupItem.getColor());
			
			ApiLayerItem layerItem = eventGroupItem.getLayerItem();
			if (layerItem == null) {
				throw new ApiException("LayerItem not found. You need to define either eventGroupItem.id or eventGroupItem.layerItem.");
			}
			
			Layer layer;
			
			if (layerItem.getId() == null) {
				
				if (StringUtils.isBlank(layerItem.getName())) {
					
					throw new ApiException("Layer name is null or empty. You need to define either layer.id or layer.name.");
				}
				
				Layer newLayer = new Layer();
				newLayer.setName(layerItem.getName());
				newLayer.setCreateDate(mNowT);
				newLayer.setUpdateDate(mNowT);
				boolean state = layerItem.getState() == null ? true : layerItem.getState();
				newLayer.setState(state);
				
				layer = layerRepository.save(newLayer);
				
			} else {
				
				layer = layerRepository.findById(layerItem.getId()).orElse(null);
			}
			
			if (layer == null) {
				throw new ApiException("Layer not found. Please be sure that the layerItem.id exists.");
			}
			
			newEventGroup.setLayer(layer);
			
			eventGroup = eventGroupRepository.save(newEventGroup);
			
		} else {
			
			eventGroup = eventGroupRepository.findById(eventGroupItem.getId()).orElse(null);
		}
		
		if (eventGroup == null) {
			throw new ApiException("EventGroup not found. Please be sure eventGroupItem.id exists.");
		}
		
		return eventGroup;
	}
	
	
	
	
	
	private String downloadFile(String urlString) {
		
		File realFile;
		String tree = getFolderTree();
		String tempFileAbsPath = null;
		
		try {
			
			File fileToWrite = createTempFile(getExtension(urlString));
			tempFileAbsPath = fileToWrite.getAbsolutePath();
			
			try (OutputStream out = new FileOutputStream(fileToWrite)) {
				
				URL url = new URL(urlString);
				URLConnection urlConnection = url.openConnection();
		        urlConnection.addRequestProperty("User-Agent", "Mozilla");
		        
				IOUtils.copyLarge(urlConnection.getInputStream(), out);
			}
			
			realFile = tempFileToRealFile(tempFileAbsPath);
			
			Files.move(fileToWrite.toPath(), realFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
		} catch (ApiException ae) {
			
			throw ae;
			
		} catch (Exception e) {
			log.debug(e);
			throw new ApiException("Media cannot be saved. Please contact system administrator.");
			
		} finally {
			
			deleteTempFile(tempFileAbsPath);
		}
		
		return new File(realFile.getName()).getPath();
	}
	
	
	private String writeBase64ToFile(String base64String) {
		
		if (StringUtils.isAnyBlank(base64String)) {
			return null;
		}
		
		File realFile;
		String tree = getFolderTree();
		
		String tempFileAbsPath = null;
		
		try {
			
			File fileToWrite = createTempFile("bmp");
			tempFileAbsPath = fileToWrite.getAbsolutePath();
			byte[] data = Base64.getDecoder().decode(base64String.split(",")[1]);

			try (OutputStream stream = new FileOutputStream(fileToWrite)) {
				
				stream.write(data);
			}
			
			realFile = tempFileToRealFile(tempFileAbsPath);
			
			Files.move(fileToWrite.toPath(), realFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
		} catch (ApiException ae) {
			
			throw ae;
		} catch (IllegalArgumentException iae) {
			
			log.debug(iae);
			throw new ApiException("Media cannot be saved. Please be sure that the given string is base64.");
		} catch (Exception e) {
			
			log.debug(e);
			throw new ApiException("Media cannot be saved. Please contact system administrator.");
		} finally {
			
			deleteTempFile(tempFileAbsPath);
		}
		
		return new File("", realFile.getName()).getPath();
	}
	
	private void deleteFile(String deleteFilePath) {
		
		if (StringUtils.isBlank(deleteFilePath)) {
			return;
		}
		File file = new File(deleteFilePath);
		try {
			Files.delete(file.toPath());
		} catch (IOException ignore) {}
	}
	
	private void deleteTempFile(String deleteFilePath) {
		
		if (StringUtils.isBlank(deleteFilePath)) {
			return;
		}
		//sadece templeri silmek için
		if (Pattern.compile(tempPrefixRegex).matcher(deleteFilePath).find()) {
			
			deleteFile(deleteFilePath);
		}
	}
	
	private boolean isValidUrl(String url) {
		
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception ignore) {
			return false;
		}
	}
	
	private String getExtension(String path) {
		
		try {
			String extension = null;
			int index = path.lastIndexOf(".") + 1;
			if (index <= path.length()) {
				extension = path.substring(index);
			}
			return extension;
		} catch (Exception e) {
			return "unparsable";
		}
	}
	
	private String getFolderTree() {
		return DateUtils.formatNow(DateUtils.FOLDER_TREE);
	}
	
	private File createTempFile(String extension) throws IOException {
		
		File dir = getDir();
		return getTempFile(dir, extension);
	}
	private File createTempFile(String tree, String extension) throws IOException {
		
		File dir = getDir(tree);
		return getTempFile(dir, extension);
	}
	
	private File getDir() {
		
		File dir = new File(SettingsUtil.settings.get("mediaPath"));
		
		if (!dir.exists() && !dir.mkdirs()) {
			//fail to create directory
			throw new ApiException("Failed to create directory. Please contact system administrator.");
		}
		return dir;
	}

	private File getDir(String tree) {
		
		File dir = new File(SettingsUtil.settings.get("mediaPath"), tree);
		
		if (!dir.exists() && !dir.mkdirs()) {
			//fail to create directory
			throw new ApiException("Failed to create directory. Please contact system administrator.");
		}
		return dir;
	}
	
	private File getBaseDir() {
	
		return new File(SettingsUtil.settings.get("mediaPath"));
	}
	
	private File tempFileToRealFile(String tempFileAbsPath) {
		String realFilePath = tempFileAbsPath.replaceFirst(tempPrefixRegex, "file_");
		return new File(realFilePath);
	}
	
	private File getTempFile(File dir, String extension) throws IOException {
		return File.createTempFile("$temp$_", "_".concat(DateUtils.formatNow(DateUtils.FILE_NAME)).concat(".").concat(extension), dir);
	}
	
	
}
