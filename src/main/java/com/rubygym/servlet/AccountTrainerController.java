package com.rubygym.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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


@WebServlet("/account-trainer")
public class AccountTrainerController extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	static SessionFactory factory = HibernateUtil.getSessionFactory();
	// admin đăng ký tài khoản mới cho trainer (do admin thực hiện)
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			System.out.print(t.toJSONString());
			AccountTrainer newAccountTrainer = new AccountTrainer();
			if (t.get("username") != null) newAccountTrainer.setUsername((String) t.get("username"));
			else {
				throw new Exception("Không được để trống tên tài khoản");
			}
			if (t.get("password") != null) newAccountTrainer.setPassword((String) t.get("password"));
			else {
				throw new Exception("Không được để trống mật khẩu");
			}
			//Kiểm tra xem đã tồn tại tại khoản trên chưa
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountTrainer> cr = cb.createQuery(AccountTrainer.class);
			Root<AccountTrainer> root  = cr.from(AccountTrainer.class);
			cr.where(root.get("username").in(t.get("username")));		
			List<AccountTrainer> result = session.createQuery(cr).getResultList();
			System.out.print(result.toString());
			if(result.size() > 0) {
				throw new Exception("Tài khoản này đã tồn tại");
			}
			
			session.save(newAccountTrainer); // tạo tài khoản lưu vào DB
			tx.commit();
			
			//Lấy thông tin tài khoản vừa tạo
			cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountTrainer> cr1 = cb.createQuery(AccountTrainer.class);
			Root<AccountTrainer> root1  = cr1.from(AccountTrainer.class);
			cr1.where(root1.get("username").in(t.get("username")));		
			List<AccountTrainer> result1 = session.createQuery(cr1).getResultList();
			
			//Gửi thông tin tài khoản vừa tạo về cho client
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(AccountTrainer temp:result1) {
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
	
	// admin lấy thông tin tài khoản: username, password của tất cả trainer (trên tài khoản của admin)
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			String[] criteria_array = HttpRequestUtil.getQuery(req);
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountTrainer> cr = cb.createQuery(AccountTrainer.class);
			Root<AccountTrainer> root  = cr.from(AccountTrainer.class);
			if(criteria_array != null) {
				for(int i=0;i<criteria_array.length;i++) {
					System.out.print(criteria_array[i].split("=")[0]);
					System.out.print(criteria_array[i].split("=")[1]);
					cr.where(root.get(criteria_array[i].split("=")[0]).in(criteria_array[i].split("=")[1]));
				}
			}
			List<AccountTrainer> result = session.createQuery(cr).getResultList();
			System.out.print(result.get(0).getUsername().toString());
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(AccountTrainer temp:result) {
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
	
	// trainer sửa mật khẩu của mình (do trainer thực hiện chức năng đổi mật khẩu)
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			AccountTrainer newAccountTrainer = new AccountTrainer();
		
			Long temp = (Long) t.get("id");
			
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountTrainer> cr = cb.createQuery(AccountTrainer.class);
			Root<AccountTrainer> root  = cr.from(AccountTrainer.class);			
			cr.where(root.get("id").in(temp.intValue()));
			List<AccountTrainer> result = session.createQuery(cr).getResultList();
			newAccountTrainer = result.get(0);		

			if (t.get("password") != null) newAccountTrainer.setPassword((String) t.get("password"));
			session.update(newAccountTrainer);
			tx.commit();
			
			
			
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
