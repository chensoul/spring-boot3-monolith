/*
 * The MIT License
 *
 *  Copyright (c) 2021, wesine.com.cn
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.chensoul.sharedlib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串常量
 *
 * @author zhijun.chen
 * @since 0.0.1
 */
public final class StringPool {
	public static final String AMPERSAND = "&";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String AT = "@";
	public static final String BACK_SLASH = "\\";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String CHINA_COMMA = "，";
	public static final String DASH = "-";
	public static final String DASH_DASH = "--";
	public static final String DOLLAR = "$";
	public static final String DOT = ".";
	public static final String DOT_DOT = "..";
	public static final String DOT_CLASS = ".class";
	public static final String DOT_JAVA = ".java";
	public static final String DOT_XML = ".xml";
	public static final String EMPTY = "";
	public static final String EQUAL = "=";
	public static final String STAR = "*";
	public static final String SLASH = "/";
	public static final String HASH = "#";
	public static final String HAT = "^";
	public static final String LEFT_BRACE = "{";
	public static final String RIGHT_BRACE = "}";
	public static final String WHOLE_BRACE = "{}";
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String WHOLE_BRACKET = "()";
	public static final String LEFT_SQ_BRACKET = "[";
	public static final String RIGHT_SQ_BRACKET = "]";
	public static final String WHOLE_SQ_BRACKET = "[]";
	public static final String DOLLAR_LEFT_BRACE = "${";
	public static final String HASH_LEFT_BRACE = "#{";
	public static final String NEWLINE = "\n";
	public static final String CRLF = "\r\n";
	public static final String NULL = "null";
	public static final String OFF = "off";
	public static final String ON = "on";
	public static final String PERCENT = "%";
	public static final String PIPE = "|";
	public static final String PLUS = "+";
	public static final String QUESTION_MARK = "?";
	public static final String EXCLAMATION_MARK = "!";
	public static final String QUOTE = "\"";
	public static final String RETURN = "\r";
	public static final String TAB = "\t";
	public static final String RIGHT_CHEV = ">";
	public static final String LEFT_CHEV = "<";
	public static final String SEMICOLON = ";";
	public static final String SINGLE_QUOTE = "'";
	public static final String BACKTICK = "`";
	public static final String SPACE = " ";
	public static final String TILDA = "~";
	public static final String UNDERSCORE = "_";
	public static final String UTF_8 = "UTF-8";
	public static final String US_ASCII = "US-ASCII";
	public static final String ISO_8859_1 = "ISO-8859-1";
	public static final String GBK = "GBK";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String NA = "NA";
	public static final String Y = "y";
	public static final String N = "n";
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String SHI = "是";
	public static final String FOU = "否";
	public static final String ONE = "1";
	public static final String ZERO = "0";
	public static final String GET = "get";
	public static final String IS = "is";
	public static final String UP = "UP";
	public static final String DOWN = "DOWN";
	public static final String HTML_NBSP = "&nbsp;";
	public static final String HTML_AMP = "&amp";
	public static final String HTML_QUOTE = "&quot;";
	public static final String HTML_LT = "&lt;";
	public static final String HTML_GT = "&gt;";
	public static final String ENABLED = "enabled";
	public static final String UNKNOWN = "unknown";
	public static final String[] EMPTY_ARRAY = new String[0];
	public static final byte[] BYTES_NEW_LINE = NEWLINE.getBytes();
	public static final String DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String LOCAL_HOST = "localhost";
	public static final String LOCAL_IP4 = "127.0.0.1";
	public static final String LOCAL_IP6 = "0:0:0:0:0:0:0:1";
	public static final String HTTP = "http://";
	public static final String HEALTH_ENDPOINT = "/actuator/health";
	public static final String INFO_ENDPOINT = "/actuator/info";
	public static final String RESPONSE_TIME_HEADER = "X-Response-Time";
	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";
	public static final String BASIC = "Basic ";
	public static final String CLIENT_ID_HEADER = "clientId";
	public static final String CONTEXT_ID_HEADER = "contextId";
	public static final String TENANT_ID_HEADER = "tenantId";
	public static final String ID_TOKEN = "id_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String PRINCIPAL = "Principal";
	public static final String USERNAME = "username";
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String OAUTH_GRANT_TYPE = "grant_type";
	public static final String OAUTH_CLIENT_CREDENTIALS = "client_credentials";
	public static final String OAUTH_PWD_GRANT_TYPE = "password";
	public static final String OAUTH_CLIENT_ID = "client_id";
	public static final String OAUTH_CLIENT_SECRET = "client_secret";
	public static final String OAUTH_CLIENT_SCOPE = "scope";
	public static final String EXTRA_ATTRIBUTES = "extraAttributes";
	public static final String OAUTH_RESOURCE = "resource";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String REFRESH_TOKEN_HEADER = "refreshToken";
	public static final String REFRESH_TOKEN_HEADER_RESPONSE = "Refresh-Token";
	public static final String[] DEFAULT_IGNORE_URLS_ARRAY = {
		"/v3/api-docs",
		"swagger",
		"/swagger/**",
		"/swagger-resources/**",
		"/swagger-ui/**",
		"/swagger-ui.html**",
		"/webjars/**",
		"/favicon.ico",
		"/actuator/health",
		"/actuator/info",
		"/resource",
		"/error"};
	public static final List<String> DEFAULT_IGNORE_URLS_LIST = new ArrayList<>(Arrays.asList(DEFAULT_IGNORE_URLS_ARRAY));
	// @formatter:off

