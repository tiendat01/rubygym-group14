package com.rubygym.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.simple.JSONObject;

@Entity
@Table(name="event") // tên bảng
public class Event extends JSONObject{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	@Column(name="title")
	private String title;
	
	@Column(name="description")
	private String description;
	
	@Column(name="category")
	private Integer category;
	@Column(name="expire")
	private LocalDate expire;

	public LocalDate getExpire() {
		return expire;
	}

	public void setExpire(LocalDate expire) {
		this.expire = expire;
	}
	
}