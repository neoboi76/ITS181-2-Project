package com.pawportal.backend.models.enums;

/** Developed by Group 6:
 * Kenji Mark Alan Arceo
 * Carl Norbi Felonia
 * Ryonan Owen Ferrer
 * Dino Alfred Timbol
 * Mike Emil Vocal
 */

/**
 * Enum class for the range of possible audit actions
 */

public enum AuditAction {
    USER_LOGIN,
    USER_LOGOUT,
    USER_REGISTER,
    PASSWORD_RESET,
    PASSWORD_FORGOT,
    LOGIN_FAILED,

    USER_PROFILE_UPDATED,
    USER_PROFILE_VIEWED,
    USER_SUSPENDED,
    USER_PROMOTED,
    USER_ACTIVATED,
    USER_DELETED,

    DOG_CREATED,
    DOG_UPDATED,
    DOG_DELETED,
    DOG_VIEWED,

    APPLICATION_CREATED,
    APPLICATION_UPDATED,
    APPLICATION_VIEWED,

    CONTACT_CREATED,
    CONTACT_VIEWED,

    SYSTEM_ERROR,
    API_KEY_USED,
    SEARCH_PERFORMED
}