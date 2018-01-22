 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;
import ua.ri.assigment4.corpusReader.CorpusReader;
import ua.ri.assigment4.indexer.Indexer;
import ua.ri.assigment4.tokenizer.SimpleTokenizer;
import ua.ri.assigment4.tokenizer.Tokenizer;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class DocumentProcessor {
    
    private final CorpusReader cr;
    private final Indexer indexer;
    private final Tokenizer tok;
    private final SimpleTokenizer st;
    private List<String> xmlTags;
    private String filename;
    private final String tokenizerType;
    private List processedList;
    private List splitByPeriod;
    private final String sentencesFilename;
    
    /**
     * Construtor para o fluxo de processamento.
     * @param dir_files
     * @param dir_stopWords 
     */
    public DocumentProcessor(Path dir_files, Path dir_stopWords) {
        this.filename = null;
        cr = new CorpusReader(dir_files);
        tok = new Tokenizer(dir_stopWords);
        indexer = new Indexer();
        st = new SimpleTokenizer();
        tokenizerType = null;
        processedList = null;
        splitByPeriod = null;
        sentencesFilename = "cranfield_sentences";
    }
    
    /**
     * Construtor para o fluxo de processamento.
     * @param dir_files
     * @param dir_stopWords 
     * @param tokenizerType 
     * @param xmlTags 
     */
    public DocumentProcessor(Path dir_files, Path dir_stopWords, String tokenizerType, List<String> xmlTags) {
        this.filename = null;
        cr = new CorpusReader(dir_files);
        tok = new Tokenizer(dir_stopWords);
        indexer = new Indexer();
        this.xmlTags = xmlTags;
        st = new SimpleTokenizer();
        this.tokenizerType = tokenizerType;
        processedList = null;
        splitByPeriod = null;
        sentencesFilename = "cranfield_sentences";
    }
    
    /**
     * Método que inicia o processamento.
     * @param fileType
     */
    public void process(String fileType){
        System.out.println("Document Processor starting to process files ...");
        
        if(tokenizerType.equals("tokenizer")){
            System.out.println("Using Tokenizer class to process terms ...");
            this.filename = "fileout_tok";
        }else{
            System.out.println("Using Simple Tokenizer class to process terms ...");
            this.filename = "fileout_simple";
        }
        
        for(int i=0; i<cr.getCorpusSize(); i++){
           HashMap<Integer, String> docAndText = null;
           // xml files type
           docAndText = cr.getCorpusText(fileType, i, xmlTags); 
           int docId = (Integer) docAndText.keySet().toArray()[0];  
           String docText = docAndText.values().toString();
           
           switch(tokenizerType){
               case "tokenizer":
                   processedList = tok.textProcessor(docText);
                   break;
               case "simple":
                   processedList = st.textProcessor(docText);
                   break;
                default:
                   processedList = tok.textProcessor(docText);
                   break;
           }
           
           // passar lista e docId para indexer e fazer contagem das palavras
           indexer.addToSetAndCount(processedList, docId);  
        }

        // Functions that answer the questions on Section 4.
        //printVocabularySize();
        //printFirstTermsInOneDoc(10, 1);
        //printNTermsHighDocFreq(10);
        
        // generating a output file
        indexer.writeToFile(filename);
    }
    
    /**
     * Método responsável por criar o ficheiro de frases a serem analisadas pelo word2vec - NÃO ESTÁ A SER UTILIZADO.
     * @param fileType 
     */
    public void createCranfieldSentencesFile(String fileType){
        System.out.println("Creating cranfield_sentences.txt ...");
        
        splitByPeriod = new ArrayList();
        for(int i=0; i<cr.getCorpusSize(); i++) {
           HashMap<Integer, String> docAndText = null;
           // xml files type
           docAndText = cr.getCorpusText(fileType, i, xmlTags); 
           int docId = (Integer) docAndText.keySet().toArray()[0];  
           String docText = docAndText.values().toString();
           
           String normalizeSpaces = StringUtils.normalizeSpace(docText);
           
           List<String> tmpSplitByPeriod = tok.splitBySpacePeriodSpace(normalizeSpaces);  
           for(String period: tmpSplitByPeriod)
                splitByPeriod.add(period); 
        }
          
        indexer.createFile(this.sentencesFilename, splitByPeriod);
    }
    
    
    /**
     * Método que imprime o tamanho do vocabulário.
     */
    public void printVocabularySize(){
        System.out.println("#################################");
        System.out.println("Vocabulary size: "+indexer.getTMSize());
    }
    
    
    /**
     * Método que percorre a lista dos termos e imprime
     * os n primeiros termos que aparecem num dado número de documentos ordenados alfabeticamente.
     * @param nTerms
     * @param nDocs 
     */
    public void printFirstTermsInOneDoc(int nTerms, int nDocs){
        System.out.println("#################################");
        System.out.printf("Printing the %d first terms (in alphabetic order) that appear in %d doc(s): \n", nTerms, nDocs);
        List<String> termsList = indexer.getFirstTerms(nTerms, nDocs);
        for(String n : termsList){
            System.out.println(n);
        }
    }
    
    /**
     * Método que imprime os n termos com maior frequência de repetição em todos os documentos.
     * @param nTerms 
     */
    public void printNTermsHighDocFreq(int nTerms){
        System.out.println("#################################");
        System.out.printf("Printing the %d terms with higher document frequency: \n", nTerms);
        List<Pair<String,Integer>> termsFreq = indexer.getTermsHighDocFreq(nTerms);
        for(int k=0; k<termsFreq.size();k++){
            System.out.println(termsFreq.get(k).getKey()+" -> "+termsFreq.get(k).getValue());
        }
    }
}