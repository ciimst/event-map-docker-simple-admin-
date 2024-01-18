package com.imst.event.map.admin.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.admin.utils.ExportItemEncryptUtils;
import com.imst.event.map.admin.vo.TileXYItem;
import com.imst.event.map.admin.vo.mobile.ExportEncryptItem;
import com.imst.event.map.admin.vo.mobile.MobileTileItem;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TileService {

	private long downloadTileCount = 0;
	private long useCacheTileCount = 0;
	
	public List<MobileTileItem> prepareTileDataForZoomLevel(String tileServerUrl, String s, Integer z, Integer minX, Integer maxX, Integer minY, Integer maxY, String filePath, Integer tileServerId){
		

		List<MobileTileItem> mobileTileItemList = new ArrayList<>();
		
		if(maxX < minX) {
			
			Integer tempNewMinX = maxX;
			maxX = minX;
			minX = tempNewMinX;
		}
		
		if(maxY < minY) {
			
			Integer tempNewMinY = maxY;
			maxY = minY;
			minY = tempNewMinY;
		}
		
		// Sınırdaki olayların etrafındaki tileları almak için yapılmıştır.
		Integer MIN_EXPECTED_DIF = 6;
		Integer difX = maxX - minX;
		if(difX < MIN_EXPECTED_DIF) {
			maxX += (MIN_EXPECTED_DIF - difX) / 2 + 1;
			minX -= (MIN_EXPECTED_DIF - difX) / 2 + 1;
		}
		
		Integer difY = maxY - minY;
		if(difY < MIN_EXPECTED_DIF) {
			maxY += (MIN_EXPECTED_DIF - difY) / 2 + 1;
			minY -= (MIN_EXPECTED_DIF - difY) / 2 + 1;
		}
		
		
		downloadTileCount = 0;
		useCacheTileCount = 0;
		
		for (int x = minX; x <= maxX; x++) {
			
			for (int y = minY; y <= maxY; y++) {

				// Bu değerlerden sonra tile bulunmamaktadır.
				double zpow = Math.pow(2, z);
				if(x >= zpow || x < 0) {
					break;
				}
				
				if(y >= zpow || y < 0) {
					continue;
				}

				MobileTileItem mobileTileItem = new MobileTileItem();


				long generateTileIndex = generateTileIndex(z, x, y);
				mobileTileItem.setKey(generateTileIndex);
				mobileTileItem.setProvider("event-map");


				byte[] tileData = getTileData(tileServerUrl, z, x, y, s, true, filePath, tileServerId);
				if(tileData == null) {
					continue;
				}
				mobileTileItem.setTile(Base64.getEncoder().encodeToString(tileData));
				

				mobileTileItemList.add(mobileTileItem);
			}
		}
		
		log.info(String.format("Finish zoom : [z : %s] [download count : %s] - [using cache count : %s]", z, downloadTileCount, useCacheTileCount));
		
		return mobileTileItemList;
	}
	
	public String writeTileDataToFile(List<MobileTileItem> mobileTileItemList, String filePath) {
		
		ObjectMapper objectMapper = new ObjectMapper();

		try {
		
			String jsonResult = objectMapper.writeValueAsString(mobileTileItemList);
			
			File fileCreate = new File(filePath);
			fileCreate.mkdir();
			
			try {				
							
				File file = fileCreate(1, filePath);				
				FileWriter fileToWrite = new FileWriter(file);
				
				int tileFileIndex = 2;				
				
				List<MobileTileItem> mobileTileItem = new ArrayList<>();	
				
				String encryptedItem = null;
				List<ExportEncryptItem> encryptList = new ArrayList<>();
				
				for(MobileTileItem item : mobileTileItemList) {
																							
					mobileTileItem.add(item);
					if(mobileTileItem.size() > 1000) {
						
						for(MobileTileItem ItemMobileTile : mobileTileItem) {
							
							//encryptedItem = ExportItemEncryptUtils.encryptedPassword(ItemMobileTile);
							encryptedItem = ExportItemEncryptUtils.encryptGeneric(ItemMobileTile);
							ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
							exportEncrypItem.setData(encryptedItem);
							encryptList.add(exportEncrypItem);
							
						}
						
						String jsonData = objectMapper.writeValueAsString(encryptList);
						fileToWrite.write(jsonData);
						
						mobileTileItem = new ArrayList<>();
						fileToWrite.close();
						file = fileCreate(tileFileIndex, filePath);						
						fileToWrite = new FileWriter(file);											
						tileFileIndex++;
					}														
				}
				
				if (mobileTileItem.size() > 0) {
					
					for(MobileTileItem ItemMobileTile : mobileTileItem) {
						
						encryptedItem = ExportItemEncryptUtils.encryptGeneric(ItemMobileTile);
						
						ExportEncryptItem exportEncrypItem = new ExportEncryptItem();
						exportEncrypItem.setData(encryptedItem);
						encryptList.add(exportEncrypItem);
						
					}
					
					
					String jsonData = objectMapper.writeValueAsString(encryptList);
					fileToWrite.write(jsonData);					
					fileToWrite.close();																	
				}
				
			} catch (IOException e) {

				log.error(e);
			}
			
			return jsonResult;
		} catch (JsonProcessingException e) {

			log.error(e);
		}
		
		return null;
	}
	
	public File fileCreate(int tileFileIndex, String filePathRoot) throws IOException {
		
		String filePath = filePathRoot + "/tile_"+tileFileIndex+".json";
		File file = new File(filePath);		
		file.createNewFile();
		return file;
	}
	
    public TileXYItem getTileXY(double lat, double lon, int zoom) {
        
    	int xtile = (int) Math.floor( (lon + 180) / 360 * (1<<zoom) );
        int ytile = (int) Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) );
        
        TileXYItem pointXYItem = new TileXYItem(xtile, ytile);
        return pointXYItem;
    }
	
	private byte[] getTileData(String urlStr, long z, long x, long y, String s, boolean checkFolder, String filePath,  Integer tileServerId) {
		
		String tileFilePath = String.format(filePath+tileServerId+"/"+"%s/%s/%s.png", z, x, y); //SettingsUtil.getString(SettingsE.LAYER_TILE_ROOT_PATH )
		File tileFile = new File(tileFilePath);
		if(checkFolder) {
			
			if(tileFile.exists()) {
				try {

					useCacheTileCount++;
					return FileUtils.readFileToByteArray(tileFile);
				} catch (IOException e) {

					log.error(e);
				}
			}
		}
		
		
		String formatedUrl = formatUrl(urlStr, z, x, y, null);
		URL url;
		try {

			url = new URL(formatedUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setConnectTimeout(30000);
			urlConnection.setReadTimeout(30000);

			byte[] tileDataByteArray = IOUtils.toByteArray(urlConnection);
			downloadTileCount++;
			
			if(checkFolder) {
				
				try {
					FileUtils.writeByteArrayToFile(tileFile, tileDataByteArray);
				} catch (Exception e) {
					log.error(e);
				}
			}

			return tileDataByteArray;
		} catch (MalformedURLException e) {
			
			log.debug(e);
		} catch (IOException e) {

			log.error("Failed : " + e.getMessage());
		}
		
		return null;
	}
	
	public String formatUrl(String url, long z, long x, long y, String s) {
		
		String formattedUrl = url;
		formattedUrl = formattedUrl.replace("{x}", x + "");
		formattedUrl = formattedUrl.replace("{y}", y + "");
		formattedUrl = formattedUrl.replace("{z}", z + "");
		
		if(s == null) {
			formattedUrl = formattedUrl.replace("{s}.", "");
		}else {
			formattedUrl = formattedUrl.replace("{s}", s);
		}
		
				
		return formattedUrl;
	}
	
	private static long generateTileIndex(long z, long x, long y) {
		
		final long index = (((z << z) + x) << z) + y;
		
		return index;
	}
	
}
