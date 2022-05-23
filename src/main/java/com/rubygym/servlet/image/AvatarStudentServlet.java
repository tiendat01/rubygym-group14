package com.rubygym.servlet.image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;

@MultipartConfig
@WebServlet("/avatar-student/*")
public class AvatarStudentServlet extends HttpServlet{
//	@Override
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		JSONArray data = new JSONArray();
//		JSONArray error = new JSONArray();
//		request.setCharacterEncoding("UTF-8");
//		response.setCharacterEncoding("UTF-8");
//		String idString = HttpRequestUtil.parseURL(request, "avatar-student");
//		
//		try {
//			response.addHeader("Access-Control-Allow-Origin", "*");
//			
//			Session session = HibernateUtil.getSessionFactory().openSession();
//			session.beginTransaction();
//			
//			String avatarUrl = (String) session.createQuery("select s.avatar from Student s where s.id = " + Integer.parseInt(idString))
//					.uniqueResult();
//			
//			JSONObject tmp = new JSONObject();
//			tmp.put("avatarUrl", avatarUrl);
//			data.add(tmp);
//			error.add(null);
//			HttpResponseUtil.setResponse(response, data, error);
//			
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//			response.addHeader("Access-Control-Allow-Origin", "*");
//			data.add(null);
//			error.add(e.getMessage());
//			HttpResponseUtil.setResponse(response, data, error);
//		}
//	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String idString = HttpRequestUtil.parseURL(request, "avatar-student");
		
		try {
//			response.addHeader("Access-Control-Allow-Origin", "*");
			
			Part part = request.getPart("avatar");
			String fileName = part.getSubmittedFileName();
			
			// restrict file ext : only allow .jpg and .png
			if (part.getSubmittedFileName().endsWith(".jpg") || part.getSubmittedFileName().endsWith(".png")) {
				InputStream fileInputStream = part.getInputStream();
				
				String path = request.getSession().getServletContext().getRealPath("/upload-gcloud-key.json");
				
				String uploadedFileUrl = uploadToCloudStorage(path, fileName, fileInputStream);
				
				Session session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
				
				session.createQuery("update Student s set s.avatar = '" + uploadedFileUrl +
						"' where s.id = " + Integer.parseInt(idString)).executeUpdate();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("avatarUrl", uploadedFileUrl);
				data.add(jsonObject);
				error.add(null);
				HttpResponseUtil.setResponse(response, data, error);
				
				session.getTransaction().commit();
				session.close();
			}
			
			else {
				//the file was not a JPG or PNG
//				response.addHeader("Access-Control-Allow-Origin", "*");
				data.add(null);
				error.add("Lỗi định dạng. Chỉ cho phép .jpg và .png");
				HttpResponseUtil.setResponse(response, data, error);
			}
			
			
			
		} catch (Exception e) {
//			response.addHeader("Access-Control-Allow-Origin", "*");
			// TODO: handle exception
			e.printStackTrace();
			error.add(e.getMessage());
			data.add(null);
			HttpResponseUtil.setResponse(response, data, error);
		}
	}
	
	private static String uploadToCloudStorage(String pathToKey, String fileName, InputStream fileInputStream) throws IOException {
		
		byte[] bytes = IOUtils.toByteArray(fileInputStream);
		String projectId = "test-upload-file-gcloud";
		String bucketName = "test-upload-file-gcloud.appspot.com";
		StorageOptions storageOptions = StorageOptions.newBuilder()
		        .setProjectId(projectId)
		        .setCredentials(GoogleCredentials.fromStream(new
		        		FileInputStream(pathToKey)))
		        .build();
		Storage storage = storageOptions.getService();
		BlobId blobId = BlobId.of(bucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
		Blob blob = storage.create(blobInfo, bytes);
		
		System.out.println(blob.getContentType());
		
		String uploadName = blob.getSelfLink().substring(
				"https://www.googleapis.com/storage/v1/b/test-upload-file-gcloud.appspot.com/o/".length());
		String publicUrl = "https://storage.googleapis.com/" + bucketName + "/" + uploadName;
		return publicUrl;
		
		
	}
	
	
	
}
