package com.woowacourse.sunbook.application.service;

import com.woowacourse.sunbook.application.dto.user.UserRequestDto;
import com.woowacourse.sunbook.application.dto.user.UserResponseDto;
import com.woowacourse.sunbook.application.dto.user.UserUpdateRequestDto;
import com.woowacourse.sunbook.application.exception.DuplicateEmailException;
import com.woowacourse.sunbook.application.exception.LoginException;
import com.woowacourse.sunbook.application.exception.NotFoundUserException;
import com.woowacourse.sunbook.domain.user.User;
import com.woowacourse.sunbook.domain.user.UserChangePassword;
import com.woowacourse.sunbook.domain.user.UserPassword;
import com.woowacourse.sunbook.domain.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(final UserRepository userRepository, final ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public UserResponseDto save(final UserRequestDto userRequestDto) {
        checkDuplicateEmail(userRequestDto);
        User user = userRepository.save(modelMapper.map(userRequestDto, User.class));

        return modelMapper.map(user, UserResponseDto.class);
    }

    private void checkDuplicateEmail(final UserRequestDto userRequestDto) {
        if (userRepository.existsByUserEmail(userRequestDto.getUserEmail())) {
            throw new DuplicateEmailException();
        }
    }

    public List<UserResponseDto> findByUserName(final String userName) {
        List<User> users = userRepository.findAllByUserNameLike(userName);

        return users.stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .collect(Collectors.toList())
                ;
    }

    @Transactional
    public UserResponseDto update(final UserResponseDto loginUser, final UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findByUserEmailAndUserPassword(
                loginUser.getUserEmail(),
                userUpdateRequestDto.getUserPassword()
        ).orElseThrow(LoginException::new);

        UserPassword loginUserPassword = userUpdateRequestDto.getUserPassword();
        UserChangePassword userChangePassword = userUpdateRequestDto.getChangePassword();

        user.updateEmail(user, userUpdateRequestDto.getUserEmail());
        user.updateName(user, userUpdateRequestDto.getUserName());
        user.updatePassword(user, userChangePassword.updatedPassword(loginUserPassword));

        return modelMapper.map(user, UserResponseDto.class);
    }

    protected User findById(final Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundUserException::new);
    }
}
