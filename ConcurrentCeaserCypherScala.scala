import akka.actor._

object ConcurrentCeaser {
  val key = 1
  val alphabet: List[String] = List("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
  var n = 100 // Editable - data size - works as a multiplier of local storage
  val m = 4 //Editable - local storage
  var decodedMessage = "" //Shared original message
  var finished = "" //Shared encoded message
  var prep = "" //Current string
  val t1 = System.nanoTime
  val system = ActorSystem("HelloSystem")

  //Ceaser Cipher
  class Encode(message: String) extends Actor {
    var encodedMessage = ""
    def receive = {
      case "Ready" => for (i <- 1 to message.length()) {
        var focusChar = message.charAt(i - 1); //Get char
        val focus = String.valueOf(focusChar); //Convert to String
        var located = 0; //initialize
        for (j <- 1 to alphabet.length) {
          if (alphabet(j - 1).equals(focus)) {
            located = j
          }
        }
        var useChar = located + key-1;
        while (useChar > 25) { //Wrapping
          useChar = useChar - 26;
        }
        encodedMessage = encodedMessage + alphabet(useChar)
      }
        finished = finished + encodedMessage
        println("final: " + finished)
        if (n >= 0) {
          val generateActor = system.actorOf(Props(new Generate(n)), name = "generateActor" + n)
          generateActor ! "Ready"
        }
          else
          {
            println((System.nanoTime - t1) / 1e9d)
            context.stop(self)
          }
        }
    }

  //Generates a subsect of shared original message.
  class Generate(count: Int) extends Actor {
    def receive = {
      case "Stop" =>
      case "Ready" =>
        if (n >= 0) { //Count check
          n = count - 1
          prep = "" //Initialize
          for (i <- 1 to m) { //For each unit of local storage
            val r = scala.util.Random
            val random = alphabet(r.nextInt(25))  //Generate random char
            prep = prep + random  //Append
            decodedMessage = decodedMessage + random
          }
          println("decodedMessage:   " + decodedMessage)  //Encode
          val encodeActor = system.actorOf(Props(new Encode(prep)), name = "encodeActor" + n)
          encodeActor ! "Ready"
        }
        else {
          val encodeActor = system.actorOf(Props(new Encode(prep)), name = "encodeActor" + n)
          encodeActor ! "Stop"  //Stop system
          context.stop(self)
        }

    }
  }

  def main(args: Array[String]) {
    val generateActor = system.actorOf(Props(new Generate(n)), name = "generateActor")
    generateActor ! "Ready";
  }
}