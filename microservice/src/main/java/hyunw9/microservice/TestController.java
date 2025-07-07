package hyunw9.microservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/health")
	public String healthCheck() {
		return "Microservice is running!";
	}
}
