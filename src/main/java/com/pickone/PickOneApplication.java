package com.pickone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.pickone")
public class PickOneApplication {

  public static void main(String[] args) {
    SpringApplication.run(PickOneApplication.class, args);
  }

}
