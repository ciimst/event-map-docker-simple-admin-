package com.imst.event.map.admin.controllers.admin.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.imst.event.map.admin.db.projections.UserProjection;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.db.repositories.redis.UserLastPageRepository;
import com.imst.event.map.admin.vo.redis.UserLastPageItem;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.redis.UserLastPage;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/admin/map/userLastPage")
public class UserLastPageController  {
	
	
	@Autowired
	private UserLastPageRepository userLastPageRepository;
	
	@Autowired
	private UserRepository userRepository;

	
	@PreAuthorize("hasRole('ROLE_USER_LAST_PAGE_LIST')")
	@Operation(summary = "Sayfalama")
	@RequestMapping({""})
	public ModelAndView getPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/map/user_last_page_info");
		
		List<UserProjection> users = userRepository.findAllProjectedByOrderByUsername();     
		modelAndView.addObject("users", users);

		return modelAndView;
	}
	
	
	@PreAuthorize("hasRole('ROLE_USER_LAST_PAGE_LIST')")
	@Operation(summary = "")
	@RequestMapping(value = "/list/data")
	public List<UserLastPageItem> data() {
		
		List<UserLastPage> list = Lists.newArrayList(userLastPageRepository.findAll());	
		List<Integer> userIdList = list.stream().map(m ->m.getId()).collect(Collectors.toList());	
		List<User> userList = Lists.newArrayList(userRepository.findAllById(userIdList));

		
		List<UserLastPageItem> userLastPageItems = new ArrayList<>();
		userList.forEach(item -> {
			
			Optional<UserLastPage> optUserLastPage = list.stream().filter(f -> f.getId().equals(item.getId())).findFirst();
			if(optUserLastPage.isPresent()) {
				
				String url = optUserLastPage.get().getUrl();
				String updateDateStr = optUserLastPage.get().getUpdateDate();
				UserLastPageItem userLastPageItem = new UserLastPageItem(item.getId(), url, item.getUsername(), updateDateStr);
				userLastPageItems.add(userLastPageItem);
			}
		});
		

		
		
		return userLastPageItems;
	}
	


}