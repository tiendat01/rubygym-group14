package com.rubygym.utils;

import java.time.LocalTime;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.rubygym.model.Time;;

public class TimeUtil {
	
	public static void main(String[] args) throws Exception {
		Time t = TimeUtil.getTime(30);
//		System.out.println(t.getId() + ' ' + t.getDayOfWeek() );//+ ' ' + t.getStart().toString() + ' ' + t.getFinish().toString());
		System.out.println(t.toString());
	}
	
	private static SessionFactory factory = HibernateUtil.getSessionFactory();
	
	public static Time getTime(int timeId) throws Exception {
		
		Session session = factory.openSession();
		session.beginTransaction();
		
		Time time = (Time) session.createQuery("from Time t where t.id = " +  timeId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();

		return time;
		
	}
	
	public static Time getTime(int dayOfWeek, LocalTime start, LocalTime finish) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Time time = (Time) session.createQuery("from Time t where t.dayOfWeek = ?0"
				+ " and t.start = cast(?1 as time) and t.finish = cast(?2 as time)" )
				.setParameter(0, dayOfWeek)
				.setParameter(1, start)
				.setParameter(2, finish)
				.uniqueResult();
		
		session.getTransaction().commit();
		session.close();
		
		return time;
	}
}
