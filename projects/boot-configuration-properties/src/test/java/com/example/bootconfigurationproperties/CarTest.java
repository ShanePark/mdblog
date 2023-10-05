package com.example.bootconfigurationproperties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CarTest {

    @Autowired
    Car car;

    @Test
    public void test() {
        assertThat(car).isNotNull();
        System.out.printf("\n\n%s\n\n", car);
    }

}
