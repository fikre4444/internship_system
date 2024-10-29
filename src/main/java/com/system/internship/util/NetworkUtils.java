package com.system.internship.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetworkUtils {
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            Pattern localIpPattern = Pattern.compile("^(192\\.168\\.|10\\.10\\.).*"); // Matches IPs in the "10.10.*"
                                                                                      // range

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    String ipAddress = address.getHostAddress();

                    // Filter for non-loopback, site-local addresses matching 10.10.* pattern
                    if (!address.isLoopbackAddress() && address.isSiteLocalAddress()
                            && localIpPattern.matcher(ipAddress).matches()) {
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1"; // Fallback IP if no suitable local IP is found
    }
}
