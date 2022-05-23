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

import com.rubygym.model.Event;
import com.rubygym.model.Service;
import com.rubygym.model.Trainer;
import com.rubygym.model.TrainerStudent;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;

@WebServlet("/service")
public class ServiceController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	static SessionFactory factory = HibernateUtil.getSessionFactory();
	// admin đăng ký tài khoản của student
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
//			res.addHeader("Access-Control-Allow-Origin", "*");
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();
			String[] criteria_array = HttpRequestUtil.getQuery(req);
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Service> cr = cb.createQuery(Service.class);
			Root<Service> root  = cr.from(Service.class);
			if(criteria_array != null) {
				for(int i=0;i<criteria_array.length;i++) {
					System.out.print(criteria_array[i].split("=")[0]);
					System.out.print(criteria_array[i].split("=")[1]);
					cr.where(root.get(criteria_array[i].split("=")[0]).in(criteria_array[i].split("=")[1]));
				}
			}
			List<Service> result = session.createQuery(cr).getResultList();
			JSONObject bodyJsonResponse = new JSONObject();
			JSONArray data = new JSONArray();
			for(Service temp:result) {
				JSONObject jo = new JSONObject();
				jo.put("id", temp.getId());
				jo.put("category", temp.getCategory());
				jo.put("period_per_week", temp.getPeriodPerWeek());
				jo.put("n_months", temp.getnMonths());
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
}
	
	
