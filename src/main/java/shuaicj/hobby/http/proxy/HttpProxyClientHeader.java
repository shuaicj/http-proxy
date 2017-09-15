package shuaicj.hobby.http.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.Getter;
import lombok.ToString;

/**
 * The http header of client.
 *
 * @author shuaicj 2017/09/15
 */
@ToString(of = {"method", "host", "port", "https"})
public class HttpProxyClientHeader {

    @Getter private String method;
    @Getter private String host;
    @Getter private int port;
    @Getter private boolean https;
    @Getter private byte[] bytes;

    private final InputStream in;
    private final ByteArrayOutputStream consumedBytes;

    private HttpProxyClientHeader(InputStream in) {
        this.in = in;
        this.consumedBytes = new ByteArrayOutputStream();
    }

    public static HttpProxyClientHeader parseFrom(InputStream in) throws IOException {
        HttpProxyClientHeader target = new HttpProxyClientHeader(in);
        target.init();
        return target;
    }

    private void init() throws IOException {
        method = readLine().split(" ")[0]; // the first word is http method name
        https = method.equalsIgnoreCase("CONNECT"); // method CONNECT means https
        for (String line = readLine(); line != null && !line.isEmpty(); line = readLine()) {
            if (line.startsWith("Host: ")) {
                String[] arr = line.split(":");
                host = arr[1].trim();
                try {
                    if (arr.length == 3) {
                        port = Integer.parseInt(arr[2]);
                    } else if (https) {
                        port = 443; // https
                    } else {
                        port = 80; // http
                    }
                } catch (NumberFormatException e) {
                    throw new IOException(e);
                }
            }
        }
        if (host == null || port == 0) {
            throw new IOException("cannot find header \'Host\'");
        }
        bytes = consumedBytes.toByteArray();
    }

    private String readLine() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int b = in.read(); b != -1; b = in.read()) {
            consumedBytes.write(b);
            builder.append((char) b);
            int len = builder.length();
            if (len >= 2 && builder.substring(len - 2).equals("\r\n")) {
                builder.delete(len - 2, len);
                return builder.toString();
            }
        }
        return builder.length() == 0 ? null : builder.toString();
    }
}
