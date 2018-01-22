 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import ua.ri.assigment3.indexer.Posting;
import ua.ri.assigment3.queries.QueryProcessor;
import ua.ri.assigment3.tokenizer.SimpleTokenizer;
import ua.ri.assigment3.tokenizer.Tokenizer;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class SearchProcessor {
    
    private final Tokenizer tok;
    private final SimpleTokenizer st;
    private final String filenameScoreiTok;
    private final String filenameScoreiSimpleTok;
    private final String filenameScoreiiTok;
    private final String filenameScoreiiSimpleTok;
    private final String tokenizerType;
    private List processedList;
    private final QueryProcessor queryProcessor;
    private String filenameInput;
    private final String filenameQueries;
    
    /**
     * Construtor para o fluxo de processamento.
     * @param dir_files
     * @param dir_stopWords 
     */
    public SearchProcessor(Path dir_files, Path dir_stopWords) {
        tok = new Tokenizer(dir_stopWords);
        st = new SimpleTokenizer();
        tokenizerType = null;
        processedList = null;
        this.filenameScoreiTok = "fileout_docscorei_tok";
        this.filenameScoreiSimpleTok = "fileout_docscorei_simple";
        this.filenameScoreiiTok = "fileout_docscoreii_tok";
        this.filenameScoreiiSimpleTok = "fileout_docscoreii_simple";
        queryProcessor = new QueryProcessor(dir_files);
        this.filenameInput = null;
        this.filenameQueries = "cranfield.queries";
    }
    
    /**
     * Construtor para o fluxo de processamento.
     * @param dir_files
     * @param dir_stopWords 
     * @param tokenizerType 
     */
    public SearchProcessor(Path dir_files, Path dir_stopWords, String tokenizerType) {
        tok = new Tokenizer(dir_stopWords);
        st = new SimpleTokenizer();
        this.tokenizerType = tokenizerType;
        processedList = null;
        this.filenameScoreiTok = "fileout_docscorei_tok";
        this.filenameScoreiSimpleTok = "fileout_docscorei_simple";
        this.filenameScoreiiTok = "fileout_docscoreii_tok";
        this.filenameScoreiiSimpleTok = "fileout_docscoreii_simple";
        queryProcessor = new QueryProcessor(dir_files);
        this.filenameInput = null;
        this.filenameQueries = "cranfield.queries";
    }
    
    /**
     * Método que inicia o processamento.
     * @param fileType
     */
    public void process(String fileType){
        System.out.println("Search Processor starting to process files ...");
        
        String filenameScorei="";
        String filenameScoreii="";
        if(tokenizerType.equals("tokenizer")){
            System.out.println("Using Tokenizer class to process terms ...");
            filenameScorei = this.filenameScoreiTok;
            filenameScoreii = this.filenameScoreiiTok;
            this.filenameInput = "fileout_tok";
        } else {
            System.out.println("Using Simple Tokenizer class to process terms ...");
            filenameScorei = this.filenameScoreiSimpleTok;
            filenameScoreii = this.filenameScoreiiSimpleTok;
            this.filenameInput = "fileout_simple";
        }
        
        TreeMap<String, LinkedList<Posting>> tmap = queryProcessor.putTMInMem(this.filenameInput);
        
        File file = new File("./data/queries/"+this.filenameQueries+".txt");
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            int queryId=0;
            
            while ((line = br.readLine()) != null) {
               queryId++;
               switch(tokenizerType){
                    case "tokenizer":
                        processedList = tok.textProcessor(line);
                        queryProcessor.calculateDocScore(tmap, processedList, "scorei", queryId);
                        queryProcessor.calculateDocScore(tmap, processedList, "scoreii", queryId);
                        break;
                    case "simple":
                        processedList = st.textProcessor(line);
                        queryProcessor.calculateDocScore(tmap, processedList, "scorei", queryId);
                        queryProcessor.calculateDocScore(tmap, processedList, "scoreii", queryId);
                        break;
                    default:
                        processedList = tok.textProcessor(line);
                        queryProcessor.calculateDocScore(tmap, processedList, "scorei", queryId);
                        queryProcessor.calculateDocScore(tmap, processedList, "scoreii", queryId);
                        break;
               }
            }
            
        } catch (IOException e) {
                e.printStackTrace();
        }            

        queryProcessor.printToFile(filenameScorei, queryProcessor.getQueryIdDocIdScoreiTM());
        queryProcessor.printToFile(filenameScoreii, queryProcessor.getQueryIdDocIdScoreiiTM());
    }
}