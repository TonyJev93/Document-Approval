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
        // 회원 가입 요청 from JSON File
        List<Map<String, Object>> request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/user/JoinRequestList.json").toURI()).toFile(), ArrayList.class);

        // 회원가입
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
    @DisplayName("문서 생성")
    void createDocument() throws Exception {

        // 회원 가입 요청 from JSON File
        DocumentCreateRequest request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentCreateRequest.json").toURI()).toFile(), DocumentCreateRequest.class);

        String jsonContent = objectMapper.writeValueAsString(request);

        // 회원가입
        joinAll();

        // 로그인
        Map<String, String> loginRequest = makeLoginRequest("test1", "password123");
        jwt = loginUser(loginRequest);

        Document document =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentCreateResponse.json").toURI()).toFile(), Document.class);

        when(documentCreateService.createDocument(any())).thenReturn(document);

        // 문서 생성
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
                                headerWithName("X-AUTH-TOKEN").description("JWT 토큰(사용자 ID, Role 보관)")
                        ),
                        requestFields(
                                attributes(key("title").value("문서 생성 요청")),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("classification").description("분류").attributes(key("remark").value("VACATION(휴가), REPORT(보고), NOTIFICATION(공지)")),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("approverList[]").description("결재자 목록").attributes(key("remark").value("seq 순으로 결재 진행")),
                                fieldWithPath("approverList[].seq").description("결재 순번"),
                                fieldWithPath("approverList[].userId").description("결재자 ID").attributes(key("remark").value("회원 가입된 사용자만 입력 가능"))
                        ),
                        responseFields(
                                attributes(key("title").value("문서 생성 응답")),
                                fieldWithPath("documentId").description("문서 ID").attributes(key("remark").value("자동 채번(Auto Increment)")),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("classification").description("분류").attributes(key("remark").value("VACATION(휴가), REPORT(보고), NOTIFICATION(공지)")),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("approvalStatus").description("결재 상태").attributes(key("remark").value("WAITING(결재 대기), APPROVING(결재 중), APPROVED(승인), REJECTED(반려)")),
                                fieldWithPath("createUserId").description("생성자 ID"),
                                fieldWithPath("createdAt").description("생성 일시"),
                                fieldWithPath("updateUserId").description("수정자 ID"),
                                fieldWithPath("updatedAt").description("수정 일시"),
                                fieldWithPath("approverList[]").description("결재자 목록"),
                                fieldWithPath("approverList[].seq").description("결재 순번"),
                                fieldWithPath("approverList[].userId").description("사용자 ID"),
                                fieldWithPath("approverList[].approvalYn").description("결재 여부").attributes(key("remark").value("true or false")),
                                fieldWithPath("approverList[].approvalStatus").description("결재 상태").attributes(key("remark").value("결재자 별 결재 상태")),
                                fieldWithPath("approverList[].comment").description("첨언").attributes(key("remark").value("승인 OR 반려 시 첨언 추가"))
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
    @DisplayName("문서 목록 조회(outbox/inbox/archive)")
    void getDocumentList() throws Exception {

        List<Map<String, Object>> documentMapList =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentRetrieveListResponse.json").toURI()).toFile(), ArrayList.class);

        List<Document> documentList = new ArrayList<>();
        for (Map<String, Object> documentMap : documentMapList) {
            documentList.add(objectMapper.convertValue(documentMap, Document.class));
        }

        when(documentRetrieveService.retrieveDocumentList(any())).thenReturn(documentList);

        // 문서 목록 조회
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
                                parameterWithName("retrieveType").description("조회 유형(세 가지 유형 : outbox, inbox, archive)")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT 토큰(사용자 ID, Role 보관)")
                        ),
                        responseFields(
                                attributes(key("title").value("문서 목록 조회 응답")),
                                fieldWithPath("total").description("조회 건수"),
                                fieldWithPath("documentList[]").description("문서 목록"),
                                fieldWithPath("documentList[].documentId").description("문서 ID").attributes(key("remark").value("자동 채번(Auto Increment)")),
                                fieldWithPath("documentList[].title").description("제목"),
                                fieldWithPath("documentList[].classification").description("분류").attributes(key("remark").value("VACATION(휴가), REPORT(보고), NOTIFICATION(공지)")),
                                fieldWithPath("documentList[].content").description("내용"),
                                fieldWithPath("documentList[].approvalStatus").description("결재 상태").attributes(key("remark").value("WAITING(결재 대기), APPROVING(결재 중), APPROVED(승인), REJECTED(반려)")),
                                fieldWithPath("documentList[].createUserId").description("생성자 ID"),
                                fieldWithPath("documentList[].createdAt").description("생성 일시"),
                                fieldWithPath("documentList[].updateUserId").description("수정자 ID"),
                                fieldWithPath("documentList[].updatedAt").description("수정 일시"),
                                fieldWithPath("documentList[].approverList[]").description("결재자 목록"),
                                fieldWithPath("documentList[].approverList[].seq").description("결재 순번"),
                                fieldWithPath("documentList[].approverList[].userId").description("사용자 ID"),
                                fieldWithPath("documentList[].approverList[].approvalYn").description("결재 여부").attributes(key("remark").value("true or false")),
                                fieldWithPath("documentList[].approverList[].approvalStatus").description("결재 상태").attributes(key("remark").value("결재자 별 결재 상태")),
                                fieldWithPath("documentList[].approverList[].comment").description("첨언").attributes(key("remark").value("승인 OR 반려 시 첨언 추가"))
                        )

                ));
    }

    @Test
    @Order(3)
    @DisplayName("문서 조회")
    void getDocument() throws Exception {

        Document document =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentRetrieveResponse.json").toURI()).toFile(), Document.class);

        when(documentRetrieveService.retrieveDocument(any())).thenReturn(document);

        // 문서 목록 조회
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
                                parameterWithName("documentId").description("문서 ID")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT 토큰(사용자 ID, Role 보관)")
                        ),
                        responseFields(
                                attributes(key("title").value("문서 조회 응답")),
                                fieldWithPath("documentId").description("문서 ID").attributes(key("remark").value("자동 채번(Auto Increment)")),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("classification").description("분류").attributes(key("remark").value("VACATION(휴가), REPORT(보고), NOTIFICATION(공지)")),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("approvalStatus").description("결재 상태").attributes(key("remark").value("WAITING(결재 대기), APPROVING(결재 중), APPROVED(승인), REJECTED(반려)")),
                                fieldWithPath("createUserId").description("생성자 ID"),
                                fieldWithPath("createdAt").description("생성 일시"),
                                fieldWithPath("updateUserId").description("수정자 ID"),
                                fieldWithPath("updatedAt").description("수정 일시"),
                                fieldWithPath("approverList[]").description("결재자 목록"),
                                fieldWithPath("approverList[].seq").description("결재 순번"),
                                fieldWithPath("approverList[].userId").description("사용자 ID"),
                                fieldWithPath("approverList[].approvalYn").description("결재 여부").attributes(key("remark").value("true or false")),
                                fieldWithPath("approverList[].approvalStatus").description("결재 상태").attributes(key("remark").value("결재자 별 결재 상태")),
                                fieldWithPath("approverList[].comment").description("첨언").attributes(key("remark").value("승인 OR 반려 시 첨언 추가"))
                        )

                ));
    }

    @Test
    @Order(4)
    @DisplayName("문서 결재(승인 / 반려)")
    void approveDocument() throws Exception {

        // 회원 가입 요청 from JSON File
        DocumentApproveRequest request =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentApprovalRequest.json").toURI()).toFile(), DocumentApproveRequest.class);

        String jsonContent = objectMapper.writeValueAsString(request);

        Document document =
                objectMapper.readValue(Paths.get(ClassLoader.getSystemResource("testcase/document/DocumentRetrieveResponse.json").toURI()).toFile(), Document.class);

        when(documentApproveService.processDocument(any(), any())).thenReturn(document);

        // 문서 목록 조회
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
                                parameterWithName("documentId").description("문서 ID"),
                                parameterWithName("method").description("결재 유형(두 가지 유형 : approve(승인), reject(반려))")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("JWT 토큰(사용자 ID, Role 보관)")
                        ),
                        requestFields(
                                attributes(key("title").value("문서 결재 요청")),
                                fieldWithPath("comment").description("첨언").attributes(key("remark").value("승인 or 반려 시 추가할 수 있는 첨언")).optional()
                        )
                ));
    }
}
