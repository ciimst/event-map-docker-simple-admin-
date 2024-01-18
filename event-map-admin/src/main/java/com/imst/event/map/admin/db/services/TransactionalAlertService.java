package com.imst.event.map.admin.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.admin.db.repositories.AlertEventRepository;
import com.imst.event.map.admin.db.repositories.AlertRepository;

@Service
public class TransactionalAlertService {

	
	@Autowired 
	private AlertEventRepository alertEventRepository;
	@Autowired
	private AlertRepository alertRepository;
	
	@Transactional(transactionManager = "masterTransactionManager")
	public void deleteAlert(Integer alertId) {

		alertEventRepository.alertEventByAlertIdDeleted(alertId);
		alertRepository.deleteById(alertId);
	}


}
