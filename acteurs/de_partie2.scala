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

      ...
