package com.imst.event.map.admin.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imst.event.map.admin.constants.SettingsE;
import com.imst.event.map.admin.services.S3Service;
import com.imst.event.map.admin.utils.MyStringUtils;
import com.imst.event.map.admin.utils.SettingsUtil;


@RequestMapping("/image")
@Controller
public class ImageController {
	@Autowired private S3Service s3Service;
	@Value("${s3BucketName}")
	private String s3BucketName;
	
	@Value("${s3Region}")
	private String s3Region;
	
	@RequestMapping(value = "/get/{path}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImageWithMediaType(@PathVariable(name = "path") String base64Path) throws IOException {

		return getImageWithPath(SettingsUtil.getString(SettingsE.MEDIA_PATH), base64Path);
	}
	
	@RequestMapping(value = "/static/{path}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImageWithStaticType(@PathVariable(name = "path") String base64Path) throws IOException {

		return getImageWithPath(SettingsUtil.getString(SettingsE.STATIC_IMAGE_ROOT_PATH), base64Path);
	}
	
	private byte[] getImageWithPath(String rootPath, String relativeBase64Path) throws IOException {

		String imageRelativePath = new String(Base64.getDecoder().decode(relativeBase64Path.getBytes()));
		imageRelativePath = imageRelativePath.replace("\\", "/");
		imageRelativePath = imageRelativePath.replace("..", "");
		
		byte[] imageByte = null;
		//static medialar için
		if(rootPath.equals(SettingsUtil.getString(SettingsE.STATIC_IMAGE_ROOT_PATH)) || (!imageRelativePath.contains(s3BucketName) && !imageRelativePath.contains(s3Region))) {
			String imageFullPath = Paths.get(rootPath, imageRelativePath).toString();
			
			File initialFile = new File(imageFullPath);
			InputStream in = new FileInputStream(initialFile);
	
			imageByte = IOUtils.toByteArray(in);
		}
		//s3 için.
		else if(rootPath.equals(SettingsUtil.getString(SettingsE.MEDIA_PATH))) {
			
			
			int index = imageRelativePath.indexOf(SettingsUtil.getString(SettingsE.MEDIA_PATH));
			String path = imageRelativePath.substring(index, imageRelativePath.length());
		
			path = MyStringUtils.getStartAndEndWithSubstring(path);

			imageByte = s3Service.download(path);
		}
		
		
		return imageByte;

	}
}
