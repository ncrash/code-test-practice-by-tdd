package spark.examples.templateview.handlebars;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import static spark.Spark.get;

public class HandlebarsHelperExample extends HandlebarsTemplateEngine {

    public HandlebarsHelperExample() {
        super();
        handlebars.registerHelpers(this);
    }

    public static void main(String[] args) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Chris");

        // hello.hbs file is in resources/templates directory
        get("/hello", (request, response) -> new ModelAndView(model, "hello_helpers.hbs"),
            new HandlebarsHelperExample());

    }

    // Example of a helper, write {{now}} in template file.
    public String now() {
        return new Date().toString();
    }
}