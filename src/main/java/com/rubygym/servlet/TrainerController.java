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

import com.rubygym.model.*;
import com.rubygym.utils.*;



@WebServlet("/trainer")
public class TrainerController extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	static SessionFactory factory = HibernateUtil.getSessionFactory();
	
	// admin tạo trainer mới , cập nhật account_trainer_id vào thông tin cá nhân của trainer
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			Trainer newTrainer = new Trainer();
			if (t.get("avatar") != null) newTrainer.setAvatar((String) t.get("avatar"));
			if (t.get("name") != null)newTrainer.setName((String) t.get("name"));
			if (t.get("sex") != null) newTrainer.setSex((String) t.get("sex"));
			if (t.get("date_of_birth") != null)newTrainer.setDateOfBirth(LocalDate.parse(((String) t.get("date_of_birth")), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			if (t.get("phone_number") != null)newTrainer.setPhoneNumber((String) t.get("phone_number"));
			if (t.get("email") != null)newTrainer.setEmail((String) t.get("email"));
			if (t.get("description") != null)newTrainer.setDescription((String) t.get("description"));
			if (t.get("account_trainer_id") != null) newTrainer.setAccountId( ((Long) t.get("account_trainer_id")).intValue());
			session.save(newTrainer);
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
	
	// lấy thông tin cá nhân của tất cả trainer
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			String[] criteria_array = HttpRequestUtil.getQuery(req);
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Trainer> cr = cb.createQuery(Trainer.class);
			Root<Trainer> root  = cr.from(Trainer.class);
			if(criteria_array != null) {
				for(int i=0;i<criteria_array.length;i++) {
					System.out.print(criteria_array[i].split("=")[0]);
					System.out.print(criteria_array[i].split("=")[1]);
					cr.where(root.get(criteria_array[i].split("=")[0]).in(criteria_array[i].split("=")[1]));
				}
			}
			List<Trainer> result = session.createQuery(cr).getResultList();
			
			for(Trainer temp:result) {
				System.out.print(temp.getName());
			}
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(Trainer temp:result) {
				JSONObject jo = new JSONObject();
				jo.put("id", temp.getId());
				jo.put("avatar", temp.getAvatar());
				jo.put("name", temp.getName());
				jo.put("sex", temp.getSex());
				jo.put("date_of_birth", temp.getDateOfBirth() == null ? null : temp.getDateOfBirth().toString());
				jo.put("phone_number", temp.getPhoneNumber());
				jo.put("description", temp.getDescription());
				jo.put("account_trainer_id", temp.getAccountId());
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
		    
		    tx.commit(); session.close();
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
	
	
	
	
	
	
	// chức năng của trainer
	
	
	// sửa thông tin cá nhân của trainer theo id
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			Trainer newTrainer = new Trainer();
		
			Long temp = (Long) t.get("id");
			
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Trainer> cr = cb.createQuery(Trainer.class);
			Root<Trainer> root  = cr.from(Trainer.class);			
			cr.where(root.get("id").in(temp.intValue()));
			List<Trainer> result = session.createQuery(cr).getResultList();
			newTrainer = result.get(0);		

			if (t.get("avatar") != null) newTrainer.setAvatar((String) t.get("avatar"));
			if (t.get("name") != null)newTrainer.setName((String) t.get("name"));
			if (t.get("sex") != null) newTrainer.setSex((String) t.get("sex"));
			if (t.get("date_of_birth") != null)newTrainer.setDateOfBirth(LocalDate.parse(((String) t.get("date_of_birth")), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			if (t.get("phone_number") != null)newTrainer.setPhoneNumber((String) t.get("phone_number"));
			if (t.get("email") != null)newTrainer.setEmail((String) t.get("email"));
			if (t.get("description") != null)newTrainer.setDescription((String) t.get("description"));
			if (t.get("account_trainer_id") != null)newTrainer.setAccountId( ((Long) t.get("account_trainer_id")).intValue());
			session.update(newTrainer);
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
