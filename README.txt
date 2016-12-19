App Android pour le projet mobile INF3041.

Newt Episode permet de recevoir une notification le jour de la sortie du prochaine épisode des séries que vous suivez.

L'app utilise :
- des ressources pour être disponible en anglais et français
- des éléments graphiques : bouton, image, zone de text, checkbox...
- marche aussi bien en portrait que en landscape
- une activité avec un système de fragment pour naviguer d'un affichage à l'autre
- des notifications (normalement envoyer vers 10h chaque jour ou losrque l'app est ouverte)
- un slide menu accessible via l'actionbar
- un service pour le téléchargement des informations sur les séries et leurs épisodes (avec notification pour un BroadCastReceiver)
  vers l'API REST de Betaseries (https://www.betaseries.com/)
- affichage des données une fois traitées dans un recyclerview (trois recyclerview en tout !)
- l'arlarmmanager pour les notifications
- les sharedpreferences pour sauvagarder la configuration de l'utilisateur
- une base de données SQLite
- la librairie externe Picasso pour charger facilement des images depuis internet dans un recyclerview
