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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    /**
     *
     * @param type
     * @return
     */
    public List<AskDTO> getPendingAsksByType(AskType type) {
        LOG.debug("## getPendingAsksByType(AskType type)");
        if(type == null) return Collections.emptyList();
        return AskMapper.toDTOList(
                this.askRepository.findAllByTypeAndStatus(type, AskStatus.PENDING));
    }

    /**
     *
     * @param page
     * @param askType
     * @param askStatus
     * @param pageSize
     * @return
     */
    public AsksPageResponse getPageAsks(int page, AskType askType, AskStatus askStatus, int pageSize) {
        LOG.debug("## getPageAsks getPageAsks(int page, AskType askType, AskStatus askStatus, int pageSize) ");
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Ask> askPage;

        if(askType == null) askType = AskType.UNDEFINED;
        if(askStatus == null) askStatus = AskStatus.UNDEFINED;

        if(!Objects.equals(askType, AskType.UNDEFINED) && !Objects.equals(askStatus, AskStatus.UNDEFINED)) {
            askPage = this.askRepository.findByTypeAndStatus(askType, askStatus, pageable);
        }
        else if(!Objects.equals(askType, AskType.UNDEFINED)) {
            askPage = this.askRepository.findByType(askType, pageable);
        }
        else if(!Objects.equals(askStatus, AskStatus.UNDEFINED)) {
            askPage = this.askRepository.findByStatus(askStatus, pageable);
        }
        else {
            askPage = this.askRepository.findAll(pageable);
        }

        AsksPageResponse response = new AsksPageResponse();
        response.setTotalPages(askPage.getTotalPages());
        response.setAsks(AskMapper.toDTOList(askPage.getContent()));
        response.setOk(true);
        return response;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @param <T>
     * @throws RuntimeException
     */
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

    /**
     *
     * @param uuid
     * @return
     */
    public Ask findByUuid(String uuid) {
        LOG.debug("## findByUuid(String uuid)");
        return this.askRepository.findByUuid(uuid);
    }

    public Ask save(Ask ask) {
        LOG.debug("## save(Ask ask)");
        if(ask == null) throw new IllegalArgumentException("Ask ne doit pas être null !");
        return this.askRepository.save(ask);
    }
}
