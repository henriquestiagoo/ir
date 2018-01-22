 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4;

import java.nio.file.Files;
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
public class Assignment4 {
    
    private static final String filenameTok = "fileout_docscoreranked_tok_3.txt";
    private static final String filenameSimpleTok = "fileout_docscoreranked_simple_3.txt";
    
    public static void main(String[] args) throws Exception {   
       
       String tokenizer = null;
       String option = null;
       String feedType = null;
       String rocchioOrWordvec = null;
       List<String> xmlTags = new ArrayList<>();
       
       if(args.length == 1) {
           tokenizer = args[0];
           option = "ranker";
           feedType = "explicit";
           rocchioOrWordvec = "rocchio";
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }else{
               xmlTags.add("DOCNO");
               xmlTags.add("TITLE");
               xmlTags.add("TEXT");
           }
       } else if(args.length == 2) {
           tokenizer = args[0];
           option = args[1];
           feedType = "explicit";
           rocchioOrWordvec = "rocchio";
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }
           if(!option.equals("indexer") && !option.equals("ranker")){
               usage();
               System.exit(0);               
           }
       } else if(args.length == 3){
           tokenizer = args[0];
           option = args[1];
           feedType = args[2];
           rocchioOrWordvec = "rocchio";
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }
           if(!option.equals("indexer") && !option.equals("ranker")){
               usage();
               System.exit(0);
           }
           if(!feedType.equals("explicit") && !feedType.equals("implicit")){
               usage();
               System.exit(0);               
           }
       } else if(args.length == 4){
           tokenizer = args[0];
           option = args[1];
           feedType = args[2];
           rocchioOrWordvec = args[3];
           if(!tokenizer.equals("tokenizer") && !tokenizer.equals("simple")){
               usage();
               System.exit(0);
           }
           if(!option.equals("indexer") && !option.equals("ranker")){
               usage();
               System.exit(0);
           }
           if(!feedType.equals("explicit") && !feedType.equals("implicit")){
               usage();
               System.exit(0);               
           }
           if(!rocchioOrWordvec.equals("rocchio") && !rocchioOrWordvec.equals("wordvec")){
               usage();
               System.exit(0);               
           }
       }else if(args.length != 7){ // == 0
           tokenizer = "tokenizer";
           option = "ranker";
           feedType = "explicit";
           rocchioOrWordvec = "rocchio";
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
               }
               feedType = args[2];
               if(!feedType.equals("explicit") && !feedType.equals("implicit")){
                   usage();
                   System.exit(0);
               }
               rocchioOrWordvec = args[3];
               if(!rocchioOrWordvec.equals("rocchio") && !rocchioOrWordvec.equals("wordvec")){
                   usage();
                   System.exit(0);
               }
           }catch(Exception ex){
               System.out.println(ex.toString());
               usage();
               System.exit(0);
           }
       }
        
       System.out.println("Running Assignment4 main program ... ");
       
       RankProcessor rp = null;
       
       // Check if file exists and run Assignment3 if doesn't
       if (!Files.exists(Paths.get("./output/"+filenameTok)) || !Files.exists(Paths.get("./output/"+filenameSimpleTok))){       
            if(option.equals("indexer")){
                DocumentProcessor dp = new DocumentProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, xmlTags);
                dp.process("xml");   
            }else if(option.equals("ranker")){
                rp = new RankProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, "3");
                rp.process("xml", "3");      
            }
       }
       
       // Run Assignment4 :)
       rp = new RankProcessor(Paths.get("./data/cranfield"), Paths.get("./data/stopWords.txt"), tokenizer, "4");
       rp.setFeedbackType(feedType, rocchioOrWordvec);
       rp.process("xml", "4");
       rp.eval("4");    
    }
    
    /**
     * Método que retorna o formato de execução do programa. 
    */
    public static void usage(){
        System.out.println("#####################################################################");
        System.out.println("Information Retrieval - Assignment4");
        System.out.println("Usage (default will run tokenizer class and indexer data flow): java Assignment3");
        System.out.println("Or: java Assignment4 <tokenizer or simple> <indexer or ranker> <explicit or implicit> <rocchio or wordvec>");
        System.out.println("Or: java Assignment4 <tokenizer or simple> <indexer or ranker> <explicit or implicit> <rocchio or wordvec> <XML_DOCID_TAG> <XML_TITLE_TAG> <XML_TEXT_TAG>");
        System.out.println("#####################################################################");
    }
}