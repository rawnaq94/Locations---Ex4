package filters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.ScanInfo;
import models.WifiNetwork;

/**
 * Device name filter
 */
public class DeviceFilter implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5390500594907555209L;
	String str;

	public DeviceFilter(String str) {
		super();
		this.str = str;
	}

	@Override
	public Map<ScanInfo, List<WifiNetwork>> filter(Map<ScanInfo, List<WifiNetwork>> scans) {
		return scans.entrySet().stream().filter(p -> p.getKey().id.contains(str))
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	@Override
	public String toStr() {
		return "('" + str + "' in device)";
	}

}
