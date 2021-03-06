package com.tonyjev.documentapproval.domain.document.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tonyjev.documentapproval.domain.document.api.dto.request.DocumentApproveRequest;
import com.tonyjev.documentapproval.domain.document.api.dto.request.DocumentCreateRequest;
import com.tonyjev.documentapproval.domain.document.application.DocumentApproveService;
import com.tonyjev.documentapproval.domain.document.application.DocumentCreateService;
import com.tonyjev.documentapproval.domain.document.application.DocumentRejectService;
import com.tonyjev.documentapproval.domain.document.application.DocumentRetrieveService;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.user.api.dto.UserDto;
import com.tonyjev.documentapproval.domain.user.application.UserCreateService;
import com.tonyjev.documentapproval.domain.user.application.UserLoginService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.generate.RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentControllerTest {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserCreateService userCreateService;

    @MockBean
    private DocumentCreateService documentCreateService;

    @MockBean
    private DocumentRetrieveService documentRetrieveService;

    @MockBean
    private DocumentApproveService documentApproveService;

    @MockBean
    private DocumentRejectService documentRejectService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private RestDocumentationResultHandler document;

    private String jwt = "";

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


    void joinAll() throws Exception {
        // ?????? ?????? ?????? from JSON File
        List<Map<String, Object>> request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/user/JoinRequestList.json").toURI()).toFile(), ArrayList.class);

        // ????????????
        for (Map<String, Object> createReq : request) {

            UserDto.CreateReq req = objectMapper.convertValue(createReq, UserDto.CreateReq.class);

            joinUser(req);
        }
    }

    private void joinUser(UserDto.CreateReq request) {
        userCreateService.join(request.toEntity());
    }

    private String loginUser(Map<String, String> request) throws Exception {
        final String jwt = userLoginService.login(request.get("userId"), request.get("password"));
        return jwt;
    }

    @Test
    @Order(1)
    @DisplayName("?????? ??????")
    void createDocument() throws Exception {

        // ?????? ?????? ?????? from JSON File
        DocumentCreateRequest request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentCreateRequest.json").toURI()).toFile(), DocumentCreateRequest.class);

        String jsonContent = objectMapper.writeValueAsString(request);

        // ????????????
        joinAll();

        // ?????????
        Map<String, String> loginRequest = makeLoginRequest("test1", "password123");
        jwt = loginUser(loginRequest);

        Document document =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentCreateResponse.json").toURI()).toFile(), Document.class);

        when(documentCreateService.createDocument(any())).thenReturn(document);

        // ?????? ??????
        this.mockMvc
                .perform(post("/api/v1.0/user/document")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", jwt)
                )
                .andExpect(status().isCreated())
                .andDo(this.document.document(
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT ??????(????????? ID, Role ??????)")
                        ),
                        requestFields(
                                attributes(key("title").value("?????? ?????? ??????")),
                                fieldWithPath("title").description("??????"),
                                fieldWithPath("classification").description("??????").attributes(key("remark").value("VACATION(??????), REPORT(??????), NOTIFICATION(??????)")),
                                fieldWithPath("content").description("??????"),
                                fieldWithPath("approverList[]").description("????????? ??????").attributes(key("remark").value("seq ????????? ?????? ??????")),
                                fieldWithPath("approverList[].seq").description("?????? ??????"),
                                fieldWithPath("approverList[].userId").description("????????? ID").attributes(key("remark").value("?????? ????????? ???????????? ?????? ??????"))
                        ),
                        responseFields(
                                attributes(key("title").value("?????? ?????? ??????")),
                                fieldWithPath("documentId").description("?????? ID").attributes(key("remark").value("?????? ??????(Auto Increment)")),
                                fieldWithPath("title").description("??????"),
                                fieldWithPath("classification").description("??????").attributes(key("remark").value("VACATION(??????), REPORT(??????), NOTIFICATION(??????)")),
                                fieldWithPath("content").description("??????"),
                                fieldWithPath("approvalStatus").description("?????? ??????").attributes(key("remark").value("WAITING(?????? ??????), APPROVING(?????? ???), APPROVED(??????), REJECTED(??????)")),
                                fieldWithPath("createUserId").description("????????? ID"),
                                fieldWithPath("createdAt").description("?????? ??????"),
                                fieldWithPath("updateUserId").description("????????? ID"),
                                fieldWithPath("updatedAt").description("?????? ??????"),
                                fieldWithPath("approverList[]").description("????????? ??????"),
                                fieldWithPath("approverList[].seq").description("?????? ??????"),
                                fieldWithPath("approverList[].userId").description("????????? ID"),
                                fieldWithPath("approverList[].approvalYn").description("?????? ??????").attributes(key("remark").value("true or false")),
                                fieldWithPath("approverList[].approvalStatus").description("?????? ??????").attributes(key("remark").value("????????? ??? ?????? ??????")),
                                fieldWithPath("approverList[].comment").description("??????").attributes(key("remark").value("?????? OR ?????? ??? ?????? ??????"))
                        )

                ));
    }

    private Map<String, String> makeLoginRequest(String userId, String password) {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("userId", userId);
        loginRequest.put("password", password);
        return loginRequest;
    }

    @Test
    @Order(2)
    @DisplayName("?????? ?????? ??????(outbox/inbox/archive)")
    void getDocumentList() throws Exception {

        List<Map<String, Object>> documentMapList =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentRetrieveListResponse.json").toURI()).toFile(), ArrayList.class);

        List<Document> documentList = new ArrayList<>();
        for (Map<String, Object> documentMap : documentMapList) {
            documentList.add(objectMapper.convertValue(documentMap, Document.class));
        }

        when(documentRetrieveService.retrieveDocumentList(any())).thenReturn(documentList);

        // ?????? ?????? ??????
        this.mockMvc
                .perform(get("/api/v1.0/user/document/list/outbox")
                        .requestAttr(ATTRIBUTE_NAME_URL_TEMPLATE, "/api/v1.0/user/document/list/{retrieveType}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", jwt)
                )
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("retrieveType").description("?????? ??????(??? ?????? ?????? : outbox, inbox, archive)")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT ??????(????????? ID, Role ??????)")
                        ),
                        responseFields(
                                attributes(key("title").value("?????? ?????? ?????? ??????")),
                                fieldWithPath("total").description("?????? ??????"),
                                fieldWithPath("documentList[]").description("?????? ??????"),
                                fieldWithPath("documentList[].documentId").description("?????? ID").attributes(key("remark").value("?????? ??????(Auto Increment)")),
                                fieldWithPath("documentList[].title").description("??????"),
                                fieldWithPath("documentList[].classification").description("??????").attributes(key("remark").value("VACATION(??????), REPORT(??????), NOTIFICATION(??????)")),
                                fieldWithPath("documentList[].content").description("??????"),
                                fieldWithPath("documentList[].approvalStatus").description("?????? ??????").attributes(key("remark").value("WAITING(?????? ??????), APPROVING(?????? ???), APPROVED(??????), REJECTED(??????)")),
                                fieldWithPath("documentList[].createUserId").description("????????? ID"),
                                fieldWithPath("documentList[].createdAt").description("?????? ??????"),
                                fieldWithPath("documentList[].updateUserId").description("????????? ID"),
                                fieldWithPath("documentList[].updatedAt").description("?????? ??????"),
                                fieldWithPath("documentList[].approverList[]").description("????????? ??????"),
                                fieldWithPath("documentList[].approverList[].seq").description("?????? ??????"),
                                fieldWithPath("documentList[].approverList[].userId").description("????????? ID"),
                                fieldWithPath("documentList[].approverList[].approvalYn").description("?????? ??????").attributes(key("remark").value("true or false")),
                                fieldWithPath("documentList[].approverList[].approvalStatus").description("?????? ??????").attributes(key("remark").value("????????? ??? ?????? ??????")),
                                fieldWithPath("documentList[].approverList[].comment").description("??????").attributes(key("remark").value("?????? OR ?????? ??? ?????? ??????"))
                        )

                ));
    }

    @Test
    @Order(3)
    @DisplayName("?????? ??????")
    void getDocument() throws Exception {

        Document document =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentRetrieveResponse.json").toURI()).toFile(), Document.class);

        when(documentRetrieveService.retrieveDocument(any())).thenReturn(document);

        // ?????? ?????? ??????
        this.mockMvc
                .perform(get("/api/v1.0/user/document/1")
                        .requestAttr(ATTRIBUTE_NAME_URL_TEMPLATE, "/api/v1.0/user/document/{documentId}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", jwt)
                )
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("documentId").description("?????? ID")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT ??????(????????? ID, Role ??????)")
                        ),
                        responseFields(
                                attributes(key("title").value("?????? ?????? ??????")),
                                fieldWithPath("documentId").description("?????? ID").attributes(key("remark").value("?????? ??????(Auto Increment)")),
                                fieldWithPath("title").description("??????"),
                                fieldWithPath("classification").description("??????").attributes(key("remark").value("VACATION(??????), REPORT(??????), NOTIFICATION(??????)")),
                                fieldWithPath("content").description("??????"),
                                fieldWithPath("approvalStatus").description("?????? ??????").attributes(key("remark").value("WAITING(?????? ??????), APPROVING(?????? ???), APPROVED(??????), REJECTED(??????)")),
                                fieldWithPath("createUserId").description("????????? ID"),
                                fieldWithPath("createdAt").description("?????? ??????"),
                                fieldWithPath("updateUserId").description("????????? ID"),
                                fieldWithPath("updatedAt").description("?????? ??????"),
                                fieldWithPath("approverList[]").description("????????? ??????"),
                                fieldWithPath("approverList[].seq").description("?????? ??????"),
                                fieldWithPath("approverList[].userId").description("????????? ID"),
                                fieldWithPath("approverList[].approvalYn").description("?????? ??????").attributes(key("remark").value("true or false")),
                                fieldWithPath("approverList[].approvalStatus").description("?????? ??????").attributes(key("remark").value("????????? ??? ?????? ??????")),
                                fieldWithPath("approverList[].comment").description("??????").attributes(key("remark").value("?????? OR ?????? ??? ?????? ??????"))
                        )

                ));
    }

    @Test
    @Order(4)
    @DisplayName("?????? ??????(?????? / ??????)")
    void approveDocument() throws Exception {

        // ?????? ?????? ?????? from JSON File
        DocumentApproveRequest request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentApprovalRequest.json").toURI()).toFile(), DocumentApproveRequest.class);

        String jsonContent = objectMapper.writeValueAsString(request);

        Document document =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentRetrieveResponse.json").toURI()).toFile(), Document.class);

        when(documentApproveService.processDocument(any(), any())).thenReturn(document);

        // ?????? ?????? ??????
        this.mockMvc
                .perform(patch("/api/v1.0/user/document/1/approve")
                        .requestAttr(ATTRIBUTE_NAME_URL_TEMPLATE, "/api/v1.0/user/document/{documentId}/{method}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-AUTH-TOKEN", jwt)
                        .content(jsonContent)
                )
                .andExpect(status().isNoContent())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("documentId").description("?????? ID"),
                                parameterWithName("method").description("?????? ??????(??? ?????? ?????? : approve(??????), reject(??????))")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT ??????(????????? ID, Role ??????)")
                        ),
                        requestFields(
                                attributes(key("title").value("?????? ?????? ??????")),
                                fieldWithPath("comment").description("??????").attributes(key("remark").value("?????? or ?????? ??? ????????? ??? ?????? ??????")).optional()
                        )
                ));
    }
}
