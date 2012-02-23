object de extends Application {

  val j1 = new Joueur(1);
  val j2 = new Joueur(2);

  val arbitre = new Arbitre(j1, j2)

  arbitre.start
}
