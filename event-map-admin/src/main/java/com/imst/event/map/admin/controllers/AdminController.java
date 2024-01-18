package com.imst.event.map.admin.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/admin")
public class AdminController {
	
	
	@RequestMapping({""})
	public ModelAndView getAdminHome(){
		
		return new ModelAndView("redirect:" + "/home");
	}
	
}
