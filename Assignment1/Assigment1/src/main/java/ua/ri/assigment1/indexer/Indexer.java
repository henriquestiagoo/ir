/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment1.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javafx.util.Pair;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class Indexer {
    
    private final TreeMap<String, LinkedList<Posting>> tokenDocIdFreq;
    private LinkedList<Posting> postings;
    
    /**
     * Constructor da classe Indexer.
     */
    public Indexer(){
        tokenDocIdFreq = new TreeMap<>();
    }
    
    /**
     * Método que faz a contagem dos termos processados e invoca o método addToTokenDocIdFreq para adicionar os termos
     * de um documento à TreeMap.
     * @param processedTerms
     * @param docId 
     */
    public void addToSetAndCount(List<String> processedTerms, Integer docId) {        
        Set<String> unique = new HashSet<>(processedTerms);
        for (String key : unique) {
            Integer freq = Collections.frequency(processedTerms, key);
            addToTokenDocIdFreq(key, docId, freq);
        }        
    }
    
    /**
     * Método que procede à introdução dos termos na linkedList e posteriormente associa essa
     * linkedList à TreeMap.
     * @param token
     * @param docId
     * @param termFreq 
     */
    public void addToTokenDocIdFreq(String token, int docId, int termFreq){
        Posting tmpPost = new Posting(docId, termFreq);
        if(!tokenDocIdFreq.containsKey(token)){
            postings = new LinkedList<>();
            postings.add(tmpPost);
            tokenDocIdFreq.put(token, postings);
        }else{
            // caso key exista
            postings = tokenDocIdFreq.get(token);
            postings.add(tmpPost);
            tokenDocIdFreq.put(token, postings);
        }
    }
    
    /**
     * Metodo que retorna a TreeMap tokenDocIdFreq.
     * @return tokenDocIdFreq
     */
    public TreeMap<String, LinkedList<Posting>> getTM(){
        return tokenDocIdFreq;
    }
    
    /**
     * Método responsável pela escrita do ficheiro no formato especificado.
     * @param filename 
     */
    public void writeToFile(String filename){
       try{
            File fileTwo = new File("./output/"+filename+".txt");
            FileOutputStream fos = new FileOutputStream(fileTwo);
            PrintWriter pw = new PrintWriter(fos);

            pw.println("// IR - Assigment 1: Miguel Oliveira nmec: 72638 and Tiago Henriques nmec: 73046");
            for(Map.Entry<String, LinkedList<Posting>> entriesTokenDocIdFreq :  tokenDocIdFreq.entrySet()){
                String docIdAndFreq = "";           
                for (Posting entryPosting : entriesTokenDocIdFreq.getValue()) 
                    docIdAndFreq += entryPosting.getDocId()+":"+entryPosting.getFreq()+",";
                // Remove the last character ','
                pw.println(entriesTokenDocIdFreq.getKey()+","+docIdAndFreq.substring(0, docIdAndFreq.length()-1));
            }
            pw.flush();
            pw.close();
            fos.close();
        }catch(IOException ex){ System.out.println(ex.toString());}
    }
    
    /**
     * Método que percorre a lista dos termos e retorna
     * os n primeiros termos que aparecem num dado número de documentos ordenados alfabeticamente.
     * @param nTerms
     * @param nDocs
     * @return tokens.subList(0, howMany);
     */
    public List getFirstTerms(int nTerms, int nDocs){
        List<String> tokens = new ArrayList<>();
        
        for(String key : tokenDocIdFreq.keySet()){
          if(tokenDocIdFreq.get(key).size() == nDocs){
            tokens.add(key);
          }
        }
        
        Collections.sort(tokens);
        return tokens.subList(0, nTerms);
    }
    
    /**
     * Método que retorna os n termos com maior frequência de repetição em todos os documentos.
     * @param nTerms
     * @return tokens.subList(0, nTerms);
     */
    public List<Pair<String,Integer>> getTermsHighDocFreq(int nTerms){

        List<Pair<String,Integer>> tokens = new ArrayList<>();

        for(String key: tokenDocIdFreq.keySet()){
          tokens.add(new Pair<>(key, tokenDocIdFreq.get(key).size()));
        }

        tokens.sort((token1, token2) -> token2.getValue().compareTo(token1.getValue()));
        return tokens.subList(0, nTerms);
    }
    
    /**
     * Método que retorna o tamanho do vocabulário.
     * @return tamanho vocabulário
     */
    public int getTMSize(){
        return tokenDocIdFreq.size();
    }
}