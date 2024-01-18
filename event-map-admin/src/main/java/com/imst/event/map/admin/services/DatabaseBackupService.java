package com.imst.event.map.admin.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.imst.event.map.admin.db.repositories.DatabaseDumpRepository;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.vo.s3.S3UploadResponseItem;
import com.imst.event.map.hibernate.entity.DatabaseDump;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service
public class DatabaseBackupService {
	   
	@Autowired
	private DatabaseDumpRepository databaseDumpRepository;	
    
    @Autowired private S3Service s3Service;
    
	@Value("${pgrestore.location}")
	private String pgRestore;
	@Value("${pgdump.location}")
	private String pgDump;
	@Value("${s3DumpsPath}")
	private String s3DumpsPath;
    
    @Autowired
    private Environment env;
    
    public static final String WORKDIR = "/event-map/dumps/";
	
	public boolean backup(boolean isManual) {
		
		DatabaseDump databaseDump = new DatabaseDump();
		String dumpFileName = "";
		
		try {
				
			String username = env.getProperty("master.datasource.username");
			String pass = env.getProperty("master.datasource.password");		
			String[] dbArray = env.getProperty("master.datasource.jdbcUrl").split("/");
			
			String[] hostNPort = dbArray[2].split(":");
			String dbName = dbArray[3].trim();
			String hostName = hostNPort[0];
			String port = hostNPort[1];
			    		    
		    Timestamp nowT = DateUtils.nowT();
		    
		    Date now = new Date(nowT.getTime());
		    		    	    
		    String date = DateUtils.format(now, DateUtils.FILE_NAME);	 
		    dumpFileName = "postgres_" + hostName + "_" + date + ".dump"; 
		    		    
		    
		    
		    databaseDump.setName(dumpFileName);
		    databaseDump.setCreateDate(nowT);
		    databaseDump.setKey(DateUtils.format(now, "yyyyMMddHH") + "00");
		    if (isManual) {
		    	databaseDump.setKey(DateUtils.format(now, "yyyyMMddHHmm"));
		    }
		    
		    databaseDumpRepository.save(databaseDump);
		    
//		    Runtime r = Runtime.getRuntime();
		    Process p;
		    ProcessBuilder pb;     
		    pb = new ProcessBuilder(pgDump, 
		    		"-Fc",
		    		"--dbname=" + dbName,
		    		"--file=" + dumpFileName, 
		    		"--column-inserts", 
	//	    		"--create", 
		    		"--username=" + username, 
		    		"--host=" + hostName, 
		    		"--port=" + port);
		    pb = pb.directory(new File(WORKDIR));
		    pb.environment().put("PGPASSWORD", pass);    
		    pb.redirectErrorStream(true);
		    
		    
		    try {
			    pb.redirectError(new File ("/event-map/logs/error.log"));
			    pb.redirectOutput(new File ("/event-map/logs/output.log"));
			} catch (Exception e) {
			}
		    
		    
		    p = pb.start(); 
		    
		    
		    
	//	    C:\\Program Files\\PostgreSQL\\15\\bin\\pg_restore.exe --dbname=xd --username=postgres --host=localhost --port=5432 \event-map\dumps\postgres_localhost-2023-12-21_19-42-23.dump
	//	    "C:/Program Files/PostgreSQL/15/bin/psql.exe" --file=C:\Users\IMST\postgres_localhost-2023-12-21_19-36-30-dump.sql --username=postgres --host=localhost --port=5432 den
//		    pg_restore.exe --dbname=xd --username=postgres --host=localhost --port=5432 postgres_localhost_2023-12-27_08-12-32.dump
		    int processComplete = p.waitFor();	
		    		    
		    if (processComplete == 0) {
		    	log.info("Database dump: " + WORKDIR + dumpFileName + " oluşturuldu.");
		    	S3UploadResponseItem pathItem = s3Service.saveTodo(dumpFileName);
		    	databaseDump.setDumpSize( (pathItem.getSize() / 1024) + " KB");
		    	databaseDumpRepository.save(databaseDump);
		    }		    
		    else {
		    	
		    	
		    	
		    	log.error("Dump " + dumpFileName + " başarısız. Dump alınamadı");
		    	databaseDumpRepository.delete(databaseDump);
		    	return processComplete == 0;
		    }
		    
		    log.info("Database dump: " + WORKDIR + dumpFileName + " S3'e yüklendi.");
		    
		    List<String> deletedDumpNameList = s3Service.deleteOldestDump(); 
		    
		    if (!deletedDumpNameList.isEmpty()) {
		    	for (String deletedDumpName: deletedDumpNameList) {
			    	Optional<DatabaseDump> deletedDatabaseDumpOptional = databaseDumpRepository.findByName(deletedDumpName);
			    	if (deletedDatabaseDumpOptional.isPresent()) {
			    		DatabaseDump deletedDatabaseDump = deletedDatabaseDumpOptional.get();
				    	databaseDumpRepository.delete(deletedDatabaseDump);
			    	}
		    	}
		    }
		    
		    return processComplete == 0;
	    
    	} catch (FileNotFoundException e) {
//			e.printStackTrace();
    		databaseDumpRepository.delete(databaseDump);
    		log.error(e);
    		return false;
		} catch (IOException e) {
//			e.printStackTrace();
			databaseDumpRepository.delete(databaseDump);
    		log.error(e);
    		return false;
		} catch (InterruptedException e) {
//			e.printStackTrace();
			databaseDumpRepository.delete(databaseDump);
    		log.error(e);
    		return false;
		} catch (Exception e) {
//			e.printStackTrace();
			databaseDumpRepository.delete(databaseDump);
    		log.error(e);
    		return false;
		}finally {
			try {
				
				File deleteFile = new File(WORKDIR + dumpFileName);						
				if(deleteFile.exists()) {	
					deleteFile.delete();	
					log.info("Geçiçi database dump: " + WORKDIR + dumpFileName + " silindi.");
				}		
			} catch (Exception e2) {
			}
		}
	    
	}
	

	
    public boolean restore(String dumpName) {
    	
    	File downloadedDump = new File(WORKDIR + dumpName);
    	
    	try {
    	
			String username = env.getProperty("master.datasource.username");
			String pass = env.getProperty("master.datasource.password");		
			String[] dbArray = env.getProperty("master.datasource.jdbcUrl").split("/");
			
			String[] hostNPort = dbArray[2].split(":");
			String dbName = dbArray[3];
			String hostName = hostNPort[0];
			String port = hostNPort[1];
			    		    
//		    byte[] dumpByte = null;
		    
		    
		    s3Service.downloadToFile(s3DumpsPath + "/" + dumpName, downloadedDump);
		    
//		    try (FileOutputStream outputStream = new FileOutputStream(downloadedDump)) {
//		        outputStream.write(dumpByte);
//		    }
		    
		    log.info("Database dump: " + WORKDIR + dumpName + " restore başlandı.");
		    
		    Runtime r = Runtime.getRuntime();
		    Process p;
		    ProcessBuilder pb;     
		    pb = new ProcessBuilder(pgRestore, 
		    		"--clean",
		    		"--dbname=" + dbName, // dbName
	//	    		"--file=" + dumpName, 
		    		"--username=" + username, 
		    		"--host=" + hostName, 
		    		"--port=" + port,
		    		dumpName
		    		);
		    pb = pb.directory(new File(WORKDIR));
		    pb.environment().put("PGPASSWORD", pass);
		    pb.redirectErrorStream(true);
		    p = pb.start();  			    	    
	    	
	        int processComplete = p.waitFor();
	        
		    if (!(processComplete == 0)) {
		    	log.error("Restore " + dumpName + " başarısız. Restore yapılamadı");
		    	return processComplete == 0;
		    }
	        
	        log.info("Database dump: " + WORKDIR + dumpName + " restore bitti.");
	        
	        return processComplete == 0;
	        
    	} catch (FileNotFoundException e) {
//			e.printStackTrace();
    		log.error(e);
    		return false;
		} catch (IOException e) {
//			e.printStackTrace();
    		log.error(e);
    		return false;
		} catch (InterruptedException e) {
//			e.printStackTrace();
    		log.error(e);
    		return false;
		}catch (Exception e) {
//			e.printStackTrace();
    		log.error(e);
    		return false;
		}finally {
			
			if(downloadedDump.exists()) {	
				downloadedDump.delete();	
				log.info("Geçiçi database dump: " + WORKDIR + dumpName + " silindi, restore.");
			}
		}
    }
}