	public static final char COMMA_CHAR = ',';
	/**
	 * The space char : ' '
	 */
	public static final char SPACE_CHAR = ' ';
	/**
	 * The exclamation char : '!'
	 */
	public static final char EXCLAMATION_CHAR = '!';
	/**
	 * The exclamation char : '"'
	 */
	public static final char DOUBLE_QUOTATION_CHAR = '"';
	/**
	 * The dollar char : '$'
	 */
	public static final char DOLLAR_CHAR = '$';
	/**
	 * The dot char : '.'
	 */
	public static final 	char DOT_CHAR = '.';
	/**
	 * The and char : '&'
	 */
	public static final char AND_CHAR = '&';
	/**
	 * The equal char : '.'
	 */
	public static final char EQUAL_CHAR = '=';
	/**
	 * The less than char : '<'
	 */
	public static final char LESS_THAN_CHAR = '<';
	/**
	 * The greater than char : '>'
	 */
	public static final 	char GREATER_THAN_CHAR = '>';
	/**
	 * The colon char : ':'
	 */
	public static final 	char COLON_CHAR = ':';
	/**
	 * The semicolon char : ';'
	 */
	public static final 	char SEMICOLON_CHAR = ';';
	/**
	 * The sharp char : '#'
	 */
	public static final char SHARP_CHAR = '#';
	/**
	 * The question mark char : '?'
	 */
	public static final 	char QUESTION_MARK_CHAR = '?';
	/**
	 * The query string char : '?'
	 */
	public static final char QUERY_STRING_CHAR = QUESTION_MARK_CHAR;
	/**
	 * The left parenthesis char : '('
	 */
	public static final 	char LEFT_PARENTHESIS_CHAR = '(';
	/**
	 * The right parenthesis char : '('
	 */
	public static final 	char RIGHT_PARENTHESIS_CHAR = ')';
	/** Constant <code>LEFT_BRACE_CHAR='{'</code> */
	public static final 	char LEFT_BRACE_CHAR = '{';
	/** Constant <code>RIGHT_BRACE_CHAR='}'</code> */
	public static final 	char RIGHT_BRACE_CHAR = '}';
	/** Constant <code>LEFT_SQ_BRACKET_CHAR='['</code> */
	public static final 	char LEFT_SQ_BRACKET_CHAR = '[';
	/** Constant <code>RIGHT_SQ_BRACKET_CHAR=']'</code> */
	public static final 	char RIGHT_SQ_BRACKET_CHAR = ']';
	/**
	 * The underscore char : '_'
	 */
	public static final 	char UNDER_SCORE_CHAR = '_';
	/**
	 * The DASH char : '-'
	 */
	public static final 	char DASH_CHAR = '-';
	/**
	 * The star char : '*'
	 */
	public static final 	char STAR_CHAR = '*';
	/**
	 * The hat char : '^'
	 */
	public static final 	char HAT_CHAR = '^';
	/**
	 * The pipe char : '|'
	 */
	public static final 	char PIPE_CHAR = '|';
	/**
	 * The plus char : '+'
	 */
	public static final 	char PLUS_CHAR = '+';
	/**
	 * The backtick char : '`'
	 */
	public static final 	char BACKTICK_CHAR = '`';
	/**
	 * The tilde char : '~'
	 */
	public static final 	char TILDA_CHAR = '~';
	/**
	 * The percent char : '%'
	 */
	public static final 	char PERCENT_CHAR = '%';

	private StringPool() {
	}
	// @formatter:on

}
