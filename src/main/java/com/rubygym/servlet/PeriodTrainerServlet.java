package com.rubygym.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.rubygym.model.Time;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.ScheduleUtil;
import com.rubygym.utils.TimeUtil;

@WebServlet(urlPatterns = "/period-trainer/*")
public class PeriodTrainerServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		List<Object[]> list = null;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			String idString = HttpRequestUtil.parseURL(req, "period-trainer");
			if (ScheduleUtil.checkTrainerId(Integer.parseInt(idString))) {
				
				Session session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
//				list = session.createQuery("select p.id, t.dayOfWeek, t.start, t.finish, p.pDate, p.content, p.note, s.name"
//						+ " from Time t, TrainerStudent ts, Period p, Student s"
//						+ " where t.id = p.timeId and ts.id = p.trainerStudentId"
//						+ " and s.id = ts.studentId and ts.trainerId = " + idString)
//						.getResultList();
//				session.getTransaction().commit();
//				
//				for (Object[] s : list) {
//					JSONObject tmp = new JSONObject();
//					tmp.put("periodId", s[0]);
//					tmp.put("dayOfWeek", s[1]);
//					tmp.put("start", s[2] == null ? null : s[2].toString());
//					tmp.put("finish", s[3] == null ? null : s[3].toString());
//					tmp.put("pDate", s[4] == null ? null : s[4].toString());
//					tmp.put("content", s[5]);
//					tmp.put("note", s[6]);
//					tmp.put("studentName", s[7]);
//					data.add(tmp);
//				}
				
				
				list = session.createQuery("select p.id, t.id, p.pDate, p.content, p.note, s.name"
						+ " from Time t, TrainerStudent ts, Period p, Student s"
						+ " where t.id = p.timeId and ts.id = p.trainerStudentId"
						+ " and s.id = ts.studentId and ts.trainerId = " + idString)
						.getResultList();
				
				session.getTransaction().commit();
				
				boolean[] checkList = new boolean[85];
				for (int i = 0; i < 85; i++) checkList[i] = false;
				
				for (Object[] s : list) {
					int i = Integer.parseInt(s[1].toString());
					checkList[i] = true;
				}
				
				for (int i = 1; i <= 84; i++) {
					if (checkList[i]) {
						List<Integer> periodId = new ArrayList<Integer>();
						List<String> studentName = new ArrayList<String>();
						List<String> content = new ArrayList<String>();
						List<String> note = new ArrayList<String>();
						String pDate = null;
						
						JSONObject tmp = new JSONObject();
						
						for (Object[] s : list) {
							if (Integer.parseInt(s[1].toString()) == i) {
								
								periodId.add(Integer.parseInt(s[0].toString()));
								studentName.add(s[5].toString());
								content.add(s[3] == null ? "" : s[3].toString());
								note.add(s[4] == null ? "" : s[4].toString());
								pDate = (s[2] == null) ? "" : s[2].toString();
								
							}
	
						}
						
						
						tmp.put("periodId", periodId);
						tmp.put("studentName", studentName);
						tmp.put("content", content);
						tmp.put("note", note);
						Time time = TimeUtil.getTime(i);
						tmp.put("dayOfWeek", time.getDayOfWeek());
						tmp.put("start", time.getStart() == null ? null : time.getStart().toString());
						tmp.put("finish", time.getFinish() == null ? null : time.getFinish().toString());
						tmp.put("pDate", pDate);
						
						
						data.add(tmp);
						
					}
					
				}
				
				
				error.add(null);
				HttpResponseUtil.setResponse(resp, data, error);
				
				session.close();
				
			}
			
			else {
				data.add(null);
				error.add("ID này không tồn tại hoặc chưa được phân công trong hệ thống.");
				HttpResponseUtil.setResponse(resp, data, error);
			}
			
		
			
		} catch (Exception e) {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			// TODO: handle exception
			e.printStackTrace();
			
			data.add(null);
			error.add(e.getMessage());
			HttpResponseUtil.setResponse(resp, data, error);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			String idString = HttpRequestUtil.parseURL(req, "period-trainer");
			JSONObject jsonObject = (JSONObject) HttpRequestUtil.getBody(req);
			Integer periodId = Integer.parseInt(jsonObject.get("periodId").toString());
			String content = jsonObject.get("content").toString();
			String note = jsonObject.get("note").toString();
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.createQuery("update Period p set p.content = ?0, p.note = ?1 where p.id = ?2")
								.setParameter(0, content)
								.setParameter(1, note)
								.setParameter(2, periodId)
								.executeUpdate();
			session.getTransaction().commit();
			
			data.add("Sửa nội dung và ghi chú cho buổi tập thành công");
			error.add(null);
			HttpResponseUtil.setResponse(resp, data, error);
			
			session.close();
			
		} catch (Exception e) {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			// TODO: handle exception
			e.printStackTrace();
			
			data.add(null);
			error.add(e.getMessage());
			HttpResponseUtil.setResponse(resp, data, error);
		}
	}
}
