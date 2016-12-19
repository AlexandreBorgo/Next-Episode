App Android pour le projet mobile INF3041.

Newt Episode permet de recevoir une notification le jour de la sortie du prochaine �pisode des s�ries que vous suivez.

L'app utilise :
- des ressources pour �tre disponible en anglais et fran�ais
- des �l�ments graphiques : bouton, image, zone de text, checkbox...
- marche aussi bien en portrait que en landscape
- une activit� avec un syst�me de fragment pour naviguer d'un affichage � l'autre
- des notifications (normalement envoyer vers 10h chaque jour ou losrque l'app est ouverte)
- un slide menu accessible via l'actionbar
- un service pour le t�l�chargement des informations sur les s�ries et leurs �pisodes (avec notification pour un BroadCastReceiver)
  vers l'API REST de Betaseries (https://www.betaseries.com/)
- affichage des donn�es une fois trait�es dans un recyclerview (trois recyclerview en tout !)
- l'arlarmmanager pour les notifications
- les sharedpreferences pour sauvagarder la configuration de l'utilisateur
- une base de donn�es SQLite
- la librairie externe Picasso pour charger facilement des images depuis internet dans un recyclerview
