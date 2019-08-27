package com.woowacourse.sunbook.presentation.controller;

import com.woowacourse.sunbook.presentation.template.TestTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchControllerTest extends TestTemplate {

    @Test
    void 유저이름_검색() {
        String userName = "mir";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("userName", userName)
                        .build()
                )
                .cookie("JSESSIONID", loginSessionId(userRequestDto))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(res -> {
                    String body = new String(res.getResponseBody());
                    assertTrue(body.contains("mir"));
                    assertTrue(body.contains("ddu0422@naver.com"));
                })
        ;
    }
}