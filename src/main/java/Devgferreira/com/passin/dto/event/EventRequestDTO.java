package Devgferreira.com.passin.dto.event;

import java.util.StringTokenizer;

public record EventRequestDTO(String title, String details, Integer maximumAttendees) {
}
