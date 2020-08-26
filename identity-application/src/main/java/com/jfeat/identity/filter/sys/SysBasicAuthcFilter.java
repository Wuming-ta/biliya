/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.identity.filter.sys;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/** 提供给其它系统调用api时使用的认证filter
 * 检查http basic: username , password
 * 和allow ip list。如果对方系统所在ip不在allow ip list 里，则返回401
 * Created by jacky on 3/3/16.
 */
public class SysBasicAuthcFilter extends OncePerRequestFilter {
    /**
     * This class's private logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SysBasicAuthcFilter.class);

    /**
     * HTTP Authorization header, equal to <code>Authorization</code>
     */
    protected static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * HTTP Authentication header, equal to <code>WWW-Authenticate</code>
     */
    protected static final String AUTHENTICATE_HEADER = "WWW-Authenticate";

    /**
     * The name that is displayed during the challenge process of authentication, defauls to <code>application</code>
     * and can be overridden by the {@link #setApplicationName(String) setApplicationName} method.
     */
    private String applicationName = "application";

    /**
     * The authcScheme to look for in the <code>Authorization</code> header, defaults to <code>BASIC</code>
     */
    private String authcScheme = HttpServletRequest.BASIC_AUTH;

    /**
     * The authzScheme value to look for in the <code>Authorization</code> header, defaults to <code>BASIC</code>
     */
    private String authzScheme = HttpServletRequest.BASIC_AUTH;

    /**
     * Returns the name to use in the ServletResponse's <b><code>WWW-Authenticate</code></b> header.
     * <p/>
     * Per RFC 2617, this name name is displayed to the end user when they are asked to authenticate.  Unless overridden
     * by the {@link #setApplicationName(String) setApplicationName(String)} method, the default value is 'application'.
     * <p/>
     * Please see {@link #setApplicationName(String) setApplicationName(String)} for an example of how this functions.
     *
     * @return the name to use in the ServletResponse's 'WWW-Authenticate' header.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Sets the name to use in the ServletResponse's <b><code>WWW-Authenticate</code></b> header.
     * <p/>
     * Per RFC 2617, this name name is displayed to the end user when they are asked to authenticate.  Unless overridden
     * by this method, the default value is &quot;application&quot;
     * <p/>
     * For example, setting this property to the value <b><code>Awesome Webapp</code></b> will result in the
     * following header:
     * <p/>
     * <code>WWW-Authenticate: Basic realm=&quot;<b>Awesome Webapp</b>&quot;</code>
     * <p/>
     * Side note: As you can see from the header text, the HTTP Basic specification calls
     * this the authentication 'realm', but we call this the 'applicationName' instead to avoid confusion with
     * Shiro's Realm constructs.
     *
     * @param applicationName the name to use in the ServletResponse's 'WWW-Authenticate' header.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Returns the HTTP <b><code>Authorization</code></b> header value that this filter will respond to as indicating
     * a login request.
     * <p/>
     * Unless overridden by the {@link #setAuthzScheme(String) setAuthzScheme(String)} method, the
     * default value is <code>BASIC</code>.
     *
     * @return the Http 'Authorization' header value that this filter will respond to as indicating a login request
     */
    public String getAuthzScheme() {
        return authzScheme;
    }

    /**
     * Sets the HTTP <b><code>Authorization</code></b> header value that this filter will respond to as indicating a
     * login request.
     * <p/>
     * Unless overridden by this method, the default value is <code>BASIC</code>
     *
     * @param authzScheme the HTTP <code>Authorization</code> header value that this filter will respond to as
     *                    indicating a login request.
     */
    public void setAuthzScheme(String authzScheme) {
        this.authzScheme = authzScheme;
    }

    /**
     * Returns the HTTP <b><code>WWW-Authenticate</code></b> header scheme that this filter will use when sending
     * the HTTP Basic challenge response.  The default value is <code>BASIC</code>.
     *
     * @return the HTTP <code>WWW-Authenticate</code> header scheme that this filter will use when sending the HTTP
     *         Basic challenge response.
     * @see #sendChallenge
     */
    public String getAuthcScheme() {
        return authcScheme;
    }

