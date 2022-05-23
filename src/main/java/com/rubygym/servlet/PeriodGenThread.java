package com.rubygym.servlet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.hibernate.Session;

import com.rubygym.model.Period;
import com.rubygym.model.Schedule;
import com.rubygym.model.Student;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.ScheduleUtil;
import com.rubygym.utils.TimeUtil;

// command này sẽ chạy vào 23h mỗi chủ nhật hàng tuần
public class PeriodGenThread implements Runnable {

	private ServletContext context;
	
	public PeriodGenThread(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public void run() {
		
		try {
			
			LocalDate now = LocalDate.now(); // ngày chủ nhật sẽ chạy gen tự động
			
			// tạo period vào database
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			session.createQuery("insert into PeriodBackup(id, trainerStudentId, timeId, pDate, content, note)"
					+ " select p.id, p.trainerStudentId, p.timeId, p.pDate, p.content, p.note from Period p").executeUpdate();
			session.createQuery("delete from Period").executeUpdate();
			
			// lọc, loại bỏ các studentId quá hạn so với now()_chủ nhật thời điểm gen
			// lọc theo trainerStudentId ra khỏi bảng đký với trainer: TrainerStudent, Schedule, Requirement
			List<Student> students = session.createQuery("from Student").getResultList();
			for (Student s : students) {
				if (ScheduleUtil.isExpired(s.getId(), now)) {
					ScheduleUtil.filterExpiredStudent(s.getId());
				}
			}
			
			// khi đó, Schedule được chuẩn hoá sau khi loại bỏ các studentId hết hạn
			List<Schedule> timetable = session.createQuery("from Schedule").getResultList();
			
			// sinh ra:
			for (Schedule s : timetable) {
				// thứ2:2 | thứ3:3 | ... | thứCN:8
				Integer dayOfWeek = TimeUtil.getTime(s.getTimeId()).getDayOfWeek();
				LocalDate pDate = now.plusDays(dayOfWeek-1);
				Period period = new Period(s.getTrainerStudentId(), s.getTimeId(), pDate, null, null);
				session.save(period);
			}
			System.out.println("DAMM!");
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
