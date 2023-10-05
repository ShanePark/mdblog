package com.example.bootconfigurationproperties.company;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompanyTest {

    @Autowired
    Company company;

    @Test
    public void test() {
        assertThat(company).isNotNull();
        System.out.printf("\n\n%s\n\n", company);
    }

}
