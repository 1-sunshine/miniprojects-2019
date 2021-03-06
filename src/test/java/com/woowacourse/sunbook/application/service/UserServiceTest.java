package com.woowacourse.sunbook.application.service;

import com.woowacourse.sunbook.MockStorage;
import com.woowacourse.sunbook.application.dto.user.UserResponseDto;
import com.woowacourse.sunbook.application.exception.DuplicateEmailException;
import com.woowacourse.sunbook.application.exception.LoginException;
import com.woowacourse.sunbook.domain.user.User;
import com.woowacourse.sunbook.domain.user.UserEmail;
import com.woowacourse.sunbook.domain.user.UserPassword;
import com.woowacourse.sunbook.domain.user.exception.MismatchUserException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UserServiceTest extends MockStorage {

    @InjectMocks
    private UserService injectUserService;

    @Test
    void 사용자_생성_성공() {
        given(modelMapper.map(userRequestDto, User.class)).willReturn(user);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(modelMapper.map(user, UserResponseDto.class)).willReturn(mock(UserResponseDto.class));

        injectUserService.save(userRequestDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void 중복_이메일로_인한_사용자_생성_실패() {
        given(userRequestDto.getUserEmail()).willReturn(mock(UserEmail.class));
        given(userRepository.existsByUserEmail(any(UserEmail.class))).willReturn(true);

        assertThrows(DuplicateEmailException.class, () -> {
            injectUserService.save(userRequestDto);
        });
    }

    @Test
    void 사용자_수정() {
        given(userResponseDto.getUserEmail()).willReturn(mock(UserEmail.class));
        given(userUpdateRequestDto.getUserEmail()).willReturn(userEmail);
        given(userUpdateRequestDto.getUserName()).willReturn(userName);
        given(userUpdateRequestDto.getUserPassword()).willReturn(userPassword);
        given(userRepository.findByUserEmailAndUserPassword(any(UserEmail.class), any(UserPassword.class)))
                .willReturn((Optional.of(user)));
        given(modelMapper.map(user, UserResponseDto.class)).willReturn(userResponseDto);
        given(userUpdateRequestDto.getChangePassword()).willReturn(userChangePassword);
        given(userChangePassword.updatedPassword(userPassword)).willReturn(userPassword);

        injectUserService.update(userResponseDto, userUpdateRequestDto);

        verify(user, times(1)).updateEmail(user, userEmail);
        verify(user, times(1)).updateName(user, userName);
        verify(user, times(1)).updatePassword(user, userPassword);
    }

    @Test
    void 사용자_수정_권한_없음() {
        given(userRepository.findByUserEmailAndUserPassword(any(UserEmail.class), any(UserPassword.class)))
                .willReturn((Optional.empty()));

        assertThrows(LoginException.class, () -> {
            injectUserService.update(userResponseDto, userUpdateRequestDto);
        });
    }

    @Test
    void 사용자_이름으로_조회() {
        given(userRepository.findAllByUserNameLike(any(String.class))).willReturn(users);
        given(modelMapper.map(user, UserResponseDto.class)).willReturn(mock(UserResponseDto.class));

        injectUserService.findByUserName("TestName");

        verify(userRepository).findAllByUserNameLike(any(String.class));
    }

    @Test
    void 사용자_아이디로_조회() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        assertDoesNotThrow(() -> injectUserService.findUser(1L));
    }

    @Test
    void 사용자가_일치하지_않는_경우_수정() {
        given(userResponseDto.getUserEmail()).willReturn(userEmail);
        given(userUpdateRequestDto.getUserPassword()).willReturn(userPassword);
        given(userRepository.findByUserEmailAndUserPassword(any(UserEmail.class), any(UserPassword.class))).willReturn(Optional.of(user));
        given(userUpdateRequestDto.getUserEmail()).willReturn(userEmail);
        given(userUpdateRequestDto.getChangePassword()).willReturn(userChangePassword);

        doThrow(MismatchUserException.class).when(user).updateEmail(any(User.class), any(UserEmail.class));

        assertThrows(MismatchUserException.class, () -> {
            injectUserService.update(userResponseDto, userUpdateRequestDto);
        });

    }
}