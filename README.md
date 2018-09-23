# :car: BlablaMove F :car:
:traffic_light: ***Projet AL SI5 V5*** :traffic_light:


## Team :family: 
- Thomas CANAVA
- Loïc GARDAIRE 
- Jeremy JUNAC
- Johann MORTARA 

## Scope :telescope:
- Recherche d'une solution
    - Resolve le chemin :question:
    - Changement de pricing :question:
- Possibilité de signaler un problème / une déviation
- Notification de problème / reprise du parcours
- Gestion d'erreur en cas de non-disponibilité
- Contact de l'assurance
- Notre service est appelé le reste de l'application et ne gère que ce qui est décrit ci-dessous
- Gestion des retards


## Utilisateurs :woman::man:
- L'étudiant voulant déplacer ses cartons
- Le conducteur du véhicule

## Scenarii Cockburn :clipboard: :movie_camera: 

### Scenario principal
1. La voiture tombe en panne.
2. Le conducteur signale le problème.
3. Le propriétaire est notifié.
4. L'application recherche une solution.
5. Lorsque l'autre conducteur récupère la marchandise, il le signale.
6. Le propriétaire est notifié du départ du carton. 
- ***Variantes***
1a. La voiture change de destination

### Scenario : Recherche d'une solution 
1. L'application trouve un autre conducteur passant par cet endroit.
2. Le colis est assigné au nouveau conducteur.
3. Une notification est envoyée au propriétaire du carton.
4. Le conducteur est crédité pour la distance qu'il a parcouru.
- ***Variantes***
1a. L'application ne trouve pas de solution.
2a. Le conducteur doit garder le colis jusqu'a nouvel ordre.
2b. Le propriétaire décide de terminer la course.

### Scenario : Un conducteur a du retard
1. La personne qui a actuellement le colis est en retard.
2. On notifie le propriétaire et le prochain conducteur, le cas échéant

## Questions à poser :question: 
- Intégrer la V1 dans notre solution ?
- Si oui, utiliser ce qu'a fait le groupe de la V1 ?
 
