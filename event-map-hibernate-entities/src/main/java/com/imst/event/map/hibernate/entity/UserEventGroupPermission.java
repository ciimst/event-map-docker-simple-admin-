package com.imst.event.map.hibernate.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * UserLayerPermission generated by hbm2java
 */
@Entity
@Table(name = "user_event_group_permission", schema = "public")
public class UserEventGroupPermission implements java.io.Serializable {

	private Integer id;
	private EventGroup eventGroup;
	private User user;

	public UserEventGroupPermission() {
	}

	public UserEventGroupPermission(EventGroup eventGroup, User user) {
		this.eventGroup = eventGroup;
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_event_group_id")
	public EventGroup getEventGroup() {
		return this.eventGroup;
	}

	public void setEventGroup(EventGroup eventGroup) {
		this.eventGroup = eventGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_id")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}