/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4.evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class Evaluation {
    
    private final Map <Integer, ArrayList<Integer>> map;
    private final int maxRelevance = 5;
    
    public Evaluation(){
        map = new HashMap<>();
    }
    
    /**
     * Método responsável pela leitura do ficheiro das query relevantes e posteriormente 
     * adiciona os documentos relevantes a uma estrutura de dados.
     * @param filename 
     */
    public void readQueryRelevanceFile(String filename){

        int queryId, docId, relevance;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
          String sCurrentLine;
          while ((sCurrentLine = br.readLine()) != null) {
            //1 184 2 
            String[] lineSplit = sCurrentLine.split(" ");
            queryId = Integer.parseInt(lineSplit[0]);
            docId = Integer.parseInt(lineSplit[1]);
            
            try{
                relevance = Integer.parseInt(lineSplit[2]);
                if(relevance < maxRelevance)
                    // K - queryId V - Arraylist com docId
                    // Só estão a ser considerados documentos relevantes aqueles com valor de relevância inferior a 5
                    // Neste caso, todos os documentos da coleção
                    map.computeIfAbsent(queryId, k -> new ArrayList()).add(docId);   
                
            }catch(NumberFormatException ex){}
            
          }
        }catch (IOException e) {
          e.printStackTrace();
        }
    }
    
    // Map em que a Key é a queryId e value é Arraylist com os docids
    // Calcular FP e TP -> para queryid percorrer arrylist com os docids
    // Verificar se contem na Treemap criada 
    public Map<String, Double> compute(HashMap<Integer, Double> results, int queryId, int corpus_size, int rankSize){
        int truePos = 0, falsePos = 0, falseNeg = 0, trueNeg = 0, truePosRank=0, falsePosRank=0;
        // ArrayList de docs relevantes -> 'cranfield.query.relevance'
        ArrayList<Integer> relevants_docs = map.get(queryId);
        // ArrayList de docsIds da query que é recebida
        ArrayList<Integer> docsId = new ArrayList<>();
        int countdocs = 0;
        double avgPrec = 0.0, mmrFirst = 0.0;
        boolean first = true;
        
        Set set = results.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
              Map.Entry me = (Map.Entry)iterator.next();
              docsId.add((Integer) me.getKey());
        }
        
        for(int docId: docsId){
            //count
            countdocs++;
            if(relevants_docs.contains(docId)){
                if(first){
                    mmrFirst = (1.0/countdocs);
                    first = false;
                }
                truePos++;
                avgPrec += (truePos * 1.0 /countdocs * 1.0);
            }
            else
                falsePos++;
            if(countdocs <= rankSize){
                truePosRank = truePos;
                falsePosRank = falsePos;
            }      
        }
        
        falseNeg = relevants_docs.stream().filter((relevant) -> (!docsId.contains(relevant))).map((_item) -> 1).reduce(falseNeg, Integer::sum);
        
        // size -> getCorpusize() 
        trueNeg = corpus_size - truePos - falsePos - falseNeg;
        
        Map<String, Double> valuescomputed = new HashMap<>();
        valuescomputed.put("truePos", (double)truePos);
        valuescomputed.put("trueNeg", (double)trueNeg);
        valuescomputed.put("falsePos", (double)falsePos);
        valuescomputed.put("falseNeg", (double)falseNeg);
        valuescomputed.put("avgPrecision", truePos != 0 ? avgPrec/truePos : 0);
        valuescomputed.put("truePosRank", (double)truePosRank);
        valuescomputed.put("falsePosRank", (double)falsePosRank);
        valuescomputed.put("recRank", mmrFirst);
        
        return valuescomputed;
    }
    
    /**
     * Método responsável por calcular o logaritmo de 2 de um dado valor.
     * @param num
     * @return 
     */
    public double log2(int num){
        return ((double)Math.log(num)/Math.log(2));
    }
    
    /**
     * Método responsável por calcular o Discounted Cumulative Gain dado um conjunto de documentos relevantes.
     * @param relevantDocsHM
     * @return 
     */
    public ArrayList<Double> calculateDCG(Map<Integer, HashMap<Integer, Integer>> relevantDocsHM) {
        
        ArrayList<Double> queryIdDCGs = new ArrayList<>();
        for(Map.Entry<Integer, HashMap<Integer, Integer>> entry : relevantDocsHM.entrySet()) {
            double dcg=0;
            HashMap<Integer, Integer> docIdRelevance = entry.getValue();
            int i = 0;
            for(Map.Entry<Integer, Integer> docs : docIdRelevance.entrySet()){
                i++;
                if(i!=1)
                    dcg += docs.getValue()/log2(i);
                else
                    dcg=docs.getValue();                    
            }
            queryIdDCGs.add(dcg);            
        }
        return queryIdDCGs;
    }
    
    /**
     * Método responsável por calcular o Discounted Cumulative Gain dado um conjunto de documentos relevantes. 
     * @param relevantDocsHM
     * @return 
     */
    public ArrayList<Double> calculateIDCG(Map<Integer, HashMap<Integer, Integer>> relevantDocsHM) {
                
        ArrayList<Double> queryIdIDCGs = new ArrayList<>();
        for(Map.Entry<Integer, HashMap<Integer, Integer>> entry : relevantDocsHM.entrySet()) {
            double idcg=0;
            HashMap<Integer, Integer> docIdRelevance = entry.getValue(); // docId relevance

            ArrayList<Integer> orderedRelevances = new ArrayList<>();
            for(Map.Entry<Integer, Integer> docs : docIdRelevance.entrySet())
                orderedRelevances.add(docs.getValue());
            
            // Sort by reverseOrder - ( > Document Relevance)
            Collections.sort(orderedRelevances, Collections.reverseOrder());
            for(int i=1; i<= orderedRelevances.size(); i++){
                if(i!=1)
                    idcg += orderedRelevances.get(i-1)/log2(i);
                else
                    idcg=orderedRelevances.get(i-1);                
            }
            queryIdIDCGs.add(idcg);            
        }       
        return queryIdIDCGs;
    }
    
    /**
     * Método que retorna o valor de precisão para a query dada.
     * @param valuescomputed
     * @param ranked
     * @return 
     */
    public double precision(Map<String, Double> valuescomputed, Boolean ranked) {
        double truePos, falsePos;
        if(!ranked){
            truePos = valuescomputed.get("truePos");
            falsePos = valuescomputed.get("falsePos");
        }else{
            truePos = valuescomputed.get("truePosRank");
            falsePos = valuescomputed.get("falsePosRank");
        }
        
        if(truePos == 0)
            return 0.0;
        return (double)truePos/(truePos+falsePos);
    }
    
    /**
     * Método que retorna o valor de recall para a query dada.
     * @param valuescomputed
     * @return 
     */
    public double recall(Map<String, Double> valuescomputed){
        double truePos = valuescomputed.get("truePos");
        double falseNeg = valuescomputed.get("falseNeg");
        
        if(truePos == 0)
            return 0.0;
        return (double)truePos/(truePos+falseNeg);
    }
    
    /**
     * Método que retorna o valor de f-measure para a query dada.
     * @param recall
     * @param precision
     * @return 
     */
    public double fmeasure(double recall, double precision){
        return 2.0*recall*precision/(recall+precision);
    }    
}