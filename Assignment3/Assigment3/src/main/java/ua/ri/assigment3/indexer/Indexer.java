/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment3.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
     * Método que retorna o valor normalizado de tf.
     * @param tf
     * @param som
     * @return 
     */
    private double normalizeTFDoc(double tf, double som){
        return  tf/som;
    }
    
    /**
     * Método que calcula o valor da normalização dos termos num dado documento.
     * @param tmp
     * @return 
     */
    private double getNormalizationValueDoc(LinkedList<Posting> tmp){
        double som=0;
        for(int i=0; i<tmp.size(); i++)
             som += Math.pow(((Posting)tmp.get(i)).getTermWeight(), 2);
        return Math.sqrt(som);
    }
    
    /**
     * Método que faz a contagem dos termos processados e invoca o método addToTokenDocIdFreq para adicionar os termos
     * de um documento à TreeMap.
     * @param processedTerms
     * @param docId 
     */
    public void addToSetAndCount(List<String> processedTerms, Integer docId) {        
        Set<String> unique = new HashSet<>(processedTerms);
        LinkedList<Posting> tmpFT = new LinkedList<>();
        
        unique.stream().map((key) -> Collections.frequency(processedTerms, key)).map((freq) -> new Posting(docId, freq)).forEach((tmpPosting) -> {
            tmpFT.push(tmpPosting);
        });
        
         // TODO tf + normalization
         LinkedList tmp = tmpFT.stream()
            .map(p-> new Posting(p.getDocId(), 1 + Math.log10(p.getTermWeight()))) 
            .collect(Collectors.toCollection(LinkedList::new));
                 
         int i=0;
         double norm = getNormalizationValueDoc(tmp);
         
         for(String key: unique){
             double tf = ((Posting)tmp.get(i)).getTermWeight();
             addToTokenDocIdFreq(key, docId, normalizeTFDoc(tf, norm));
             i++;
         }                        
    }
    
    /**
     * Método que procede à introdução dos termos na linkedList e posteriormente associa essa
     * linkedList à TreeMap.
     * @param token
     * @param docId
     * @param termWeight 
     */
    public void addToTokenDocIdFreq(String token, int docId, double termWeight){
        Posting tmpPost = new Posting(docId, termWeight);
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

            pw.println("// IR - Assigment 3: Miguel Oliveira nmec: 72638 and Tiago Henriques nmec: 73046");
            tokenDocIdFreq.entrySet().stream().forEach((entriesTokenDocIdFreq) -> {
                String docIdAndFreq = "";
                docIdAndFreq = entriesTokenDocIdFreq.getValue().stream().map((entryPosting) -> entryPosting.getDocId()+":"+entryPosting.getTermWeight()+",").reduce(docIdAndFreq, String::concat);
                // Remove the last character ','
                pw.println(entriesTokenDocIdFreq.getKey()+","+docIdAndFreq.substring(0, docIdAndFreq.length()-1));
           });
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
        
        tokenDocIdFreq.keySet().stream().filter((key) -> (tokenDocIdFreq.get(key).size() == nDocs)).forEach((key) -> {
            tokens.add(key);
        });
        
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

        tokenDocIdFreq.keySet().stream().forEach((key) -> {
            tokens.add(new Pair<>(key, tokenDocIdFreq.get(key).size()));
        });

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