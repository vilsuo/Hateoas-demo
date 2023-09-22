
package com.example.demo.employee.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/*
TODO
- write tests validation in integration test
- test validation (need all Beans?)
	
	- handle also controller advice

If you need to start a full running server, we recommend that you use random ports. If you use @SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT), an available port is picked at random each time your test runs.

The @LocalServerPort annotation can be used to inject the actual port used into your test. For convenience, tests that need to make REST calls to the started server can additionally @Autowire a WebTestClient, which resolves relative links to the running server and comes with a dedicated API for verifying responses
*/


/*
As the name suggests, integration tests focus on integrating different layers 
of the application. That also means no mocking is involved.

The test cases for the integration tests might look similar to the Controller 
layer unit tests. The difference from the Controller layer unit tests is that
here nothing is mocked and end-to-end scenarios will be executed
*/


/*
The @SpringBootTest annotation tells Spring Boot to look for a main 
configuration class (one with @SpringBootApplication, for instance) and use that
to start a Spring application context

a test annotated with @SpringBootTest will bootstrap the full application 
context, which means we can @Autowire any bean that’s picked up by component 
scanning into our test

By default, @SpringBootTest will not start a server. You can use the 
webEnvironment attribute of @SpringBootTest to further refine how your tests run

MOCK(Default) : Loads a web ApplicationContext and provides a mock web
environment. Embedded servers are not started when using this annotation. If a 
web environment is not available on your classpath, this mode transparently 
falls back to creating a regular non-web ApplicationContext. It can be used in 
conjunction with @AutoConfigureMockMvc or @AutoConfigureWebTestClient for 
mock-based testing of your web application
*/
@SpringBootTest

/*
Another useful approach is to not start the server at all but to test only the 
layer below that, where Spring handles the incoming HTTP request and hands it 
off to your controller. That way, almost of the full stack is used, and your 
code will be called in exactly the same way as if it were processing a real 
HTTP request but without the cost of starting the server. To do that, use 
Spring’s MockMvc and ask for that to be injected for you by using the 
@AutoConfigureMockMvc annotation on the test case.

In this test, the full Spring application context is started but without the 
server. We can narrow the tests to only the web layer by using @WebMvcTest
*/
@AutoConfigureMockMvc
public class EmployeeControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void shouldReturnDefaultMessage() throws Exception {
		mockMvc.perform(get("/employees"))
			.andExpect(status().isOk());
			//.andExpect(content().string(containsString("Hello, World")));
	}
}
