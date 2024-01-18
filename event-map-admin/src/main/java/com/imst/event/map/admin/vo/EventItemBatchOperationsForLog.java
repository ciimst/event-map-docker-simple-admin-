package com.imst.event.map.admin.vo;

import java.util.List;

import com.imst.event.map.hibernate.entity.State;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventItemBatchOperationsForLog {

	private List<Integer> eventIdList;
	private State state;
}
