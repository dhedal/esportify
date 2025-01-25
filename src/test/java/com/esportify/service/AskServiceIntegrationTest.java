package com.esportify.service;

import com.esportify.dto.AskDTO;
import com.esportify.dto.AskRequest;
import com.esportify.dto.ProcessAskRequest;
import com.esportify.dto.Response;
import com.esportify.entity.Ask;
import com.esportify.entity.User;
import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;
import com.esportify.enumerations.UserStatus;
import com.esportify.repository.AskRepository;
import com.esportify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AskServiceIntegrationTest {

    @Autowired
    private AskService askService;

    @Autowired
    private AskRepository askRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;

    @BeforeEach
    public void setup() {
        askRepository.deleteAll();
        userRepository.deleteAll();

        this.author = new User();
        this.author.setPseudo("John Doe");
        this.author.setEmail("john.doe@example.com");
        this.author.setPassword("SecurePass123!");
        this.author.setStatus(UserStatus.PLAYER);
        this.author = this.userRepository.saveAndFlush(this.author);
    }

    @Test
    public void test_requestOrganizerStatus_NullRequestParameter_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.askService.requestOrganizerStatus(null, new Response(), this.author);
        });
        assertEquals("Le paramètre request ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_requestOrganizerStatus_NullResponseParameter_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.askService.requestOrganizerStatus(new AskRequest(), null, this.author);
        });
        assertEquals("Le paramètre response ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_requestOrganizerStatus_NullAuthorParameter_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.askService.requestOrganizerStatus(new AskRequest(), new Response(), null);
        });
        assertEquals("Le paramètre author ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_requestOrganizerStatus_FailsForUserAlreadyOrganizer() {
        List<String> tests = new ArrayList<>();

        this.author.setStatus(UserStatus.ORGANIZER);
        this.userRepository.save(this.author);

        AskRequest request = new AskRequest();
        request.setType(AskType.ASK_ORGANIZER);
        request.setMessage("Je veux devenir ORGANIZER.");
        Response response = this.askService.requestOrganizerStatus(request, new Response(), this.author);
        tests.add("Vous avez déjà les droits d'organisateur");
        this.checkResponse(response, tests);
        assertTrue(response.isOk());

        this.author.setStatus(UserStatus.PLAYER);
        this.userRepository.save(this.author);
    }

    @Test
    public void test_requestOrganizerStatus_FailsForDuplicateRequest() {
        List<String> tests = new ArrayList<>();

        AskRequest request1 = new AskRequest();
        request1.setType(AskType.ASK_ORGANIZER);
        request1.setMessage("Je veux devenir ORGANIZER.");
        Response response1 = this.askService.requestOrganizerStatus(request1, new Response(), author);
        assertTrue(response1.isOk());

        AskRequest request2 = new AskRequest();
        request2.setType(AskType.ASK_ORGANIZER);
        request2.setMessage("Encore une demande.");
        Response response2 = askService.requestOrganizerStatus(request2, new Response(), author);
        tests.add("Une demande de passage à ORGANIZER est déjà en attente.");
        this.checkResponse(response2, tests);
        assertFalse(response2.isOk());
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_acceptOrganizerRequest_NullRequestParameter_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.askService.acceptOrganizerRequest(null, new Response());
        });
        assertEquals("Le paramètre request ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_acceptOrganizerRequest_NullResponseParameter_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.askService.acceptOrganizerRequest(new ProcessAskRequest(), null);
        });
        assertEquals("Le paramètre response ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_acceptOrganizerRequest_AskNotExist() {
        List<String> tests = new ArrayList<>();
        ProcessAskRequest request = new ProcessAskRequest();
        request.setUuid(UUID.randomUUID().toString());
        request.setStatus(AskStatus.APPROVED);
        tests.add("La demande n'existe pas");
        Response reponse = this.checkResponse(this.askService.acceptOrganizerRequest(request, new Response()), tests);
        assertFalse(reponse.isOk());
    }

    @Test
    public void test_acceptOrganizerRequest_FailsForWrongAskType() {
        Ask ask = new Ask();
        ask.setType(AskType.SIMPLE);
        ask.setAuthor(this.author);
        ask.setMessage("Je veux devenir ORGANIZER.");
        ask.setStatus(AskStatus.PENDING);
        ask = this.askRepository.saveAndFlush(ask);

        List<String> tests = new ArrayList<>();
        ProcessAskRequest request = new ProcessAskRequest();
        request.setUuid(ask.getUuid());
        request.setStatus(AskStatus.APPROVED);
        tests.add("Cette demande ne concerne pas un passage au statut ORGANIZER.");
        Response reponse = this.checkResponse(this.askService.acceptOrganizerRequest(request, new Response()), tests);
        assertFalse(reponse.isOk());
    }

    @Test
    public void test_acceptOrganizerRequest_FailsForNonPendingStatus() {
        Ask ask = new Ask();
        ask.setType(AskType.ASK_ORGANIZER);
        ask.setAuthor(this.author);
        ask.setMessage("Je veux devenir ORGANIZER.");
        ask.setStatus(AskStatus.APPROVED);
        ask = this.askRepository.saveAndFlush(ask);

        List<String> tests = new ArrayList<>();
        ProcessAskRequest request = new ProcessAskRequest();
        request.setUuid(ask.getUuid());
        request.setStatus(AskStatus.APPROVED);
        tests.add("Cette demande a déjà été traitée.");
        Response reponse = this.checkResponse(this.askService.acceptOrganizerRequest(request, new Response()), tests);
        assertFalse(reponse.isOk());
    }


    @Test
    public void test_acceptOrganizerRequest_Success() {
        Ask ask = new Ask();
        ask.setType(AskType.ASK_ORGANIZER);
        ask.setAuthor(author);
        ask.setMessage("Je veux devenir ORGANIZER.");
        ask.setStatus(AskStatus.PENDING);
        ask = askRepository.saveAndFlush(ask);

        ProcessAskRequest processRequest = new ProcessAskRequest();
        processRequest.setUuid(ask.getUuid());
        processRequest.setStatus(AskStatus.APPROVED);
        processRequest.setAdminComment("Accepté par l'admin.");

        Response response = askService.acceptOrganizerRequest(processRequest, new Response());

        assertTrue(response.isOk());
        Ask updatedAsk = askRepository.findById(ask.getId()).orElse(null);
        assertNotNull(updatedAsk);
        assertEquals(AskStatus.APPROVED, updatedAsk.getStatus());
        assertEquals("Accepté par l'admin.", updatedAsk.getAdminComment());

        User updatedUser = userRepository.findById(author.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(UserStatus.ORGANIZER, updatedUser.getStatus());
    }

    @Test
    public void test_acceptOrganizerRequest_RejectOrganizerRequest() {
        Ask ask = new Ask();
        ask.setType(AskType.ASK_ORGANIZER);
        ask.setAuthor(author);
        ask.setMessage("Je veux devenir ORGANIZER.");
        ask.setStatus(AskStatus.PENDING);
        ask = askRepository.saveAndFlush(ask);


        ProcessAskRequest processRequest = new ProcessAskRequest();
        processRequest.setUuid(ask.getUuid());
        processRequest.setStatus(AskStatus.REJECTED);
        processRequest.setAdminComment("Refusé pour des raisons X.");

        Response response = askService.acceptOrganizerRequest(processRequest, new Response());


        assertTrue(response.isOk());
        Ask updatedAsk = askRepository.findById(ask.getId()).orElse(null);
        assertNotNull(updatedAsk);
        assertEquals(AskStatus.REJECTED, updatedAsk.getStatus());
        assertEquals("Refusé pour des raisons X.", updatedAsk.getAdminComment());


        User updatedUser = userRepository.findById(author.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(UserStatus.PLAYER, updatedUser.getStatus());
    }

    @Test
    public void test_getPendingAsksByType() {
        Ask ask1 = new Ask();
        ask1.setType(AskType.ASK_ORGANIZER);
        ask1.setAuthor(author);
        ask1.setMessage("Demande 1.");
        ask1.setStatus(AskStatus.PENDING);
        askRepository.saveAndFlush(ask1);

        Ask ask2 = new Ask();
        ask2.setType(AskType.ASK_ORGANIZER);
        ask2.setAuthor(author);
        ask2.setMessage("Demande 2.");
        ask2.setStatus(AskStatus.APPROVED);
        askRepository.saveAndFlush(ask2);

        List<AskDTO> pendingAsks = askService.getPendingAsksByType(AskType.ASK_ORGANIZER);

        assertEquals(1, pendingAsks.size());
        assertEquals(ask1.getUuid(), pendingAsks.get(0).getUuid());
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
