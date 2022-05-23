package com.rubygym.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.ScheduleUtil;

@WebServlet(urlPatterns = "/period-student/*")
public class PeriodStudentServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<Object[]> list = null;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			String idString = HttpRequestUtil.parseURL(req, "period-student");
			if (ScheduleUtil.checkStudentId(Integer.parseInt(idString))) {
				Session session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
				
				list = session.createQuery("select p.id, t.dayOfWeek, t.start, t.finish, p.pDate, p.content, p.note"
						+ " from Time t, TrainerStudent ts, Period p"
						+ " where t.id = p.timeId and ts.id = p.trainerStudentId and ts.studentId = " + idString)
						.getResultList();
				
				for (Object[] s : list) {
					JSONObject tmp = new JSONObject();
					tmp.put("periodId", s[0]);
					tmp.put("dayOfWeek", s[1]);
					tmp.put("start", s[2] == null ? null : s[2].toString());
					tmp.put("finish", s[3] == null ? null : s[3].toString());
					tmp.put("pDate", s[4] == null ? null : s[4].toString());
					tmp.put("content", s[5]);
					tmp.put("note", s[6]);
					data.add(tmp);
				}
				error.add(null);
				HttpResponseUtil.setResponse(resp, data, error);
				
				session.getTransaction().commit();
				
				session.close();
				
			}
			else {
				data.add(null);
				error.add("ID này không đăng ký gói tập luyện với PT hoặc chưa tồn tại trong hệ thống. \nKhông có chức năng này");
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
}
