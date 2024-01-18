package com.imst.event.map.admin.db.services;

import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.repositories.EventBlackListRepository;
import com.imst.event.map.admin.db.repositories.EventRepository;
import com.imst.event.map.admin.vo.EventItem;
import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.EventBlackList;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransactionalEventBlackListService {
	
	@Autowired private EventRepository eventRepository;
	@Autowired private EventBlackListRepository eventBlackListRepository;
	
	@Transactional(transactionManager = "masterTransactionManager")
	public void blackListIsStateTrue(List<EventItem> eventList, BlackList blackList) {
		
		List<Integer> updateListForBlackList = eventList.stream().filter(f -> f.getStateId().equals(StateE.BLACKLISTED.getValue()) || f.getStateId().equals(StateE.TRUE.getValue()) || f.getStateId().equals(StateE.FALSE.getValue())).map(EventItem::getId).collect(Collectors.toList());	
		eventRepository.batchOperationsUpdateEventBlackList(StateE.BLACKLISTED.getValue(), updateListForBlackList);
		
		//burada ekleme işlemi yapmak gerekiyor.
		this.addedEventBlackList(updateListForBlackList, blackList.getId());
	}
	
	
	@Transactional(transactionManager = "masterTransactionManager")
	public void blackListIsStateFalse(List<EventItem> eventList, BlackList blackList) {
		//önce delete çağırılacak sonra ara tabloda kalan event list olarak dönülüp true olarak güncellenecek.
		
		List<Integer> updateListForBlackListStateSetToTrue = eventList.stream().filter(f -> f.getStateId().equals(StateE.BLACKLISTED.getValue())).map(EventItem::getId).collect(Collectors.toList());
		List<Integer> eventIdListSetToTrue = this.deletedEventBlackList(updateListForBlackListStateSetToTrue, blackList.getId());
		
		eventRepository.batchOperationsUpdateEventBlackList(StateE.TRUE.getValue(),eventIdListSetToTrue);
	}
	
	
	public boolean addedEventBlackList(List<Integer> eventIds, Integer blackListId) {
		
		try {
			
			List<EventBlackList> list = eventBlackListRepository.findAllByEventIdInAndBlackListId(eventIds, blackListId);
			
			BlackList blackList = new BlackList();
			blackList.setId(blackListId);
			
			List<EventBlackList> savedList = new ArrayList<>();
			eventIds.forEach(item -> {
				
				Long count = list.stream().filter(f -> f.getEvent().getId().equals(item) && f.getBlackList().getId().equals(blackListId)).count();
				
				if(count == 0) {
					EventBlackList eventBlackList = new EventBlackList();
					Event event = new Event();
					event.setId(item);
					
					eventBlackList.setEvent(event);
					eventBlackList.setBlackList(blackList);
					savedList.add(eventBlackList);
				}
				
			});
			
			eventBlackListRepository.saveAll(savedList);
			
		}catch (Exception e) {
			log.debug(e);
			return false;
		}
		
		return true;
	}
	
	
	public List<Integer> deletedEventBlackList(List<Integer> eventIds, Integer blackListId) {
		
		
		List<Integer> stateSetToTrueEventIdList = new ArrayList<>();
		try {
			eventBlackListRepository.eventBlackListDeleted(eventIds, blackListId);
			List<Integer> eventIdConflictList = eventBlackListRepository.findAllByEventIdInAndBlackListIdIsNot(eventIds, blackListId).stream().map(EventBlackList::getEvent).map(Event::getId).collect(Collectors.toList());
			
			
			eventIds.forEach(item -> {
				
				boolean status = eventIdConflictList.stream().anyMatch(f -> f.equals(item));
				if(!status) {
					stateSetToTrueEventIdList.add(item);
				}
			});
						
			
			if(eventIdConflictList.isEmpty()) {
				
				stateSetToTrueEventIdList.addAll(eventIds);
			}
			
			
		}catch (Exception e) {
			log.debug(e);
			return null;
		}
		
		
		return stateSetToTrueEventIdList;
	}

	
	
}
