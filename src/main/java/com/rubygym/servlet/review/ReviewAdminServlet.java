package com.rubygym.servlet.review;

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

import com.rubygym.model.ReviewAdmin;
import com.rubygym.model.ReviewStudent;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.TrainerStudentUtil;

@WebServlet("/review-admin")
public class ReviewAdminServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<ReviewAdmin> list = null;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			list = session.createQuery("from ReviewAdmin ra").getResultList();
			
			session.getTransaction().commit();
			session.close();
			
			for (int i = 0; i < list.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				Integer studentId = list.get(i).getStudentId();
				jsonObject.put("studentId", studentId);
				jsonObject.put("name", TrainerStudentUtil.getStudent(studentId).getName());
				jsonObject.put("review", list.get(i).getReviewFromStudent());
				jsonObject.put("rate", list.get(i).getRate());
				jsonObject.put("date", list.get(i).getDate() == null ? null : list.get(i).getDate().toString());
				data.add(jsonObject);
			}
			
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
			JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
			Integer studentId = Integer.parseInt(dataClient.get("studentId").toString());
			Boolean action = (Boolean) dataClient.get("action");
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			if (action) {
				ReviewStudent fromStudent = (ReviewStudent) session.createQuery("from ReviewStudent rs "
						+ "where rs.studentId = " + studentId).uniqueResult();
				
				ReviewAdmin fromAdmin = (ReviewAdmin) session.createQuery("from ReviewAdmin ra "
						+ "where ra.studentId = " + studentId).uniqueResult();
				
				if (fromAdmin == null) {
					// thêm mới vào bảng bình luận của admin (trang chủ)
					fromAdmin = new ReviewAdmin();
					fromAdmin.setStudentId(fromStudent.getStudentId());
					fromAdmin.setReviewFromStudent(fromStudent.getReview());
					fromAdmin.setRate(fromStudent.getRate());
					fromAdmin.setDate(fromStudent.getDate());
					session.save(fromAdmin);

				}
				
				else {
					// sửa
					session.createQuery("update ReviewAdmin ra "
							+ "set ra.reviewFromStudent = ?0, ra.rate = ?1 , ra.date = ?2 "
							+ "where ra.id = ?3")
					.setParameter(0, fromStudent.getReview())
					.setParameter(1, fromStudent.getRate())
					.setParameter(2, fromStudent.getDate())
					.setParameter(3, studentId)
					.executeUpdate();
				}
				
				session.createQuery("update ReviewStudent rs "
						+ "set rs.state = 1 "
						+ "where rs.studentId = " + studentId).executeUpdate();
				
			}
			
			// "từ chối" bình luận này
			else {
				session.createQuery("update ReviewStudent rs "
						+ "set rs.state = -1 "
						+ "where rs.studentId = " + studentId).executeUpdate();
			}
			
			session.getTransaction().commit();
			session.close();
			
			data.add("Thao tác thành công");
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
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			
			JSONObject dataClient = (JSONObject) HttpRequestUtil.getBody(req);
			Integer studentId = Integer.parseInt(dataClient.get("studentId").toString());
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			session.createQuery("delete from ReviewAdmin ra "
					+ "where ra.studentId = " + studentId).executeUpdate();
			
			session.getTransaction().commit();
			session.close();
			
			data.add("Xoá phản hồi của khách hàng (trên trang chủ) thành công");
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
