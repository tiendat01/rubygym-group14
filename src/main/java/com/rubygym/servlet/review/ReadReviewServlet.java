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
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.TrainerStudentUtil;

@WebServlet("/read-review")
public class ReadReviewServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<ReviewStudent> list = null;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			list = session.createQuery("from ReviewStudent").getResultList();
			
			session.getTransaction().commit();
//			HibernateUtil.shutdown();
			
			for (int i = 0; i < list.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				Integer studentId = list.get(i).getStudentId();
				jsonObject.put("studentId", studentId);
				jsonObject.put("name", TrainerStudentUtil.getStudent(studentId).getName());
				jsonObject.put("review", list.get(i).getReview());
				jsonObject.put("rate", list.get(i).getRate());
				jsonObject.put("date", list.get(i).getDate() == null ? null : list.get(i).getDate().toString());
				
				// admin chỉ hiển thị các đánh giá/phản hồi chưa đc xử lý
				if (list.get(i).getState() == 0) {
					data.add(jsonObject);
				}
				
			}
			session.close();
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
