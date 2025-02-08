package com.esportify.service;

import com.esportify.dto.*;
import com.esportify.entity.Ask;
import com.esportify.entity.User;
import com.esportify.enumerations.AskStatus;
import com.esportify.enumerations.AskType;
import com.esportify.enumerations.UserStatus;
import com.esportify.mapper.AskMapper;
import com.esportify.repository.AskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class AskService {
    private static final Logger LOG = LoggerFactory.getLogger(AskService.class);

    private AskRepository askRepository;
    private Validator validator;
    private UserService userService;

    @Autowired
    public AskService(AskRepository askRepository,
                      Validator validator,
                      UserService userService) {
        this.askRepository = askRepository;
        this.validator = validator;
        this.userService = userService;
    }

    /**
     *
     * @param request
     * @param response
     * @param author
     * @return
     */
    public Response requestOrganizerStatus(AskRequest request, Response response, User author) {
        LOG.debug("## requestOrganizerStatus(AskRequest request, Response response)");
        if (Objects.isNull(author)) {
            throw new IllegalArgumentException("Le paramètre author ne doit pas être null");
        }

        if (!validateRequest(request, response)) {
            return response;
        }

        if(request.getType() == AskType.ASK_ORGANIZER &&
                (author.getStatus() == UserStatus.ORGANIZER || author.getStatus() == UserStatus.ADMIN)) {
            response.addMessage("Vous avez déjà les droits d'organisateur");
            response.setOk(true);
            return response;
        }

        boolean existingAsk = this.askRepository.findAllByTypeAndStatus(AskType.ASK_ORGANIZER, AskStatus.PENDING)
                .stream().anyMatch(a -> a.getAuthor().equals(author));

        if (existingAsk) {
            response.addMessage("Une demande de passage à ORGANIZER est déjà en attente.");
            return response;
        }

        Ask ask = new Ask();
        ask.setType(request.getType());
        ask.setAuthor(author);
        ask.setMessage(request.getMessage());
        ask.setStatus(AskStatus.PENDING);

        ask = this.askRepository.save(ask);
        response.setOk(true);

        return response;
    }

    @Transactional
    public Response acceptOrganizerRequest(ProcessAskRequest request, Response response) {
        LOG.debug("## acceptOrganizerRequest(ProcessAskRequest request, Response response)");

        if (!this.validateRequest(request, response)) {
            return response;
        }

        Ask ask = this.askRepository.findByUuid(request.getUuid());
        if(ask == null) {
            response.addMessage("La demande n'existe pas");
            return response;
        }

        if (ask.getType() != AskType.ASK_ORGANIZER) {
            response.addMessage("Cette demande ne concerne pas un passage au statut ORGANIZER.");
            return response;
        }

        if (ask.getStatus() != AskStatus.PENDING) {
            response.addMessage("Cette demande a déjà été traitée.");
            return response;
        }

        User author = ask.getAuthor();
        if(author == null) {
            response.addMessage("L'auteur de cette demande n'existe pas");
            return response;
        }

        if (request.getStatus() == AskStatus.APPROVED) {
            if (author.getStatus() == UserStatus.ORGANIZER) {
                response.addMessage("Cet utilisateur est déjà ORGANIZER.");
                return response;
            }
            author.setStatus(UserStatus.ORGANIZER);
            this.userService.save(author);
        }

        ask.setStatus(request.getStatus());
        ask.setAdminComment(request.getAdminComment());
        this.askRepository.save(ask);

        response.setOk(true);
        return response;
    }

    /**
     *
     * @return
     */
    public List<AskDTO> getPendingAsksByType(AskType type) {
        LOG.debug("## getPendingAsksByType(AskType type)");
        if(type == null) return Collections.emptyList();
        return AskMapper.toDTOList(
                this.askRepository.findAllByTypeAndStatus(type, AskStatus.PENDING));
    }

    private <T> boolean validateRequest(T request, Response response) throws RuntimeException{
        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("Le paramètre request ne doit pas être null");
        }
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Le paramètre response ne doit pas être null");
        }
        Set<ConstraintViolation<T>> violations = this.validator.validate(request);
        if(!violations.isEmpty()) {
            violations.forEach(violation -> response.addMessage(violation.getMessage()));
            return false;
        }
        return true;
    }

}
