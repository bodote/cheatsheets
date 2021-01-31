

package de.datev.timp.pkce.endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;

import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import de.datev.timp.application.security.ParsedCSRFToken;
import de.datev.timp.application.service.impl.AesGcmSecretService;
import de.datev.timp.pkce.PkceProperties;
import de.datev.timp.pkce.service.CookieService;
import de.datev.timp.pkce.service.PkceTokenRequestService;
import de.datev.timp.pkce.service.UserAgentStateService;
import de.datev.timp.pkce.util.TokenConstants;

@ExtendWith(MockitoExtension.class)
public class PkceTokenRestControllerTest {
    @Mock
    UserAgentStateService userAgentStateService;
    @Mock
    PkceProperties pkceProperties;
    @Mock
    CookieService cookieService;
    @Mock
    PkceTokenRequestService pkceTokenRequestService;
    @Mock
    BearerTokenValidator tokenValidator;
    @Mock
    BearerToken parsedToken;

    AesGcmSecretService aesGcmSecretService;

    PkceTokenRestController pcseTRC;

    final String csrfSessionStateSecret = "geheim";

    @BeforeEach
    public void init() {
        aesGcmSecretService = new AesGcmSecretService();
        pcseTRC = new PkceTokenRestController(pkceProperties, cookieService, pkceTokenRequestService, userAgentStateService,
                aesGcmSecretService, tokenValidator);
        ReflectionTestUtils.setField(pcseTRC, "csrfSessionStateSecret", csrfSessionStateSecret);
    }

    @Test
    public void testTokenFromCodeWithRedirect() throws IOException, GeneralSecurityException {
        // setup
        JWTClaimsSet jwtCS = new JWTClaimsSet.Builder().expirationTime(new Date()).claim("session_state", "test-session_state-value")
                .build();
        Mockito.doReturn(jwtCS).when(parsedToken).getClaims();

        Mockito.doReturn(parsedToken).when(tokenValidator).extract(null);

        PkceTokenRequestService.TokenResponse tokenResp = Mockito.mock(PkceTokenRequestService.TokenResponse.class);
        Mockito.doReturn(tokenResp).when(pkceTokenRequestService).requestToken("123", "123", "123");

        // PkceTokenRestController pcseTRC = new PkceTokenRestController(pkceProperties, cookieService, pkceTokenRequestService,
        // userAgentStateService);
        // pcseTRC.tokenValidator = tokenValidator;
        // ;
        // pcseTRC.csrfSessionStateSecret = "geheim";

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Map<String, Cookie> cookieMap = new HashMap<>();
        Mockito.doAnswer(invocation -> {
            Cookie cookie = invocation.getArgument(0);
            if (cookie != null) {
                cookieMap.put(cookie.getName(), cookie);
            }
            return null;
        }).when(response).addCookie(Mockito.any());

        // act
        pcseTRC.getTokenFromCodeWithRedirect("123", "123", "123", response);

        // assert
        assertTrue(cookieMap.containsKey(TokenConstants.CSRF_TOKEN_COOKIE_NAME));
        String cookieVal = cookieMap.get(TokenConstants.CSRF_TOKEN_COOKIE_NAME).getValue();
        String decoded = aesGcmSecretService.decrypt(cookieVal, csrfSessionStateSecret);
        ParsedCSRFToken parsedCsrfToken = new ObjectMapper().readValue(decoded, ParsedCSRFToken.class);
        assertEquals("test-session_state-value", parsedCsrfToken.getSessionState());
    }

}


package de.datev.timp.pkce.endpoint;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import de.datev.timp.application.security.ParsedCSRFToken;
import de.datev.timp.application.service.impl.AesGcmSecretService;
import de.datev.timp.application.util.EnvironmentConstants;
import de.datev.timp.pkce.PkceProperties;
import de.datev.timp.pkce.service.CookieService;
import de.datev.timp.pkce.service.PkceTokenRequestService;
import de.datev.timp.pkce.service.UserAgentStateService;
import de.datev.timp.pkce.util.TokenConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;

@Api(value = "OAUTH2 PKCE Token")
@RestController("OAUTH2 PKCE Token Controller")
@RequestMapping(path = "${pkce.token-endpoint:/oauth2/token}")
@Slf4j
public class PkceTokenRestController {

