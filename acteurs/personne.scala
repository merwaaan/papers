object App {

       class Personne(nom: String) {

             def parle(phrase: String) {
                 println(nom + " : " + phrase)
             }
       }

       var moi = new Personne("Merwan")
       moi.parle("Bonjour !")
}
