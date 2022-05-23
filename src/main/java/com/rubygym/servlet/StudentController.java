package com.rubygym.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.protobuf.DoubleValue;
import com.rubygym.model.*;
import com.rubygym.utils.*;



@WebServlet("/student")
public class StudentController extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	static SessionFactory factory = HibernateUtil.getSessionFactory();
	
	// admin tạo mới 1 student, cập nhật account_student_id vào thông tin cá nhân
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			Student newStudent = new Student();
			if (t.get("avatar") != null) newStudent.setAvatar((String) t.get("avatar"));
			if (t.get("name") != null)newStudent.setName((String) t.get("name"));
			if (t.get("sex") != null) newStudent.setSex((String) t.get("sex"));
			if (t.get("date_of_birth") != null)newStudent.setDateOfBirth(LocalDate.parse(((String) t.get("date_of_birth")), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			if (t.get("phone_number") != null)newStudent.setPhoneNumber((String) t.get("phone_number"));
			if (t.get("email") != null)newStudent.setEmail((String) t.get("email"));
			if (t.get("description") != null)newStudent.setDescription((String) t.get("description"));
			if (t.get("weight") != null) newStudent.setWeight((Double) t.get("weight"));
			if (t.get("height") != null) newStudent.setHeight((Double) t.get("height"));
			if (t.get("bmi") != null) newStudent.setBmi((Double) t.get("bmi"));
			if (t.get("others") != null) newStudent.setOthers((String) t.get("others"));
			if (t.get("target") != null) newStudent.setTarget((String) t.get("target"));
			if (t.get("account_student_id") != null) newStudent.setAccountId( ((Long) t.get("account_student_id")).intValue());
			session.save(newStudent);
			tx.commit();
			
			
			//gửi http response về cho client
//			res.addHeader("Access-Control-Allow-Origin", "*");
			JSONObject bodyJsonResponse = new JSONObject();
			bodyJsonResponse.put("error", "null");
			bodyJsonResponse.put("data", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush();  	
		    
		    session.close();
			
		} catch (Exception e) {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			JSONObject bodyJsonResponse = new JSONObject();
			bodyJsonResponse.put("error", e.getMessage());
			JSONArray errors = new JSONArray();
			((ArrayList) errors).add(e.getMessage());
			bodyJsonResponse.put("error", errors);
			bodyJsonResponse.put("data", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush();  				
		}
	}
	
	// lấy tất cả thông tin cá nhân của student
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			String[] criteria_array = HttpRequestUtil.getQuery(req);
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Student> cr = cb.createQuery(Student.class);
			Root<Student> root  = cr.from(Student.class);
			if(criteria_array != null) {
				for(int i=0;i<criteria_array.length;i++) {
					System.out.print(criteria_array[i].split("=")[0]);
					System.out.print(criteria_array[i].split("=")[1]);
					cr.where(root.get(criteria_array[i].split("=")[0]).in(criteria_array[i].split("=")[1]));
				}
			}
			List<Student> result = session.createQuery(cr).getResultList();
			
			for(Student temp:result) {
				System.out.print(temp.getName());
			}
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(Student temp:result) {
				JSONObject jo = new JSONObject();
				jo.put("id", temp.getId());
				jo.put("avatar", temp.getAvatar());
				jo.put("name", temp.getName());
				jo.put("sex", temp.getSex());
				jo.put("date_of_birth", temp.getDateOfBirth() == null ? null : temp.getDateOfBirth().toString());
				jo.put("phone_number", temp.getPhoneNumber());
				jo.put("description", temp.getDescription());
				jo.put("account_student_id", temp.getAccountId());
				jo.put("height", temp.getHeight());
				jo.put("weight", temp.getWeight());
				jo.put("bmi", temp.getBmi());
				jo.put("others", temp.getOthers());
				jo.put("target", temp.getTarget());
				
			
				((ArrayList) data).add(jo);
			}
			bodyJsonResponse.put("data", data);
			bodyJsonResponse.put("error", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush(); 
		    session.close();
		}
		catch(Exception e) {
//			 res.addHeader("Access-Control-Allow-Origin", "*");
			JSONObject bodyJsonResponse = new JSONObject();
			bodyJsonResponse.put("error", e.getMessage());
			JSONArray errors = new JSONArray();
			((ArrayList) errors).add(e.getMessage());
			bodyJsonResponse.put("error", errors);
			bodyJsonResponse.put("data", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush();  		
		}
	}
	
	
	
	
	// Các chức năng của student
	
	
	
	// student sửa thông tin cá nhân của 1 student theo Id
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			Student newStudent = new Student();
		
			Long temp = (Long) t.get("id");
			
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Student> cr = cb.createQuery(Student.class);
			Root<Student> root  = cr.from(Student.class);			
			cr.where(root.get("id").in(temp.intValue()));
			List<Student> result = session.createQuery(cr).getResultList();
			newStudent = result.get(0);		

			if (t.get("avatar") != null) newStudent.setAvatar((String) t.get("avatar"));
			if (t.get("name") != null)newStudent.setName((String) t.get("name"));
			if (t.get("sex") != null) newStudent.setSex((String) t.get("sex"));
			if (t.get("date_of_birth") != null)newStudent.setDateOfBirth(LocalDate.parse(((String) t.get("date_of_birth")), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			if (t.get("phone_number") != null)newStudent.setPhoneNumber((String) t.get("phone_number"));
			if (t.get("email") != null)newStudent.setEmail((String) t.get("email"));
			if (t.get("description") != null)newStudent.setDescription((String) t.get("description"));
			if (t.get("height") != null)newStudent.setHeight(((Double) t.get("height")).doubleValue());
			if (t.get("weight") != null)newStudent.setWeight(((Double) t.get("weight")).doubleValue());
			if (t.get("bmi") != null)newStudent.setBmi(((Double) t.get("bmi")).doubleValue());
			if (t.get("others") != null)newStudent.setOthers((String) t.get("others"));
			if (t.get("target") != null)newStudent.setTarget((String) t.get("target"));
			if (t.get("account_student_id") != null)newStudent.setAccountId(((Long) t.get("account_student_id")).intValue());
			session.update(newStudent);
			tx.commit();
			
			session.close();
			
			
			//gửi http response về cho client
			JSONObject bodyJsonResponse = new JSONObject();
			bodyJsonResponse.put("error", "null");
			bodyJsonResponse.put("data", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush();  					
			
		} catch (Exception e) {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			JSONObject bodyJsonResponse = new JSONObject();
			bodyJsonResponse.put("error", e.getMessage());
			JSONArray errors = new JSONArray();
			((ArrayList) errors).add(e.getMessage());
			bodyJsonResponse.put("error", errors);
			bodyJsonResponse.put("data", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush();  				
		}
	}
	
}
