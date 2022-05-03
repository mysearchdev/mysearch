package dev.mysearch.rest.endpont.server;

import org.springframework.stereotype.Service;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Data;
import oshi.SystemInfo;

@Service
public class ServerInfoEndpoint extends AbstractRestEndpoint<ServerInfoEndpoint.ServerInfo> {

	@Data
	public static class Cpu {
		int physicalPackageCount;
		int physicalProcessorCount;
		int logicalProcessorCount;
		boolean cpu64bit;
		String microarchitecture;
	}

	@Data
	public static class Memory {
		long total;
		long available;
	}

	@Data
	public static class OsVersionInfo {

		String version;
		String codeName;
		String buildNumber;

	}

	@Data
	public static class Os {
		String manufacturer;
		int bitness;
		int processCount;
		long systemUptime;
		long systemBootTime;
		String family;

		OsVersionInfo versionInfo = new OsVersionInfo();
		OsNetworkParams networkParams = new OsNetworkParams();
	}

	@Data
	public static class OsNetworkParams {
		String domainName;
		String ipv4DefaultGateway;
		String ipv6DefaultGateway;
		String hostName;
		String[] dnsDrivers;
	}

	@Data
	public static class ServerInfo {
		int processId;
		Cpu cpu = new Cpu();
		Memory memory = new Memory();
		Os os = new Os();
	}

	@Override
	public ServerInfo service(HttpRequest req, QueryStringDecoder dec) throws Exception {

		var info = new ServerInfo();

		var si = new SystemInfo();

		// OS
		var os = si.getOperatingSystem();
		info.setProcessId(os.getProcessId());
		info.getOs().setManufacturer(os.getManufacturer());
		info.getOs().setBitness(os.getBitness());
		info.getOs().setProcessCount(os.getProcessCount());
		info.getOs().setSystemUptime(os.getSystemUptime());
		info.getOs().setSystemBootTime(os.getSystemBootTime());
		info.getOs().setFamily(os.getFamily());
		info.getOs().getVersionInfo().setBuildNumber(os.getVersionInfo().getBuildNumber());
		info.getOs().getVersionInfo().setCodeName(os.getVersionInfo().getCodeName());
		info.getOs().getVersionInfo().setVersion(os.getVersionInfo().getVersion());

		info.getOs().getNetworkParams().setDomainName(os.getNetworkParams().getDomainName());
		info.getOs().getNetworkParams().setIpv4DefaultGateway(os.getNetworkParams().getIpv4DefaultGateway());
		info.getOs().getNetworkParams().setIpv6DefaultGateway(os.getNetworkParams().getIpv6DefaultGateway());
		info.getOs().getNetworkParams().setHostName(os.getNetworkParams().getHostName());
		info.getOs().getNetworkParams().setDnsDrivers(os.getNetworkParams().getDnsServers());

		// Memory
		info.getMemory().setAvailable(si.getHardware().getMemory().getAvailable());
		info.getMemory().setTotal(si.getHardware().getMemory().getTotal());

		// CPU
		info.getCpu().setCpu64bit(si.getHardware().getProcessor().getProcessorIdentifier().isCpu64bit());
		info.getCpu().setLogicalProcessorCount(si.getHardware().getProcessor().getLogicalProcessorCount());
		info.getCpu()
				.setMicroarchitecture(si.getHardware().getProcessor().getProcessorIdentifier().getMicroarchitecture());
		info.getCpu().setPhysicalPackageCount(si.getHardware().getProcessor().getPhysicalPackageCount());
		info.getCpu().setPhysicalProcessorCount(si.getHardware().getProcessor().getPhysicalProcessorCount());

		return info;

	}

}
