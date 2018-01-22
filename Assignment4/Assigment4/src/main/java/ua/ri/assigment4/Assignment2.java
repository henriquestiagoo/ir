 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4;

import java.nio.file.Paths;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/**
 * Classe Main que vai ser executada.
 */
public class Assignment2 {

    public static void main(String[] args) {
               
       String tokenizer = null;
       
        switch (args.length) {
            case 0:
                tokenizer = "tokenizer";
                break;
            case 1:
                tokenizer = args[0];
                if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
                    usage();
                    System.exit(0);
                }    
                break;
            default:
                try{
                    tokenizer = args[0];
                    if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
                        usage();
                        System.exit(0);
                    }
                }catch(Exception ex){
                    System.out.println(ex.toString());
                    usage();
                    System.exit(0);
                }    
                break;
        }
       
        // Previous iteration
        //SearchProcessor sp = new SearchProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer);
        //sp.process("xml");
        
        System.out.println("Running Assignment2 main program ... ");
        RankProcessor rp = new RankProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, "2");
        rp.process("xml", "2");
        rp.eval("2");
    }
    
    /**
     * Método que retorna o formato de execução do programa. 
    */
    public static void usage(){
        System.out.println("#####################################################################");
        System.out.println("Information Retrieval - Assignment2");
        System.out.println("Usage (default will run tokenizer class): java Assignment2");
        System.out.println("Or: java Assignment2 <tokenizer or simple>");
        System.out.println("#####################################################################");
    }
}