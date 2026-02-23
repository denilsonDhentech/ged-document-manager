package com.dhensouza.ged;

import com.dhensouza.ged.infrastructure.configuration.TestBeanConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestBeanConfiguration.class)
class GedDocumentManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
