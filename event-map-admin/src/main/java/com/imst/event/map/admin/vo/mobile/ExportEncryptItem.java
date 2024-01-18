package com.imst.event.map.admin.vo.mobile;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportEncryptItem {

	private String data;
	
	public ExportEncryptItem() {
		
	}
	
	public ExportEncryptItem(String data) {
		this.data = data;
	}
	
}
