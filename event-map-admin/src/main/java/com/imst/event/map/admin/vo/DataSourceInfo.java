package com.imst.event.map.admin.vo;

import javax.sql.DataSource;

import com.imst.event.map.admin.constants.MultitenantDatabaseE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataSourceInfo {
	
	private String name;
	private MultitenantDatabaseE multitenantDatabaseE;
	private DataSource datasource;

}
