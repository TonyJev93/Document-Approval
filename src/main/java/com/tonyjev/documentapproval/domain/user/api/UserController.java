package com.tonyjev.documentapproval.domain.user.api;

import com.tonyjev.documentapproval.domain.user.api.dto.UserDto;
import com.tonyjev.documentapproval.domain.user.application.UserCreateService;
import com.tonyjev.documentapproval.domain.user.application.UserDeleteService;
import com.tonyjev.documentapproval.domain.user.application.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class UserController {
    private final UserCreateService userCreateService;
    private final UserLoginService userLoginService;
    private final UserDeleteService userDeleteService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.CreateRes createUser(@RequestBody @Valid UserDto.CreateReq createReq) {
        return UserDto.CreateRes.of(userCreateService.join(createReq.toEntity()));
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.LoginRes login(@RequestBody @Valid UserDto.Login request) {
        return UserDto.LoginRes.builder().jwtToken(
                userLoginService.login(request.getUserId(), request.getPassword())
        ).build();
    }

    @DeleteMapping("/admin/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        userDeleteService.delete(userId);
    }

}
