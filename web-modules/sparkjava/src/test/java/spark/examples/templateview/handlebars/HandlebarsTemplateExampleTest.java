package spark.examples.templateview.handlebars;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import static org.junit.Assert.*;

public class HandlebarsTemplateExampleTest {
	@Test
	public void render() throws Exception {
		String templateVariable = "Hello Handlebars!";
		Map<String, Object> model = new HashMap<>();
		model.put("message", templateVariable);
		String expected = String.format("<h1>%s</h1>", templateVariable);
		String actual = new HandlebarsTemplateEngine().render(new ModelAndView(model, "hello.hbs"));
		assertEquals(expected, actual);
	}

}