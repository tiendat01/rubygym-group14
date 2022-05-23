package com.rubygym.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.tool.schema.spi.SchemaDropper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.rubygym.model.Requirement;
import com.rubygym.model.Schedule;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.ScheduleUtil;
import com.rubygym.utils.TimeUtil;
import com.rubygym.utils.TrainerStudentUtil;

@WebServlet(urlPatterns = "/schedule-trainer/*")
public class ScheduleTrainerServlet extends HttpServlet {
	
//	private SessionFactory factory = HibernateUtil.getSessionFactory();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<Object[]> list = null;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
//		if (HttpRequestUtil.checkAuthentication(req)) {
			
			try {
//				resp.addHeader("Access-Control-Allow-Origin", "*");
				String idString = HttpRequestUtil.parseURL(req, "schedule-trainer"); // c1
				
				// c2: truy van tren query string:
//				Map<String, String> paramsMap = HttpRequestUtil.parseQuery(req);
//				
//				// kiểm tra mã trainer
//				String idString = paramsMap.get("trainerId");
//				if (idString != null) {
				
				if (ScheduleUtil.checkTrainerId(Integer.parseInt(idString))) {
					list = ScheduleUtil.getTrainerSchedule(Integer.parseInt(idString));
					for (Object[] s : list) {
						JSONObject tmp = new JSONObject();
						tmp.put("timeId", s[0]);
						tmp.put("dayOfWeek", s[1]);
						tmp.put("start", s[2] == null ? null : s[2].toString());
						tmp.put("finish", s[3] == null ? null : s[3].toString());
						tmp.put("studentNames", s[4]);
						
						data.add(tmp);
					}
					error.add(null);
					HttpResponseUtil.setResponse(resp, data, error);
				}
				
				else {
					data.add(null);
					error.add("ID này không tồn tại hoặc chưa được phân công trong hệ thống.");
					HttpResponseUtil.setResponse(resp, data, error);
				}
				
			} catch (Exception e) {
//				resp.addHeader("Access-Control-Allow-Origin", "*");
				// TODO: handle exception
				e.printStackTrace();
				
				data.add(null);
				error.add(e.getMessage());
				HttpResponseUtil.setResponse(resp, data, error);
			}
			
			
//		}
//		
//		
//		else {
//			data.add(null);
//			error.add("Yêu cầu đăng nhập");
//			HttpResponseUtil.setResponse(resp, data, error);	
//		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String idString = HttpRequestUtil.parseURL(req, "schedule-trainer");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			System.out.println("checkPOST?");
			JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
			Integer requireId = Integer.parseInt(dataClient.get("requireId").toString());
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			Requirement addRequirement = (Requirement) session.createQuery("from Requirement r where r.id = " + requireId).uniqueResult();
			Integer studentId = TrainerStudentUtil.getStudentId(addRequirement.getTrainerStudentId());
			
			if (!ScheduleUtil.isOvertime(Integer.parseInt(idString), addRequirement.getTimeId())) {
				
				if (!ScheduleUtil.isExceedStudent(Integer.parseInt(idString), addRequirement.getTimeId())) {
					
					// check lịch tập mới
					if (ScheduleUtil.isTimeIdValid(addRequirement.getTimeId(), studentId)) {
						Schedule schedule = new Schedule(addRequirement.getTrainerStudentId(), addRequirement.getTimeId());
						session.save(schedule);
						
						data.add("Đã thêm lịch tập vào thời khoá biểu. ");
						error.add(null);
						HttpResponseUtil.setResponse(resp, data, error);
					}
					
					else {
						data.add(null);
						error.add("Bạn là huấn luyện viên tồi."
								+ " Yêu cầu tạo lịch tập mới của người tập bị trùng với schedule hoặc không đáp ứng yêu cầu của trung tâm."
								+ " Một buổi tập không tập quá 2h");
						HttpResponseUtil.setResponse(resp, data, error);
					}
				}
				
				else {
					data.add(null);
					error.add("Bạn tham lam quá. Bạn sẽ hướng dẫn vượt số người tối đa/buổi do trung tâm quy định vào ngày thứ "
							+ TimeUtil.getTime(addRequirement.getTimeId()).getDayOfWeek() + " "
							+ TimeUtil.getTime(addRequirement.getTimeId()).getStart().toString() + " "
							+ TimeUtil.getTime(addRequirement.getTimeId()).getFinish().toString() + " ."
							+ " Huấn luyện viên chỉ được phép hướng dẫn tối đa 3 người/buổi tập");
					HttpResponseUtil.setResponse(resp, data, error);
				}
			}
			
