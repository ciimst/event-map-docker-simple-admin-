package com.imst.event.map.admin.vo.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class S3UploadResponseItem {
	
	private String url;
	private Long size;

}
