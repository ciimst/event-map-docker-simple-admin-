package com.imst.event.map.admin.cron;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.imst.event.map.admin.db.repositories.SettingsRepository;
import com.imst.event.map.admin.utils.SettingsUtil;
import com.imst.event.map.hibernate.entity.Settings;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class ScheduledQueries {
	
	@Autowired private SettingsRepository settingsRepository;
	
	
	@Scheduled(initialDelayString="${settings.update.initial.delay}", fixedDelayString ="${settings.update.interval}")
	public void generalSettings() {
		
		try {
			
			List<Settings> all = settingsRepository.findAll();
			for (Settings settings : all) {
				
				SettingsUtil.settings.put(settings.getSettingsKey(), settings.getSettingsValue());
			}
		}
		catch (Exception e) {
			
			log.debug(e);
		}
		
	}
	
//	@Scheduled(initialDelay=10000, fixedDelay = 10000)
//	public void blackListScheduler() {
//		
//		try {
//			
//			//action state pending, running olan ve state değeri true, false olan blacklistler çekiliyor.
//			List<BlackList> blackListForAction = blackListRepository.findAllByActionStateIdInAndStateIdIn(Arrays.asList(ActionStateE.PENDING.getValue(), ActionStateE.RUNNING.getValue()), Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue(), StateE.DELETED.getValue()));
//
//			for (BlackList blackList : blackListForAction) {
//				
//				blackListService.oldEventUpdateForBlackList(blackList);
//			}
//
//		}
//		catch (Exception e) {
//			
//			log.debug(e);
//		}
//		
//	}
	
}
