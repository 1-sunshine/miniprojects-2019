package com.woowacourse.sunbook.presentation.controller;

import com.woowacourse.sunbook.application.dto.article.ArticleRequestDto;
import com.woowacourse.sunbook.application.dto.article.ArticleResponseDto;
import com.woowacourse.sunbook.application.service.ArticleService;
import com.woowacourse.sunbook.domain.article.ArticleFeature;
import com.woowacourse.sunbook.presentation.support.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleApiController {
    private final ArticleService articleService;

    @Autowired
    public ArticleApiController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponseDto>> show(LoginUser loginUser) {
        List<ArticleResponseDto> articles = articleService.findAll(loginUser.getId());

        return ResponseEntity.ok().body(articles);
    }

    @PostMapping
    public ResponseEntity<ArticleResponseDto> save(ArticleRequestDto articleRequestDto, LoginUser loginUser) {
        ArticleResponseDto articleResponseDto = articleService.save(articleRequestDto, loginUser.getId());

        return ResponseEntity.ok().body(articleResponseDto);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleResponseDto> modify(@PathVariable Long articleId,
                                                     @RequestBody ArticleFeature articleFeature,
                                                     LoginUser loginUser) {
        ArticleResponseDto articleResponseDto = articleService.modify(articleId, articleFeature, loginUser.getId());

        return ResponseEntity.ok().body(articleResponseDto);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Boolean> remove(@PathVariable Long articleId, LoginUser loginUser) {
        return ResponseEntity.ok().body(articleService.remove(articleId, loginUser.getId()));
    }
}
