package com.chensoul.sharedlib.util;

import static com.chensoul.sharedlib.util.StringPool.LOCAL_HOST;
import static com.chensoul.sharedlib.util.StringPool.LOCAL_IP4;
import static com.chensoul.sharedlib.util.StringPool.LOCAL_IP6;
import com.chensoul.sharedlib.util.lang.function.FunctionUtils;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 网络相关工具
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
public abstract class InetAddressUtils {
	private static final Pattern IP4_REGEXP = Pattern.compile("^((?:1?[1-9]?\\d|2(?:[0-4]\\d|5[0-5]))\\.){4}$");

	private static InetAddress localAddress;

	static {
		final List<InetAddress> localAddresses =
			loadInetAddress(address -> address.isSiteLocalAddress()
									   && !address.isLoopbackAddress()
									   && !address.getHostAddress().contains(":"));

		if (localAddresses.size() <= 0) {
			try {
				localAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// ignore
			}
		} else {
			localAddress = localAddresses.get(0);
		}
	}

	private InetAddressUtils() {
	}

	public static boolean isUnknown(String ipAddress) {
		return StringUtils.isBlank(ipAddress) || StringPool.UNKNOWN.equalsIgnoreCase(ipAddress);
	}

	/**
	 * <p>getReverseProxyIp.</p>
	 *
	 * @param ip a {@link String} object
	 * @return a {@link String} object
	 */
	public static String getReverseProxyIp(String ip) {
		if (ip != null) {
			String[] ips = ip.trim().split(",");
			for (String subIp : ips) {
				if (!isUnknown(subIp)) {
					ip = subIp;
					break;
				}
			}
		}
		return LOCAL_IP6.equals(ip) ? LOCAL_IP4 : ip;
	}

	/**
	 * <p>isInternalIp.</p>
	 *
	 * @param ipAddress a {@link String} object
	 * @return a boolean
	 */
	public static boolean isInternalIp(String ipAddress) {
		if (ipAddress.equals(LOCAL_IP4)) {
			return true;
		}
		String[] ipParts = ipAddress.split("\\.");

		if (ipParts.length != 4) {
			return false;
		}

		int firstPart = Integer.parseInt(ipParts[0]);
		int secondPart = Integer.parseInt(ipParts[1]);

		return firstPart == 10 ||
			   firstPart == 172 && secondPart >= 16 && secondPart <= 31 ||
			   firstPart == 192 && secondPart == 168;
	}

	/**
	 * Returns IP address as integer.
	 *
	 * @param ipAddress a {@link String} object
	 * @return a int
	 */
	public static int getIpAsInt(final String ipAddress) {
		int ipIntValue = 0;
		final String[] tokens = StringUtils.split(ipAddress, StringPool.DOT);
		for (final String token : tokens) {
			if (ipIntValue > 0) {
				ipIntValue <<= 8;
			}
			ipIntValue += Integer.parseInt(token);
		}
		return ipIntValue;
	}

	/**
	 * Checks given string against IP address v4 format.
	 *
	 * @param ipAddress an ip address - may be null
	 * @return <tt>true</tt> if param has a valid ip v4 format <tt>false</tt> otherwise
	 * @see <a href="https://en.wikipedia.org/wiki/IP_address#IPv4_addresses">ip address
	 * v4</a>
	 */
	public static boolean isIpv4(final String ipAddress) {
		final Matcher m = IP4_REGEXP.matcher(ipAddress + '.');
		return m.matches();
	}

	/**
	 * Resolves IP address from a hostname.
	 *
	 * @param hostname a {@link String} object
	 * @return a {@link InetAddress} object
	 */
	public static InetAddress getByHostname(final String hostname) {
		try {
			final InetAddress netAddress;
			if (hostname == null || hostname.equalsIgnoreCase(LOCAL_HOST)) {
				netAddress = InetAddress.getLocalHost();
			} else {
				netAddress = InetAddress.getByName(hostname);
			}
			return netAddress;
		} catch (final UnknownHostException ignore) {
			return null;
		}
	}

	/**
	 * <p>getByUrl.</p>
	 *
	 * @param urlAddr a {@link String} object
	 * @return a {@link InetAddress} object
	 */
	public static InetAddress getByUrl(final String urlAddr) {
		return FunctionUtils.tryApply((String u) -> {
			URL url = new URI(u).toURL();
			return InetAddress.getAllByName(url.getHost())[0];
		}, e -> {
			log.trace("Host name could not be determined automatically.", e);
			return null;
		}).apply(urlAddr);
	}

	/**
	 * 获取所有满足过滤条件的本地IP地址对象
	 *
	 * @param addressFilter 过滤器，null表示不过滤，获取所有地址
	 * @return 过滤后的地址对象列表
	 * @since 4.5.17
	 */
	public static List<InetAddress> loadInetAddress(Predicate<InetAddress> addressFilter) {
		final LinkedList<InetAddress> ipSet = new LinkedList<>();

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

			while (networkInterfaces.hasMoreElements()) {
				final NetworkInterface networkInterface = networkInterfaces.nextElement();
				final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					final InetAddress inetAddress = inetAddresses.nextElement();
					if (inetAddress != null && (null == addressFilter || addressFilter.test(inetAddress))) {
						ipSet.add(inetAddress);
					}
				}
			}
		} catch (SocketException e) {
			return ipSet;
		}
		return ipSet;
	}

	/**
	 * <p>getLocalhost.</p>
	 *
	 * @return a {@link InetAddress} object
	 */
	public static InetAddress getLocalhost() {
		return localAddress;
	}

	/**
	 * <p>getLocalHostAddress.</p>
	 *
	 * @return a {@link String} object
	 */
	public static String getLocalHostAddress() {
		if (null != localAddress) {
			return localAddress.getHostAddress();
		}
		return null;
	}

	/**
	 * <p>getLocalHostName.</p>
	 *
	 * @return a {@link String} object
	 */
	public static String getLocalHostName() {
		return FunctionUtils.tryGet(() -> {
			String hostName = localAddress.getHostName();
			int index = hostName.indexOf('.');
			if (index > 0) {
				return hostName.substring(0, index);
			}
			return hostName;
		}, throwable -> "unknown").get();
	}

}