    /**
     * Sets the HTTP <b><code>WWW-Authenticate</code></b> header scheme that this filter will use when sending the
     * HTTP Basic challenge response.  The default value is <code>BASIC</code>.
     *
     * @param authcScheme the HTTP <code>WWW-Authenticate</code> header scheme that this filter will use when
     *                    sending the Http Basic challenge response.
     * @see #sendChallenge
     */
    public void setAuthcScheme(String authcScheme) {
        this.authcScheme = authcScheme;
    }


    private SysRealm realm;

    public SysRealm getRealm() {
        return realm;
    }

    public void setRealm(SysRealm realm) {
        this.realm = realm;
    }

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordToken token = (UsernamePasswordToken) createToken(servletRequest, servletResponse);
        realm.init();

        String clientIp = getIpAddr((HttpServletRequest) servletRequest);
        if (log.isDebugEnabled()) {
            log.debug("client is {}", clientIp);
            for (String ip : realm.getAllowIps()) {
                log.debug("allow ip is {}", ip);
            }
        }

        boolean allowAccess = false;
        for (String ip : realm.getAllowIps()) {
            if (clientIp.equals(ip)) {
                allowAccess = true;
                break;
            }
        }
        if (realm.getAllowIps().size() > 0 && !allowAccess) {
            sendChallenge(servletRequest, servletResponse);
            return;
        }

        String userName = token.getUsername();
        char[] password = token.getPassword();
        if (userName != null && realm.getUserName() != null
                && userName.equals(realm.getUserName())
                && realm.getPassword() != null
                && Arrays.equals(realm.getPassword().toCharArray(), password)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            sendChallenge(servletRequest, servletResponse);
        }
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (null != ip && !"".equals(ip.trim())
                && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (null != ip && !"".equals(ip.trim())
                && !"unknown".equalsIgnoreCase(ip)) {
            // get first ip from proxy ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * Builds the challenge for authorization by setting a HTTP <code>401</code> (Unauthorized) status as well as the
     * response's {@link #AUTHENTICATE_HEADER AUTHENTICATE_HEADER}.
     * <p/>
     * The header value constructed is equal to:
     * <p/>
     * <code>{@link #getAuthcScheme() getAuthcScheme()} + " realm=\"" + {@link #getApplicationName() getApplicationName()} + "\"";</code>
     *
     * @param request  incoming ServletRequest, ignored by this implementation
     * @param response outgoing ServletResponse
     * @return false - this sends the challenge to be sent back
     */
    protected boolean sendChallenge(ServletRequest request, ServletResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication required: sending 401 Authentication challenge response.");
        }
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String authcHeader = getAuthcScheme() + " realm=\"" + getApplicationName() + "\"";
        httpResponse.setHeader(AUTHENTICATE_HEADER, authcHeader);
        return false;
    }

    /**
     * Returns the {@link #AUTHORIZATION_HEADER AUTHORIZATION_HEADER} from the specified ServletRequest.
     * <p/>
     * This implementation merely casts the request to an <code>HttpServletRequest</code> and returns the header:
     * <p/>
     * <code>HttpServletRequest httpRequest = {@link WebUtils#toHttp(ServletRequest) toHttp(reaquest)};<br/>
     * return httpRequest.getHeader({@link #AUTHORIZATION_HEADER AUTHORIZATION_HEADER});</code>
     *
     * @param request the incoming <code>ServletRequest</code>
     * @return the <code>Authorization</code> header's value.
     */
    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(AUTHORIZATION_HEADER);
    }

    /**
     * Creates an AuthenticationToken for use during login attempt with the provided credentials in the http header.
     * <p/>
     * This implementation:
     * <ol><li>acquires the username and password based on the request's
     * {@link #getAuthzHeader(ServletRequest) authorization header} via the
     * {@link #getPrincipalsAndCredentials(String, ServletRequest) getPrincipalsAndCredentials} method</li>
     * <li>The return value of that method is converted to an <code>AuthenticationToken</code> via the
     * {@link #createToken(String, String, ServletRequest, ServletResponse) createToken} method</li>
     * <li>The created <code>AuthenticationToken</code> is returned.</li>
     * </ol>
     *
     * @param request  incoming ServletRequest
     * @param response outgoing ServletResponse
     * @return the AuthenticationToken used to execute the login attempt
     */
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String authorizationHeader = getAuthzHeader(request);
        if (authorizationHeader == null || authorizationHeader.length() == 0) {
            // Create an empty authentication token since there is no
            // Authorization header.
            return createToken("", "", request, response);
        }

        if (log.isDebugEnabled()) {
            log.debug("Attempting to execute login with headers [" + authorizationHeader + "]");
        }

        String[] prinCred = getPrincipalsAndCredentials(authorizationHeader, request);
        if (prinCred == null || prinCred.length < 2) {
            // Create an authentication token with an empty password,
            // since one hasn't been provided in the request.
            String username = prinCred == null || prinCred.length == 0 ? "" : prinCred[0];
            return createToken(username, "", request, response);
        }

        String username = prinCred[0];
        String password = prinCred[1];

        return createToken(username, password, request, response);
    }

