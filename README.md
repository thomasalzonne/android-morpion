# android-morpion

Bonjour !

Voici notre projet MOBILE, un morpion en multijoueur

L'application se base sur le template avec sidenav, et nous avons créé 4 fragments

- Login Fragment (page login)
Une page simple pour choisir un pseudonyme pour le matchmaking

- Matchmaking Fragment (page matchmaking)
Un fois loggé, on est directement redirigé vers cette page, qui nous ajoute dans une collection "matchmaking" de firebase. 
La collection contient notre pseudo, la date à laquelle on a rejoint la queue, et un champs "refreshedAt" qui se rafraichit toutes les 2 sec
avec la date actuelle (pour s'assurer qu'on est toujours en recherche de partie)
Le fragment s'abonne à ce matchmaking et si il trouve quelqu'un de disponible, le premier ayant rejoint le queue créé la partie.

- Game Fragment (page game)
Ici on s'abonne a un document de la collection "games" qui contient le player 1, player 2, turn, grid, et createdAt
A chaque modification de la grille, on actualise l'affichage et on vérifie si quelqu'un a gagné ou si c'est une égalité
Si c'est le cas, un bouton "rejouer" apparaît.
Une fois la partie terminée, les scores sont mis à jour sur la collection "users"


- Leaderboard Fragment (page leaderboard)
Ce fragment contient un ListAdapter fonctionnel et récupère le classement depuis firebase


BUGS
Malheureusement l'application crash si on sort du game fragment

