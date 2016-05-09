
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {

	public static void main(String[] args) {
		// secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
		String layout = "templates/layout.vtl";
		staticFileLocation("/public");

		get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
//		      model.put("users", User.all());
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
	  }, new VelocityTemplateEngine());

		get("transfer", (request, response) -> {
  		// response.redirect("/");
			String user = request.queryParams("user");
			String password = request.queryParams("pass");
			return user + " " + password;
		});

	}

}
