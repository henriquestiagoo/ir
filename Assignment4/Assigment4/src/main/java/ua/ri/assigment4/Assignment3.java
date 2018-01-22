 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/**
 * Classe Main que vai ser executada.
 */
public class Assignment3 {

    public static void main(String[] args) {
               
       String tokenizer = null;
       String option = null;
       List<String> xmlTags = new ArrayList<>();
       
       if(args.length == 1){
           tokenizer = args[0];
           option = "indexer";
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }else{
               xmlTags.add("DOCNO");
               xmlTags.add("TITLE");
               xmlTags.add("TEXT");
           }
       }else if(args.length == 2){
           tokenizer = args[0];
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }
           option = args[1];
           if(!option.equals("indexer") && !option.equals("ranker")){
               usage();
               System.exit(0);
           }else{
               xmlTags.add("DOCNO");
               xmlTags.add("TITLE");
               xmlTags.add("TEXT");
           }
       }else if(args.length != 5){ // == 0
           tokenizer = "tokenizer";
           option = "indexer";
           xmlTags.add("DOCNO");
           xmlTags.add("TITLE");
           xmlTags.add("TEXT");
       }else {
           try{
               tokenizer = args[0];
               if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
                    usage();
                    System.exit(0);
               }
               option = args[1];
               if(!option.equals("indexer") && !option.equals("ranker")){
                    usage();
                    System.exit(0);
               }else{
                   xmlTags.add(args[1]);
                   xmlTags.add(args[2]);
                   xmlTags.add(args[3]);
               }
           }catch(Exception ex){
               System.out.println(ex.toString());
               usage();
               System.exit(0);
           }
       }
       
       System.out.println("Running Assignment3 main program ... ");
       if(option.equals("indexer")){
           DocumentProcessor dp = new DocumentProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, xmlTags);
           dp.process("xml");   
       }else if(option.equals("ranker")){
           RankProcessor rp = new RankProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, "3");
           rp.process("xml", "3");
           rp.eval("3");       
       }
    }
    
    /**
     * Método que retorna o formato de execução do programa. 
    */
    public static void usage(){
        System.out.println("#####################################################################");
        System.out.println("Information Retrieval - Assignment3");
        System.out.println("Usage (default will run tokenizer class and indexer data flow): java Assignment3");
        System.out.println("Or: java Assignment3 <tokenizer or simple> <indexer or ranker>");
        System.out.println("Or: java Assignment3 <tokenizer or simple> <indexer or ranker> <XML_DOCID_TAG> <XML_TITLE_TAG> <XML_TEXT_TAG>");
        System.out.println("#####################################################################");
    }
}