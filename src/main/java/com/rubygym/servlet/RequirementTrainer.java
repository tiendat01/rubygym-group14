package com.rubygym.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.rubygym.model.Requirement;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;
import com.rubygym.utils.ScheduleUtil;

@WebServlet(urlPatterns = "/requirement-trainer/*")
public class RequirementTrainer extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<Object[]> list = null;
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
//		if (HttpRequestUtil.checkAuthentication(req)) {
			
			try {
//				resp.addHeader("Access-Control-Allow-Origin", "*");
				String idString = HttpRequestUtil.parseURL(req, "requirement-trainer");
				if (ScheduleUtil.checkTrainerId(Integer.parseInt(idString))) {
					Session session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
					
					list = session.createQuery("select r.timeId, r.scheduleId, r.category, s.name, r.id"
							+ " from Requirement r, TrainerStudent ts, Student s"
							+ " where r.trainerStudentId = ts.id and s.id = ts.studentId"
							+ " and ts.trainerId = " + Integer.parseInt(idString)).getResultList();
					
					for (Object[] r : list) {
						// update requirement
						if (r[0] != null && r[1] != null) {
							// new time
							r[0] = session.createQuery("select t from Time t where t.id = " + (int) r[0]).uniqueResult();
							// old time
							r[1] = session.createQuery("select t"
									+ " from Time t, Schedule s"
									+ " where t.id = s.timeId and"
									+ " s.id = " + (int) r[1]).uniqueResult();
							
						}
						// delete requirement
						else if ((int) r[2] == -1) {
							r[0] = null;
							r[1] = session.createQuery("select t"
									+ " from Time t, Schedule s"
									+ " where t.id = s.timeId and"
									+ " s.id = " + (int) r[1]).uniqueResult();
						}
						// create requirement
						else if ((int) r[2] == 1) {
							r[0] = session.createQuery("select t from Time t where t.id = " + (int) r[0]).uniqueResult();
							r[1] = null;
						}
					}
					
					session.getTransaction().commit();
					session.close();
					
					for (Object[] r : list) {
						JSONObject tmp = new JSONObject();
						
						tmp.put("oldTime", r[1]);
						tmp.put("newTime", r[0]);
						tmp.put("category", r[2]);
						tmp.put("studentName", r[3]);
						tmp.put("requireId", r[4]);
						data.add(tmp);
						
					}
					error.add(null);
					HttpResponseUtil.setResponse(resp, data, error);
				}
				
				else {
					data.add(null);
					error.add("ID trainer chưa tồn tại trong hệ thống hoặc chưa được phân công. \nKhông có chức năng này");
					HttpResponseUtil.setResponse(resp, data, error);
				}
				
			} catch (Exception e) {
//				resp.addHeader("Access-Control-Allow-Origin", "*");
				// TODO: handle exception
				e.printStackTrace();
				
				data.add(null);
				error.add(e.getMessage());
				HttpResponseUtil.setResponse(resp, data, error);
			}
		
//		}
//		else {
//			data.add(null);
//			error.add("Yêu cầu đăng nhập");
//			HttpResponseUtil.setResponse(resp, data, error);
//		}
		
	}
	
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String idString = HttpRequestUtil.parseURL(req, "requirement-trainer");
		
		try {
//			resp.addHeader("Access-Control-Allow-Origin", "*");
			JSONObject jsonObject = (JSONObject) HttpRequestUtil.getBody(req);
			long action = (long) jsonObject.get("action");
			Integer requireId = Integer.parseInt(jsonObject.get("requireId").toString());
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			Requirement requirement = (Requirement) session.createQuery("from Requirement r where r.id = " + requireId).uniqueResult();
					
			// accept requirement from student
			if (action == 1) {
				
				JSONObject requestBody = new JSONObject();
				requestBody.put("requireId", requireId);
				
				URL url = new URL("http://localhost:8080/cnpm/schedule-trainer/" + idString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Authentication", "1");
				connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				connection.setRequestProperty("Accept", "application/json");
				
				// delete
				if (requirement.getCategory() == -1) {
					connection.setRequestMethod("DELETE");
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(requestBody.toJSONString());
					writer.flush();
					
					// read the response:
					InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
					BufferedReader br = new BufferedReader(reader);
					JSONObject respJsonObject = (JSONObject) JSONValue.parse(HttpRequestUtil.readAllLines(br));
					JSONArray errorMessage = (JSONArray) respJsonObject.get("error");
					JSONArray dataArray = (JSONArray) respJsonObject.get("data");
					data.add(dataArray.get(0));
					error.add(errorMessage.get(0));
					
				}
				// update
				else if (requirement.getCategory() == 0){
					connection.setRequestMethod("PUT");
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(requestBody.toJSONString());
					writer.flush();
					
					// read the response:
					InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
					BufferedReader br = new BufferedReader(reader);
					JSONObject respJsonObject = (JSONObject) JSONValue.parse(HttpRequestUtil.readAllLines(br));
					JSONArray errorMessage = (JSONArray) respJsonObject.get("error");
					JSONArray dataArray = (JSONArray) respJsonObject.get("data");
					data.add(dataArray.get(0));
					error.add(errorMessage.get(0));
				}
				// create
				else if (requirement.getCategory() == 1) {
					connection.setRequestMethod("POST");
					connection.setDoOutput(true);
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(requestBody.toJSONString());
					writer.flush();
					
					// read the response:
//					connection.setDoInput(true);
					InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
					BufferedReader br = new BufferedReader(reader);
					JSONObject respJsonObject = (JSONObject) JSONValue.parse(HttpRequestUtil.readAllLines(br));
					JSONArray errorMessage = (JSONArray) respJsonObject.get("error");
					JSONArray dataArray = (JSONArray) respJsonObject.get("data");
					data.add(dataArray.get(0));
					error.add(errorMessage.get(0));
				}
			}
			
			// finally: delete requirement from database
			session.createQuery("delete from Requirement r where r.id = " + requireId).executeUpdate();
			
			session.getTransaction().commit();
			session.close();
			
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