    protected AuthenticationToken createToken(String username, String password,
                                              ServletRequest request, ServletResponse response) {
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        return createToken(username, password, rememberMe, host);
    }

    protected AuthenticationToken createToken(String username, String password,
                                              boolean rememberMe, String host) {
        return new UsernamePasswordToken(username, password, rememberMe, host);
    }

    /**
     * Returns the host name or IP associated with the current subject.  This method is primarily provided for use
     * during construction of an <code>AuthenticationToken</code>.
     * <p/>
     * The default implementation merely returns {@link ServletRequest#getRemoteHost()}.
     *
     * @param request the incoming ServletRequest
     * @return the <code>InetAddress</code> to associate with the login attempt.
     */
    protected String getHost(ServletRequest request) {
        return request.getRemoteAddr();
    }

    /**
     * Returns <code>true</code> if &quot;rememberMe&quot; should be enabled for the login attempt associated with the
     * current <code>request</code>, <code>false</code> otherwise.
     * <p/>
     * This implementation always returns <code>false</code> and is provided as a template hook to subclasses that
     * support <code>rememberMe</code> logins and wish to determine <code>rememberMe</code> in a custom mannner
     * based on the current <code>request</code>.
     *
     * @param request the incoming ServletRequest
     * @return <code>true</code> if &quot;rememberMe&quot; should be enabled for the login attempt associated with the
     *         current <code>request</code>, <code>false</code> otherwise.
     */
    protected boolean isRememberMe(ServletRequest request) {
        return false;
    }

    /**
     * Returns the username obtained from the
     * {@link #getAuthzHeader(ServletRequest) authorizationHeader}.
     * <p/>
     * Once the {@code authzHeader} is split per the RFC (based on the space character ' '), the resulting split tokens
     * are translated into the username/password pair by the
     * {@link #getPrincipalsAndCredentials(String, String) getPrincipalsAndCredentials(scheme,encoded)} method.
     *
     * @param authorizationHeader the authorization header obtained from the request.
     * @param request             the incoming ServletRequest
     * @return the username (index 0)/password pair (index 1) submitted by the user for the given header value and request.
     * @see #getAuthzHeader(ServletRequest)
     */
    protected String[] getPrincipalsAndCredentials(String authorizationHeader, ServletRequest request) {
        if (authorizationHeader == null) {
            return null;
        }
        String[] authTokens = authorizationHeader.split(" ");
        if (authTokens == null || authTokens.length < 2) {
            return null;
        }
        return getPrincipalsAndCredentials(authTokens[0], authTokens[1]);
    }

    /**
     * Returns the username and password pair based on the specified <code>encoded</code> String obtained from
     * the request's authorization header.
     * <p/>
     * Per RFC 2617, the default implementation first Base64 decodes the string and then splits the resulting decoded
     * string into two based on the ":" character.  That is:
     * <p/>
     * <code>String decoded = Base64.decodeToString(encoded);<br/>
     * return decoded.split(":");</code>
     *
     * @param scheme  the {@link #getAuthcScheme() authcScheme} found in the request
     *                {@link #getAuthzHeader(ServletRequest) authzHeader}.  It is ignored by this implementation,
     *                but available to overriding implementations should they find it useful.
     * @param encoded the Base64-encoded username:password value found after the scheme in the header
     * @return the username (index 0)/password (index 1) pair obtained from the encoded header data.
     */
    protected String[] getPrincipalsAndCredentials(String scheme, String encoded) {
        String decoded = Base64.decodeToString(encoded);
        return decoded.split(":", 2);
    }
}
