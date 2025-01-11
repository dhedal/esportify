package com.esportify.service;

import com.esportify.repository.EventParticipantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventParticipantService {
    private static final Logger LOG = LoggerFactory.getLogger(EventParticipantService.class);

    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    public EventParticipantService(EventParticipantRepository eventParticipantRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
    }
}
