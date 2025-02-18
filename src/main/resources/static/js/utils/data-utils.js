

/**
 *
 */
export class EventStatus {
    static UNDEFINED = {key:0, label: "Statut inconnu"};
    static PENDING = {key:1, label: "En attente de validation"};
    static VALIDATED = {key:2, label: "Inscriptions ouvertes"};
    static ON_GOING = {key:3, label: "En cours"};
    static FULL = {key:4, label: "Complet - Plus d'inscription"};
    static CANCELLED = {key:5, label: "Annulé"};
    static CLOSED = {key:6, label: "Terminé"};

    static getByKey(key) {
        switch(key) {
            case 1 : return EventStatus.PENDING;
            case 2 : return EventStatus.VALIDATED;
            case 3 : return EventStatus.ON_GOING;
            case 4 : return EventStatus.FULL;
            case 5 : return EventStatus.CANCELLED;
            case 6 : return EventStatus.CLOSED;
            default: return EventStatus.UNDEFINED;
        }
    }
}

/**
 *
 */
export class UserStatus {
    static VISITOR = {key: 0, label: "Visiteur"};
    static PLAYER = {key: 1, label: "Joueur"};
    static ORGANIZER = {key: 2, label: "Organisateur"};
    static ADMIN = {key: 3, label: "Administrateur"};

    static getByKey(key) {
        switch(key) {
            case 1 : return UserStatus.PLAYER;
            case 2 : return UserStatus.ORGANIZER;
            case 3 : return UserStatus.ADMIN;
            default: return UserStatus.VISITOR;
        }
    }

}

/**
 *
 */
export class AskStatus {
    static UNDEFINED = {key:0, label: "Indéfini"};
    static PENDING = {key:1, label: "En attente de validation"};
    static APPROVED = {key:2, label: "Demande acceptée"};
    static REJECTED = {key:3, label: "Demande refusée"};

    static getByKey(key) {
        switch(key) {
            case 1 : return AskStatus.PENDING;
            case 2 : return AskStatus.APPROVED;
            case 3 : return AskStatus.REJECTED;
            default: return AskStatus.UNDEFINED;
        }
    }
}