			else {
				data.add(null);
				error.add("Bạn tham lam quá. Bạn sẽ làm vượt quá thời gian trung tâm quy định vào ngày thứ "
						+ TimeUtil.getTime(addRequirement.getTimeId()).getDayOfWeek()
						+ ". Huấn luyện viên chỉ được phép làm việc tối đa 8h/ngày");
				HttpResponseUtil.setResponse(resp, data, error);
			}
			
			session.getTransaction().commit();
		}
		
		catch (Exception e) {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			// TODO: handle exception
			e.printStackTrace();
			error.add(e.getMessage());
			data.add(null);
			HttpResponseUtil.setResponse(resp, data, error);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String idString = HttpRequestUtil.parseURL(req, "schedule-trainer");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			System.out.println("checkUPDATE?");
			JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
			Integer requireId = Integer.parseInt(dataClient.get("requireId").toString());
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			Requirement updateRequirement = (Requirement) session.createQuery("from Requirement r where r.id = " + requireId).uniqueResult();
			Integer studentId = TrainerStudentUtil.getStudentId(updateRequirement.getTrainerStudentId());
			
			if (!ScheduleUtil.isOvertime(Integer.parseInt(idString), updateRequirement.getTimeId())) {
				
				if (!ScheduleUtil.isExceedStudent(Integer.parseInt(idString), updateRequirement.getTimeId())) {
					
					// check lịch tập mới
					if (ScheduleUtil.isTimeIdValid(updateRequirement.getTimeId(), studentId)) {
						
						// chưa check scheduleId cần xoá có tồn tại trong Schedule database ko?
						session.createQuery("update Schedule s set s.timeId = " + updateRequirement.getTimeId()
								+ " where s.id = " + updateRequirement.getScheduleId()).executeUpdate();
						
						data.add("Đã cập nhật lịch tập mới vào thời khoá biểu. ");
						error.add(null);
						HttpResponseUtil.setResponse(resp, data, error);
					}
					
					else {
						data.add(null);
						error.add("Bạn là huấn luyện viên tồi."
								+ " Yêu cầu tạo lịch tập mới của người tập bị trùng với schedule hoặc không đáp ứng yêu cầu của trung tâm."
								+ " Một buổi tập không tập quá 2h");
						HttpResponseUtil.setResponse(resp, data, error);
					}
				}
				
				else {
					data.add(null);
					error.add("Bạn tham lam quá. Bạn sẽ hướng dẫn vượt số người tối đa/buổi do trung tâm quy định vào ngày thứ "
							+ TimeUtil.getTime(updateRequirement.getTimeId()).getDayOfWeek() + " "
							+ TimeUtil.getTime(updateRequirement.getTimeId()).getStart().toString() + " "
							+ TimeUtil.getTime(updateRequirement.getTimeId()).getFinish().toString() + " ."
							+ ". Huấn luyện viên chỉ được phép hướng dẫn tối đa 3 người/buổi tập");
					HttpResponseUtil.setResponse(resp, data, error);
				}
			}
			
			else {
				data.add(null);
				error.add("Bạn tham lam quá. Bạn sẽ làm vượt quá thời gian trung tâm quy định vào ngày thứ "
						+ TimeUtil.getTime(updateRequirement.getTimeId()).getDayOfWeek()
						+ ". Huấn luyện viên chỉ được phép làm việc tối đa 8h/ngày");
				HttpResponseUtil.setResponse(resp, data, error);
			}
			
			session.getTransaction().commit();
			
		}
		catch (Exception e) {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			// TODO: handle exception
			e.printStackTrace();
			error.add(e.getMessage());
			data.add(null);
			HttpResponseUtil.setResponse(resp, data, error);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String idString = HttpRequestUtil.parseURL(req, "schedule-trainer");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			System.out.println("checkDELETE?");
			JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
			Integer requireId = Integer.parseInt(dataClient.get("requireId").toString());
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			Requirement deleteRequirement = (Requirement) session.createQuery("from Requirement r where r.id = " + requireId).uniqueResult();
			Integer studentId = TrainerStudentUtil.getStudentId(deleteRequirement.getTrainerStudentId());
			
			// chưa check scheduleId cần xoá có tồn tại trong Schedule database ko?
			session.createQuery("delete from Schedule s where s.id = " + deleteRequirement.getScheduleId()).executeUpdate();
			
			session.getTransaction().commit();
			
			data.add("Đã xoá lịch tập trong thời khoá biểu. ");
			error.add(null);
			HttpResponseUtil.setResponse(resp, data, error);
		}
		catch (Exception e) {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			// TODO: handle exception
			e.printStackTrace();
			error.add(e.getMessage());
			data.add(null);
			HttpResponseUtil.setResponse(resp, data, error);
			
		}
	}
}
