package com.pawportal.backend.models.enums;

public enum AuditAction {
    USER_LOGIN,
    USER_LOGOUT,
    USER_REGISTER,
    PASSWORD_RESET,
    LOGIN_FAILED,

    // User Management
    USER_PROFILE_UPDATED,
    USER_PROFILE_VIEWED,

    SYSTEM_ERROR,
    API_KEY_USED,
    SEARCH_PERFORMED
}