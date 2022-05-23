package com.rubygym.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="period_backup")
public class PeriodBackup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="trainer_student_id")
	private Integer trainerStudentId;
	
	@Column(name="time_id")
	private Integer timeId;
	
	@Column(name="p_date")
	private LocalDate pDate;
	
	@Column(name="content")
	private String content;
	
	@Column(name="note")
	private String note;

	
	
	public PeriodBackup(Integer id, Integer trainerStudentId, Integer timeId, LocalDate pDate, String content, String note) {
		super();
		this.id = id;
		this.trainerStudentId = trainerStudentId;
		this.timeId = timeId;
		this.pDate = pDate;
		this.content = content;
		this.note = note;
	}


	public PeriodBackup() {

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


	public LocalDate getpDate() {
		return pDate;
	}


	public void setpDate(LocalDate pDate) {
		this.pDate = pDate;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}
