package com.tonyjev.documentapproval.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    // Common Error
    INVALID_INPUT_VALUE(400, "Invalid Input Value"),
    INVALID_TYPE_VALUE(400, "Invalid Type Value"),
    INVALID_INPUT_METHOD(400, "Invalid Input Method"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    HANDLE_ACCESS_DENIED(403, "Access is Denied"),
    UNAUTHORIZED_EXCEPTION(401, "Unauthorized"),
    ENTITY_NOT_FOUND_EXCEPTION(404, "Entity Not Found."),
    NOT_FOUND_HANDLE_EXCEPTION(500, "Not Found Handle Exception"),

    // Login
    ALREADY_EXIST_USER_ID_EXCEPTION(409, "이미 존재하는 사용자 입니다."),
    PASSWORD_NOT_CORRECT(400, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_USER(404, "사용자를 찾을 수 없습니다."),

    // Document
    NOT_FOUND_DOCUMENT(404, "해당 문서가 존재하지 않습니다."),

    // Approval
    NOT_FOUND_APPROVER(404, "해당 결재자를 찾을 수 없습니다."),
    APPROVAL_TURN_NOT_MINE(400, "결재할 차례가 아닙니다."),
    APPROVAL_AUTHORIZATION_NOT_HAVE(400, "결재 권한이 없습니다."),
    ALREADY_APPROVED_APPROVER(400, "이미 결재하였습니다."),
    DOCUMENT_APPROVAL_NOT_POSSIBLE(400, "이미 처리가 완료된 문서입니다."),
    ;

    private int status;
    private String message;
}
