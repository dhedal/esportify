package com.esportify.dto;

import com.esportify.validation.ValidEventDuration;
import com.esportify.validation.ValidStartDate;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@ValidEventDuration(message = "La durée de l'événement doit être au minimum de 30 mininutes")
public class EventRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 255, message = "Le titre doit contenir entre 5 et 255 caractères")
    private String title;
    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
    private String description;
    @Min(value = 2, message = "Il doit y avoir au moins 2 joueurs")
    @Max(value = 1000, message = "Le nombre maximum de joueurs est 1000")
    private int maxPlayers;
    @NotNull(message = "La date et l'heure de début est obligatoire")
    @ValidStartDate(message = "L'événement doit être créer au minimum 3 jours avant")
    private LocalDateTime startDateTime;
    @NotNull(message = "La date et l'heure de fin est obligatoire")
    private LocalDateTime endDateTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
