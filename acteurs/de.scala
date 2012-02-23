import scala.actors.Actor
import scala.actors.Actor._
import scala.util.Random

case object Jouez
case object Fin
case class Choix(id: Integer, x : Integer)

class Joueur(id : Int) extends Actor {

  def act() {

    Console.println(id + " : je rentre dans la partie")

    loop {
      react {

        case Jouez =>

          var random = new Random
          var c = random.nextInt(5) + 1
          Console.println(id + " : je joue " + c);

          sender ! new Choix(id, c)

        case Fin =>
          Console.println(id + " : je sors de la partie")
          exit()
      }
    }
  }
}

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
              Console.println("Egalité")

            j1 ! Fin
            j2 ! Fin
        }
    }
  }
}

object de extends Application {

  val j1 = new Joueur(1);
  val j2 = new Joueur(2);

  val arbitre = new Arbitre(j1, j2)

  arbitre.start
}