    private final PkceProperties pkceProperties;
    private final CookieService cookieService;
    private final PkceTokenRequestService pkceTokenRequestService;
    private final UserAgentStateService userAgentStateService;

    @Autowired
    private BearerTokenValidator tokenValidator;

    @Value("${" + EnvironmentConstants.CSRF_SESSIONSTATE_SECRET + "}")
    private String csrfSessionStateSecret;// TODO: make private again for JUnittest!

    @Autowired
    private AesGcmSecretService aesGcmSecretService;

    public PkceTokenRestController(PkceProperties pkceProperties, CookieService cookieService,
            PkceTokenRequestService pkceTokenRequestService, UserAgentStateService userAgentStateService,
            AesGcmSecretService aesGcmSecretService, BearerTokenValidator tokenValidator) {
        this.pkceProperties = pkceProperties;
        this.cookieService = cookieService;
        this.pkceTokenRequestService = pkceTokenRequestService;
        this.userAgentStateService = userAgentStateService;
        this.aesGcmSecretService = aesGcmSecretService;
        this.tokenValidator = tokenValidator;

    }

    // @formatter:off
    @ApiOperation(value = "Get token for code without provided redirect-uri", code = 302)
    @ApiResponses(value = {
        @ApiResponse(code = HttpServletResponse.SC_FOUND, message = "Redirect back to user agent", responseHeaders = {
            @ResponseHeader(name = "Location", response = String.class, description = "Url to origin/referer/redirectUri"),
            @ResponseHeader(name = "Set-Cookie", response = String.class, description =
                TokenConstants.ACCESS_TOKEN_COOKIE_NAME + "=<access-token-value>; Path=/; Secure; HttpOnly; Max-Age=<token's max-age value>"),
            // why i am using \0's?
            // look here: https://github.com/OAI/OpenAPI-Specification/issues/1237#issuecomment-423955715
            @ResponseHeader(name = "\0Set-Cookie", response = String.class, description =
                TokenConstants.REFRESH_TOKEN_COOKIE_NAME + "=<refresh-token-value>; Path=/; Secure; HttpOnly; Max-Age=<token's max-age value>"),
            @ResponseHeader(name = "\0\0Set-Cookie", response = String.class, description =
                TokenConstants.CODE_VERIFIER_COOKIE_NAME + "=null; Path=/; Secure; HttpOnly; Max-Age=0"),
            @ResponseHeader(name = "\0\0\0Set-Cookie", response = String.class, description =
                TokenConstants.USER_AGENT_STATE_COOKIE_NAME + "=null; Path=/; Secure; HttpOnly; Max-Age=0")
            }
        )
    })
    // @formatter:on
    @GetMapping(params = { TokenConstants.CODE_REQUEST_PARAMETER_NAME })
    public void getTokenFromCode(@RequestParam(TokenConstants.CODE_REQUEST_PARAMETER_NAME) String code,
            @CookieValue(name = TokenConstants.CODE_VERIFIER_COOKIE_NAME) String codeVerifier,
            @CookieValue(name = TokenConstants.USER_AGENT_STATE_COOKIE_NAME) String userAgentStateValue, HttpServletResponse response)
            throws IOException {
        try {
            UserAgentStateService.UserAgentState userAgentState = userAgentStateService.readUserAgentState(userAgentStateValue);

            Cookie deleteUserAgentState = userAgentStateService.deleteUserAgentStateCookie();
            response.addCookie(deleteUserAgentState);

            getTokenForCode(code, userAgentState.getRedirectUri(), userAgentState.getUserAgentPage(), codeVerifier, response);

        } catch (Exception e) {
            log.error("pkce exception: ", e);
            log.error("pkce exception with callstack: " + e.getStackTrace(), e);
            throw e;
        }
    }

