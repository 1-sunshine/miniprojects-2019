package com.woowacourse.sunbook.presentation.controller;

import com.woowacourse.sunbook.domain.relation.Relationship;
import com.woowacourse.sunbook.presentation.template.TestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RelationApiControllerTest extends TestTemplate {

    @Test
    void 상대방과_관계_확인() {
        String sessionId = loginSessionId(temp1);
        respondApi(loginAndRequest(HttpMethod.GET, "/api/friends/997", Relationship.NONE, HttpStatus.OK, sessionId))
                .jsonPath("$..relationship").isEqualTo("NONE");
    }

    @Test
    void 친구_수락() {
        String sessionId = loginSessionId(temp1);
        respondApi(loginAndRequest(HttpMethod.POST, "/api/friends/997", Relationship.NONE, HttpStatus.OK, sessionId))
                .consumeWith(signOut -> {
                    webTestClient.method(HttpMethod.DELETE).uri("/signout")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .exchange()
                            .expectBody()
                            .consumeWith(userLogin -> {
                                String userSessionId = loginSessionId(temp2);
                                respondApi(loginAndRequest(HttpMethod.PUT, "/api/friends/998", Relationship.NONE, HttpStatus.OK, userSessionId))
                                        .jsonPath("$..relationship").isEqualTo("FRIEND");
                            });
                })
                .consumeWith(deleteRelation -> {
                    String userSessionId = loginSessionId(temp2);
                    respondApi(loginAndRequest(HttpMethod.DELETE, "api/friends/998", Relationship.NONE, HttpStatus.OK, userSessionId));
                });
    }

    @Test
    void 친구_거절() {
        String sessionId = loginSessionId(temp1);
        respondApi(loginAndRequest(HttpMethod.POST, "/api/friends/997", Relationship.NONE, HttpStatus.OK, sessionId))
                .consumeWith(signOut -> {
                    webTestClient.method(HttpMethod.DELETE).uri("/signout")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .exchange()
                            .expectBody()
                            .consumeWith(userLogin -> {
                                String userSessionId = loginSessionId(temp2);
                                respondApi(loginAndRequest(HttpMethod.DELETE, "/api/friends/998", Relationship.NONE, HttpStatus.OK, userSessionId))
                                        .jsonPath("$..relationship").isEqualTo("NONE");
                            });
                });
    }

    @Test
    void 친구_요청_받은_리스트() {
        String sessionId = loginSessionId(temp1);
        respondApi(loginAndRequest(HttpMethod.POST, "/api/friends/997", Relationship.NONE, HttpStatus.OK, sessionId))
                .consumeWith(signOut -> {
                    webTestClient.method(HttpMethod.DELETE).uri("/signout")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .exchange()
                            .expectBody()
                            .consumeWith(userLogin -> {
                                String userSessionId = loginSessionId(temp2);
                                respondApi(loginAndRequest(HttpMethod.GET, "/api/friends/friends/requested", Void.class, HttpStatus.OK, userSessionId))
                                        .jsonPath("$.length()").isEqualTo("1");
                            });
                })
                .consumeWith(deleteRelation -> {
                    String userSessionId = loginSessionId(temp2);
                    respondApi(loginAndRequest(HttpMethod.DELETE, "api/friends/998", Relationship.NONE, HttpStatus.OK, userSessionId));
                });
    }

    @Test
    void 친구_리스트() {
        String sessionId = loginSessionId(temp1);
        respondApi(loginAndRequest(HttpMethod.POST, "/api/friends/997", Relationship.NONE, HttpStatus.OK, sessionId))
                .consumeWith(signOut -> {
                    webTestClient.method(HttpMethod.DELETE).uri("/signout")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .exchange()
                            .expectBody()
                            .consumeWith(userLogin -> {
                                String userSessionId = loginSessionId(temp2);
                                respondApi(loginAndRequest(HttpMethod.GET, "/api/friends/friends", Void.class, HttpStatus.OK, userSessionId))
                                        .jsonPath("$.length()").isEqualTo("0");
                            });
                })
                .consumeWith(deleteRelation -> {
                    String userSessionId = loginSessionId(temp2);
                    respondApi(loginAndRequest(HttpMethod.DELETE, "api/friends/998", Relationship.NONE, HttpStatus.OK, userSessionId));
                });
    }
}