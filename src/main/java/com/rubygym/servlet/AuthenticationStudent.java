package com.rubygym.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
@WebServlet("/authentication-student")
public class AuthenticationStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static SessionFactory factory = HibernateUtil.getSessionFactory();
	
	// check đăng nhập cho student và trả về thông tin cá nhân cho client, sở hữu tài khoản nầy (nếu tồn tại)
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			
			//đọc body của http request
			JSONObject t =  (JSONObject) HttpRequestUtil.getBody(req);
			AccountStudent newAccountStudent = new AccountStudent();
			if (t.get("username") != null) newAccountStudent.setUsername((String) t.get("username"));
			else {
				throw new Exception("Không được để trống tên tài khoản");
			}
			if (t.get("password") != null) newAccountStudent.setPassword((String) t.get("password"));
			else {
				throw new Exception("Không được để trống mật khẩu");
			}
			//Tìm xem có tồn tại tài khoản nào như trên không
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<AccountStudent> cr = cb.createQuery(AccountStudent.class);
			Root<AccountStudent> root  = cr.from(AccountStudent.class);		
			cr.where(root.get("username").in(t.get("username")), (root.get("password").in(t.get("password"))));
			List<AccountStudent> result = session.createQuery(cr).getResultList();
			if(result.size() == 0) {
				throw new Exception("Sai tài khoản hoặc sai tên mật khẩu");
			}
			//Tìm kiếm thông tin user có tài khoản đã đăng nhập
			cb = session.getCriteriaBuilder();
			CriteriaQuery<Student> cr1 = cb.createQuery(Student.class);
			Root<Student> root1  = cr1.from(Student.class);
			cr1.where(root1.get("accountId").in(result.get(0).getId()));
			List<Student> result1 = session.createQuery(cr1).getResultList();	
			System.out.print(result1.toString());
			System.out.print(result1.get(0).getName());
			System.out.print(result1.get(0).getId());
		
			//Gửi thông tin cá nhân của chủ sở hữu tài khoản này về cho client
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(Student temp:result1) {
				JSONObject jo = new JSONObject();
				jo.put("id", temp.getId());
				jo.put("avatar", temp.getAvatar());
				jo.put("name", temp.getName());
				jo.put("sex", temp.getSex());
				jo.put("date_of_birth", (temp.getDateOfBirth() == null)? null : temp.getDateOfBirth().toString());
				jo.put("phone_nunmber", temp.getPhoneNumber());
				jo.put("email", temp.getEmail());
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
