package com.example.demo.contracts;

import com.example.demo.DemoApplication;
import com.example.demo.controller.ItemController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest(classes = {DemoApplication.class})
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.REMOTE,
    properties = {
        "stubs.find-producer=true"
    })
public class ContractVerifierBase {

    @Autowired
    private ItemController itemController;

    @Autowired
    private WebApplicationContext webAppContext;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.standaloneSetup(itemController);
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
            .webAppContextSetup(webAppContext)
            .build());
    }
}
