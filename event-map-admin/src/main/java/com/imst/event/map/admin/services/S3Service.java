package com.imst.event.map.admin.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.imst.event.map.admin.constants.SettingsE;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.MyStringUtils;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.admin.vo.s3.S3UploadResponseItem;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class S3Service {


	@Value("${s3BucketName}")
	private String s3BucketName;

	@Value("${s3DumpsPath}")
	private String s3DumpsPath;

	@Autowired 
	private AmazonS3 amazonS3;

	Region AWS_REGION = Region.EU_North_1;

	private static final String WORKDIR = "/event-map/dumps/";	

	public S3UploadResponseItem saveTodo(String uniqPath) {


		S3UploadResponseItem imageS3Path = new S3UploadResponseItem();
		Boolean isDump = false;
		try {

			if(uniqPath.indexOf(".dump") != -1) {
				isDump = true;
			}

			if (isDump) {
				uniqPath = WORKDIR + uniqPath;
			}
			else {
				uniqPath = SettingsUtil.settings.get("mediaPath") + uniqPath;
			}
			File file = new File(uniqPath);		

			if (!file.exists()) {
				throw new IllegalStateException("Cannot upload empty file");
			}

			FileInputStream fis = new FileInputStream(file);

			String fileName = file.getName();
			String folderTree = DateUtils.formatNow(DateUtils.FOLDER_TREE);


			String contentType = new MimetypesFileTypeMap().getContentType( file.getName() );
			long size = Files.size(Paths.get(uniqPath));			

			Map<String, String> metadata = new HashMap<>();
			metadata.put("Content-Type", contentType);
			metadata.put("Content-Length", String.valueOf(size));

			folderTree = MyStringUtils.getStartAndEndWithSubstring(folderTree);


			String path = String.format("%s%s", s3BucketName, String.format("%s%s", SettingsUtil.settings.get("mediaPath"), folderTree));
			fileName = fileName.replace(DateUtils.formatNow(DateUtils.FOLDER_TREE), "");//String.format("%s", fileName);//file.getOriginalFilename()
			if (isDump) {
				path = String.format("%s%s", s3BucketName, "/" + s3DumpsPath);
			}
			imageS3Path = this.upload(path, fileName, Optional.of(metadata), fis);

		} catch (IOException e) {
			log.error("Failed to upload file", e);
			throw new IllegalStateException("Failed to upload file", e);
		}

		return imageS3Path;

	}

	public S3UploadResponseItem upload(String path, String fileName, Optional<Map<String, String>> optionalMetaData, InputStream inputStream) {


		ObjectMetadata objectMetadata = new ObjectMetadata();
		optionalMetaData.ifPresent(map -> {

			if (!map.isEmpty()) {
				map.forEach(objectMetadata::addUserMetadata);
			}
		});


		try {

			PutObjectRequest request = new PutObjectRequest(path, fileName, inputStream, objectMetadata);
			amazonS3.putObject(request);
			URL s3Url = amazonS3.getUrl(path, fileName);

			ObjectMetadata objectMetadata2 = amazonS3.getObjectMetadata(path, fileName);

			return new S3UploadResponseItem(s3Url.toString(), objectMetadata2.getContentLength());

			//			return s3Url.toString();

		} catch (AmazonServiceException e) {
			log.error("Failed to upload the file", e);
			throw new IllegalStateException("Failed to upload the file", e);
		}
	}

	// Not Stream
	public byte[] download(String fileName) {
		try {
			S3Object object = amazonS3.getObject(s3BucketName, fileName);//event-map/images/media/2023/06/22/10.jpg
			S3ObjectInputStream objectContent = object.getObjectContent();
			return IOUtils.toByteArray(objectContent);
		} catch (AmazonServiceException | IOException e) {
			log.error("Failed to download the file", e);
			throw new IllegalStateException("Failed to download the file", e);
		}
	}

	// Stream
	public void downloadToFile(String fileName, File file) {
		
		try {
			
			S3Object object = amazonS3.getObject(s3BucketName, fileName);//event-map/images/media/2023/06/22/10.jpg
			S3ObjectInputStream objectContent = object.getObjectContent();
			
			FileUtils.copyToFile(objectContent, file);
			
		} catch (AmazonServiceException | IOException e) {
			log.error("Failed to download the file", e);
			throw new IllegalStateException("Failed to download the file", e);
		}
	}

	public void delete(String fileName) {

		try {

			fileName = MyStringUtils.getStartAndEndWithSubstring(fileName);
			amazonS3.deleteObject(s3BucketName, fileName);

		}catch(Exception ex) {
			log.error(ex);
		}

	} 

	public String move(String sourceFileName, String destinationFileName) {

		try {
			String folderTree = DateUtils.formatNow(DateUtils.FOLDER_TREE);
			folderTree = MyStringUtils.getStartAndEndWithSubstring(folderTree);
			String path = String.format("%s%s", s3BucketName, String.format("%s%s", SettingsUtil.settings.get("mediaPath"), folderTree));
			destinationFileName = destinationFileName.replace(DateUtils.formatNow(DateUtils.FOLDER_TREE), "");


			destinationFileName = MyStringUtils.getStartAndEndWithSubstring(destinationFileName);
			amazonS3.copyObject(path, sourceFileName, path, destinationFileName);
			URL s3Url = amazonS3.getUrl(path, destinationFileName);


			delete(SettingsUtil.settings.get("mediaPath") + DateUtils.formatNow(DateUtils.FOLDER_TREE) + sourceFileName);

			return s3Url.toString();

		}catch(Exception ex) {
			log.error(ex);
		}

		return "";
	} 


	public List<String> deleteOldestDump() {

		String deletedDumpName = "";

		List<String> deletedDumpNameList = new ArrayList<>();

		try {

			Integer count = SettingsUtil.getInteger(SettingsE.DATABASE_BACKUP_COUNT);

			List<S3ObjectSummary> s3objects = amazonS3.listObjects(s3BucketName, s3DumpsPath + "/").getObjectSummaries();

			List<S3ObjectSummary> s3ObjectsFiltered = s3objects.stream().filter(f -> f.getKey().equals(s3DumpsPath + "/")).collect(Collectors.toList());		 
			S3ObjectSummary folder = s3ObjectsFiltered.get(0);		
			s3objects.remove(folder);			 			 

			while (s3objects.size() > count) {	
				List<S3ObjectSummary> liste = s3objects.stream().sorted((o1,o2) -> o1.getLastModified().compareTo(o2.getLastModified())).distinct().limit(1).collect(Collectors.toList());
				S3ObjectSummary deletedDump = liste.get(0);
				s3objects.remove(deletedDump);
				this.delete(deletedDump.getKey());	

				deletedDumpName = deletedDump.getKey();
				deletedDumpName = deletedDumpName.replace(s3DumpsPath + "/", "");
				deletedDumpNameList.add(deletedDumpName);
				//				 List<Date> dat = s3objects.stream().map(S3ObjectSummary::getLastModified).sorted(Collections.reverseOrder(Date::compareTo)).distinct().limit(1).collect(Collectors.toList());		
			}

		} catch(Exception ex) {
			log.error(ex);

		}

		return deletedDumpNameList;

	}

	public boolean checkDump(String dumpName) {

		try {

			boolean ifExist = amazonS3.doesObjectExist(s3BucketName, s3DumpsPath + "/" + dumpName);		 
			return ifExist;	 

		}catch(Exception ex) {
			log.error(ex);
			return false;
		}


	}



}
