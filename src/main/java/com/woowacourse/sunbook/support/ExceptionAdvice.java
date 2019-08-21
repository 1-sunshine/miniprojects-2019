package com.woowacourse.sunbook.support;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.woowacourse.sunbook.application.exception.DuplicateEmailException;
import com.woowacourse.sunbook.application.exception.LoginException;
import com.woowacourse.sunbook.domain.validation.exception.InvalidValueException;
import com.woowacourse.sunbook.mir.application.exception.NotFoundCommentException;
import com.woowacourse.sunbook.mir.domain.exception.MismatchAuthException;
import com.woowacourse.sunbook.presentation.excpetion.NotFoundArticleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler({DuplicateEmailException.class, LoginException.class, InvalidValueException.class,
            MismatchAuthException.class, NotFoundArticleException.class, NotFoundCommentException.class})
    public ResponseEntity<ErrorMessage> responseExceptionMessage(RuntimeException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidDefinitionException.class)
    public ResponseEntity<ErrorMessage> response(HttpMessageConversionException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getCause().getCause().getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.OK);
    }
}
