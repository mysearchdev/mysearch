/**

Copyright (C) 2022 MySearch.Dev contributors (dev@mysearch.dev) 
Copyright (C) 2022 Sergey Nechaev (serg.nechaev@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 

*/
package dev.mysearch.rest.endpont.server;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import dev.mysearch.rest.endpont.AbstractRestEndpoint;
import dev.mysearch.rest.endpont.MySearchException;
import dev.mysearch.rest.endpont.RestEndpointContext;
import lombok.Data;
import oshi.SystemInfo;

@Service
public class ServerInfoEndpoint extends AbstractRestEndpoint<ServerInfoEndpoint.ServerInfo> {

	@Autowired
	private BuildProperties buildProperties;
	
	@Autowired
	private Environment environment;
	
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
		String strTotal;
		String strAvailable;
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
		BuildProperties build;
	}
	
	@Override
	public ServerInfo service(RestEndpointContext ctx) throws MySearchException, Exception {

		var info = new ServerInfo();
		info.build = buildProperties;
		
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
		info.getMemory().setStrAvailable(FileUtils.byteCountToDisplaySize(info.getMemory().getAvailable()));
		info.getMemory().setStrTotal(FileUtils.byteCountToDisplaySize(info.getMemory().getTotal()));

		// CPU
		info.getCpu().setCpu64bit(si.getHardware().getProcessor().getProcessorIdentifier().isCpu64bit());
		info.getCpu().setLogicalProcessorCount(si.getHardware().getProcessor().getLogicalProcessorCount());
		info.getCpu()
				.setMicroarchitecture(si.getHardware().getProcessor().getProcessorIdentifier().getMicroarchitecture());
		info.getCpu().setPhysicalPackageCount(si.getHardware().getProcessor().getPhysicalPackageCount());
		info.getCpu().setPhysicalProcessorCount(si.getHardware().getProcessor().getPhysicalProcessorCount());

		return info;

	}

	@Override
	public String[] getSupportedHttpMethods() {
		return new String[] { HttpMethod.GET.name()};
	}

}
