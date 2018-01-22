/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4.queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ua.ri.assigment4.corpusReader.CorpusReader;
import ua.ri.assigment4.indexer.Posting;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class QueryProcessor {
        
    private final CorpusReader cr;
    // docScorei
    private final TreeMap<Integer, LinkedList<DocScore>> queryIdDocIdScorei;
    private LinkedList<DocScore> docsScorei;
    // docScoreii
    private final TreeMap<Integer, LinkedList<DocScore>> queryIdDocIdScoreii;
    private LinkedList<DocScore> docsScoreii;
    
    /**
     * Constructor da classe QueryProcessor.
     * @param dir_files
     */
    public QueryProcessor(Path dir_files){
        cr = new CorpusReader(dir_files);
        queryIdDocIdScorei = new TreeMap<>();
        queryIdDocIdScoreii = new TreeMap<>();
    }
    
    /**
     * Método que coloca em memória o ficheiro gerado na primeira iteração.
     * @param filename
     * @return tmap
     */
    public TreeMap<String, LinkedList<Posting>> putTMInMem(String filename){
        TreeMap<String, LinkedList<Posting>> tmap = putInTM("./output/"+filename+".txt");
        return tmap;
    }
   
    
    /**
     * Método responsável por calcular o score, dependendo do tipo de score a calcular.
     * @param tmap
     * @param processedList
     * @param docScore
     * @param queryId 
     */
    public void calculateDocScore(TreeMap<String, LinkedList<Posting>> tmap, List processedList, String docScore, int queryId){        
        if(docScore.equals("scorei")){
            calculateScorei(processedList, tmap, queryId);
        }else if(docScore.equals("scoreii")){
            calculateScoreii(processedList, tmap, queryId);
        }        
    }
    
    /**
     * Método que procede à introdução dos termos na linkedList e posteriormente associa essa
     * linkedList à TreeMap.
     * @param docId
     * @param queryId
     * @param score
     * @param tmap
     * @param docsScore
     */
    public void addToQueryIdDocIdScoreTM(int docId, int queryId, int score, TreeMap<Integer, LinkedList<DocScore>> tmap, LinkedList<DocScore> docsScore){

        DocScore tmpScore = new DocScore(docId, score);
        if(!tmap.containsKey(queryId)){
            docsScore = new LinkedList<>();
            docsScore.add(tmpScore);
            tmap.put(queryId, docsScore);
        }else{
            // caso key exista
            docsScore = tmap.get(queryId);
            docsScore.add(tmpScore);
            tmap.put(queryId, docsScore);
        }
    }
    
    /**
     * Método que faz a contagem do score do tipo 1.
     * @param processedList
     * @param tmap
     * @param queryId
    */
    public void calculateScorei(List processedList, TreeMap<String, LinkedList<Posting>> tmap, int queryId){
        
        for(int i=1; i<=cr.getCorpusSize(); i++){
            int score=0;
            for(int query_term=0; query_term<processedList.size(); query_term++){
                String key = (String) processedList.get(query_term);
                if(tmap.containsKey(key)){
                    LinkedList<Posting> tmpPostings = new LinkedList<>();
                    tmpPostings = tmap.get(key);

                    for (Posting entryPosting : tmpPostings) {
                        if(entryPosting.getDocId() == i){
                            score += 1;
                        } 
                    }
                }
            }
            if(score!=0)
                addToQueryIdDocIdScoreTM(i, queryId, score, queryIdDocIdScorei, docsScorei);
        }         
    }
    
    /**
     * Método que faz a contagem do score do tipo 2.
     * @param processedList
     * @param tmap
     * @param queryId 
     */
    public void calculateScoreii(List processedList, TreeMap<String, LinkedList<Posting>> tmap, int queryId){
        
        for(int i=1; i<=cr.getCorpusSize(); i++){
            int score=0;
            for(int query_term=0; query_term<processedList.size(); query_term++){
                String key = (String) processedList.get(query_term);
                if(tmap.containsKey(key)){
                    LinkedList<Posting> tmpPostings = new LinkedList<>();
                    tmpPostings = tmap.get(key);

                    for (Posting entryPosting : tmpPostings) {
                        if(entryPosting.getDocId() == i){
                            score += entryPosting.getTermWeight();
                        } 
                    }
                }
            }
            if(score!=0)
                addToQueryIdDocIdScoreTM(i, queryId, score, queryIdDocIdScoreii, docsScoreii);
        }
    }    
    
    /**
     * Método responsável pela geração do ficheiro de saída.
     * @param filename
     * @param tmap 
     */
    public void printToFile(String filename, TreeMap<Integer, LinkedList<DocScore>> tmap){
       try{
            File fileTwo = new File("./output/"+filename+".txt");
            FileOutputStream fos = new FileOutputStream(fileTwo);
            PrintWriter pw = new PrintWriter(fos);
            
            pw.println("Qi  doc_id  doc_score");
            for (Map.Entry<Integer, LinkedList<DocScore>> entry : tmap.entrySet()) { 
                for(DocScore entryDocScore : entry.getValue()){
                    pw.println(entry.getKey()+"     "+entryDocScore.getDocId()+"        "+entryDocScore.getScore());
                }
            }
            pw.flush();
            pw.close();
            fos.close();
        }catch(IOException ex){ System.out.println(ex.toString());}
    }
    
    /**
     * Método responsável por colocar em memória o ficheiro previamente gerado na primeira iteração.
     * @param filename
     * @return tmpMap
     */
    public TreeMap<String, LinkedList<Posting>> putInTM(String filename) {
        
        TreeMap<String, LinkedList<Posting>> tmpMap = new TreeMap<>();
        
        File file = new File(filename);
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                LinkedList<Posting> tmpPostings = new LinkedList<>();
                String[] parts = line.split(",");
                for(int i=1; i<parts.length; i++){
                    String[] tmpPosting = parts[i].split(":");
                    Posting tmp = new Posting(Integer.parseInt(tmpPosting[0]), Integer.parseInt(tmpPosting[1]));
                    tmpPostings.add(tmp);
                }
                tmpMap.put(parts[0], tmpPostings);
            }

        } catch (IOException e) {
                e.printStackTrace();
        }
        return tmpMap;
    }
    
        /**
     * Metodo que retorna a TreeMap getQueryIdDocIdScoreiTM.
     * @return queryIdDocIdScorei
     */    
    public TreeMap<Integer, LinkedList<DocScore>> getQueryIdDocIdScoreiTM(){
        return queryIdDocIdScorei;
    }
    
    /**
     * Metodo que retorna a TreeMap getQueryIdDocIdScoreiiTM.
     * @return queryIdDocIdScoreii
     */
    public TreeMap<Integer, LinkedList<DocScore>> getQueryIdDocIdScoreiiTM(){
        return queryIdDocIdScoreii;
    }
}