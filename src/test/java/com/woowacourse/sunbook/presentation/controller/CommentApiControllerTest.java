package com.woowacourse.sunbook.presentation.controller;

import com.woowacourse.sunbook.domain.comment.CommentFeature;
import com.woowacourse.sunbook.presentation.template.TestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class CommentApiControllerTest extends TestTemplate {
    private static final Long ID = 1L;

    @Test
    void 댓글_등록() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.POST, "/api/articles/1/comments", new CommentFeature("abc"), HttpStatus.OK, sessionId))
                .jsonPath("$..id").isEqualTo(3)
                .jsonPath("$..contents").isEqualTo("abc")
                .jsonPath("$..authorName").isEqualTo("mir")
                ;
    }

    @Test
    void 댓글_전체_조회() {
        respondApi(request(HttpMethod.GET, "/api/articles/1/comments", Void.class, HttpStatus.OK))
                .jsonPath("$").isNotEmpty();
    }

    @Test
    void 권한_있는_사용자_댓글_수정() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.PUT, "/api/articles/1/comments/1", new CommentFeature("abcd"), HttpStatus.OK, sessionId))
                .jsonPath("$..id").isEqualTo(ID.intValue())
                .jsonPath("$..contents").isEqualTo("abcd")
                .jsonPath("$..authorName").isEqualTo("mir")
                ;
    }

    @Test
    void 권한_없는_사용자_댓글_수정() {
        String sessionId = loginSessionId(otherRequestDto);
        respondApi(loginAndRequest(HttpMethod.PUT, "/api/articles/1/comments/1", new CommentFeature("abcd"), HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("권한이 없습니다.")
                ;
    }

    @Test
    void 다른_페이지_댓글_수정() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.PUT, "/api/articles/2/comments/1", new CommentFeature("abcd"), HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("권한이 없습니다.")
                ;
    }

    @Test
    void 존재하지_않는_댓글_수정() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.PUT, "/api/articles/1/comments/0", new CommentFeature("abcd"), HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("존재하지 않는 댓글입니다.")
                ;
    }

    @Test
    void 존재하지_않는_페이지_댓글_수정() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.PUT, "/api/articles/0/comments/1", new CommentFeature("abcd"), HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("찾을 수 없는 게시글입니다.")
                ;
    }

    @Test
    void 권한_있는_사용자_댓글_삭제() {
        String sessionId = loginSessionId(userRequestDto);
        loginAndRequest(HttpMethod.DELETE, "/api/articles/1/comments/2", Void.class, HttpStatus.OK, sessionId);
    }

    @Test
    void 권한_없는_사용자_댓글_삭제() {
        String sessionId = loginSessionId(otherRequestDto);
        respondApi(loginAndRequest(HttpMethod.DELETE, "/api/articles/1/comments/1", Void.class, HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("권한이 없습니다.")
                ;
    }

    @Test
    void 다른_페이지_댓글_삭제() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.DELETE, "/api/articles/2/comments/1", Void.class, HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("권한이 없습니다.")
                ;
    }

    @Test
    void 존재하지_않는_댓글_삭제() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.DELETE, "/api/articles/1/comments/0", Void.class, HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("존재하지 않는 댓글입니다.")
                ;
    }

    @Test
    void 존재하지_않는_게시글_댓글_삭제() {
        String sessionId = loginSessionId(userRequestDto);
        respondApi(loginAndRequest(HttpMethod.DELETE, "/api/articles/0/comments/1", Void.class, HttpStatus.OK, sessionId))
                .jsonPath("$.errorMessage").isEqualTo("찾을 수 없는 게시글입니다.")
                ;
    }
}