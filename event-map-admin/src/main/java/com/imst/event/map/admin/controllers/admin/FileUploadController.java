package com.imst.event.map.admin.controllers.admin;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.admin.utils.FileManager;
import com.imst.event.map.admin.utils.SettingsUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/upload")
public class FileUploadController {
	
	@RequestMapping("/aad")
	public ResponseEntity<?> single(HttpServletRequest request) {
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/single")
	public ResponseEntity<?> singleFileUpload(MultipartFile file, MultipartHttpServletRequest request) {

		return multiFileUpload(Arrays.asList(file), request);
	}
	
	@RequestMapping("/multiple")
	public ResponseEntity<?> multiFileUpload(List<MultipartFile> files, HttpServletRequest request) {
		
		try {
			if (files == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			
			FileManager.FileManagerBuilder builder = FileManager.builder();
			FileManager fileManager = builder.mediaPath(SettingsUtil.settings.get("mediaPath")).files(files).build();
			
			FileManager.UploadResponse uploadResponse = fileManager.upload();
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			return new ResponseEntity<>(objectMapper.writeValueAsString(uploadResponse), HttpStatus.OK);
			
		} catch (IOException e) {
			
			log.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
}
