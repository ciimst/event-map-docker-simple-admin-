package com.imst.event.map.admin.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imst.event.map.admin.constants.StateE;
import com.imst.event.map.admin.db.projections.LayerAlertCountProjection;
import com.imst.event.map.admin.db.repositories.AlertEventRepository;
import com.imst.event.map.admin.db.repositories.AlertRepository;
import com.imst.event.map.admin.db.repositories.EventGroupRepository;
import com.imst.event.map.admin.db.repositories.EventRepository;
import com.imst.event.map.admin.db.repositories.EventTypeRepository;
import com.imst.event.map.admin.db.repositories.GeoLayerRepository;
import com.imst.event.map.admin.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.admin.db.repositories.MapAreaRepository;
import com.imst.event.map.admin.db.repositories.StateRepository;
import com.imst.event.map.admin.db.repositories.TileServerRepository;
import com.imst.event.map.admin.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.admin.db.repositories.UserRepository;
import com.imst.event.map.admin.security.UserItemDetails;
import com.imst.event.map.admin.services.UserPermissionService;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.EventTypeItem;
import com.imst.event.map.admin.vo.UserEventGroupPermissionItem;
import com.imst.event.map.admin.vo.UserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.State;
import com.imst.event.map.hibernate.entity.User;


@Controller
@RequestMapping({"/","home"})
public class HomeController {

	@Value("${spring.session.redis.web.namespace}")
	private String redisWebNamespace;
	
	@Autowired UserRepository userRepository;
	@Autowired EventRepository eventRepository;
	@Autowired UserLayerPermissionRepository userLayerpermissionRepository;
	@Autowired EventGroupRepository eventGroupRepository;
	@Autowired AlertRepository alertRepository;
	@Autowired EventTypeRepository eventTypeRepository;
	@Autowired GeoLayerRepository geoLayerRepository;
	@Autowired MapAreaRepository mapAreaRepository;
	@Autowired MapAreaGroupRepository mapAreaGroupRepository;
	@Autowired TileServerRepository tileServerRepository;
	@Autowired AlertEventRepository alertEventRepository;
	@Autowired UserLayerPermissionRepository userLayerPermissionRepository;
	@Autowired UserPermissionService userPermissionService;
	@Autowired StateRepository stateRepository;
	@Autowired RedisTemplate<String, String> redisTemplate;
	
	@RequestMapping({"/","home"})
	public String home(Model model) {
				
		return "page/home";
	}
	
	@GetMapping(value = {"/live"})
	public ResponseEntity<?> livenessProbe() {
		try {
			List<State> cont = null;
			cont = stateRepository.findAll();
			if ((cont != null && cont.isEmpty()) || cont == null) {
				return ResponseEntity.badRequest().build();
			}
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok().build();
	}
	
	
	@RequestMapping("/sessionCount")
	@ResponseBody
	public Integer sessionCount() {
		
		
		Set<String> keys = redisTemplate.keys(redisWebNamespace + ":index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:*");
		List<String> list = new ArrayList<String>(keys);
		
		return list.size();
	}

	@GetMapping(value = {"/ready"})
	public ResponseEntity<?> readinessProbe() {
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping("/userCount")
	@ResponseBody
	public Integer userCount() {
		
		long userCount =  userRepository.countBy();		
		
		return (int)userCount;
	}
	
	@RequestMapping("/layerCount")
	@ResponseBody
	public Integer layerCount() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
				
		List<Integer> userLayerPermissionIdList = sessionUser.getUserLayerPermissionList().stream().map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
		
		return (int) userLayerPermissionIdList.size();
	}
	
	@RequestMapping("/eventCount")
	@ResponseBody
	public Integer eventCount() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		
		long eventsFiltered = eventRepository.countByStateIdAndEventGroupIdInAndUserIdInAndGroupIdIn(StateE.TRUE.getValue(), userEventGroupPermissionIdList, userIdList, groupIdList);
					
		return (int) eventsFiltered;
	}
	
	@RequestMapping("/alarmCount")
	@ResponseBody
	public Integer alarmCount() {
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
				
		long alertList = alertRepository.countByUser(user);
		
		return (int) alertList;
	}

	@RequestMapping("/eventAlarmCount")
	@ResponseBody
	public Integer eventAlarmCount() {
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		long alertEventCount = alertEventRepository.countByUser(user);
			
		return (int) alertEventCount;
	}
	

	
	@RequestMapping("/eventTypeInfo")
	@ResponseBody
	public Map<String, Long> eventTypeName() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		List<Integer> userEventGroupPermissionIdList = sessionUser.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();	
		
		List<Object[]> eventTypeIdAndEventCount = eventRepository.findPermissionedEventTypeIdAndEventTypeNameAndCountByAndStateId(PageRequest.of(0, 10), userEventGroupPermissionIdList, userIdList, groupIdList, StateE.TRUE.getValue());
		
		List<Integer> eventTypeIdList = new ArrayList<>();
		for (Object[] obj : eventTypeIdAndEventCount) {
			eventTypeIdList.add((Integer) obj[0]);
		}
	
		List<EventTypeItem> eventTypeItemList = eventTypeRepository.findByIdIn(eventTypeIdList);
		Map<Integer, EventTypeItem> eventTypeItemMap = eventTypeItemList.stream().collect(Collectors.toMap(EventTypeItem::getId, Function.identity()));
		
		Map<String, Long> result = new LinkedHashMap<>();
		
		for (Object[] obj : eventTypeIdAndEventCount) {
			
			Integer eventTypeId = (Integer) obj[0];
			
			result.put(eventTypeItemMap.get(eventTypeId).getCode(), (Long) obj[1]);
		}
		
		return result;
	}
	
	@RequestMapping("/layerAlarmInfo")
	@ResponseBody
	public List<LayerAlertCountProjection> layerAlarmName(){
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			
		List<Integer> userLayerPermissionIdList = sessionUser.getUserLayerPermissionList().stream().filter(o -> o.getHasFullPermission() == true).map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
	
		List<LayerAlertCountProjection> layerAlertCountList = alertRepository.findPermissionedLayerAlertCountProjectedBy(PageRequest.of(0, 10), userLayerPermissionIdList);
		
		return layerAlertCountList;
	}
	
	@RequestMapping(value="/unReadAlarmCount", method=RequestMethod.POST)
	@ResponseBody
	public Integer unReadAlarmCount() {
		
	
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		long unReadAlarmCount = alertEventRepository.countByUserAndReadStateIsFalse(user);
		
		return (int) unReadAlarmCount;
	}
	
	@RequestMapping(value="/sharedAlertCount", method=RequestMethod.POST)
	@ResponseBody
	public Integer sharedAlarmCount() {
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		long sharedAlertCount = alertRepository.countByUserAndSharedByNotNull(user);
		
		return (int) sharedAlertCount;
	}
	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginShow(Model model) {
		
		return "login";
	}
	
	@RequestMapping(value = "/keycloaklogout", method = RequestMethod.GET)
	public String logoutShow(Model model) {
		
		return "logout";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginCheck() {
		
		return "page/home";
	}
	
}
