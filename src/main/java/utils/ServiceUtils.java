package utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public final class ServiceUtils {
	
	private ServiceUtils() {}
	
	public static List<UUID> parseCommaSeparatedUUID(String input) {
	    if (input == null || input.isEmpty()) {
	        return List.of();
	    }
	    List<UUID> list = Arrays.stream(input.split(","))
	                 .map(String::trim)
	                 .map(UUID::fromString)
	                 .collect(Collectors.toList());
	    
	    return list;
	}
}
