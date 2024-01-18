package com.imst.event.map.hibernate.entity;

import java.sql.Timestamp;
import com.vividsolutions.jts.geom.Polygon;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Alert generated by hbm2java
 */
@Entity
@Table(name = "alert", schema = "public")
public class Alert implements java.io.Serializable {

	private Integer id;
	private EventGroup eventGroup;
	private EventType eventType;
	private Layer layer;
	private User user;
	private String name;
	private Polygon polygonCoordinate;
	private Timestamp createDate;
	private Timestamp updateDate;
	private String query;
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	private String eventGroupDbName;
	private String sharedBy;
	private String color;
	private Set<AlertEvent> alertEvents = new HashSet<AlertEvent>(0);

	public Alert() {
	}

	public Alert(EventGroup eventGroup, EventType eventType, Layer layer, User user, String name,
			Polygon polygonCoordinate, Timestamp createDate, Timestamp updateDate, String query, String reservedKey,
			String reservedType, String reservedId, String reservedLink, String eventGroupDbName, String sharedBy, String color,
			Set<AlertEvent> alertEvents) {
		this.eventGroup = eventGroup;
		this.eventType = eventType;
		this.layer = layer;
		this.user = user;
		this.name = name;
		this.polygonCoordinate = polygonCoordinate;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.query = query;
		this.reservedKey = reservedKey;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.reservedLink = reservedLink;
		this.eventGroupDbName = eventGroupDbName;
		this.sharedBy = sharedBy;
		this.color = color;
		this.alertEvents = alertEvents;
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
	@JoinColumn(name = "fk_event_type_id")
	public EventType getEventType() {
		return this.eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_layer_id")
	public Layer getLayer() {
		return this.layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_id")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "name", length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "polygon_coordinate")
	public Polygon getPolygonCoordinate() {
		return this.polygonCoordinate;
	}

	public void setPolygonCoordinate(Polygon polygonCoordinate) {
		this.polygonCoordinate = polygonCoordinate;
	}

	@Column(name = "create_date", length = 29)
	@org.hibernate.annotations.ColumnDefault("now()")
	public Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@Column(name = "update_date", length = 29)
	public Timestamp getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	@Column(name = "query", length = 256)
	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Column(name = "reserved_key", length = 256)
	public String getReservedKey() {
		return this.reservedKey;
	}

	public void setReservedKey(String reservedKey) {
		this.reservedKey = reservedKey;
	}

	@Column(name = "reserved_type", length = 256)
	public String getReservedType() {
		return this.reservedType;
	}

	public void setReservedType(String reservedType) {
		this.reservedType = reservedType;
	}

	@Column(name = "reserved_id", length = 256)
	public String getReservedId() {
		return this.reservedId;
	}

	public void setReservedId(String reservedId) {
		this.reservedId = reservedId;
	}

	@Column(name = "reserved_link", length = 4096)
	public String getReservedLink() {
		return this.reservedLink;
	}

	public void setReservedLink(String reservedLink) {
		this.reservedLink = reservedLink;
	}

	@Column(name = "event_group_db_name", length = 64)
	public String getEventGroupDbName() {
		return this.eventGroupDbName;
	}

	public void setEventGroupDbName(String eventGroupDbName) {
		this.eventGroupDbName = eventGroupDbName;
	}

	@Column(name = "shared_by", length = 256)
	public String getSharedBy() {
		return this.sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "alert")
	public Set<AlertEvent> getAlertEvents() {
		return this.alertEvents;
	}

	public void setAlertEvents(Set<AlertEvent> alertEvents) {
		this.alertEvents = alertEvents;
	}

}