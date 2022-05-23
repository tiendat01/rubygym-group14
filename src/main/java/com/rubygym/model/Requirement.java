package com.rubygym.model;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "requirement")
public class Requirement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "trainer_student_id")
	private Integer trainerStudentId;
	
	@Column(name = "schedule_id")
	private Integer scheduleId = 1; // mặc định là +1, nếu thao tác là create
	
	@Column(name = "time_id_new")
	private Integer timeId;
	
	@Column(name = "category")
	private Integer category;


	public Requirement(Integer id, Integer trainerStudentId, Integer scheduleId, Integer timeId, Integer category) {
		super();
		this.id = id;
		this.trainerStudentId = trainerStudentId;
		this.scheduleId = scheduleId;
		this.timeId = timeId;
		this.category = category;
	}
	
	public Requirement(Integer trainerStudentId, Integer scheduleId, Integer timeId, Integer category) {
		
		this.trainerStudentId = trainerStudentId;
		this.scheduleId = scheduleId;
		this.timeId = timeId;
		this.category = category;
	}
	
	public Requirement() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTrainerStudentId() {
		return trainerStudentId;
	}

	public void setTrainerStudentId(Integer trainerStudentId) {
		this.trainerStudentId = trainerStudentId;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}


	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getTimeId() {
		return timeId;
	}

	public void setTimeId(Integer timeId) {
		this.timeId = timeId;
	}
	
	
}
