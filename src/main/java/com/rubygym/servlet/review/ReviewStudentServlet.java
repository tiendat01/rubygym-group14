package com.rubygym.servlet.review;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.rubygym.model.ReviewStudent;
import com.rubygym.model.Student;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.TrainerStudentUtil;

@WebServlet("/review-student/*")
public class ReviewStudentServlet extends HttpServlet {
	
	MyInterface myInterface = (trangthai) -> {
		if (trangthai == -1) return "Không nhận được phản hồi từ admin";
		else if (trangthai == 0) return "Chờ xử lý";
		else if (trangthai == 1) return "Đã được xử lý";
		else return "";
	};
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ReviewStudent reviewStudent;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			String idString = HttpRequestUtil.parseURL(req, "review-student");
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			reviewStudent = (ReviewStudent) session.createQuery(""
					+ "from ReviewStudent rs where rs.studentId = " 
					+ Integer.parseInt(idString)).uniqueResult();
			
			session.getTransaction().commit();
			session.close();
			
			Student s = TrainerStudentUtil.getStudent(Integer.parseInt(idString));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("studentId", s.getId());
			jsonObject.put("name", s.getName());
			jsonObject.put("review", reviewStudent.getReview());
			jsonObject.put("rate", reviewStudent.getRate());
			jsonObject.put("date", reviewStudent.getDate() == null ? null : reviewStudent.getDate().toString());
			jsonObject.put("state", myInterface.execute(reviewStudent.getState()));
			
			data.add(jsonObject);
			error.add(null);
			
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			HttpResponseUtil.setResponse(resp, data, error);
			
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			ReviewStudent reviewStudent = null;
			String idString = HttpRequestUtil.parseURL(req, "review-student");
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			reviewStudent = (ReviewStudent) session.createQuery(""
					+ "from ReviewStudent rs where rs.studentId = " 
					+ Integer.parseInt(idString)).uniqueResult();
			
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			
			if (reviewStudent == null) {
				reviewStudent = new ReviewStudent();
				JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
				reviewStudent.setStudentId(Integer.parseInt(idString));
				if (dataClient.get("rate") != null) reviewStudent.setRate(Integer.parseInt(dataClient.get("rate").toString()));
				if (dataClient.get("review") != null) reviewStudent.setReview(dataClient.get("review").toString());
				if (dataClient.get("date") != null) reviewStudent.setDate(LocalDate.parse(((String) dataClient.get("date")), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				reviewStudent.setState(0);
				
				session.save(reviewStudent);
				
				
				data.add("Đánh giá / Phản hồi thành công. Đợi người quản lý xử lý !");
				error.add(null);
				HttpResponseUtil.setResponse(resp, data, error);
			}
			
			else {
				data.add(null);
				error.add("Bạn đã phản hồi đánh giá rồi !");
				HttpResponseUtil.setResponse(resp, data, error);
			}
			
			session.getTransaction().commit();
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
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			
			String idString = HttpRequestUtil.parseURL(req, "review-student");
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			session.createQuery("delete from ReviewStudent rs where rs.studentId = "
					+ Integer.parseInt(idString) ).executeUpdate();
			
			session.getTransaction().commit();
			session.close();
			
			data.add("Xoá đánh giá / phản hồi thành công");
			error.add(null);
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			HttpResponseUtil.setResponse(resp, data, error);
			
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
		ReviewStudent reviewStudent = new ReviewStudent();
		
		try {
			
			String idString = HttpRequestUtil.parseURL(req, "review-student");
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
			if (dataClient.get("rate") != null) reviewStudent.setRate(Integer.parseInt(dataClient.get("rate").toString()));
			if (dataClient.get("review") != null) reviewStudent.setReview(dataClient.get("review").toString());
			if (dataClient.get("date") != null) reviewStudent.setDate(LocalDate.parse(((String) dataClient.get("date")), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			reviewStudent.setState(0);
			
			session.createQuery("update ReviewStudent rs "
					+ "set rs.review = ?0, rs.rate = ?1, rs.date = ?2, rs.state = ?3 "
					+ "where rs.id = " + Integer.parseInt(idString))
			.setParameter(0, reviewStudent.getReview())
			.setParameter(1, reviewStudent.getRate())
			.setParameter(2, reviewStudent.getDate())
			.setParameter(3, reviewStudent.getState())
			.executeUpdate();
			
			session.getTransaction().commit();
			session.close();
			
			data.add("Chỉnh sửa đánh giá / phản hồi thành công");
			error.add(null);
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			HttpResponseUtil.setResponse(resp, data, error);
			
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
