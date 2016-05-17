
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;


import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {

	public static void main(String[] args) {
		// secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
		String layout = "templates/layout.vtl";
		staticFileLocation("/public");

		get("/", (request, response) -> {
			HashMap<String, Object> model = new HashMap<String, Object>();
		    model.put("template", "templates/index.vtl");
		    return new ModelAndView(model, layout);
		}, new VelocityTemplateEngine());

		get("verifyUser", (request, response) -> {
			String user = request.queryParams("user");
			String password = request.queryParams("pass");
			return DB.verifyUser(user, password);
		});

		post("documents", (request, response) -> {
			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("template", "templates/document.vtl");
			String user = request.queryParams("login");
			String password = request.queryParams("password");
			Session session = new Session(user, password);
			session.setFTPFiles(FTPReader.loadFiles(session));
			model.put("session", session);
			return new ModelAndView(model, layout);
		}, new VelocityTemplateEngine());

		get("/getList", (request, response) -> {
			String user = request.queryParams("user");
			Session session = Session.findSessionByUserLogin(user);
			return session.getFilesJSON();
		});

//		get("tiff", (request, response) -> {
//			try {
//		        File file = new File("temp/201600004068.tif");
//		        FileInputStream inputStream = new FileInputStream(file);
//		        byte byteStream[] = new byte[(int)file.length()];
//		        inputStream.read(byteStream);
//
//		        response.raw().setContentType("data:attachment;chatset=utf-8;application/tiff");
//		        response.raw().setContentLength((int) file.length());
//
//		        response.raw().getOutputStream().write(byteStream);
//		        response.raw().getOutputStream().flush();
//		        response.raw().getOutputStream().close();
//		        return response;
//		    } catch (FileNotFoundException e) {
//		        e.printStackTrace();
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		    }
//			return null;
//		});

	}

}
