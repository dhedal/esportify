INSERT INTO User (id, uuid, pseudo, email, password, status, created_at, updated_at) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'AdminMaster', 'admin@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 3, NOW(), NOW()),
(2, '550e8400-e29b-41d4-a716-446655440002', 'EventBoss', 'organizer1@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 2, NOW(), NOW()),
(3, '550e8400-e29b-41d4-a716-446655440003', 'TournamentKing', 'organizer2@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 2, NOW(), NOW()),
(4, '550e8400-e29b-41d4-a716-446655440004', 'SniperPro', 'player1@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW()),
(5, '550e8400-e29b-41d4-a716-446655440005', 'SpeedRacer', 'player2@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW()),
(6, '550e8400-e29b-41d4-a716-446655440006', 'ShadowNinja', 'player3@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW()),
(7, '550e8400-e29b-41d4-a716-446655440007', 'BattleMage', 'player4@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW()),
(8, '550e8400-e29b-41d4-a716-446655440008', 'CyberWarrior', 'player5@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW()),
(9, '550e8400-e29b-41d4-a716-446655440009', 'StealthAgent', 'player6@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW()),
(10, '550e8400-e29b-41d4-a716-446655440010', 'RocketGamer', 'player7@esportify.com', '$2b$12$aUAt0pNFmcHANGiE/ujAuu9X69frtSvcn37/gqM9voFnRerjHBORm', 1, NOW(), NOW());



INSERT INTO event (id, uuid, title, description, max_players, status, start_date_time, end_date_time, user_id, created_at, updated_at) VALUES
-- Événements en attente de validation (PENDING)
(1, '111e8400-e29b-41d4-a716-446655440001', 'Tournoi en attente 1', 'Tournoi en cours de validation', 100, 1, '2025-06-10 14:00:00', '2025-06-10 18:00:00', 2, NOW(), NOW()),
(2, '111e8400-e29b-41d4-a716-446655440002', 'Tournoi en attente 2', 'Deuxième tournoi en attente', 80, 1, '2025-06-15 16:00:00', '2025-06-15 20:00:00', 3, NOW(), NOW()),
-- Événements validés et ouverts aux inscriptions (VALIDATED)
(3, '222e8400-e29b-41d4-a716-446655440003', 'Championnat MOBA', 'Tournoi officiel de MOBA', 120, 2, '2025-07-05 15:00:00', '2025-07-05 19:00:00', 2, NOW(), NOW()),
(4, '222e8400-e29b-41d4-a716-446655440004', 'Duel FPS', '1v1 sur une carte fermée', 50, 2, '2025-07-12 18:00:00', '2025-07-12 22:00:00', 3, NOW(), NOW()),
(5, '222e8400-e29b-41d4-a716-446655440005', 'Battle Royale Weekly', 'Affrontez 99 autres joueurs', 100, 2, '2025-07-20 14:00:00', '2025-07-20 18:00:00', 2, NOW(), NOW()),
(6, '222e8400-e29b-41d4-a716-446655440006', 'Compétition Racing', 'Grand tournoi de vitesse', 60, 2, '2025-07-28 17:00:00', '2025-07-28 21:00:00', 3, NOW(), NOW()),
-- Événements en cours (ON_GOING)
(7, '333e8400-e29b-41d4-a716-446655440007', 'Demi-finale Esport', 'Tournoi de demi-finale en direct', 90, 3, '2025-01-25 13:00:00', '2025-01-25 17:00:00', 2, NOW(), NOW()),
(8, '333e8400-e29b-41d4-a716-446655440008', 'Match exhibition', 'Duel entre champions', 30, 3, '2025-01-26 19:00:00', '2025-01-26 22:00:00', 3, NOW(), NOW()),
(9, '333e8400-e29b-41d4-a716-446655440009', 'Tournoi mensuel RPG', 'Tournoi spécial pour les fans de RPG', 70, 3, '2025-01-27 12:00:00', '2025-01-27 16:00:00', 2, NOW(), NOW()),
-- Événements complets (FULL)
(10, '444e8400-e29b-41d4-a716-446655440010', 'Finale Esport Pro', 'Grand match final de la saison', 150, 4, '2025-01-20 18:00:00', '2025-01-20 23:00:00', 3, NOW(), NOW()),
(11, '444e8400-e29b-41d4-a716-446655440011', 'Clash des Légendes', 'Compétition annuelle', 200, 4, '2025-01-22 14:00:00', '2025-01-22 20:00:00', 2, NOW(), NOW()),
(12, '444e8400-e29b-41d4-a716-446655440012', 'Elite Cup', 'Tournoi réservé aux joueurs classés', 80, 4, '2025-01-23 15:00:00', '2025-01-23 19:00:00', 3, NOW(), NOW());
