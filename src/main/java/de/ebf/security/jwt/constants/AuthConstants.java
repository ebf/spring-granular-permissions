package de.ebf.security.jwt.constants;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class AuthConstants {

    private AuthConstants() {
    }

    public static final String CLAIMS_SYSTEM_FUNCTIONS = "systemFunctions";
    public static final String CLAIMS_TENANT_INFORMATION = "tenantInformation";
    public static final String CLAIMS_LABEL = "label";
    public static final String CLAIMS_GROUP = "group";
    public static final String ISSUER = "de.ebf.mi.da";

    public static final String TOKEN_RETRIEVAL_ENDPOINT = "/auth/token";

    public static final String MISSING_CREDENTIALS = "MISSING_CREDENTIALS";
    public static final String TOKEN_SIGNING_ERROR_PLEASE_INFORM_EBF = "TOKEN_SIGNING_ERROR (Please inform EBF)";
    public static final String INVALID_CREDENTIALS_OR_USER_UNCONFIGURED = "INVALID_CREDENTIALS_OR_USER_UNCONFIGURED";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String HEADER_TENANT = "X-Tenant";
    public static final String EXPIRED_TOKEN = "EXPIRED_TOKEN";

}
