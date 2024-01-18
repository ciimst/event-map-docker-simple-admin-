package com.imst.event.map.admin.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.imst.event.map.admin.constants.ActionStateE;
import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.dao.MasterDao;
import com.imst.event.map.admin.db.projections.EventGroupProjection;
import com.imst.event.map.admin.db.repositories.BlackListRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.services.TransactionalEventBlackListService;
import com.imst.event.map.admin.db.specifications.EventSpecificationForBlackList;
import com.imst.event.map.admin.utils.DateUtils;
import com.imst.event.map.admin.utils.EventGroupTree;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.admin.vo.EventItemForBlackList;
import com.imst.event.map.hibernate.entity.BlackList;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BlackListService {

	@Autowired private BlackListRepository blackListRepository;
	@Autowired private EventGroupRepository eventGroupRepository;
	@Autowired private MasterDao masterDao;
	@Autowired private TransactionalEventBlackListService transactionalEventBlackListService;
	
	public boolean oldEventUpdateForBlackList(BlackList blackList) {
		
		if(blackList == null) {
			return false;
		}
		
		log.info(String.format("Blacklist scheduled job started [name : %s]", blackList.getName()));
		
		//BlackList değeri şuan işlem yapılıyor olarak güncelleniyor
		blackList.setActionState(ActionStateE.RUNNING.getActionState());
		blackListRepository.save(blackList);
		

		// Pasiften tekrar aktife çekilme durumunda çılştırılma işlemi, ActionState alanının aktife çekilme esnasında false yapılması ile yapılır.
		List<EventGroupProjection> allEventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(blackList.getLayer());
		EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList, true);
		
		EventItemForBlackList eventItem = new EventItemForBlackList();
		eventItem.setLayerId(blackList.getLayer().getId());
		eventItem.setBlackListTag(blackList.getTag());
		
		if(blackList.getEventGroup() != null) {
			
			List<Integer> permissionEventGroupIdList = eventGroupTree.getPermissionEventGroup(Arrays.asList(blackList.getEventGroup().getId()));
			permissionEventGroupIdList.add(blackList.getEventGroup().getId());
			eventItem.setEventGroupIdList(permissionEventGroupIdList);
		}
		
		if (blackList.getEventType() != null) {
			eventItem.setEventTypeId(blackList.getEventType().getId());
		}
		
		
		if(!StateE.getIntegerStateToBoolean(blackList.getState().getId()) || blackList.getState().getId().equals(StateE.DELETED.getValue())) {		
			eventItem.setBlackListId(blackList.getId());
		}
		
		
		EventSpecificationForBlackList eventSpecificationForBlackList = new EventSpecificationForBlackList(eventItem);
		
		int index = 0;
		int count = 0;
		while (true) {

			PageRequest pageRequest = PageRequest.of(index, 10000, Sort.by(Order.asc("id")));

			Page<EventItem> eventListPage = masterDao.findAll(eventSpecificationForBlackList, pageRequest);
			List<EventItem> eventList = eventListPage.getContent();

			if(eventList.size() == 0) {
				break;
			}
			
			
			try {

				if(StateE.getIntegerStateToBoolean(blackList.getState().getId())) {
					
					transactionalEventBlackListService.blackListIsStateTrue(eventList, blackList);
				}
				
				if(!StateE.getIntegerStateToBoolean(blackList.getState().getId()) || blackList.getState().getId().equals(StateE.DELETED.getValue())) {
					
					transactionalEventBlackListService.blackListIsStateFalse(eventList, blackList);
				}
				
			} catch (Exception e) {
				log.debug(e);
				return false;
			}
			count += eventList.size(); 
			index++;
		}
		
		
		
		blackList.setActionState(ActionStateE.FINISHED.getActionState());
		blackList.setActionDate(DateUtils.nowT());
		blackListRepository.save(blackList);
		
		log.info(String.format("Blacklist scheduled job finished [name : %s] [count : %s]", blackList.getName(), count));
		
		return true;
	}
	
	
	//blacklist silindiğinde olayların durumu true yapılıyor.
//	public boolean updatingEventsAfterBlackListDeleted(BlackList blackList) {
//		
//		List<EventGroupProjection> allEventGroupList = eventGroupRepository.findAllProjectedByLayerOrderByName(blackList.getLayer());
//		EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupList, true);
//		EventItemForBlackList eventItem = new EventItemForBlackList();
//		eventItem.setLayerId(blackList.getLayer().getId());
//		eventItem.setBlackListTag(blackList.getTag());
//		eventItem.setBlackListId(blackList.getId());
//		if(blackList.getEventGroup() != null) {
//			
//			List<Integer> permissionEventGroupIdList = eventGroupTree.getPermissionEventGroup(Arrays.asList(blackList.getEventGroup().getId()));
//			permissionEventGroupIdList.add(blackList.getEventGroup().getId());
//			eventItem.setEventGroupIdList(permissionEventGroupIdList);
//		}
//		
//		if (blackList.getEventType() != null) {
//			eventItem.setEventTypeId(blackList.getEventType().getId());
//		}
//		EventSpecificationForBlackList eventSpecificationForBlackList = new EventSpecificationForBlackList(eventItem);
//		
//		int index = 0;
//		while (true) {
//
//			PageRequest pageRequest = PageRequest.of(index, 10000, Sort.by(Order.asc("id")));
//
//			Page<EventItem> eventListPage = masterDao.findAll(eventSpecificationForBlackList, pageRequest);
//			List<EventItem> eventList = eventListPage.getContent();
//
//			if(eventList.size() == 0) {
//				break;
//			}
//				
//			try {
//				transactionalEventBlackListService.blackListIsStateFalse(eventList, blackList);	
//				
//			} catch (Exception e) {
//				log.debug(e);
//				return false;
//			}
//			
//			index++;
//		}
//		
//		log.info("BlackList silindiği için, bu blackliste ait olayların durumu aktif yapılmıştır.");
//		return true;
//		
//	}
	

	

	
	
	
}
