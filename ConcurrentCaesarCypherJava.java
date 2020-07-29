package newceaser;

import java.util.Random;
public class NewCeaser {

 String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
int n = 100; // Editable - data size - works as a multiplyer of local storage
int m = 4; //Editable - local storage
int key = 1;
int multipleEditor = 3; //change this to pick out multiples of a number
double t1 = System.nanoTime();
String prep = "";
String[] numbers = new String[m]; // Memory space
String finished = "";
String decodedMessage = "";
String finishedPrep = "";

    //Thread A Generates a random string.
    public class ThreadA implements Runnable {
   
    Thread athread;
    String random;
    
        ThreadA(){
            athread = new Thread(this, "Thread A");
            athread.start();
        }
        
        public void run() {    
            try {
                prep = "";
                for (int i=0 ;i<m;i++){ 
                    Random r = new Random();           //Generate char                
                    random = alphabet[r.nextInt(25)];
                    prep = prep + random;       //Store local result
                    decodedMessage = decodedMessage + random;   //Store original message           
                }                
            Thread.sleep(25);   
            }         
            catch(InterruptedException e){
                System.out.println("thread interrupted");
            }       
        }
    
        public String getValue() { //Method used to fill array
            return prep;
        }     
    }
    
    //Thread B encodes section of string
    public class ThreadB implements Runnable {
    Thread mythread;
    
    ThreadB(){
        mythread = new Thread(this, "Thread B");
        mythread.start();
    }
    
    public void run() {    
        try {            
            for (int i=0 ;i<n;i++){   //Big memory itterations                     
                System.out.println("Before : " + decodedMessage);
                for (int j=0 ;j<m;j++){ //Small memory itterations                   
                   finishedPrep = encode(key, prep);    //encode
                   finished = finished + finishedPrep;     //Apphend string                                 
                }
                System.out.println("After : " + finished);
                Thread.sleep(25);
                //This keep both in sync as no thread is active untill all are restarted                                       
            }                               //negates writing time
        }          
        catch(InterruptedException e){
            System.out.println("thread interrupted");
        } 
    }   
}
    
    public void Master() {  //Controls other threads
        
        ThreadB tb = new ThreadB();
     
        for (int i=0 ;i<n;i++){ //i = n
        ThreadA ta = new ThreadA(); //Start Generator Thread
        synchronized (ta){            
            try { 
                Thread.sleep(5);   //Waits untill A is done setting first position
                int x = 0;                                                //Initialise sample loop              
                while(ta.athread.isAlive()){
                    Thread.sleep(25);   //Sync with A
                    if (x != m-1){    //Stops random errors where x moves above array bounds
                    x++;
                    }
                }                                         
                synchronized (tb){               
                    tb.notify();                
                }                                                                     
            }                 
            catch(InterruptedException e) {
                System.out.println("Main thread interrupted");
            }
        }     //Loop            
        }
         System.out.println("Main thread run is over - All threads are finished.");
         System.out.println(t1);
    }
    
    public String encode(int key, String message){
        int located = 0;
        String encodedMessage = "";
        for (int i=0; i<message.length(); i++) {    //For each char in message       
            char focusChar = message.charAt(i);  //Get char
            String focus = String.valueOf(focusChar);   //Convert to String        
            located = 0;      //initialize
            for (int j = 0; j < alphabet.length; j++) {            
                if (alphabet[j].equals(focus)){
                    located = j;
                }            
            }
            int useChar = located + key;       
            while (useChar > 25){
                useChar = useChar - 26;
            }
            encodedMessage = encodedMessage + alphabet[useChar];
        }
    prep = "";    
    return encodedMessage;
    }
    
    public static void main(String[] args) {
       NewCeaser e = new NewCeaser();
       e.Master();      }
    }
