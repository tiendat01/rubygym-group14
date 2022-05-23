package com.rubygym.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
import com.rubygym.model.Student;
import com.rubygym.model.Trainer;
import com.rubygym.model.TrainerStudent;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;

@WebServlet("/trainer-student")
public class TrainerStudentController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	static SessionFactory factory = HibernateUtil.getSessionFactory();
	// admin Ä‘Äƒng kÃ½ tÃ i khoáº£n cá»§a student
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//Ä‘á»�c body cá»§a http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			TrainerStudent newTrainerStudent = new TrainerStudent();
			if (t.get("trainer_id") != null) newTrainerStudent.setTrainerId(((Long) t.get("trainer_id")).intValue());
			else {
				throw new Exception("KhÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng trainer_id");
			}
			if (t.get("student_id") != null) newTrainerStudent.setStudentId(((Long) t.get("student_id")).intValue());
			else {
				throw new Exception("KhÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng student_id");
			}
			
			session.save(newTrainerStudent);
			tx.commit();
			
			session.close();
			
			
			//gá»­i http response vá»� cho client
			JSONObject bodyJsonResponse = new JSONObject();
			bodyJsonResponse.put("error", "null");
			bodyJsonResponse.put("data", "null");
			String bodyStringResponse = bodyJsonResponse.toJSONString();
			PrintWriter out = res.getWriter();
		    res.setContentType("application/json");
		    res.setCharacterEncoding("UTF-8");
		    out.print(bodyStringResponse);
		    out.flush();  				
		}
		catch(Exception e) {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			System.out.print(e.getMessage());
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
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			String[] criteria_array = HttpRequestUtil.getQuery(req);
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<TrainerStudent> cr = cb.createQuery(TrainerStudent.class);
			Root<TrainerStudent> root  = cr.from(TrainerStudent.class);
			if(criteria_array != null) {
				for(int i=0;i<criteria_array.length;i++) {
					System.out.print(criteria_array[i].split("=")[0]);
					System.out.print(criteria_array[i].split("=")[1]);
					cr.where(root.get(criteria_array[i].split("=")[0]).in(criteria_array[i].split("=")[1]));
				}
			}
			List<TrainerStudent> result = session.createQuery(cr).getResultList();
			
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(TrainerStudent temp:result) {
				JSONObject jo = new JSONObject();
				//more detail
				cb = session.getCriteriaBuilder();
				CriteriaQuery<Student> cr2 = cb.createQuery(Student.class);
				Root<Student> root2  = cr2.from(Student.class);
				cr2.where(root2.get("id").in(temp.getStudentId()));
				List<Student> result2 = session.createQuery(cr2).getResultList();
				//
				jo.put("name", (result2.get(0)).getName());
				jo.put("date_of_birth", (result2.get(0)).getDateOfBirth() == null ? null : (result2.get(0)).getDateOfBirth().toString());
				jo.put("sex", (result2.get(0)).getSex());
				jo.put("description", (result2.get(0)).getDescription());
				jo.put("weight", (result2.get(0)).getWeight());
				jo.put("target", (result2.get(0)).getTarget());
				jo.put("phone_number", (result2.get(0)).getPhoneNumber());
				jo.put("height", (result2.get(0)).getHeight());
				jo.put("bmi", (result2.get(0)).getBmi());
				
				jo.put("id", temp.getId());
				jo.put("trainer_id", temp.getTrainerId());
				jo.put("student_id", temp.getStudentId());
				jo.put("route", temp.getRoute());
				jo.put("comment", temp.getComment());
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
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//Ä‘á»�c body cá»§a http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			TrainerStudent newTrainerStudent = new TrainerStudent();
		
			Long temp = (Long) t.get("id");
			
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<TrainerStudent> cr = cb.createQuery(TrainerStudent.class);
			Root<TrainerStudent> root  = cr.from(TrainerStudent.class);			
			cr.where(root.get("id").in(temp.intValue()));
			List<TrainerStudent> result = session.createQuery(cr).getResultList();
			newTrainerStudent = result.get(0);		

			if (t.get("trainer_id") != null) newTrainerStudent.setTrainerId( (Integer) t.get("trainer_id"));
			if (t.get("student_id") != null) newTrainerStudent.setStudentId( (Integer) t.get("student_id"));
			if (t.get("route") != null)newTrainerStudent.setRoute((String) t.get("route"));
			if (t.get("comment") != null)newTrainerStudent.setComment((String) t.get("comment"));
		
			session.update(newTrainerStudent);
			tx.commit();
			
			session.close();
			
			
			//gá»­i http response vá»� cho client
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
	protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//Ä‘á»�c body cá»§a http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			TrainerStudent newTrainerStudent = new TrainerStudent();
			newTrainerStudent.setId(((Long) t.get("id")).intValue());
			
	
			session.delete(newTrainerStudent);
			tx.commit();
			
			session.close();
			
			
			//gá»­i http response vá»� cho client
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
	
