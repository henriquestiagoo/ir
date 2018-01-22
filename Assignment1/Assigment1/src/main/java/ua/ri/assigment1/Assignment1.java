 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment1;

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
public class Assignment1 {

    public static void main(String[] args) {
               
       String tokenizer = null;
       List<String> xmlTags = new ArrayList<>();
       
       if(args.length == 1){
           tokenizer = args[0];
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }else{
               xmlTags.add("DOCNO");
               xmlTags.add("TITLE");
               xmlTags.add("TEXT");
           }
       }else if(args.length != 4){ // == 0
           tokenizer = "tokenizer";
           xmlTags.add("DOCNO");
           xmlTags.add("TITLE");
           xmlTags.add("TEXT");
       }else {
           try{
               tokenizer = args[0];
               if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
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
       
       DocumentProcessor dp = new DocumentProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, xmlTags);
       dp.process("xml");
    }
    
    /**
     * Método que retorna o formato de execução do programa. 
    */
    public static void usage(){
        System.out.println("#####################################################################");
        System.out.println("Information Retrieval - Assignment1");
        System.out.println("Usage (default will run tokenizer class): java Assignment1");
        System.out.println("Or: java Assignment1 <tokenizer or simple>");
        System.out.println("Or: java Assignment1 <tokenizer or simple> <XML_DOCID_TAG> <XML_TITLE_TAG> <XML_TEXT_TAG>");
        System.out.println("#####################################################################");
    }
}