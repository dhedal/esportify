package com.esportify.service;


import com.esportify.dto.*;
import com.esportify.entity.Event;
import com.esportify.entity.User;
import com.esportify.enumerations.EventStatus;
import com.esportify.enumerations.UserStatus;
import com.esportify.repository.EventRepository;
import com.esportify.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
public class EventServiceIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(EventServiceIntegrationTest.class);

    private EventService eventService;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private User organizer;
    private Event event;
    private EventRequest request;

    @Autowired
    public EventServiceIntegrationTest(
            EventService eventService, UserRepository userRepository, EventRepository eventRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @BeforeEach
    public void createOrganizer() {
//        this.eventRepository.deleteAll();
//        this.userRepository.deleteAll();

        this.organizer = new User();
        this.organizer.setPseudo("dhedal");
        this.organizer.setEmail("dhedal@esportify.com");
        this.organizer.setPassword("StrongPassword!24");
        this.organizer.setStatus(UserStatus.ADMIN);
        this.organizer = this.userRepository.save(this.organizer);

        this.event = new Event();
        this.event.setTitle("Esport Tournament");
        this.event.setDescription("Un tournoi de jeu compétitif.");
        this.event.setMaxPlayers(100);
        this.event.setStartDateTime(LocalDateTime.now().plusHours(1)); // Débute dans 1h
        this.event.setEndDateTime(this.event.getStartDateTime().plusHours(3));
        this.event.setStatus(EventStatus.VALIDATED);
        this.event.setOrganizer(this.organizer);
        this.event = this.eventRepository.saveAndFlush(this.event);

    }

    @Test
    public void test_createEvent_NullRequest_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventService.createEvent(null, new Response(), this.organizer);
        });
        assertEquals("EventRequest ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_createEvent_NullResponse_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventService.createEvent(new EventRequest(), null, this.organizer);
        });
        assertEquals("Response ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_createEvent_NullOrganizer_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventService.createEvent(new EventRequest(), new Response(), null);
        });
        assertEquals("L'organisateur de l'événement est obligatoire", exception.getMessage());
    }

    @Test
    public void test_createEvent_UniqueTitle_ShouldFailOnDuplicate() {
        List<String> tests = new ArrayList<>();

        EventRequest request1 = new EventRequest();
        request1.setTitle("Tournoi FIFA");
        request1.setDescription("Compétition FIFA 2024");
        request1.setMaxPlayers(16);
        request1.setStartDateTime(LocalDateTime.now().plusDays(4));
        request1.setEndDateTime(request1.getStartDateTime().plusHours(3));

        tests.add("L'événement est en attente de validation !");
        Response response1 = this.checkResponse(
                this.eventService.createEvent(request1, new Response(), this.organizer), tests);
        assertTrue(response1.isOk());
        tests.clear();

        EventRequest request2 = new EventRequest();
        request2.setTitle("Tournoi FIFA");
        request2.setDescription("Compétition FIFA 2024");
        request2.setMaxPlayers(60);
        request2.setStartDateTime(request1.getStartDateTime());
        request2.setEndDateTime(request2.getStartDateTime().plusHours(3));

        tests.add("Ce titre est déja enregistré par un autre événement pour même date/heure que le votre");
        Response response2 = this.checkResponse(
                this.eventService.createEvent(request2, new Response(), this.organizer), tests);
        assertFalse(response2.isOk());
        tests.clear();

    }

    @Test
    public void test_createEvent_InvalidRequest() {
        List<String> tests = new ArrayList<>();

        this.resetEventRequest();
        this.request.setTitle(null);
        tests.add("Le titre est obligatoire");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setTitle("");
        tests.add("Le titre est obligatoire");
        tests.add("Le titre doit contenir entre 5 et 255 caractères");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setTitle("tour");
        tests.add("Le titre doit contenir entre 5 et 255 caractères");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setDescription(null);
        tests.add("La description est obligatoire");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setDescription("");
        tests.add("La description est obligatoire");
        tests.add("La description doit contenir entre 10 et 500 caractères");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setDescription("qsdfsfd");
        tests.add("La description doit contenir entre 10 et 500 caractères");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setMaxPlayers(1);
        tests.add("Il doit y avoir au moins 2 joueurs");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setMaxPlayers(1001);
        tests.add("Le nombre maximum de joueurs est 1000");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setStartDateTime(null);
        tests.add("La durée de l'événement doit être au minimum de 30 mininutes");
        tests.add("La date et l'heure de début est obligatoire");
        tests.add("L'événement doit être créer au minimum 3 jours avant");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setStartDateTime(LocalDateTime.now());
        tests.add("L'événement doit être créer au minimum 3 jours avant");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setEndDateTime(null);
        tests.add("La date et l'heure de fin est obligatoire");
        tests.add("La durée de l'événement doit être au minimum de 30 mininutes");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setEndDateTime(LocalDateTime.now());
        tests.add("La durée de l'événement doit être au minimum de 30 mininutes");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();

        this.resetEventRequest();
        this.request.setEndDateTime(this.request.getStartDateTime().plusMinutes(29));
        tests.add("La durée de l'événement doit être au minimum de 30 mininutes");
        this.checkResponse(this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        tests.clear();
    }

    @Test
    public void test_createEvent_success() {
        List<String> tests = new ArrayList<>();
        this.resetEventRequest();
        tests.add("L'événement est en attente de validation !");
        Response response = this.checkResponse(
                this.eventService.createEvent(this.request, new Response(), this.organizer), tests);
        assertTrue(response.isOk());

        List<EventDTO> list = this.eventService.listByOrganizer(this.organizer);
        assertNotNull(list);
        assertFalse(list.isEmpty());

        boolean check = false;
        for(EventDTO dto: list) {
            if( Objects.equals(this.request.getTitle(), dto.getTitle()) ||
                    Objects.equals(this.request.getDescription(), dto.getDescription()) ||
                    Objects.equals(this.request.getMaxPlayers(), dto.getMaxPlayers()) ||
                    Objects.equals(this.request.getStartDateTime(), dto.getStartDateTime()) ||
                    Objects.equals(this.request.getEndDateTime(), dto.getEndDateTime()) ||
                    Objects.equals(EventStatus.PENDING, dto.getStatus())
            ){
                check = true;
                break;
            }
        }
        assertTrue(check);
    }


    @Test
    public void test_getUpcomingAndOngoingEvents_ShouldReturnCorrectEvents() {
        this.eventRepository.saveAll(List.of(
            createEvent("Tournoi MOBA", "Compétition MOBA", 100, EventStatus.VALIDATED,
                    LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(3), organizer),
            createEvent("Finale FPS", "Grande finale FPS", 50, EventStatus.ON_GOING,
                    LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2), organizer),
            createEvent("Clash Royal", "Battle des meilleurs", 200, EventStatus.FULL,
                    LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(5), organizer)
        ));

        List<EventDTO> events = this.eventService.getUpcomingAndOngoingEvents();
        assertNotNull(events);
        assertTrue(3 <= events.size(), "Il doit y avoir au moin événements récupérés.");

        for (EventDTO event : events) {
            assertTrue(List.of(EventStatus.VALIDATED, EventStatus.ON_GOING, EventStatus.FULL).contains(event.getStatus()),
                    "L'événement doit être VALIDATED, ON_GOING ou FULL.");
        }
    }

    @Test
    public void test_updateEvent_Success() {
        // Création d'un événement
        this.resetEventRequest();
        Response createResponse = this.eventService.createEvent(request, new Response(), organizer);
        assertTrue(createResponse.isOk());

        Event event = this.eventRepository.findByTitle(request.getTitle()).get(0);
        assertNotNull(event);

        // Mise à jour
        UpdateEventRequest updateRequest = new UpdateEventRequest();
        updateRequest.setUuid(event.getUuid());
        updateRequest.setTitle("Tournoi mis à jour");
        updateRequest.setDescription("Nouvelle description");
        updateRequest.setMaxPlayers(200);
        updateRequest.setStartDateTime(event.getStartDateTime().plusDays(2));
        updateRequest.setEndDateTime(updateRequest.getStartDateTime().plusHours(4));

        Response updateResponse = this.eventService.updateEvent(updateRequest, new Response(), organizer);
        assertTrue(updateResponse.isOk());
        assertEquals("Mis à jour réussie !", updateResponse.getMessages().get(0));

        // Vérification en base
        Event updatedEvent = this.eventRepository.findByUuid(event.getUuid());
        assertNotNull(updatedEvent);
        assertEquals("Tournoi mis à jour", updatedEvent.getTitle());
        assertEquals("Nouvelle description", updatedEvent.getDescription());
        assertEquals(200, updatedEvent.getMaxPlayers());
    }


    @Test
    public void test_startEvent_NullRequest_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventService.startEvent(null, new Response());
        });
        assertEquals("UUIDRequest ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_startEvent_NullResponse_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.eventService.startEvent(new UUIDRequest(), null);
        });
        assertEquals("Response ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_startEvent_EventNotFound_ShouldFail() {
        UUIDRequest request = new UUIDRequest(UUID.randomUUID().toString());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("Cet événement est introuvable !"));
    }

    @Test
    public void test_startEvent_FailsIfEventPending() {
        this.event.setStatus(EventStatus.PENDING);
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("L'évenement ne peut pas démarrer car il est en attente de validation"));
    }

    @Test
    public void test_startEvent_FailsIfEventOngoing() {
        this.event.setStatus(EventStatus.ON_GOING);
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("L'évenement est déjà cours"));
    }

    @Test
    public void test_startEvent_FailsIfEventCancelled() {
        this.event.setStatus(EventStatus.CANCELLED);
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("L'évenement ne peut pas démarrer car il a été annulé"));
    }

    @Test
    public void test_startEvent_FailsIfEventClosed() {
        this.event.setStatus(EventStatus.CLOSED);
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("L'évenement ne peut pas démarrer car il a été cloturé"));
    }

    @Test
    public void test_startEvent_FailsTooEarly() {
        this.event.setStartDateTime(LocalDateTime.now().plusHours(2)); // Débute dans 2h
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("L'événement ne peut être démarré que 30 minutes avant son début."));
    }

    @Test
    public void test_startEvent_FailsIfEventAlreadyStarted() {
        this.event.setStartDateTime(LocalDateTime.now().minusMinutes(5)); // Déjà commencé
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertFalse(response.isOk());
        assertTrue(response.getMessages().contains("L'événement est déjà censé avoir commencé."));
    }

    @Test
    public void test_startEvent_Success() {
        this.event.setStartDateTime(LocalDateTime.now().plusMinutes(25)); // Démarrage autorisé (moins de 30 min)
        this.eventRepository.saveAndFlush(this.event);

        UUIDRequest request = new UUIDRequest(this.event.getUuid());
        Response response = this.eventService.startEvent(request, new Response());

        assertTrue(response.isOk());
        assertTrue(response.getMessages().contains("L'événement a bien été démarré."));

        Event updatedEvent = this.eventRepository.findByUuid(this.event.getUuid());
        assertNotNull(updatedEvent);
        assertEquals(EventStatus.ON_GOING, updatedEvent.getStatus());
    }


    private static Event createEvent( String title, String description, int maxPlayer,
                               EventStatus status, LocalDateTime start, LocalDateTime end, User organizer) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setMaxPlayers(maxPlayer);
        event.setStatus(status);
        event.setStartDateTime(start);
        event.setEndDateTime(end);
        event.setOrganizer(organizer);
        return event;
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

    private void resetEventRequest() {
        if(Objects.isNull(this.request)) this.request = new EventRequest();
        this.request.setTitle("Tournoi tekken 8");
        this.request.setDescription("c'est un test d'intégration pour la méthode createEvent");
        this.request.setMaxPlayers(100);
        this.request.setStartDateTime(LocalDateTime.now().plusDays(4));
        this.request.setEndDateTime(request.getStartDateTime().plusHours(3));
    }

}
