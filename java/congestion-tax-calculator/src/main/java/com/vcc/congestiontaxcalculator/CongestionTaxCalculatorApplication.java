package com.vcc.congestiontaxcalculator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "Congestion Tax Calculator API",
		version = "0.0",
		description = "<li>Congestion Tax Calculator API to calculate the Congestion Charges for a vehicle. </li>" +
				"<li>This app is written in JAVA spring-boot as part of a coding assignment.</li>" +
				"<li>Tax Rules and Red Days can be applied for various Cities and Years through an external configuration (JSON).</li>",
		contact = @Contact(url = "https://github.com/kishor-p", name = "Kishor Prakash", email = "kishor.p.15389@gmail.com"))
)
public class CongestionTaxCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongestionTaxCalculatorApplication.class, args);
	}

}
