package com.rubygym.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.rubygym.model.Schedule;
import com.rubygym.model.Time;

import net.bytebuddy.description.field.FieldDescription.InGenericShape;

public class ScheduleUtil {
	
	private static SessionFactory factory = HibernateUtil.getSessionFactory();
	
	// xoá các thành phần của studentID hết hạn
	public static void filterExpiredStudent(Integer studentId) throws Exception {
		Integer trainerStudentId = TrainerStudentUtil.getTrainerStudentId(studentId);
		
		Session session = factory.openSession();
		session.beginTransaction();
		
		session.createQuery("delete from Schedule s where s.trainerStudentId = " + trainerStudentId);
		session.createQuery("delete from Requirement r where r.trainerStudentId = " + trainerStudentId);
		session.createQuery("delete from TrainerStudent ts where ts.id = " + trainerStudentId);
		
		session.getTransaction().commit();
		session.close();
		
	}
	
	
	// studentId lấy từ bảng TrainerStudent
	public static boolean isExpired(Integer studentId, LocalDate checkDate) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		LocalDate expireDate = LocalDate.parse(session.createQuery("select acc.expireDate"
				+ " from Student s, AccountStudent acc"
				+ " where s.accountId = acc.id and s.id = " + studentId).uniqueResult().toString()
				, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		session.getTransaction().commit();
		session.close();
		
		if (expireDate.isBefore(checkDate)) return true; // đã hết hạn so với ngày check
		return false;
	}
	
	
	
	
	// for requirement add/update, do tạo ra newTime
	public static boolean isTimeIdValid(Integer timeId, Integer studentId) throws Exception {
		
		Session session = factory.openSession();
		session.beginTransaction();
		
		// kiểm tra timeId mới có tồn tại trong schedule chưa?
		Object duplicateObject = session.createQuery("select s.id from Schedule s, TrainerStudent ts"
				+ " where s.trainerStudentId = ts.id and ts.studentId = ?0 and s.timeId = ?1")
				.setParameter(0, studentId)
				.setParameter(1, timeId)
				.uniqueResult(); 
		if (duplicateObject != null) return false;
		
		
		// kiểm tra xem có đký newTime thoả mãn 1 buổi tập ko vượt quá tối đa 2h, mỗi timeId (1 buổi tập) tương đương 1h
		// có nghĩa là chỉ có tối đa 2 buổi tập liền nhau
		List<Integer> list = session.createQuery("select s.timeId"
				+ " from Schedule s, TrainerStudent ts"
				+ " where s.trainerStudentId = ts.id and ts.studentId = " + studentId).getResultList();
		
//		List<Integer> list1 = session.createQuery("select r.timeId"
//				+ " from Requirement r, TrainerStudent ts"
//				+ " where r.trainerStudentId = ts.id and ts.studentId = " + studentId
//				+ " and r.category <> -1").getResultList();
		session.getTransaction().commit();
		session.close();
//		
//		list.addAll(list1);
		list.add(timeId);
		Collections.sort(list);
		ArrayList<Integer> arr = new ArrayList<Integer>(list);
		int index = arr.indexOf(timeId);
		// 1 ngày có 12 kíp chạy theo timeId: 7 ngày có 1 -> 84
		if (index > 0 && index < arr.size()-1 && arr.get(index)-1 == arr.get(index-1) && arr.get(index)+1 == arr.get(index+1)) {
			// sang ngày mới
			if (arr.get(index) % 12 == 0 || arr.get(index) % 12 == 1)
				return true;
			else
				return false;
		}
		if (index < arr.size()-2 && arr.get(index)+1 == arr.get(index+1) && arr.get(index)+2 == arr.get(index+2)) {
			if (arr.get(index+1) % 12 == 0 || arr.get(index+1) % 12 == 1)
				return true;
			else return false;
		}
		if (index > 1 && arr.get(index)-1 == arr.get(index-1) && arr.get(index)-2 == arr.get(index-2)) {
			if (arr.get(index-1) % 12 == 0 || arr.get(index-1) % 12 == 1)
				return true;
			else return false;
		}
		return true;
		
	}
	
