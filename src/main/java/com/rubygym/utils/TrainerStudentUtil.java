package com.rubygym.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.rubygym.model.Student;
import com.rubygym.model.Trainer;

public class TrainerStudentUtil {
	
	private static SessionFactory factory = HibernateUtil.getSessionFactory();
	
	public static Student getStudent(Integer studentId) {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Student s = (Student) session.createQuery(""
				+ "from Student s where s.id = "
				+ studentId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
//		HibernateUtil.shutdown();
		return s;
	}
	
	public static Trainer getTrainer(Integer trainerId) {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Trainer t = (Trainer) session.createQuery(""
				+ "from Trainer t where t.id = "
				+ trainerId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
//		HibernateUtil.shutdown();
		return t;
	}
	
	public static Integer getTrainerId(int studentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Integer trainerId = (Integer) session.createQuery("select ts.trainerId"
				+ " from TrainerStudent ts where ts.studentId = " + studentId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
//		HibernateUtil.shutdown();
		return trainerId;
	}
	
	public static Integer getTrainerStudentId(int studentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Integer trainerStudentId = (Integer) session.createQuery("select ts.id"
				+ " from TrainerStudent ts where ts.studentId = " + studentId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
//		HibernateUtil.shutdown();
		return trainerStudentId;
	}
	
	public static Integer getStudentId(int trainerStudentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Integer studentId = (Integer) session.createQuery("select ts.studentId"
				+ " from TrainerStudent ts where ts.id = " + trainerStudentId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
//		HibernateUtil.shutdown();
		return studentId;
	}
}
