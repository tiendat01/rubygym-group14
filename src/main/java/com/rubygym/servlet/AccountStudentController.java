package com.rubygym.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
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


@WebServlet("/account-student")
public class AccountStudentController extends HttpServlet  {
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
			System.out.print(t.toJSONString());
			AccountStudent newAccountStudent = new AccountStudent();
			if (t.get("username") != null) newAccountStudent.setUsername((String) t.get("username"));
			else {
				throw new Exception("KhÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng tÃªn tÃ i khoáº£n");
			}
			if (t.get("password") != null) { newAccountStudent.setPassword((String) t.get("password"));
			newAccountStudent.setAccumulation(0);
			newAccountStudent.setExpireDate(LocalDate.now());
			}
			
			else {
				throw new Exception("KhÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng máº­t kháº©u");
			}
			//Kiá»ƒm tra xem Ä‘Ã£ tá»“n táº¡i táº¡i khoáº£n trÃªn chÆ°a
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountStudent> cr = cb.createQuery(AccountStudent.class);
			Root<AccountStudent> root  = cr.from(AccountStudent.class);
			cr.where(root.get("username").in(t.get("username")));		
			List<AccountStudent> result = session.createQuery(cr).getResultList();
			System.out.print(result.toString());
			if(result.size() > 0) {
				throw new Exception("TÃ i khoáº£n nÃ y Ä‘Ã£ tá»“n táº¡i");
			}
			
			session.save(newAccountStudent);
			tx.commit();
			
			//Láº¥y thÃ´ng tin tÃ i khoáº£n vá»«a táº¡o
			cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountStudent> cr1 = cb.createQuery(AccountStudent.class);
			Root<AccountStudent> root1  = cr1.from(AccountStudent.class);
			cr1.where(root1.get("username").in(t.get("username")));		
			List<AccountStudent> result1 = session.createQuery(cr1).getResultList();
			
			//Gá»­i thÃ´ng tin tÃ i khoáº£n vá»«a táº¡o vá»� cho client
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(AccountStudent temp:result1) {
				JSONObject jo = new JSONObject();
				jo.put("id", temp.getId());
				jo.put("username", temp.getUsername());
				jo.put("password", temp.getPassword());
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
							
			
		} catch (Exception e) {
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
	
	// admin láº¥y táº¥t cáº£ thÃ´ng tin vá»� tÃ i khoáº£n cá»§a student
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			String[] criteria_array = HttpRequestUtil.getQuery(req);
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountStudent> cr = cb.createQuery(AccountStudent.class);
			Root<AccountStudent> root  = cr.from(AccountStudent.class);
			if(criteria_array != null) {
				for(int i=0;i<criteria_array.length;i++) {
					System.out.print(criteria_array[i].split("=")[0]);
					System.out.print(criteria_array[i].split("=")[1]);
					cr.where(root.get(criteria_array[i].split("=")[0]).in(criteria_array[i].split("=")[1]));
				}
			}
			List<AccountStudent> result = session.createQuery(cr).getResultList();
			System.out.print(result.get(0).getUsername().toString());
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(AccountStudent temp:result) {
				JSONObject jo = new JSONObject();
				jo.put("id", temp.getId());
				jo.put("username", temp.getUsername());
				jo.put("password", temp.getPassword());
				jo.put("accumulation", temp.getAccumulation());
				jo.put("expire", temp.getExpireDate() == null ? null : temp.getExpireDate().toString());
				jo.put("service_id", temp.getServiceId());
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
	
	
	
	// student sá»­a máº­t kháº©u
	// thÃªm chá»©c nÄƒng gia háº¡n gÃ³i táº­p táº¡i Ä‘Ã¢y 
	// phÃ¢n biá»‡t báº±ng body request ......
	
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//Ä‘á»�c body cá»§a http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			AccountStudent newAccountStudent = new AccountStudent();
		
			Long temp = (Long) t.get("id");
			
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountStudent> cr = cb.createQuery(AccountStudent.class);
			Root<AccountStudent> root  = cr.from(AccountStudent.class);			
			cr.where(root.get("id").in(temp.intValue()));
			List<AccountStudent> result = session.createQuery(cr).getResultList();
			newAccountStudent = result.get(0);		

			if (t.get("password") != null) newAccountStudent.setPassword((String) t.get("password"));
			//if (t.get("expire") != null) newAccountStudent.setExpireDate((LocalDate) LocalDate.parse((CharSequence) t.get("expire")));
			//if (t.get("accumulation") != null) newAccountStudent.setAccumulation((Integer) t.get("accumulation"));
			if (t.get("service_id") != null) {
			newAccountStudent.setServiceId(((Long) t.get("service_id")).intValue());
			
			//tim kiem trong bang service record tuong ung voi service_id
			CriteriaQuery<Service> cr1 = cb.createQuery(Service.class);
			Root<Service> root1  = cr1.from(Service.class);			
			cr1.where(root.get("id").in(((Long) t.get("service_id")).intValue()));
			List<Service> result1 = session.createQuery(cr1).getResultList();
			if(result1.size()==0) {
				throw new Exception("KhÃ´ng tá»“n táº¡i gÃ³i táº­p");
			}
			else {	
				if(newAccountStudent.getAccumulation() >= 12 && result1.get(0).getnMonths()>=12) {
					newAccountStudent.setExpireDate( newAccountStudent.getExpireDate().plusMonths(result1.get(0).getnMonths() + 3 ));			
					newAccountStudent.setAccumulation( newAccountStudent.getAccumulation() + result1.get(0).getnMonths() + 3);
					System.out.println("Ban la thanh vien thien thiet");
				}
				else {
					newAccountStudent.setExpireDate( newAccountStudent.getExpireDate().plusMonths(result1.get(0).getnMonths()));			
					newAccountStudent.setAccumulation( newAccountStudent.getAccumulation() + result1.get(0).getnMonths());
					System.out.println("Ban khong la thanh vien thien thiet");
				}
				
				
			
			}
			}
			//tim kiem trong bang service record tuong ung voi service_id end
			session.update(newAccountStudent);
			tx.commit();
			
			
			
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
	
}
