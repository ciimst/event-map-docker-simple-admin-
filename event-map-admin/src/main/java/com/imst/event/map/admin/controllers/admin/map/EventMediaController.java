package com.imst.event.map.admin.controllers.admin.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.imst.event.map.admin.services.S3Service;
import com.imst.event.map.admin.utils.FileManagerEventMedia;
import com.imst.event.map.admin.utils.FileManagerEventMedia.FileManagerEventMediaBuilder;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.vo.s3.S3UploadResponseItem;
import com.imst.event.map.hibernate.entity.EventMedia;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class EventMediaController {

	@Autowired S3Service s3Service;
	
	@RequestMapping("/admin/map/event-basic/add")
	public List<String> multiFileUpload(@RequestParam("file") List<MultipartFile> files) throws Exception {
		
		List<String> path = new ArrayList<>();
		
		try {
			
			FileManagerEventMediaBuilder builder = FileManagerEventMedia.builder();
			FileManagerEventMedia fileManager = builder.mediaPath(SettingsUtil.settings.get("mediaPath")).files(files).build(); // \event-map\images\media\2020\08\19\temp							
			fileManager.upload();
			
			
			
			List<String> mediaPathList = eventMediaFileOperations(fileManager.fileToWrite.getPath());	
			
			if(mediaPathList != null) {
				
				for(String mediaPath : mediaPathList) {
					
					EventMedia eventMedia = new EventMedia();
					
					if(mediaPath.endsWith(".mp4") || mediaPath.endsWith(".mp4/")) {
						eventMedia.setIsVideo(true);
					}else {
						eventMedia.setIsVideo(false);
					}
					
					S3UploadResponseItem pathItem = s3Service.saveTodo(mediaPath);
					
					path.add(pathItem.getUrl());
					
					//geçici olarak oluşturulan dosya siliniyor.
					File deleteFile = new File(SettingsUtil.settings.get("mediaPath") + mediaPath);				///*SettingsE.MEDIA_PATH) +		
					if(deleteFile.exists()) {	
						
						deleteFile.delete();												 
					}
				}
			}
			
			
			
			
		} catch (Exception e) {
			
			System.out.println();
			log.error(e);
			throw e;
			//return null;
		}
		
		return path; //\event-map\images\media\2020\08\19\$temp$_4487534627795672520_2020-08-19_16-35-43.PNG
		
	}
	
	
	
	private List<String> eventMediaFileOperations(String mediaPaths) {
		
		if(mediaPaths == null) {
			return null;
		}
		
		String[] paths = mediaPaths.split(",");		
		List<String> mediaPath = Arrays.asList(paths); 
		
		List<String> eventMediaPaths = new ArrayList<>();
		for(String key : mediaPath) {					
		
			String filename = "";
			int index = key.lastIndexOf("$") + 1;
			if (index <= key.length()) {
				filename = key.substring(index);
			}
			
			String [] filenameSplit = filename.split("/");
			filename = filenameSplit[0];	
		
			filename = "$temp$" + filename;
			try {
	             eventMediaPaths.add(filename);
	            				           	            
	        }catch (Exception e){
	            log.error("Hata Kopyalama Başarısız");
	            log.error(e);
	        }		
	
		}
		return eventMediaPaths;
		 
		
	}
	

	
}
