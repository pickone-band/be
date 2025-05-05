package com.PickOne;

import com.PickOne.test.TestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Disabled
@Import(TestConfig.class)
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
