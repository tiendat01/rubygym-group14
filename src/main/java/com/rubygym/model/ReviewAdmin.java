package com.rubygym.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "review_admin")
public class ReviewAdmin {
	
	@Id
	@Column(name = "student_id")
	private Integer studentId;
	
	@Column(name = "review")
	private String reviewFromStudent;
	
	@Column(name = "rate")
	private Integer rate;
	
	@Column(name = "date")
	private LocalDate date;
	
	public ReviewAdmin() {
		
	}

	public ReviewAdmin(Integer studentId, String reviewFromStudent, Integer rate, LocalDate date) {
		super();
		this.studentId = studentId;
		this.reviewFromStudent = reviewFromStudent;
		this.rate = rate;
		this.date = date;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String getReviewFromStudent() {
		return reviewFromStudent;
	}

	public void setReviewFromStudent(String reviewFromStudent) {
		this.reviewFromStudent = reviewFromStudent;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	
}
