package com.rubygym.servlet.image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
import com.rubygym.model.Picture;
import com.rubygym.utils.HibernateUtil;
import com.rubygym.utils.HttpRequestUtil;
import com.rubygym.utils.HttpResponseUtil;

@MultipartConfig
@WebServlet(urlPatterns = "/image-event/*")
public class ImageEventServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONArray data = new JSONArray();
		JSONArray error = new JSONArray();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String eventIdString = HttpRequestUtil.parseURL(request, "image-event");
		
		try {
			
			Part part = request.getPart("image");
			String fileName = part.getSubmittedFileName();
			if (part.getSubmittedFileName().endsWith(".jpg") || part.getSubmittedFileName().endsWith(".png")) {
				InputStream fileInputStream = part.getInputStream();
				
				String keyPath = request.getSession().getServletContext().getRealPath("/upload-gcloud-key.json");
			
				String uploadedFileUrl = uploadToCloudStorage(keyPath, fileName, fileInputStream);
			
				Session session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
				
				Picture newPicture = (Picture) session.createQuery("from Picture p "
						+ "where p.eventId = " + Integer.parseInt(eventIdString)).uniqueResult();
				
				if (newPicture != null) {
					// sửa 
					session.createQuery("update Picture p set p.imageUrl = "
							+ uploadedFileUrl + " where p.eventId = " + Integer.parseInt(eventIdString))
					.executeUpdate();
				}
				else {
					// thêm mới
					newPicture = new Picture();
					newPicture.setEventId(Integer.parseInt(eventIdString));
					newPicture.setImageUrl(uploadedFileUrl);
					session.save(newPicture);
				}
				
				session.getTransaction().commit();
				session.close();
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("imageUrl", uploadedFileUrl);
				data.add(jsonObject);
				error.add(null);
//				response.addHeader("Access-Control-Allow-Origin", "*");
				HttpResponseUtil.setResponse(response, data, error);
				
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
