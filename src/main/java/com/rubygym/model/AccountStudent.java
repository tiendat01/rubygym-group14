package com.rubygym.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account_student")
public class AccountStudent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "accumulation")
	private Integer accumulation;
	
	@Column(name = "expire")
	private LocalDate expireDate;
	
	@Column(name = "service_id")
	private Integer serviceId;

	public AccountStudent(Integer id, String username, String password, Integer accumulation, LocalDate expireDate,
			Integer serviceId) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.accumulation = accumulation;
		this.expireDate = expireDate;
		this.serviceId = serviceId;
	}
	
	public AccountStudent() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(Integer accumulation) {
		this.accumulation = accumulation;
	}

	public LocalDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	
	
}

