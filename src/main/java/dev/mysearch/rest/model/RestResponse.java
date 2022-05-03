package dev.mysearch.rest.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@JsonInclude(Include.NON_NULL)
public class RestResponse<T> {

	private String uuid = StringUtils.substring(DigestUtils.md2Hex(UUID.randomUUID().toString()), 0, 10);

	private String date = ISO_8601_EXTENDED.format(System.currentTimeMillis());

	private String node = nodeIp;

	private Boolean error;

	private String errorMessage;

	private static String nodeIp;

	private T value;

	static {
		try {
			var ip = InetAddress.getLocalHost();
			nodeIp = StringUtils.substring(DigestUtils.md2Hex(ip.getHostName()), 10);
		} catch (UnknownHostException e) {
			log.error("Error: ", e);
		}
	}

	static final FastDateFormat ISO_8601_EXTENDED = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

	public static <T> RestResponse of(T obj) {
		var resp = new RestResponse<T>();
		resp.setValue(obj);
		return resp;
	}

}
