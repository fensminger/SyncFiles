package org.fer.syncfiles.config;

/**
 * Application constants.
 */
public final class Constants {
    //Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SPRING_PROFILE_ENCH1 = "ench1";
    public static final String SPRING_PROFILE_ENCH2 = "ench2";
    public static final String SPRING_PROFILE_REC = "rec";
    public static final String SPRING_PROFILE_HOM = "hom";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    private Constants() {
    }
}
