 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ua.ri.assigment4.evaluation.Evaluation;
import ua.ri.assigment4.indexer.Posting;
import ua.ri.assigment4.queries.RankedRetrieval;
import ua.ri.assigment4.tokenizer.SimpleTokenizer;
import ua.ri.assigment4.tokenizer.Tokenizer;
import java.util.Scanner;
import ua.ri.assigment4.relevanceFeedback.Feedback;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class RankProcessor {
    
    private final Tokenizer tok;
    private final SimpleTokenizer st;
    private final Evaluation eval ;
    private final String filenameScoreTok;
    private final String filenameScoreSimpleTok;
    private final String tokenizerType;
    private List processedList;
    private final RankedRetrieval rankRetrieval;
    private String filenameInput;
    private final String filenameQueries;
    private final String filename;
    private final Scanner sc;
    private double queryT;
    private double queryL;
    // Assignment4
    private final Feedback feedback;
    private String filenameImplicitRelevantDocs;
    private String filenameExplicitRelevantDocs;
    private String filenameOutFeedback;
    private String feedType;
    private String rocchioOrWordvec;
    private Map<Integer, HashMap<Integer, Integer>> explicitRelevantDocs;
    private Map<Integer, ArrayList<Integer>> implicitDocsRel;

    /**
     * Construtor para o fluxo de processamento.
     * @param dir_files
     * @param dir_stopWords 
     */
    public RankProcessor(Path dir_files, Path dir_stopWords) {
        tok = new Tokenizer(dir_stopWords);
        st = new SimpleTokenizer();
        eval = new Evaluation();
        tokenizerType = null;
        processedList = null;
        this.filenameScoreTok = "fileout_docscoreranked_tok";
        this.filenameScoreSimpleTok = "fileout_docscoreranked_simple";
        rankRetrieval = new RankedRetrieval(dir_files);
        this.filenameInput = null;
        this.filenameQueries = "cranfield.queries";
        this.filename = "cranfield.query.relevance.txt";
        sc = new Scanner(System.in);
        queryT = 0.0;
        queryL = 0.0;
        // Assignment4
        feedback = new Feedback();
        this.filenameImplicitRelevantDocs = null;
        this.filenameExplicitRelevantDocs = "cranfield.query.relevance.txt";
        this.filenameOutFeedback = null;
        this.feedType = null;
        this.rocchioOrWordvec = null;
    }
    
    /**
     * Construtor para o fluxo de processamento.
     * @param dir_files
     * @param dir_stopWords 
     * @param tokenizerType 
     * @param assignment 
     */
    public RankProcessor(Path dir_files, Path dir_stopWords, String tokenizerType, String assignment) {
        tok = new Tokenizer(dir_stopWords);
        st = new SimpleTokenizer();
        eval = new Evaluation();
        this.tokenizerType = tokenizerType;
        processedList = null;
        this.filenameScoreTok = "fileout_docscoreranked_tok_"+assignment;
        this.filenameScoreSimpleTok = "fileout_docscoreranked_simple_"+assignment;
        rankRetrieval = new RankedRetrieval(dir_files);
        this.filenameInput = null;
        this.filenameQueries = "cranfield.queries";
        this.filename = "cranfield.query.relevance.txt";
        sc = new Scanner(System.in);
        queryT = 0.0;
        queryL = 0.0;
        // Assignment4
        feedback = new Feedback();
        this.filenameImplicitRelevantDocs = null;
        this.filenameExplicitRelevantDocs = "cranfield.query.relevance.txt";
        this.filenameOutFeedback = null;
        this.feedType = null;
        this.rocchioOrWordvec = null;
    }
    
    /**
     * Método responsável por definir o tipo de feedback a ser implementado - explicito ou implicito.
     * @param feedType
     * @param rocchioOrWordvec 
     */
    public void setFeedbackType(String feedType, String rocchioOrWordvec){
        System.out.println("Setting Relevance Feedback type ...");
        
        /* Rocchio or Wordvec process */
        switch (rocchioOrWordvec) {
            case "rocchio":
                System.out.println("Using Rocchio Feedback ...");
                this.rocchioOrWordvec = rocchioOrWordvec;
                /* Implicit or explicit Feedback */
                switch (feedType) {
                    case "explicit":
                        System.out.println("Using Explicit Relevance Feedback ...");
                        this.filenameOutFeedback = "explicitRelevanceFeedback";
                        this.feedType = feedType;
                        break;
                    case "implicit":
                        System.out.println("Using Implicit Relevance Feedback ...");
                        this.filenameOutFeedback = "implicitRelevanceFeedback";
                        this.feedType = feedType;
                        break;
                    default:
                        System.out.println("Shouldn't enter here :( ...");
                        this.filenameOutFeedback = "explicitRelevanceFeedback";
                        this.feedType = "explicit";
                        break;
                }   break;
            case "wordvec":
                System.out.println("Using Wordvec Feedback ...");
                this.rocchioOrWordvec = rocchioOrWordvec;
                this.filenameOutFeedback = "word2vecRelevanceFeedback";
                this.feedType = feedType;
                break;
            default:
                System.out.println("Shouldn't enter here :( ...");
                this.rocchioOrWordvec = "rocchio";
                this.feedType = "explicit";
                break;
        }
    }
    
    /**
     * Método que inicia o processamento.
     * @param fileType
     * @param assignment
     */
    public void process(String fileType, String assignment){
        System.out.println("Rank Processor starting to process files ...");
        
        String filenameScore="";
        if(tokenizerType.equals("tokenizer")){
            System.out.println("Using Tokenizer class to process terms ...");
            filenameScore = this.filenameScoreTok;            
            this.filenameInput = "fileout_tok";
            this.filenameImplicitRelevantDocs = "fileout_docscoreranked_tok_3.txt";
        } else {
            System.out.println("Using Simple Tokenizer class to process terms ...");
            filenameScore = this.filenameScoreSimpleTok;
            this.filenameInput = "fileout_simple";
            this.filenameImplicitRelevantDocs = "fileout_docscoreranked_simple_3.txt";
            this.filenameExplicitRelevantDocs = "cranfield.query.relevance.txt";
        }
        
        File file = new File("./data/queries/"+this.filenameQueries+".txt");
        
        long begin;
        double diff_time = 0;
        int queryId=0;
        TreeMap<String, LinkedList<Posting>> tmap;
        
        if(assignment.equals("2")) {
            
            tmap = rankRetrieval.putTMInMem(this.filenameInput+"_assign2");
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                while ((line = br.readLine()) != null) {
                   queryId++;
                   switch(tokenizerType){
                        case "tokenizer":
                            processedList = tok.textProcessor(line);
                            begin = System.nanoTime();
                            rankRetrieval.calculateScoreAssign2(processedList, tmap, queryId);
                            break;
                        case "simple":
                            processedList = st.textProcessor(line);
                            begin = System.nanoTime();
                            rankRetrieval.calculateScoreAssign2(processedList, tmap, queryId);
                            break;
                        default:
                            processedList = tok.textProcessor(line);
                            begin = System.nanoTime();
                            rankRetrieval.calculateScoreAssign2(processedList, tmap, queryId);
                            break;
                   }
                   diff_time += (System.nanoTime() - begin);
                }

            } catch (IOException e) {
                    e.printStackTrace();
            } 
            
            rankRetrieval.printToFile(filenameScore+"_i", rankRetrieval.getQueryIdDocIdScorei(), "2");
            rankRetrieval.printToFile(filenameScore+"_ii", rankRetrieval.getQueryIdDocIdScoreii(), "2");
            
            // Metrics
            this.queryL = diff_time/queryId; 
            this.queryT = (1.0/(this.queryL)) * 1000 *1000 *1000; // to convert to seconds
            
        } else if(assignment.equals("3")) {
            
            tmap = rankRetrieval.putTMInMem(this.filenameInput);
            // populate TreeMap<term, nr docs that contain term>
            rankRetrieval.populateTMTermsInDocs(tmap);
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;            
                while ((line = br.readLine()) != null) {
                   queryId++;
                   switch(tokenizerType){
                        case "tokenizer":
                            processedList = tok.textProcessor(line);
                            begin = System.nanoTime();
                            rankRetrieval.calculateRank(processedList, tmap, queryId);
                            break;
                        case "simple":
                            processedList = st.textProcessor(line);
                            begin = System.nanoTime();
                            rankRetrieval.calculateRank(processedList, tmap, queryId);
                            break;
                        default:
                            processedList = tok.textProcessor(line);
                            begin = System.nanoTime();
                            rankRetrieval.calculateRank(processedList, tmap, queryId);
                            break;
                   }
                   diff_time += (System.nanoTime() - begin);
                }

            } catch (IOException e) {
                    e.printStackTrace();
            }  

            rankRetrieval.printToFile(filenameScore, rankRetrieval.getQueryIdDocIdScoreTM(), "3");

            // Metrics
            this.queryL = diff_time/queryId; 
            this.queryT = (1.0/(this.queryL)) * 1000 *1000 *1000; // to convert to seconds
            
        } else if(assignment.equals("4")) {
            
            tmap = rankRetrieval.putTMInMem(this.filenameInput);
            // populate TreeMap<term, nr docs that contain term>
            rankRetrieval.populateTMTermsInDocs(tmap);
            
            feedback.getRelevantDocs("./output/"+this.filenameImplicitRelevantDocs, "implicit");
            feedback.getRelevantDocs("./data/queries/"+this.filenameExplicitRelevantDocs, "explicit");

            // Put Maps in Memory - Explicit and Implicit Feedback
            explicitRelevantDocs = feedback.getExplicitRelevantDocsHM();
            implicitDocsRel = feedback.getImplicitRelevantDocsHM();

            // Check relevant and non-relevant docs
            rankRetrieval.checkRelevantAndNonRelevantDocs(explicitRelevantDocs, implicitDocsRel);
            TreeMap<Integer, ArrayList<Integer>> tmapNonRelDocs = rankRetrieval.getNonRelevantDocs();
            
            /* Rocchio or wordvec process */
            if(this.rocchioOrWordvec.equals("rocchio"))
                System.out.println("Applying the Rocchio relevance feedback method ...");
            else if(this.rocchioOrWordvec.equals("wordvec"))
                System.out.println("Applying the Query Expansion ...");
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;            
                while ((line = br.readLine()) != null) {
                   queryId++;
                   
                   switch(tokenizerType){
                        case "tokenizer":
                            processedList = tok.textProcessor(line);
                            begin = System.nanoTime();
                            if(this.rocchioOrWordvec.equals("rocchio"))
                                rankRetrieval.calculateNewQuery(processedList, tmap, explicitRelevantDocs, tmapNonRelDocs, implicitDocsRel, queryId, this.feedType);
                            else if(this.rocchioOrWordvec.equals("wordvec"))
                                rankRetrieval.calculateWeightsWord2vec(processedList, tmap, queryId, 4);
                            break;
                        case "simple":
                            processedList = st.textProcessor(line);
                            begin = System.nanoTime();
                            if(this.rocchioOrWordvec.equals("rocchio"))
                                rankRetrieval.calculateNewQuery(processedList, tmap, explicitRelevantDocs, tmapNonRelDocs, implicitDocsRel, queryId, this.feedType);
                            else if(this.rocchioOrWordvec.equals("wordvec"))
                                rankRetrieval.calculateWeightsWord2vec(processedList, tmap, queryId, 4);
                            break;
                        default:
                            processedList = tok.textProcessor(line);
                            begin = System.nanoTime();
                            if(this.rocchioOrWordvec.equals("rocchio"))
                                rankRetrieval.calculateNewQuery(processedList, tmap, explicitRelevantDocs, tmapNonRelDocs, implicitDocsRel, queryId, this.feedType);
                            else if(this.rocchioOrWordvec.equals("wordvec"))
                                rankRetrieval.calculateWeightsWord2vec(processedList, tmap, queryId, 4);
                            break;
                   }
                   diff_time += (System.nanoTime() - begin);
                }

            } catch (IOException e) {
                    e.printStackTrace();
            }
            
            // Print stuff
            switch(this.rocchioOrWordvec) {
                case "rocchio":
                    if(this.feedType.equals("explicit"))
                        rankRetrieval.printToFile(this.filenameOutFeedback, rankRetrieval.getQueryIdDocIdRankExplicit(), "4");
                    else if(this.feedType.equals("implicit"))
                        rankRetrieval.printToFile(this.filenameOutFeedback, rankRetrieval.getQueryIdDocIdRankImplicit(), "4");
                    break;
                case "wordvec":
                    rankRetrieval.printToFile(this.filenameOutFeedback, rankRetrieval.getQueryIdDocIdRankWord2Vec(), "4");
                    break;
                default:
                    break;
            }
                   
            // Metrics
            this.queryL = diff_time/queryId; 
            this.queryT = (1.0/(this.queryL)) * 1000 *1000 *1000; // to convert to seconds
        }   
    }
    
    /**
     * Menu para retornar a métrica desejada.
     */
    private void printMenu(){
        System.out.println();
        System.out.println("##################################");
        System.out.println("####### Efficiency metrics #######");
        System.out.println("##################################");
        System.out.println("# 1 - Mean queries Precision     #");
        System.out.println("# 2 - Mean queries Recall        #");
        System.out.println("# 3 - Mean queries F-measure     #");
        System.out.println("# 4 - Mean Average Precision     #");
        System.out.println("# 5 - Mean Precision at Rank 10  #");
        System.out.println("# 6 - Mean Reciprocal Rank       #");
        System.out.println("# 7 - Query throughput           #");
        System.out.println("# 8 - Mean query latency         #");
        System.out.println("# 9 - NDCG at Rank 10            #");
        System.out.println("# 10 - Exit                       #");
        System.out.println("##################################");
        System.out.print("Option: ");
    }
    
    /**
     * Método responsável pelo cálculo das métricas.
     * @param assignment
     */
    public void eval(String assignment){
        TreeMap<Integer, HashMap<Integer,Double>> tmpMap= new TreeMap<>();
        
        if(assignment.equals("2"))
            // Score tipo 1
            tmpMap = rankRetrieval.getQueryIdDocIdScorei();
        else if(assignment.equals("3"))
            tmpMap = rankRetrieval.getQueryIdDocIdScoreTM();
        else if(assignment.equals("4")){
            switch(this.rocchioOrWordvec){
                case "rocchio":
                    switch (this.feedType) {
                        case "explicit":
                            tmpMap = rankRetrieval.getQueryIdDocIdRankExplicit();
                            break;
                        case "implicit":
                            tmpMap = rankRetrieval.getQueryIdDocIdRankImplicit();
                            break;
                        default:
                            tmpMap = rankRetrieval.getQueryIdDocIdRankExplicit();
                            break;
                    }
                    break;
                case "wordvec":
                    tmpMap = rankRetrieval.getQueryIdDocIdRankWord2Vec();
                    break;
            }
        }
        
        // Evaluation
        eval.readQueryRelevanceFile("./data/queries/"+filename);
        double precision =0,recall =0, fmeasure=0, precisionRanked=0, ndcg=0;
        
        double sumAvgPrec=0;
        double sumMMR=0;
        int rankSize = 10;
        Map<String, Double> m;
        
        // Assignment4 - Normalized Discounted Comulative Gain
        //ArrayList<Double> dcgs = eval.calculateDCG(explicitRelevantDocs);
        //ArrayList<Double> idcgs = eval.calculateIDCG(explicitRelevantDocs);
        
        ArrayList<Double> dcgs = new ArrayList<>();
        ArrayList<Double> idcgs = new ArrayList<>();
        System.out.println(this.feedType);
        if(this.feedType.equals("explicit")){
            dcgs = eval.calculateDCG(explicitRelevantDocs);
            idcgs = eval.calculateIDCG(explicitRelevantDocs);
        }else if(this.feedType.equals("implicit")){
            dcgs = eval.calculateDCG(rankRetrieval.getRelDocsAndRelevance());
            idcgs = eval.calculateIDCG(rankRetrieval.getRelDocsAndRelevance());
        }
         
        int i=0;
        for(Map.Entry<Integer, HashMap<Integer, Double>> entry : tmpMap.entrySet()){
            Integer corpusSize = rankRetrieval.sizeCorpus(); //1400
            m = eval.compute(entry.getValue(), entry.getKey(), corpusSize, rankSize);
            precision += eval.precision(m, false);
            recall += eval.recall(m);
            fmeasure += eval.fmeasure(recall, precision);
            precisionRanked += eval.precision(m, true);
            sumAvgPrec += m.get("avgPrecision");
            sumMMR += m.get("recRank");
            if(idcgs.get(i) != 0.0)
                ndcg += dcgs.get(i)/idcgs.get(i);
            i++;
        }
     
        int option=0;
        do{
           printMenu();
           try{
               option = sc.nextInt();
           }catch(Exception ex){
               System.out.println("A number from 1 to 10 needs to be inserted ...");
               System.exit(0);
           }
           
           switch(option){
            case 1:
                System.out.printf("Mean Precision: %.6f \n",precision/tmpMap.entrySet().size());
                break;
            case 2:
                System.out.printf("Mean Recall: %.6f \n",recall/tmpMap.entrySet().size());
                break;
            case 3:
                System.out.printf("Mean F-measure: %.6f \n",fmeasure/tmpMap.entrySet().size());
                break;
            case 4:
                System.out.printf("Mean average Precision: %.6f \n",sumAvgPrec/tmpMap.entrySet().size());
                break;
            case 5:
                System.out.printf("Mean Precision at Rank %d: %.6f\n", rankSize, precisionRanked/rankSize); 
                break;
            case 6:
                System.out.printf("Mean Reciprocal Rank: %.6f\n",sumMMR/tmpMap.entrySet().size());
                break;
            case 7:
                System.out.printf("Query throughput: %.0f queries/sec\n",queryT);
                break;
            case 8:
                System.out.printf("Mean query latency: %.0f s\n",queryL);
                break;
            case 9:
                System.out.printf("NDCG at Rank 10: %f\n",ndcg/tmpMap.entrySet().size());
                break;
            case 10:
                System.exit(0);
                break;
            }        
        }while(option != 10);
    }   
}