package com.esportify.service;


import com.esportify.dto.EventRequest;
import com.esportify.dto.Response;
import com.esportify.entity.User;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventServiceIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(EventServiceIntegrationTest.class);

    private EventService eventService;
    private UserRepository userRepository;
    private User organizer;
    private EventRequest request;

    @Autowired
    public EventServiceIntegrationTest(EventService eventService, UserRepository userRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @BeforeAll
    public void createOrganizer() {
        this.organizer = new User();
        this.organizer.setName("dhedal");
        this.organizer.setEmail("dhedal@esportify.com");
        this.organizer.setPassword("StrongPassword!24");
        this.organizer.setAdmin(true);
        this.organizer = this.userRepository.save(this.organizer);

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
    public void test_createEvent_InvalidRequest() {
        List<String> tests = new ArrayList<>();

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
        Response response = this.checkResponse(
                this.eventService.createEvent(this.request, new Response(), this.organizer), tests);

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

    public void resetEventRequest() {
        if(Objects.isNull(this.request)) this.request = new EventRequest();
        this.request.setDescription("c'est un test d'intégration pour la méthode createEvent");
        this.request.setMaxPlayers(100);
        this.request.setStartDateTime(LocalDateTime.now().plusDays(4));
        this.request.setEndDateTime(request.getStartDateTime().plusHours(3));
    }

}
