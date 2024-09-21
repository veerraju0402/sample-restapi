package com.example.demo.controller;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.aopadvice.TrackExecutionTime;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("http://localhost:3001")
public class ControllerClass {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerClass.class);

	@Autowired
	ServiceClass serviceClass;
	@Autowired
	private ApplicationContext applicationContext;
	private Bucket bucket;

	// @Autowired
	// private HttpServletRequest httpServletRequest;

	@Value("${service.var}")
	private String var;

	@Value("${service.var2}")
	private String var2;
	
	@Value("${service.var3}")
	private String var3;

	@GetMapping("/")
	@Hidden
	public void redirecting(HttpServletResponse response) throws IOException {
		response.sendRedirect("/swagger-ui.html");
	}

	// custome annotation in aop which executes in filter for every request
	@TrackExecutionTime
	@GetMapping("/now")
	public Date dkd() {
		Date testBean = (Date) applicationContext.getBean("date");
		LOGGER.info("logger || testBean:" + testBean);
		return testBean;
	}

	// rate limit
	// https://www.youtube.com/watch?v=HYg6l0pUQwM
	@GetMapping(value = "/rateLimit1")
	public ResponseEntity<String> welCome1(@RequestParam(value = "id", required = false, defaultValue = "5") long id) {

		Refill refill = Refill.intervally(id /* noOfReqs */, Duration.ofMinutes(1));
		// Refill refill =Refill.of(5, Duration.ofMinutes(1));
		bucket = Bucket.builder().addLimit(Bandwidth.classic(id, refill)).build();
		return new ResponseEntity<String>("successfully generated string:" + bucket, HttpStatus.OK);
	}

	@GetMapping(value = "/msg")
	public ResponseEntity<String> welCome() throws InterruptedException {
		if (bucket.tryConsume(1)) {
			return new ResponseEntity<String>("successfully", HttpStatus.OK);
		}
		return new ResponseEntity<String>("Too many requess", HttpStatus.OK);
	}

	// ratelimiting from yml is not working
	@GetMapping(value = "/msg1")
	@RateLimiter(name = "service1", fallbackMethod = "welCome3")
	public ResponseEntity<Map<String, String>> welCome2() throws InterruptedException {
		System.err.println("Test");
		Map<String,String> myMap=new HashMap<String,String>();
		myMap.put("var", var);
		myMap.put("var2", var2);
		myMap.put("var3", var3);
		//logging order 
		LOGGER.error("logger ERROR || Test:"+myMap);
		LOGGER.warn("logger warn || Test:"+myMap);
		LOGGER.info("logger info || Test:"+myMap);
		LOGGER.debug("logger degug || Test:"+myMap);
		LOGGER.trace("logger trace || Test:"+myMap);
		return new ResponseEntity<Map<String,String>>(myMap, HttpStatus.OK);
	}

	@PostMapping(value = "/msg2")
	public ResponseEntity<String> welCome3(@RequestParam String msg) {
		System.err.println("Test:" + msg);
		return new ResponseEntity<String>("hello:" + msg, HttpStatus.OK);
	}

	@GetMapping(value = "/employee", produces = { "application/xml" })
	// @GetMapping(value = "/employee", produces = {
	// "application/json","application/xml" })
	// @GetMapping(value = "/employee")
	public Optional<Employee> firstService(@RequestParam long id) {
		if (id < 1) {
			Employee employee = new Employee();
			employee.setName("raju");
			employee.setEmail("raju@gmail.com");
			return Optional.of(employee);
		}
		return serviceClass.getEmployee(id);
	}

	@GetMapping(value = "/employee1")
	public Employee firstService1(@RequestParam long id) {
		return serviceClass.getEmployee1(id);
	}

	@GetMapping(value = "/employee2")
	public Employee firstService2(@RequestParam long id) {
		return serviceClass.getEmployee2(id);
	}

	@GetMapping(value = "/employee3/{id}")
	public Employee firstService3(@PathVariable long id) {
		return serviceClass.getEmployee2(id);
	}

	// https://www.youtube.com/watch?v=0Y1ECAeuw3I
	@RequestMapping(value = "/employee", method = RequestMethod.POST)
	public Employee secondService(@RequestBody Employee employee) {
		// StringBuffer g=httpServletRequest.getRequestURL();
		// System.out.println(g+"--Inside POST Method:" +
		// httpServletRequest.getHeader("testHead"));
		try {
			System.out.println("Post");
			return serviceClass.saveEmployee(employee);
		} catch (Exception e) {
			System.out.println("Exc:" + e.getMessage());
			throw e;
		}
	}

	// @TrackExecutionTime
	@GetMapping(value = "/employees")
	@RateLimiter(name = "service1", fallbackMethod = "dkd")
	public Page<Employee> firstService(@RequestParam int pageNumber) {
		LOGGER.info("get employees page: {}", pageNumber);
		if (pageNumber >= 0) {
			Pageable contactsPageable = PageRequest.of(pageNumber, 3, Sort.by("empId").descending());

			Page<Employee> ePages = serviceClass.findAll(contactsPageable);

			return ePages;
		} else {
			List<Employee> eList = serviceClass.getEmployees();
			Page<Employee> empPage = new PageImpl<Employee>(eList);
			return (Page<Employee>) eList;
		}
	}

	@DeleteMapping(value = "/employee")
	public String deleteService(@RequestParam(required=false, defaultValue = "0") long id) {
		serviceClass.deleteEmployee(id);
		return "Deleted";
	}

	@RequestMapping(value = "/employees", method = RequestMethod.POST)
	public List<Employee> secondService(@RequestBody List<Employee> employees) {
		// System.out.println("Inside POST Method2:" + employees);
		for (Employee employee : employees) {
			employee = serviceClass.saveEmployee(employee);
			// System.out.println("emp2:" + employee);
		}
		return employees;
	}

	public ResponseEntity<String> rateLimiterFallback(Exception e) {
		System.err.println("e:" + e.getMessage());
		return new ResponseEntity<String>("order service does not permit further calls", HttpStatus.TOO_MANY_REQUESTS);

	}

	// https://www.youtube.com/watch?v=CNGScm944Vs
	// https://www.youtube.com/watch?v=uwTWJHREhI8
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				// Thread.sleep(3000);
				System.out.println("Hello World");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@GetMapping("/timelimiter") // http://localhost:9094/api/reports/timelimiter
	@TimeLimiter(name = "myTimeLim")
	public CompletableFuture<Void> timeLimiter() {
		return CompletableFuture.runAsync(runnable);
	}

	@GetMapping("/timelimiter2") // http://localhost:9094/api/reports/timelimiter
	@Retry(name = "allCustomer", fallbackMethod = "showError")
	// import org.springframework.retry.annotation.Retryable;
	// @Retryable(maxAttempts = 2, backoff = @Backoff(delay = HUNDERED_MS),
	// noRetryFor = {BusinessException.class,
	// HttpClientErrorException.class})
	public ResponseEntity<String> timeLimiter2() throws Exception {
		System.err.println("date:" + new Date());
		throw new BadRequestException();
	}

	// we can add @CircuitBreaker(name = "memberService") at class level,in such
	// case we can add fallback as below
	// fallback method,copy the actual method signature,just add Fallback to the
	// actual method and RuntimeException ex in inputs
	// @Recover
	//// public Optional<MemberServiceResponse>
	// validateMemberCodeFallback(RuntimeException ex, String partnerCode) {
	// public Optional<MemberServiceResponse>
	// validateMemberCodeFallback(RuntimeException ex, String partnerCode) {
	// }

	public ResponseEntity<String> showError(Exception ex) {
		System.err.println("###### This is default Response ####");
		return new ResponseEntity<String>("This is default response", HttpStatus.OK);
	}

	//// changinging rate limiter at runtime
	@Autowired
	private RateLimiterRegistry registry;

	@GetMapping("/updateRateLimiter")
	public String updateRatelimiter() {
		updateRateLimits("service1", 2, Duration.ofSeconds(2));
		return "updated";
	}

	public void updateRateLimits(String rateLimiterName, int newLimitForPeriod, Duration newTimeoutDuration) {
		io.github.resilience4j.ratelimiter.RateLimiter limiter = registry.rateLimiter(rateLimiterName);
		limiter.changeLimitForPeriod(newLimitForPeriod);
		limiter.changeTimeoutDuration(newTimeoutDuration);
	}

	///
	@GetMapping(value = "/employee4")
	public List<Employee> firstService4(@RequestParam String id) {
		LOGGER.error("logger error || employee4:"+id);
		return serviceClass.getEmployee3(id);
	}

	@GetMapping(value = "/fileupload")
	public String firstService5(@RequestParam long id) {
		Employee emp = serviceClass.getEmployee2(id);
		serviceClass.uploadFile(emp.toString().getBytes(), "tax-rates-scheduler-dev", "myFileName", "text/csv");
		return "uploaded";
	}
	 
//console"   "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
//	"2024-09-07 13:39:20 - logger || Test:{var=avr3, var3=mapping, var2=fromRuntime3}
//file       "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
//	""2024-09-07 13:39:20 [http-nio-8081-exec-2] ERROR c.e.demo.controller.ControllerClass - logger || Test:{var=avr3, var3=mapping, var2=fromRuntime3}

//	//logging order 
//			LOGGER.error("logger ERROR || Test:"+myMap);
//			LOGGER.warn("logger warn || Test:"+myMap);
//			LOGGER.info("logger info || Test:"+myMap);
//			LOGGER.debug("logger degug || Test:"+myMap);
//			LOGGER.trace("logger trace || Test:"+myMap);
}
