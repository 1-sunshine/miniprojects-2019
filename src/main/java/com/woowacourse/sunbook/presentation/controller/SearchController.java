package com.woowacourse.sunbook.presentation.controller;

import com.woowacourse.sunbook.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {
    private final UserService userService;

    @Autowired
    public SearchController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ModelAndView searchByUserName(String userName) {
        return new ModelAndView("search", "users", userService.findByUserName(userName));
    }
}
