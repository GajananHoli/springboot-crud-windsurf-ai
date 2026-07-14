package com.spring_boot_jpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringBootJpaApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationContextLoads(ApplicationContext context) {
		assertThat(context).isNotNull();
		assertThat(context.getBeanDefinitionNames()).isNotEmpty();
	}

	/*@Test
	void mainMethodStartsApplication() {
		SpringBootJpaApplication.main(new String[]{});
	}*/

}
