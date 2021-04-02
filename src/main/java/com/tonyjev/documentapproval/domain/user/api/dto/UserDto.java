package com.tonyjev.documentapproval.domain.user.api.dto;

import com.tonyjev.documentapproval.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateReq {
        @NotEmpty
        private String userId;

        @Valid
        @NotEmpty
        private String password;

        @NotEmpty
        private String name;

        @NotEmpty
        private List<String> roles;

        public User toEntity() {
            return User.of(userId, password, name, roles);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Login {
        @NotEmpty
        private String userId;
        @NotEmpty
        private String password;
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateRes {
        private String userId;
        private String password;
        private String name;
        private List<String> roles;

        @Builder
        private CreateRes(String userId, String password, String name, List<String> roles) {
            this.userId = userId;
            this.password = password;
            this.name = name;
            this.roles = roles;
        }

        public static CreateRes of(User user) {
            return CreateRes.builder()
                    .userId(user.getUserId())
                    .password(user.getPassword())
                    .name(user.getName())
                    .roles(user.getRoles())
                    .build();
        }

    }

    @Getter
    @Builder
    public static class LoginRes {
        private String jwtToken;
    }


}
