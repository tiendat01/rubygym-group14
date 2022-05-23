package com.rubygym.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="schedule")
public class Schedule {
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	@Column(name="id")
	private Integer id;
	
	@Column(name="trainer_student_id")
	private Integer trainerStudentId;
	
	@Column(name = "time_id")
	private Integer timeId;


	public Schedule(Integer id, Integer trainerStudentId, Integer timeId) {
		super();
		this.id = id;
		this.trainerStudentId = trainerStudentId;
		this.timeId = timeId;
	}
	
	public Schedule(Integer trainerStudentId, Integer timeId) {

		this.trainerStudentId = trainerStudentId;
		this.timeId = timeId;
	}

	public Schedule() {
		
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

	public Integer getTimeId() {
		return timeId;
	}

	public void setTimeId(Integer timeId) {
		this.timeId = timeId;
	}
	
	
	
	
}
