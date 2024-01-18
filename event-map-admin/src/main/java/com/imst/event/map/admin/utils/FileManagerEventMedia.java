package com.imst.event.map.admin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Builder
public class FileManagerEventMedia {
	
	private File basePath;
	private String mediaPath;
	private List<MultipartFile> files;
	@Builder.Default private boolean tree = true;
	@Builder.Default private boolean temp = true; //isInTempFolder
	public File fileToWrite;
	
	public UploadResponse upload() throws IOException {
		
//		String path = (isTree() ? DateUtils.formatNow(DateUtils.FOLDER_TREE) : ""); // 2020/08/19/temp
		String path = (isTree() ? "" : ""); // 2020/08/19/temp
		this.basePath = path.isEmpty() ? new File(getMediaPath()) : new File(getMediaPath(), path); // \event-map\images\media\2020\08\19\temp
		FileUtils.forceMkdir(getBasePath());
		return uploadMultipartFiles();
	}
	
	private UploadResponse uploadMultipartFiles() {

		HashMap<String, String> success = new HashMap<>();
		HashMap<String, String> failed = new HashMap<>();

		for (MultipartFile file : getFiles()) {

			String originalFilename = file.getOriginalFilename();
			if (originalFilename == null) {
				log.debug("filename null");
				continue;
			}
			

			if (file.isEmpty()) {

				log.debug("Empty file upload skip. [{}]", originalFilename);
				//boş gelenleri yazmıyoruz
				continue; //next pls
			}
			
		
			try {
				fileToWrite = getTempFile(getBasePath(), getExtension(originalFilename)); // \event-map\images\media\2020\08\19\temp\$temp$_4487534627795672520_2020-08-19_16-35-43.PNG
			} catch (Exception e) {
				continue;
			}
			
			success.put(originalFilename, fileToWrite.getName());

			try (OutputStream out = new FileOutputStream(fileToWrite)) {

				IOUtils.copyLarge(file.getInputStream(), out);				

			} catch (Exception e) {

				failed.put(originalFilename, fileToWrite.getName());
				log.debug("Empty file copy from inputStream failed skip. [{}]", originalFilename);
				log.debug(e);
			}
			
		}
		
		return new UploadResponse(success, failed);
	}
	
	private String getExtension(String path) {
		
		try {
			String extension = null;
			int index = path.lastIndexOf(".") + 1;
			if (index <= path.length()) {
				extension = path.substring(index);
			}
			return extension.toLowerCase();
		} catch (Exception e) {
			return "unparsable";
		}
	}
	
	public static String getFolderTree() {
		return DateUtils.formatNow(DateUtils.FOLDER_TREE);
	}
	
	private File getTempFile(File dir, String extension) throws IOException {
		return File.createTempFile("$temp$_", "_".concat(DateUtils.formatNow(DateUtils.FILE_NAME)).concat(".").concat(extension), dir);
	}	
	
	@Getter
	public static class UploadResponse {
		HashMap<String, String> success = new HashMap<>();
		HashMap<String, String> failed = new HashMap<>();
		
		public UploadResponse(HashMap<String, String> success, HashMap<String, String> failed) {
			
			this.success = success;
			this.failed = failed;
		}
	}
}