    // @formatter:off
    @ApiOperation(value = "Get token for code with provided redirect-uri", code = 302)
    @ApiResponses(value = {
        @ApiResponse(code = HttpServletResponse.SC_FOUND, message = "Redirect to IDP login page", responseHeaders = {
            @ResponseHeader(name = "Location", response = String.class, description = "Url to user agent"),
            @ResponseHeader(name = "Set-Cookie", response = String.class, description =
                TokenConstants.ACCESS_TOKEN_COOKIE_NAME + "=<access-token-value>; Path=/; Secure; HttpOnly; Max-Age=<token's max-age value>"),
            // why i am using \0's?
            // look here: https://github.com/OAI/OpenAPI-Specification/issues/1237#issuecomment-423955715
            @ResponseHeader(name = "\0Set-Cookie", response = String.class, description =
                TokenConstants.REFRESH_TOKEN_COOKIE_NAME + "=<refresh-token-value>; Path=/; Secure; HttpOnly; Max-Age=<token's max-age value>"),
            @ResponseHeader(name = "\0\0Set-Cookie", response = String.class, description =
                TokenConstants.CODE_VERIFIER_COOKIE_NAME + "=null; Path=/; Secure; HttpOnly; Max-Age=0"),
            @ResponseHeader(name = "\0\0\0Set-Cookie", response = String.class, description =
                TokenConstants.USER_AGENT_STATE_COOKIE_NAME + "=null; Path=/; Secure; HttpOnly; Max-Age=0")
            }
        )
    })
    // @formatter:on
    @GetMapping(params = { TokenConstants.CODE_REQUEST_PARAMETER_NAME, TokenConstants.REDIRECT_URI_PARAM_NAME })
    public void getTokenFromCodeWithRedirect(@RequestParam(TokenConstants.CODE_REQUEST_PARAMETER_NAME) String code,
            @RequestParam(name = TokenConstants.REDIRECT_URI_PARAM_NAME) String redirectUri,
            @CookieValue(name = TokenConstants.CODE_VERIFIER_COOKIE_NAME) String codeVerifier, HttpServletResponse response)
            throws IOException {
        try {
            Cookie deleteUserAgentState = userAgentStateService.deleteUserAgentStateCookie();
            response.addCookie(deleteUserAgentState);

            getTokenForCode(code, redirectUri, redirectUri, codeVerifier, response);

        } catch (Exception e) {
            log.error("pkce exception: ", e);
            log.error("pkce exception with callstack: " + e.getStackTrace(), e);
            throw e;
        }
    }

    private void getTokenForCode(String code, String redirectUri, String originUri, String codeVerifier, HttpServletResponse response)
            throws IOException {
        PkceTokenRequestService.TokenResponse bearerToken = pkceTokenRequestService.requestToken(code, codeVerifier, redirectUri);

        response.addCookie(
                createTokenCookie(TokenConstants.ACCESS_TOKEN_COOKIE_NAME, bearerToken.getAccess_token(), bearerToken.getExpires_in()));
        response.addCookie(createTokenCookie(TokenConstants.REFRESH_TOKEN_COOKIE_NAME, bearerToken.getRefresh_token(),
                bearerToken.anyRefreshTokenExpireIn()));

        BearerToken parsedToken = tokenValidator.extract(bearerToken.getAccess_token());
        ParsedCSRFToken fromTokenResponse = new ParsedCSRFToken(parsedToken);
        Cookie cookie = new Cookie(TokenConstants.CSRF_TOKEN_COOKIE_NAME,
                fromTokenResponse.toCookieString(aesGcmSecretService, csrfSessionStateSecret));
        cookie.setPath("/");
        cookie.setMaxAge(bearerToken.getExpires_in().intValue());
        response.addCookie(cookie);

        response.addCookie(deleteCodeVerifierCookie());
        response.addCookie(deleteCodeVerifierCookieForDeprecatedEndpoint());

        response.sendRedirect(originUri);
    }

    // Cookie not deleted. they expire.
    private Cookie createTokenCookie(String name, String token, Long expiration) {
        return cookieService.creationCookie(name, token, "/", expiration.intValue());
    }

    private Cookie deleteCodeVerifierCookie() {
        return cookieService.deletionCookie(TokenConstants.CODE_VERIFIER_COOKIE_NAME, pkceProperties.getTokenEndpoint());
    }

    private Cookie deleteCodeVerifierCookieForDeprecatedEndpoint() {
        return cookieService.deletionCookie(TokenConstants.CODE_VERIFIER_COOKIE_NAME, pkceProperties.getAuthEndpoint());
    }
}