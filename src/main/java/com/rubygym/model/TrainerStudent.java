package com.rubygym.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="trainer_student")
public class TrainerStudent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="trainer_id")
	private Integer trainerId;
	
	@Column(name="student_id")
	private Integer studentId;
	
	@Column(name="route")
	private String route;
	
	@Column(name="comment")
	private String comment;

	public TrainerStudent(Integer id, Integer trainerId, Integer studentId, String route, String comment) {
		super();
		this.id = id;
		this.trainerId = trainerId;
		this.studentId = studentId;
		this.route = route;
		this.comment = comment;
	}
	
	public TrainerStudent() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(Integer trainerId) {
		this.trainerId = trainerId;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
