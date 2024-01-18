package com.imst.event.map.admin.controllers.admin;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.imst.event.map.admin.constants.LogTypeE;
import com.imst.event.map.admin.db.repositories.SettingsRepository;
import com.imst.event.map.admin.services.DBLogger;
import com.imst.event.map.admin.utils.ApplicationContextUtils;
import com.imst.event.map.admin.vo.GenericResponseItem;
import com.imst.event.map.admin.vo.SettingsItem;
import com.imst.event.map.hibernate.entity.Settings;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/admin/settings")
public class SettingsController {
	
	@Autowired
	private SettingsRepository settingsRepository;
	@Autowired
	private DBLogger dbLogger;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PreAuthorize("hasRole('ROLE_MANAGE_SETTINGS')")
	@Operation(summary = "Güncelleme")
	@RequestMapping({"", "/edit"})
	public ModelAndView getUserPage() {
		
		ModelAndView modelAndView = new ModelAndView("page/admin/settings");
		
		List<Settings> settingsList = settingsRepository.findAll();
		List<Settings> generalSettingList = settingsList
				.stream()
				.filter(generalSetting -> !"none".equalsIgnoreCase(generalSetting.getType()))
				.collect(Collectors.toList());
		
		generalSettingList.forEach(generalSetting -> {
			if ("pass".equalsIgnoreCase(generalSetting.getType())) {
				generalSetting.setSettingsValue(null);
			}
		});
		
	
		
		Map<String, List<Settings>> contentMap = generalSettingList
				.stream()
				.collect(Collectors.groupingBy(Settings::getGroupName));
		
		
		LinkedHashMap<String, List<Settings>> sortedMap = contentMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		for (Entry<String, List<Settings>> entrySet : sortedMap.entrySet()) {
			
			List<Settings> settingsListTemp = entrySet.getValue();
			List<Settings> sortedSettingsListTemp = settingsListTemp.stream().sorted((o1,o2) -> o1.getId().compareTo(o2.getId())).collect(Collectors.toList());
			entrySet.setValue(sortedSettingsListTemp);
		}
		
		modelAndView.addObject("settingsMap", sortedMap);
		
		
		return modelAndView;
	}
	
	
	@PreAuthorize("hasRole('ROLE_MANAGE_SETTINGS')")
	@Operation(summary = "Kaydet")
	@RequestMapping(value = "/save")
	public GenericResponseItem save(HttpServletRequest request) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		List<Settings> settingsList = settingsRepository.findAll();
		
		Map<String, Object> settingsForLog = new TreeMap<>();
		List<Object> oldSettingsForLog = new ArrayList<>();
		List<Object> newSettingsForLog = new ArrayList<>();
		List<Settings> changeList = new ArrayList<>();
		
		for (Settings settings : settingsList) {
			
			try {
				if ("none".equalsIgnoreCase(settings.getType())) {//ekrandan değişemez
					continue;
				}
				
				String value = request.getParameter(settings.getSettingsKey());
				
				if (StringUtils.isAllBlank(settings.getSettingsValue(), value)) {
					continue;
				}
				
				
				if (StringUtils.equals(settings.getSettingsValue(), value) && !"checkbox".equalsIgnoreCase(settings.getType())) {
					continue;
				}
				
				SettingsItem oldSettings = SettingsItem.newInstanceForLog(settings);
				
				switch (settings.getType()) {
					case "checkbox":
						value = !StringUtils.isBlank(value) + "";
						if (StringUtils.equals(settings.getSettingsValue(), value)) {
							continue;
						}
						settings.setSettingsValue(value);
						break;
						
					case "pass":
						//boşsa continue.(en alta ulaşınca changeList e ekleneceği için)
						if (StringUtils.isBlank(value)) {
							continue;
						}
						settings.setSettingsValue(passwordEncoder.encode(value));
						break;
						
					default:
						settings.setSettingsValue(value);
						break;
				}
				
				//buraya kadar geldiyse değişim var demektir.
				changeList.add(settings);
				
				//belki password ile ilgili olan logdaki valueları silmek gerekebilir.
				oldSettingsForLog.add(oldSettings);
				newSettingsForLog.add(SettingsItem.newInstanceForLog(settings));
				
				
			} catch (Exception e) {
				//Hata halinde tek kaydın etkilenmesi için for içinde burası
				log.error(e);
			}
		}
		
		
		if (!changeList.isEmpty()) {
			
			settingsRepository.saveAll(changeList);

			settingsForLog.put("old", oldSettingsForLog);
			settingsForLog.put("new", newSettingsForLog);
			dbLogger.log(new Gson().toJson(settingsForLog), LogTypeE.SETTINGS_EDIT);
		}
		
		return genericResponseItem;
	}
	
	
}