	// nếu update/create lịch tập mới vào đây (timeId), có xảy ra overtime
	// trong 1 ngày (1 dayOfWeek) 1 hlv làm tối đa 8h <=> 8 timeId
	public static boolean isOvertime(Integer trainerId, Integer timeId) throws Exception {
		Time t = TimeUtil.getTime(timeId);
		Session session = factory.openSession();
		session.beginTransaction();
		List<Integer> periodPerDay = session.createQuery("select s.timeId"
				+ " from Schedule s, TrainerStudent ts, Time t"
				+ " where s.trainerStudentId = ts.id and s.timeId = t.id and t.dayOfWeek = ?0 and ts.trainerId = ?1"
				+ " group by s.timeId")
				.setParameter(0, t.getDayOfWeek())
				.setParameter(1, trainerId)
				.getResultList();
		session.getTransaction().commit();
		session.close();
		
		if (periodPerDay == null) return false;
		Integer countPeriodPerDay = periodPerDay.size();
		
		if (countPeriodPerDay + 1 > 8) return true; // thêm lịch tập mới gây ra overtime
		return false;
	}
	
	// nếu update/create lịch tập mới (timeId) vào đây có xảy ra exceed?
	// trong 1 buổi (1 timeId) 1 hlv chỉ có thể kèm tối đa 3 người
	public static boolean isExceedStudent(Integer trainerId, Integer timeId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		Object countStudentPerPeriod = session.createQuery("select count(*)"
				+ " from Schedule s, TrainerStudent ts"
				+ " where s.trainerStudentId = ts.id and s.timeId = ?0 and ts.trainerId = ?1")
				.setParameter(0, timeId)
				.setParameter(1, trainerId)
				.uniqueResult();
		session.getTransaction().commit();
		session.close();
		if (countStudentPerPeriod == null) return false;
		
		if (Integer.parseInt(countStudentPerPeriod.toString()) + 1 > 3) return true; // bị vượt quá số student có thể handle trong 1 period
		else return false;
	}
	
	
	// for requirement add/create, không vượt quá số buổi tối đa/tuần của gói tập
	public static boolean isExceedPeriod(Integer studentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		Object maxPeriod = session.createQuery("select service.periodPerWeek from Student s, AccountStudent accs, Service service"
				+ " where s.accountId = accs.id and accs.serviceId = service.id and s.id = " + studentId)
				.uniqueResult();
		Object countSchedulePeriod =  session.createQuery("select count(*) from TrainerStudent ts, Schedule s"
				+ " where ts.id = s.trainerStudentId and ts.studentId = " + studentId)
				.uniqueResult();
		Object countAddRequirementPeriod = session.createQuery("select count(*) from TrainerStudent ts, Requirement r"
				+ " where ts.id = r.trainerStudentId and r.category = 1 and ts.studentId = " + studentId)
				.uniqueResult();
		
		session.getTransaction().commit();
		session.close();
		
		if (maxPeriod == null || countAddRequirementPeriod == null || countSchedulePeriod == null) return false;
		
		if (Integer.parseInt(maxPeriod.toString()) <= Integer.parseInt(countAddRequirementPeriod.toString())
				+ Integer.parseInt(countSchedulePeriod.toString())) {
			return true;
		}
		return false;
	}
	
