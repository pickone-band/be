package com.PickOne;

import com.PickOne.test.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
