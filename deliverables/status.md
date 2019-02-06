# _BlablaMove_ - Équipe F

## _Status_ semaine 6 

:vertical_traffic_light: **Green** 

 - Programmation par aspect terminée et connectée au Chaos Monkey
 - Ajout des nouvelles queues pour les callbacks des gateways terminée
 - Tests d'intégration réparés avec le nouveau notifieur

### Prévisions semaine 7

- Réflexion sur les paramètres intéressants à intégrer au Chaos Monkey
- Ajout de ces paramètres au Chaos Monkey
- Mise en place de monitoring pour observer l'impact du Chaos Monkey

## _Status_ semaine 5 

:vertical_traffic_light: **Green** 

 - Programmation par aspect mise en place, besoin d'y connecter le Chaos Monkey
 - Ajout de nouvelles queues en cours pour les callbacks des gateways
 - Nouveau notifieur passant par la file de messages implémenté
 - Mise à jour du backend en cours pour recevoir les messages du notifieur

### Prévisions semaine 6

- Programmation par aspect et Chaos Monkey connectés entre eux
- Notifieur et backend connectés via la file de messages
- Réflexion sur les résultats à faire ressortir du Chaos Monkey

## _Status_ semaine 4 

:vertical_traffic_light: **Green** 

 - Chaos Monkey fonctionnel et mise en place de la programmation par aspect en cours
 - Remise en question de l'architecture du projet au niveau de l'interaction entre client, serveur et notifieur
 - Tests qui passent avec la file de messages
 - Gateways implémentées et connectées à la file de messages pour les services externes

### Prévisions semaine 5

- Fin de l'implémentation de la programmation par aspect pour les fonctionnalités impactées par le Chaos Monkey
- Connexion du client à la file de messages pour les notifications
- Début d'implémentation des fonctionnalités restantes du scope initial : les retards et les déviations

## _Status_ semaine 44-45 

:vertical_traffic_light: **Green** 

 - Fin de la persistance
 - Ajout de la BD dans le docker-compose
 - Fin du client + tests _end-to-end_ (client → backend → external → backend → client)

## _Status_ semaine 43-44 

:vertical_traffic_light: **Green** 

 - Prise de conscience que le Registry n'était pas nécessaire
 - Persistance des principaux objets du système
 - Docker-compose (sans la BD)
 - Amélioration de notre suite de tests (intégration + fonctionnels)
 - Implémentation de la gestion des points (Web Service + composant + service externe)
 - Début de l'implémentation du client
 
 ### Prévisions semaine 44-45

 - Fin de la persistance
 - Ajout d'une _messaging queue_
 - Ajout de la BD et de la _messaging queue_ dans le docker-compose
 - Fin du client + tests _end-to-end_ (client → backend → external → backend → client)

## _Status_ semaine 42-43 

:vertical_traffic_light: **Green** 

 - Implémentation des services externes d'assurance et de reroutage
 - Implémentation des Web Services qui gère les problèmes (point d'entrée du système) et envoie les notifications
 - Mise en place de tests fonctionnels pour les Web Services
 - Début d'implémentation de la gestion des points
 
 ### Prévisions semaine 43-44

 - Ajout d'un composant Registry pour accéder et éditer les données
 - Mise en place de la persistance
 - Continuer l'implémentation de la gestion des points
 - Mise en place du docker-compose
 - Amélioration de nos suites de test
 - Réflexions sur l'utilisation d'un broker de messages

## _Status_ semaine 41-42 

:vertical_traffic_light: **Green**  

 - Justification du diagramme de composants
 - Mise en place de la pile technologique (Spring)
 - Walking skeleton du composant qui pose problème, à savoir UserNotifier + 2 composants qui l'appellent
 - Tests unitaires + tests d'intégrations basiques pour vérifier le walking skeleton 
 
### Prévisions semaine 42-43

 - Implémenter une meilleure stratégie de test
 - Développer de nouveaux services qui réalisent notre scénario car crash
 - Rendre les mocks de nos services plus intelligents

## _Status_ semaine 40-41 

:vertical_traffic_light: **Green**  

 - Diagramme de composant
 - Définition de la _roadmap_ et des rôles
 - Début du choix de la pile technologique
 
### Prévisions semaine 41-42

 - Fin du choix de la pile technologique
 - Mise en place de la pile technologique
 - Développement du premier composant


## _Status_ semaine 39

:vertical_traffic_light: **Green**  
 
Nous avons défini le scope du projet ainsi que précisé les cas d'utilisation que nous voulons traiter.

### Prévisions semaine 40

Continuer à travailler sur le projet.
