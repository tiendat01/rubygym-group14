package com.rubygym.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service")
public class Service {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "period_per_week")
	private Integer periodPerWeek;
	
	@Column(name = "n_months")
	private Integer nMonths;

	public Service(Integer id, String category, Integer periodPerWeek, Integer nMonths) {
		super();
		this.id = id;
		this.category = category;
		this.periodPerWeek = periodPerWeek;
		this.nMonths = nMonths;
	}
	
	public Service() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getPeriodPerWeek() {
		return periodPerWeek;
	}

	public void setPeriodPerWeek(Integer periodPerWeek) {
		this.periodPerWeek = periodPerWeek;
	}

	public Integer getnMonths() {
		return nMonths;
	}

	public void setnMonths(Integer nMonths) {
		this.nMonths = nMonths;
	}
	
	
}
