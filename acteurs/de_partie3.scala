class Arbitre(j1 : Joueur, j2 : Joueur) extends Actor {

  def act() {

    j1.start
    j2.start

    j1 ! Jouez
    j2 ! Jouez

    react {
      case Choix(id1, x1) =>

        react {
          case Choix(id2, x2) =>

            if(x1 > x2)
              Console.println("Le joueur " + id1 + " gagne")
            else if(x1 < x2)
              Console.println("Le joueur " + id2 + " gagne")
            else
              Console.println("Egalite")

            j1 ! Fin
            j2 ! Fin
        }
    }
  }
}
