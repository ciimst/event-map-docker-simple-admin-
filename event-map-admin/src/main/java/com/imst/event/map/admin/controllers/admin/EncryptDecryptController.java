package com.imst.event.map.admin.controllers.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.utils.DatabasePasswordEncryptUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@RequestMapping("/admin/encrypt-decrypt")
public class EncryptDecryptController {
	
	@Value("${jasypt.encryptor.password}")
	public String databaseEncryptPrivateKey;
	
	@PreAuthorize("hasRole('ROLE_ENCRYPT_MANAGE')")
	@Operation(summary = "Sayfalama. Örn:/api/encrypt-decrypt/page?page=0&size=10&sort=name,desc")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/encrypt_decrypt");
		
		return modelAndView;
	}
	
	@PreAuthorize("hasRole('ROLE_ENCRYPT_MANAGE')")
	@Operation(summary = "Şifrele")
	@RequestMapping(value = "/encrypt")
	public GenericResponseItem encrypt(String password) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.encrypt"));
		
		password = DatabasePasswordEncryptUtils.encryptedPassword(password, databaseEncryptPrivateKey);
	       
		genericResponseItem.setData(password);
	    return genericResponseItem;
		
	}
	
	@PreAuthorize("hasRole('ROLE_ENCRYPT_MANAGE')")
	@Operation(summary = "Şifre Çöz")
	@RequestMapping(value = "/decrypt")
	public GenericResponseItem decrypt(String password) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.decrypt"));
		try {	
			
			password = DatabasePasswordEncryptUtils.decriptedPassword(password, databaseEncryptPrivateKey);
			
			genericResponseItem.setData(password);
		    return genericResponseItem;
						
		 }
		 catch (Exception e) {
			
			log.error(e);
		 }
		
		 return genericResponseItem;
	 }
	
}









	
	
	
	
	
	
	

