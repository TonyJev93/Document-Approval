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
    @DisplayName("?????? ?????? ?????????")
    @Order(1)
    void createUser() throws Exception {

        // ?????? ?????? ?????? from JSON File
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
                                attributes(key("title").value("?????? ?????? ??????")),
                                fieldWithPath("userId").description("????????? ID"),
                                fieldWithPath("password").description("????????????").attributes(key("remark").value("????????? ???")),
                                fieldWithPath("name").description("????????? ??????"),
                                fieldWithPath("roles[]").description("?????? ??????").attributes(key("remark").value("ROLE_USER, ROLE_ADMIN ??????")).optional()
                        ),
                        responseFields(
                                attributes(key("title").value("?????? ?????? ??????")),
                                fieldWithPath("userId").description("????????? ID"),
                                fieldWithPath("password").description("????????????(?????????)").attributes(key("remark").value("????????? ???")),
                                fieldWithPath("name").description("????????? ??????"),
                                fieldWithPath("roles[]").description("?????? ??????")
                        )

                ));
    }

    @Test
    @DisplayName("????????? ?????????")
    @Order(2)
    void login() throws Exception {

        // ????????? ?????? from JSON File
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
                                attributes(key("title").value("????????? ??????")),
                                fieldWithPath("userId").description("????????? ID"),
                                fieldWithPath("password").description("????????????").attributes(key("remark").value("????????? ???"))
                        ),
                        responseFields(
                                attributes(key("title").value("????????? ??????")),
                                fieldWithPath("jwtToken").description("JWT ??????").attributes(key("remark").value("????????? ?????? ??? ????????? ?????? ??????(Header : X-AUTH-TOKEN)"))
                        )
                ));
    }
}
