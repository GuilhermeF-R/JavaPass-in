package Devgferreira.com.passin.services;

import Devgferreira.com.passin.domain.attendee.Attendee;
import Devgferreira.com.passin.domain.attendee.exceptions.AttendeeAlreadyRegisteredException;
import Devgferreira.com.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import Devgferreira.com.passin.domain.checkin.CheckIn;
import Devgferreira.com.passin.dto.attendee.AttendeeBadgeResponseDTO;
import Devgferreira.com.passin.dto.attendee.AttendeeDetails;
import Devgferreira.com.passin.dto.attendee.AttendeesListResponseDTO;
import Devgferreira.com.passin.dto.attendee.AttendeeBadgeDTO;
import Devgferreira.com.passin.repositories.AttendeeRepository;
import Devgferreira.com.passin.repositories.CheckinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final CheckInService checkInService;

    public List<Attendee> getAllAttendeesFromEvent(String eventId){
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetails> attendeeDetailsList = attendeeList.stream().map(attendee -> {
           Optional<CheckIn> checkIn = this.checkInService.getCheckIn(attendee.getId());
           LocalDateTime checkedInAt = checkIn.isPresent() ? checkIn.get().getCreatedAt() : null;
           return new AttendeeDetails(attendee.getId(), attendee.getEmail(), attendee.getName(), attendee.getCreatedAt(), checkedInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeeDetailsList);
    }

    public void verifyAttendeeSubscription(String email, String eventId){
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(email, eventId);
        if (isAttendeeRegistered.isPresent()) throw new AttendeeAlreadyRegisteredException("Attendee is already registered");
    }

    public Attendee registerAttendee(Attendee newAttendee){
        this.attendeeRepository.save(newAttendee);
        return newAttendee;

    }
    public void checkInAttendee(String attendeeId){
        Attendee attendee = this.getAttendee(attendeeId);
        this.checkInService.registerCheckIn(attendee);

    }

    private Attendee getAttendee(String attendeeId){
        return this.attendeeRepository.findById(attendeeId).orElseThrow(() -> new AttendeeNotFoundException("Attendee not found with ID" + attendeeId) );
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder){
        Attendee attendee = this.getAttendee(attendeeId);

        var uri = uriComponentsBuilder.path("/attendees/{attendeesId}/check-in").buildAndExpand(attendeeId).toUri().toString();

        AttendeeBadgeDTO attendeeBadgeDTO = new AttendeeBadgeDTO(attendee.getName(),attendee.getEmail(),uri,attendee.getEvent().getId());
        return new AttendeeBadgeResponseDTO(attendeeBadgeDTO);
    }
}
