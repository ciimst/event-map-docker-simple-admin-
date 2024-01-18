package com.imst.event.map.hibernate.entity;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SpatialTest generated by hbm2java
 */
@Entity
@Table(name = "spatial_test", schema = "public")
public class SpatialTest implements java.io.Serializable {

	private Integer id;
	private String name;
	private Polygon polyTest;
	private Point pointTest;

	public SpatialTest() {
	}

	public SpatialTest(String name, Polygon polyTest, Point pointTest) {
		this.name = name;
		this.polyTest = polyTest;
		this.pointTest = pointTest;
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

	@Column(name = "name", length = 32)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "poly_test")
	public Polygon getPolyTest() {
		return this.polyTest;
	}

	public void setPolyTest(Polygon polyTest) {
		this.polyTest = polyTest;
	}

	@Column(name = "point_test")
	public Point getPointTest() {
		return this.pointTest;
	}

	public void setPointTest(Point pointTest) {
		this.pointTest = pointTest;
	}

}