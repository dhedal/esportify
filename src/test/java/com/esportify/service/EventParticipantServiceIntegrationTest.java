package com.esportify.service;

import com.esportify.dto.EventParticipantRequest;
import com.esportify.dto.Response;
import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.EventStatus;
import com.esportify.repository.EventParticipantRepository;
import com.esportify.repository.EventRepository;
import com.esportify.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventParticipantServiceIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantServiceIntegrationTest.class);

    private EventParticipantService eventParticipantService;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventParticipantRepository eventParticipantRepository;

    private User organizer;
    private Event event;
    private User participant, participant2, participant3;
    private EventParticipantRequest request;

    @Autowired
    public EventParticipantServiceIntegrationTest(
            EventParticipantService eventParticipantService,
            EventRepository eventRepository,
            UserRepository userRepository,
            EventParticipantRepository eventParticipantRepository
    ) {
        this.eventParticipantService = eventParticipantService;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    @BeforeAll
    public void initUserAndEvent() {
        this.organizer = new User();
        this.organizer.setName("dhedal");
        this.organizer.setEmail("dhedal@esportify.com");
        this.organizer.setPassword("StrongPassword!24");
        this.organizer.setAdmin(true);
        this.organizer = this.userRepository.save(this.organizer);

        this.participant = new User();
        this.participant.setName("Jane Doe");
        this.participant.setEmail("jane.doe@example.com");
        this.participant.setPassword("securePassword**123");
        this.participant = userRepository.save(this.participant);

        this.participant2 = new User();
        this.participant2.setName("John Doe");
        this.participant2.setEmail("john.doe@example.com");
        this.participant2.setPassword("securePassword**123");
        this.participant2 = userRepository.save(this.participant2);

        this.participant3 = new User();
        this.participant3.setName("Jin Kasama");
        this.participant3.setEmail("jinkasam@teken.com");
        this.participant3.setPassword("securePassword**123");
        this.participant3 = userRepository.save(this.participant3);

        this.event = new Event();
        this.event.setTitle("Tournoi de tekken 8");
        this.event.setDescription("Un description pour faire le test d'intégration d'EventParticipantService");
        this.event.setMaxPlayers(1);
        this.event.setStartDateTime(LocalDateTime.now().plusDays(4));
        this.event.setEndDateTime(this.event.getStartDateTime().plusHours(3));
        this.event.setStatus(EventStatus.VALIDATED);
        this.event.setOrganizer(this.organizer);
        this.event = this.eventRepository.save(this.event);

        this.request = new EventParticipantRequest();
        this.request.setUuid(this.event.getUuid());

    }

    @Test
    public void test_jointEvent_NullRequest_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventParticipantService.jointEvent(null, new Response(), this.participant);
        });
        assertEquals("EventParticipantRequest ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_jointEvent_NullResponse_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventParticipantService.jointEvent(this.request, null, this.participant);
        });
        assertEquals("Response ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_jointEvent_NullParticipant_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventParticipantService.jointEvent(this.request, new Response(), null);
        });
        assertEquals("Le participant de l'événement est obligatoire", exception.getMessage());
    }

    @Test
    public void test_jointEvent_InvalidRequest() {
        List<String> tests = new ArrayList<>();

        this.request.setUuid(null);
        tests.add("L'uuid est obligatoire");
        tests.add("L'uuid doit être valide");
        this.checkResponse(this.eventParticipantService.jointEvent(this.request, new Response(), this.participant), tests);
        tests.clear();

        this.request.setUuid("");
        tests.add("L'uuid est obligatoire");
        tests.add("L'uuid doit être valide");
        this.checkResponse(this.eventParticipantService.jointEvent(this.request, new Response(), this.participant), tests);
        tests.clear();

        this.request.setUuid("qsff-qfqf-qfqfqsf-qsfsf");
        tests.add("L'uuid doit être valide");
        this.checkResponse(this.eventParticipantService.jointEvent(this.request, new Response(), this.participant), tests);
        tests.clear();
    }

    @Test
    public void test_jointEvent_EventNotExist() {
        List<String> tests = new ArrayList<>();
        this.request.setUuid(UUID.randomUUID().toString());
        tests.add("Cet événement n'existe pas");
        this.checkResponse(this.eventParticipantService.jointEvent(this.request, new Response(), this.participant), tests);
        tests.clear();
    }

    @Test
    public void test_jointEvent_EventStatus_PENDING() {
        this.event.setStatus(EventStatus.PENDING);
        this.event = this.eventRepository.save(this.event);

        List<String> tests = new ArrayList<>();
        this.request.setUuid(this.event.getUuid());
        tests.add("Cet événement ne prend pas encore de participant");
        this.checkResponse(this.eventParticipantService.jointEvent(this.request, new Response(), this.participant), tests);
        tests.clear();
    }


    @Test
    public void test_jointEvent_EventStatus_FULL() {
        this.event.setStatus(EventStatus.VALIDATED);
        this.event.setMaxPlayers(1);
        this.event = this.eventRepository.save(this.event);

        List<String> tests = new ArrayList<>();
        this.request.setUuid(this.event.getUuid());

        Response response1 = this.eventParticipantService.jointEvent(this.request, new Response(), this.participant);
        this.checkResponse(response1, tests);
        assertTrue(response1.isOk());
        tests.clear();

        this.event = this.eventRepository.findWithParticipantsByUuid(this.event.getUuid());
        assertEquals(1, this.event.getParticipants().size());


        tests.add("Le nombre maximum de participants est déjà atteint");
        Response response2 = this.eventParticipantService.jointEvent(this.request, new Response(), this.participant2);
        assertFalse(response2.isOk());
        this.checkResponse(response2, tests);
        tests.clear();

        this.event = this.eventRepository.findWithParticipantsByUuid(this.event.getUuid());
        assertNotNull(this.event);
        assertEquals(this.event.getStatus(), EventStatus.FULL);
    }

    @Test
    public void  test_jointEvent_CannotRegisterTwiceForSameEvent() {
        this.event.setStatus(EventStatus.VALIDATED);
        this.event.setMaxPlayers(50);
        this.event = this.eventRepository.save(this.event);

        List<String> tests = new ArrayList<>();
        Response response1 = this.eventParticipantService.jointEvent(this.request, new Response(), this.participant3);
        this.checkResponse(response1, tests);
        assertTrue(response1.isOk());
        tests.clear();

        tests.add("Le participant est déjà inscrit à cet événement.");
        Response response2 = eventParticipantService.jointEvent(this.request, new Response(), this.participant3);
        this.checkResponse(response2, tests);
        assertFalse(response2.isOk());
        tests.clear();
    }

    @Test
    public void test_jointEvent_success() {
        this.event.setStatus(EventStatus.VALIDATED);
        this.event.setMaxPlayers(50);
        this.event = this.eventRepository.save(this.event);

        List<String> tests = new ArrayList<>();
        this.request.setUuid(this.event.getUuid());

        Response response = this.eventParticipantService.jointEvent(this.request, new Response(), this.participant2);
        this.checkResponse(response, tests);
        tests.clear();
        assertTrue(response.isOk());
    }

    private <T extends Response> T checkResponse(T response, List<String> tests) {
        assertNotNull(response);
        List<String> messages = response.getMessages();
        System.out.println();
        messages.forEach(System.out::println);
        assertTrue(messages.isEmpty() == tests.isEmpty());

        for(String test: tests) {
            assertTrue(messages.contains(test));
        }
        return response;
    }
}