	// từ student request
	// check requirement có tồn tại trg requirement db
	// 1 schedule chỉ có thể xoá/sửa, ko đc cả 2 thao tác
	public static boolean checkDuplicatedRequirement(int category, Integer oldScheduleId, Integer timeId, Integer studentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		Object isExistR = null; // co ton tai time trong requirement
		
		if (category == -1) {
			isExistR = session.createQuery("from Requirement r, TrainerStudent ts"
					+ " where r.trainerStudentId = ts.id and r.scheduleId = ?0 and r.category <> 1 and ts.studentId = ?1")
					.setParameter(0, oldScheduleId)
					.setParameter(1, studentId)
					.uniqueResult();
		}
		
		else if (category == 0) {
			// newTime set bằng với oldTime => ko được thêm yêu cầu cập nhật
			if (getSchedule(oldScheduleId).getTimeId() == timeId) {
				return false;
			}
			isExistR = session.createQuery("from Requirement r, TrainerStudent ts"
					+ " where r.trainerStudentId = ts.id and r.scheduleId = ?0 and r.category <> 1 and ts.studentId = ?1")
					.setParameter(0, oldScheduleId)
					.setParameter(1, studentId)
					.uniqueResult();
		}
		
		else if (category == 1) {
			isExistR = session.createQuery("from Requirement r, TrainerStudent ts"
					+ " where r.trainerStudentId = ts.id and r.timeId = ?0 and r.category <> -1 and ts.studentId = ?1")
					.setParameter(0, timeId)
					.setParameter(1, studentId)
					.uniqueResult();
		}
		
		else {
			return true;
		}
		
		session.getTransaction().commit();
		session.close();
		
		if (isExistR != null) return false;
		return true;
		
	}
	
	// lấy schedule từ scheduleId
	public static Schedule getSchedule(int scheduleId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		Schedule schedule = (Schedule) session.createQuery("from Schedule s where s.id = " + scheduleId).uniqueResult();
		
		session.getTransaction().commit();
		session.close();
		
		return schedule;
	}
	
	// có tồn tại trong bảng phân công TrainerStudent ko?
	public static boolean checkTrainerId(int trainerId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		List list = session.createQuery("from TrainerStudent ts where"
				+ " ts.trainerId = " + trainerId).getResultList();
		
		session.getTransaction().commit();
		session.close();
		if (list.size() > 0)
			return true;
		return false;
	}
	
	// có tồn tại trong bảng phân công TrainerStudent ko?
	public static boolean checkStudentId(int studentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		List list = session.createQuery("from TrainerStudent ts where"
				+ " ts.studentId = " + studentId).getResultList();
		
		session.getTransaction().commit();
		session.close();
		if (list.size() > 0)
			return true;
		return false;
	}
	
	public static List<Object[]> getTrainerSchedule(int trainerId) throws Exception {

		Session session = factory.openSession();
		session.beginTransaction();
		
		List<Object[]> list = session.createQuery("select t.id, t.dayOfWeek, t.start, t.finish, t.id" 
				+ " from Schedule s, TrainerStudent ts, Time t where"
				+ " s.trainerStudentId = ts.id and s.timeId = t.id and "
				+ " ts.trainerId = " + trainerId
				+ " group by t.id, t.dayOfWeek, t.start, t.finish, t.id").getResultList();
		
		// lấy ds tên trong 1 buổi tập của 1 hlv (theo timeId)
//		Map<Integer, List<String>> studentNames = new HashMap<Integer, List<String>>();
		for (int i = 0; i < list.size(); i++) {
			int timeId = (int) list.get(i)[0];
			List<String> names = session.createQuery("select student.name"
					+ " from Schedule s, TrainerStudent ts, Student student"
					+ " where s.trainerStudentId = ts.id and student.id = ts.studentId"
					+ " and s.timeId = " + timeId).getResultList();
//			studentNames.put(timeId, names);
			list.get(i)[4] = names;
		}
		
		
		session.getTransaction().commit();
		session.close();
		
		return list;
	}
	
	public static List<Object[]> getStudentSchedule(int studentId) throws Exception {
		Session session = factory.openSession();
		session.beginTransaction();
		
		List<Object[]> list = session.createQuery("select s.id, t.dayOfWeek, t.start, t.finish, ts.trainerId" 
				+ " from Schedule s, TrainerStudent ts, Time t where"
				+ " s.trainerStudentId = ts.id and s.timeId = t.id and "
				+ " ts.studentId = " + studentId).getResultList();
		
		session.getTransaction().commit();
		session.close();
		
		return list;
	}
}
