package pl.edu.pjatk.lnpayments.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
    }

}
