package Devgferreira.com.passin.services;

import Devgferreira.com.passin.domain.attendee.Attendee;
import Devgferreira.com.passin.domain.event.Event;
import Devgferreira.com.passin.domain.event.exceptions.EventIsFullException;
import Devgferreira.com.passin.domain.event.exceptions.EventNotFoundException;
import Devgferreira.com.passin.dto.attendee.AttendeeIdDTO;
import Devgferreira.com.passin.dto.attendee.AttendeeRequestDTO;
import Devgferreira.com.passin.dto.event.EventIdDTO;
import Devgferreira.com.passin.dto.event.EventRequestDTO;
import Devgferreira.com.passin.dto.event.EventResponseDTO;
import Devgferreira.com.passin.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId){
        Event event = this.getEventById(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);
        return new EventResponseDTO(event, attendeeList.size());
    }
    public EventIdDTO createEvent(EventRequestDTO eventDTO){
        Event newEvent = new Event();

        newEvent.setTitle(eventDTO.title());
        newEvent.setDetails(eventDTO.details());
        newEvent.setMaximumAttendees(eventDTO.maximumAttendees());
        newEvent.setSlug(this.createSlug(eventDTO.title()));

        this.eventRepository.save(newEvent);

        return new EventIdDTO(newEvent.getId());
    }

    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO attendeeRequestDTO){
        this.attendeeService.verifyAttendeeSubscription(attendeeRequestDTO.email(), eventId);


        Event event = this.getEventById(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        if (event.getMaximumAttendees() <= attendeeList.size()) throw new EventIsFullException("Event is Full");

        Attendee newAttendee = new Attendee();
        newAttendee.setName(attendeeRequestDTO.name());
        newAttendee.setEmail(attendeeRequestDTO.email());
        newAttendee.setEvent(event);
        newAttendee.setCreatedAt(LocalDateTime.now());
        this.attendeeService.registerAttendee(newAttendee);

        return new AttendeeIdDTO(newAttendee.getId());
    }

    private Event getEventById(String eventId){
        return this.eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not find with ID: " + eventId));


    }
    private String createSlug(String text){
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}", "")
                .replaceAll("[^\\w\\s]","")
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }
}
