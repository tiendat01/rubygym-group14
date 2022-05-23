package com.rubygym.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "review_student")
public class ReviewStudent {

	@Id
	@Column(name = "student_id")
	private Integer studentId;
	
	@Column(name = "review")
	private String review;
	
	@Column(name = "rate")
	private Integer rate;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "state")
	private Integer state = 0;
	
	public ReviewStudent() {
		
	}

	public ReviewStudent(Integer studentId, String review, Integer rate, LocalDate date, Integer state) {
		super();
		this.studentId = studentId;
		this.review = review;
		this.rate = rate;
		this.date = date;
		this.state = state;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	
	
	
}
