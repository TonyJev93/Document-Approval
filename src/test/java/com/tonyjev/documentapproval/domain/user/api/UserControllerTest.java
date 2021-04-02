package com.tonyjev.documentapproval.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tonyjev.documentapproval.domain.user.api.dto.UserDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Paths;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private RestDocumentationResultHandler document;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.document = document(
                "{class-name}/{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .alwaysDo(this.document)
                .apply(documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080))
                .build();
    }

    @Test
    @DisplayName("회원 가입 테스트")
    @Order(1)
    void createUser() throws Exception {

        // 회원 가입 요청 from JSON File
        UserDto.CreateReq request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/user/JoinRequest.json").toURI()).toFile(), UserDto.CreateReq.class);

        String jsonContent = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(post("/api/v1.0/join")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(this.document.document(
                        requestFields(
                                attributes(key("title").value("회원 가입 요청")),
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("password").description("비밀번호").attributes(key("remark").value("인코딩 전")),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("roles[]").description("권한 목록").attributes(key("remark").value("ROLE_USER, ROLE_ADMIN 존재")).optional()
                        ),
                        responseFields(
                                attributes(key("title").value("회원 가입 응답")),
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("password").description("비밀번호(인코딩)").attributes(key("remark").value("인코딩 후")),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("roles[]").description("권한 목록")
                        )

                ));
    }

    @Test
    @DisplayName("로그인 테스트")
    @Order(2)
    void login() throws Exception {

        // 로그인 요청 from JSON File
        UserDto.Login request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/user/LoginRequest.json").toURI()).toFile(), UserDto.Login.class);

        String jsonContent = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(post("/api/v1.0/login")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        requestFields(
                                attributes(key("title").value("로그인 요청")),
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("password").description("비밀번호").attributes(key("remark").value("인코딩 전"))
                        ),
                        responseFields(
                                attributes(key("title").value("로그인 응답")),
                                fieldWithPath("jwtToken").description("JWT 토큰").attributes(key("remark").value("서비스 이용 시 사용자 검증 전용(Header : X-AUTH-TOKEN)"))
                        )
                ));
    }
}
