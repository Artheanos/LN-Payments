package pl.edu.pjatk.lnpayments.webservice.common;

public class Constants {

    public static final String PAYMENTS_PATH = "/payments";
    public static final String AUTH_PATH = "/auth";
    public static final String INFO_PATH = "/info";
    public static final String ALL_PATH = "/all";
    public static final String REGISTER_PATH = "/register";
    public static final String LOGIN_PATH = "/login";
    public static final String REFRESH_PATH = "/refreshToken";
    public static final String TEMPORARY_PATH = "/temporary";
    public static final String PAYMENTS_WS_PATH = "/payment";
    public static final String ADMIN_PATH = "/admins";
    public static final String WALLET_PATH = "/wallet";
    public static final String CLOSE_CHANNELS_PATH = "/closeChannels";
    public static final String TRANSFER_PATH = "/transfer";
    public static final String KEYS_PATH = "/keys";

    public static final int FORCE_CLOSE_INACTIVE_CHANNEL_DAYS = 7;

    public static final String ROOT_USER_EMAIL = "admin@admin.pl";
    public static final String ROOT_USER_PASSWORD = "admin";

    private Constants() {}

}
