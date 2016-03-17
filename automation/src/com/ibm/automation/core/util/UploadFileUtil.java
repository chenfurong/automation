package com.ibm.automation.core.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class UploadFileUtil {
	
	public static List<String> upload(HttpServletRequest request){
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		List<String> pathList = new ArrayList<String>();
		if(multipartResolver.isMultipart(request)){
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<?> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iter.next());
				if(file.getSize() == 0){
					break;
				}
				//获取工程的物理路径
				String paths=request.getSession().getServletContext().getRealPath("/upload");
				//DateFormat
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String date = sdf.format(new Date());
				if(file != null){
					String fileName = date+file.getOriginalFilename();
					String path = paths+"/"+fileName;
					pathList.add(path);
					File localFile = new File(path);
					try {
						file.transferTo(localFile);
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return pathList;
	}
	public static String uploadIcon(HttpServletRequest request){
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		String path = "";
		try {
			if(multipartResolver.isMultipart(request)){
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<?> iter = multiRequest.getFileNames();
				while (iter.hasNext()) {
					MultipartFile file = multiRequest.getFile((String) iter.next());
					if(file.getSize() == 0){
						break;
					}
					//获取工程的物理路径
					String paths=request.getSession().getServletContext().getRealPath("/");
					//DateFormat
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String date = sdf.format(new Date());
					if(file != null){
						String fileName = date+file.getOriginalFilename();
						paths = paths+"upload/"+fileName;
						path = "upload/"+fileName;
						File localFile = new File(paths);
						file.transferTo(localFile); 
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}catch (IllegalStateException e) {
			e.printStackTrace();
		} 
		return path;
	}
}
