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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import ua.ri.assigment4.Word2VecGenerator;
import ua.ri.assigment4.corpusReader.CorpusReader;
import ua.ri.assigment4.indexer.Posting;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class RankedRetrieval {
    
    // Assignment2
    private final TreeMap<Integer, HashMap<Integer, Double>> queryIdDocIdScorei;
    private final TreeMap<Integer, HashMap<Integer, Double>> queryIdDocIdScoreii;
    // Assignment3
    private final CorpusReader cr;
    private final TreeMap<Integer, HashMap<Integer,Double>> queryIdDocIdRank;
    private final TreeMap<String, Integer> termInDocs;  
    // Assignment4
    private final TreeMap<Integer, HashMap<Integer,Double>> queryIdDocIdRankImplicit;
    private final TreeMap<Integer, HashMap<Integer,Double>> queryIdDocIdRankExplicit;
    private final TreeMap<Integer, HashMap<Integer, Integer>> relevantDocsHM;
    private final TreeMap<Integer, ArrayList<Integer>> nonRelevantDocsHM;
    private final Word2VecGenerator wv;
    private final TreeMap<Integer, HashMap<Integer,Double>> queryIdDocIdRankWord2Vec;
    
    /**
     * Constructor da classe RankedRetrieval.
     * @param dir_files
     */
    public RankedRetrieval(Path dir_files){
        cr = new CorpusReader(dir_files);
        queryIdDocIdRank = new TreeMap<>();
        termInDocs = new TreeMap<>();
        // Assignment2
        queryIdDocIdScorei = new TreeMap<>();
        queryIdDocIdScoreii = new TreeMap<>();
        // Assignment4
        queryIdDocIdRankImplicit = new TreeMap<>();
        queryIdDocIdRankExplicit = new TreeMap<>();
        relevantDocsHM = new TreeMap<>();
        nonRelevantDocsHM = new TreeMap();
        wv = new Word2VecGenerator();
        queryIdDocIdRankWord2Vec = new TreeMap<>();
    }
    
    /**
     * Método que coloca em memória o ficheiro gerado na primeira/terceira iteração.
     * @param filename
     * @return tmap
     */
    public TreeMap<String, LinkedList<Posting>> putTMInMem(String filename){
        TreeMap<String, LinkedList<Posting>> tmap = putInTM("./output/"+filename+".txt");
        return tmap;
    }
    
    /**
     * Método que procede à introdução dos termos na linkedList e posteriormente associa essa
     * linkedList à TreeMap.
     * @param docId
     * @param queryId
     * @param score
     * @param tmap
     */
    public void addToQueryIdDocIdScoreTM(int docId, int queryId, double score, TreeMap<Integer, HashMap<Integer,Double>> tmap){
        
        HashMap<Integer, Double> docsScores;
        //DocWeight tmpScore = new DocWeight(docId, score);
        if(!tmap.containsKey(queryId)){
            docsScores = new HashMap<>();
            docsScores.put(docId,score);
     
        } else {
            // caso key exista
            docsScores = tmap.get(queryId);
            if(docsScores.containsKey(docId))
                docsScores.replace(docId,docsScores.get(docId)+ score);
            else
                docsScores.put(docId,score);
        }
        tmap.put(queryId, docsScores);
    }

    /**
     * Método responsável por popular a estrutura de dados em que é associado o número de documentos onde 
     * existe um dado termo.
     * @param tmap 
     */
    public void populateTMTermsInDocs(TreeMap<String, LinkedList<Posting>> tmap){
        
        tmap.entrySet().stream().forEach((entriesTokenDocIdFreq) -> {
            Integer docs=0;
            docs = entriesTokenDocIdFreq.getValue().stream().map((_item) -> 1).reduce(docs, Integer::sum);
            termInDocs.put(entriesTokenDocIdFreq.getKey(), docs);
        }); 
    }
    
    /**
     * Método que retorna o valor tf da query dada a frequência desse termo n query.
     * @param freq
     * @return 1 + Math.log10(freq)
     */
    private double getTFQuery(int freq){
        return 1 + Math.log10(freq);
    }
    
    /**
     * Método que retorna o número de documentos onde o termo existe.
     * @param term
     * @return nr of docs
     */
    private int getNrDocsByTerm(String term){
        if(!termInDocs.containsKey(term))
            return 0;
        return termInDocs.get(term);
    }

    /**
     * Método que retorna o valor do idf dado o tamanho do corpus e o número de documentos
     * onde existe uma dada query.
     * @param corpusSize
     * @param nrDocsByTerm
     * @return 
     */
    private double calculateIdfQuery(int corpusSize, int nrDocsByTerm){
        if(nrDocsByTerm == 0)
            return 0;
        return Math.log10(corpusSize/nrDocsByTerm);
    }
    
    /**
     * Método que retorna o valor da normalização dos termos da query.
     * @param tmpMap
     * @param corpusSize
     * @return 
     */
    private double getNormalizationValueQuery(Map<String, Integer> tmpMap, Integer corpusSize){
       double norm=0;
       norm = tmpMap.entrySet().stream().map((entry) -> getTFQuery(entry.getValue()) * calculateIdfQuery(corpusSize, getNrDocsByTerm(entry.getKey()))).map((tmp) -> Math.pow(tmp, 2)).reduce(norm, (accumulator, _item) -> accumulator + _item); // tmp += tf * idf;
       return Math.sqrt(norm);
    }
    
    /**
     * Método responsável por calcular o ranking para uma dada query.
     * @param processedList
     * @param tmap
     * @param queryId
    */
    public void calculateRank(List processedList, TreeMap<String, LinkedList<Posting>> tmap, int queryId) {        
        
        // normalização: raiz quadrada do quadrado de todos os tfs da query        
        Map<String, Integer> tmpMap = new TreeMap<>();
        
        Set<String> unique = new HashSet<>(processedList);
        unique.forEach((key) -> {
            Integer freq = Collections.frequency(processedList, key);
            tmpMap.put(key, freq);
        });     

        double norm = getNormalizationValueQuery(tmpMap, cr.getCorpusSize());
        
        //Score for a document-query pair: sum over terms t in both q and d:             
        for(int query_term=0; query_term<processedList.size(); query_term++){
            String key = (String) processedList.get(query_term);

            if(tmap.containsKey(key)){
                LinkedList<Posting> tmpPostings = tmap.get(key);
                // term: 2:0.06801255271278411,588:0.08628994543116196,1039:0.09235327689371566        
                int termQueryFreq = Collections.frequency(processedList, key);

                tmpPostings.forEach((entryPosting) -> {
                    // score = Wt,d * Wt,q = (tfdoc*1) * (tfquery*idfquery)
                    double wtd = entryPosting.getTermWeight();
                    double wtq = (getTFQuery(termQueryFreq) * calculateIdfQuery(cr.getCorpusSize(), getNrDocsByTerm(key)))/norm; 
                    double score = wtd*wtq;
                    if (score != 0) {
                        addToQueryIdDocIdScoreTM(entryPosting.getDocId(), queryId, score, queryIdDocIdRank);
                    }
                });
            }
        }        
    }
    
    /**
     * Método responsável por calcular o ranking para uma dada query aplicando a expansão da query
     *  (Query Expansion).
     * @param processedList
     * @param tmap
     * @param queryId
     * @param numWords
    */
    public void calculateWeightsWord2vec(List processedList, TreeMap<String, LinkedList<Posting>> tmap, int queryId, int numWords) {        
        
        // normalização: raiz quadrada do quadrado de todos os tfs da query        
        Map<String, Integer> tmpMap = new TreeMap<>();
        List newProcessedList = new LinkedList<>();
        newProcessedList.addAll(processedList);
        
        // Adicionar termos semelhantes a cada termo da query à lista de termos processada
        for(int i=0; i<processedList.size(); i++){
            String term = (String) processedList.get(i);
            Collection<String> newWords=null;
            newWords = wv.generateNearestWords(term, numWords);

            if(newWords!=null){
                for(String newWord: newWords)
                    newProcessedList.add(newWord);
            }    
        }
        
        Set<String> unique = new HashSet<>(newProcessedList);
        unique.forEach((key) -> {
            Integer freq = Collections.frequency(newProcessedList, key);
            tmpMap.put(key, freq);
        });     

        double norm = getNormalizationValueQuery(tmpMap, cr.getCorpusSize());
        
        //Score for a document-query pair: sum over terms t in both q and d:             
        for(int query_term=0; query_term<newProcessedList.size(); query_term++){
            String key = (String) newProcessedList.get(query_term);

            if(tmap.containsKey(key)){
                LinkedList<Posting> tmpPostings = tmap.get(key);
                // term: 2:0.06801255271278411,588:0.08628994543116196,1039:0.09235327689371566        
                int termQueryFreq = Collections.frequency(newProcessedList, key);

                tmpPostings.forEach((entryPosting) -> {
                    // score = Wt,d * Wt,q = (tfdoc*1) * (tfquery*idfquery)
                    double wtd = entryPosting.getTermWeight();
                    double wtq = (getTFQuery(termQueryFreq) * calculateIdfQuery(cr.getCorpusSize(), getNrDocsByTerm(key)))/norm; 
                    double score = wtd*wtq;
                    if (score != 0) {
                        addToQueryIdDocIdScoreTM(entryPosting.getDocId(), queryId, score, queryIdDocIdRankWord2Vec);
                    }
                });
            }
        }        
    }
    
    /**
     * Método que verifica se documentos estão no ficheiro 'gold standard'
     * @param goldStandardRel
     * @param relDocs
     */
    public void checkRelevantAndNonRelevantDocs(Map<Integer, HashMap<Integer, Integer>> goldStandardRel, Map<Integer, ArrayList<Integer>> relDocs){
        
        relDocs.entrySet().forEach((entry) -> {
            ArrayList<Integer> tmpArrayList = entry.getValue(); // arraylist docs
            HashMap<Integer, Integer> docIdRelevanceHM = new HashMap<>();
            ArrayList<Integer> nonRelevantDocs = new ArrayList<>();
            HashMap<Integer, Integer> tmp = goldStandardRel.get(entry.getKey());
            tmpArrayList.stream().filter((docRel) -> (goldStandardRel.containsKey(entry.getKey()))).forEachOrdered((docRel) -> {
                if(tmp.containsKey(docRel))
                    docIdRelevanceHM.put(docRel, tmp.get(docRel));
                else
                    nonRelevantDocs.add(docRel);
            });
            relevantDocsHM.put(entry.getKey(), docIdRelevanceHM);
            nonRelevantDocsHM.put(entry.getKey(), nonRelevantDocs);
        });
    }

    /**
     * Método que retorna a soma das relevâncias dos documentos relevantes de modo explícito.
     * @param explicitRelevantDocs
     * @return 
     */
    public double getSumOfRelevant(HashMap<Integer, Integer> explicitRelevantDocs){
        int sum = 0;
        sum = explicitRelevantDocs.entrySet().stream().map((entry) -> entry.getValue()).reduce(sum, Integer::sum);
        return sum/(explicitRelevantDocs.size());
    }
    
    
    /**
     * Método responsável aplicar o algoritmo de rocchio e no fim por calcular os pesos da nova query.
     * @param processedList
     * @param tmap
     * @param explicitRelevantDocs
     * @param noRelevantDocs
     * @param implicitRelDocs
     * @param queryId
     * @param feedType 
     */
    public void calculateNewQuery(List processedList, TreeMap<String, LinkedList<Posting>> tmap, Map<Integer, 
            HashMap<Integer, Integer>> explicitRelevantDocs, Map<Integer,ArrayList<Integer>> noRelevantDocs, Map<Integer, ArrayList<Integer>> implicitRelDocs, int queryId, String feedType) {
        
        double alpha = 1.0;
        double betha = 0.75;
        double miu = 0.25;
        
        // normalização: raiz quadrada do quadrado de todos os tfs da query        
        Map<String, Integer> tmpMap = new TreeMap<>();
        
        Set<String> unique = new HashSet<>(processedList);
        unique.forEach((key) -> {
            Integer freq = Collections.frequency(processedList, key);
            tmpMap.put(key, freq);
        });     

        double norm = getNormalizationValueQuery(tmpMap, cr.getCorpusSize());
        
        HashMap<Integer, Integer> relevantDocsExplicit = explicitRelevantDocs.get(queryId); // docId relevance
        ArrayList<Integer> norelevantdocsExplicit = noRelevantDocs.get(queryId); // docId
        ArrayList<Integer> relevantDocsImplicit = implicitRelDocs.get(queryId); // [1, 44, 220, 43]
        
        TreeMap<String, Double> tmpTM = new TreeMap<>();
        
        //Score for a document-query pair: sum over terms t in both q and d:             
        for(int query_term=0; query_term<processedList.size(); query_term++){
            String key = (String) processedList.get(query_term);
       
            if(tmap.containsKey(key)){
                LinkedList<Posting> tmpPostings = tmap.get(key);
                // term: 2:0.06801255271278411,588:0.08628994543116196,1039:0.09235327689371566
                int termQueryFreq = Collections.frequency(processedList, key);
                
                if(feedType.equals("explicit")){
                    tmpPostings.forEach((entryPosting) -> {
                        // Betha
                        if(relevantDocsExplicit.containsKey(entryPosting.getDocId())){
                            int relevance = relevantDocsExplicit.get(entryPosting.getDocId());
                            double wtqOriginal = ((getTFQuery(termQueryFreq) * calculateIdfQuery(cr.getCorpusSize(), getNrDocsByTerm(key)))/norm)*alpha; 
                            double wtdPositive = betha*relevance*entryPosting.getTermWeight()/getSumOfRelevant(relevantDocsExplicit);
                            double finalScore = (wtqOriginal+wtdPositive); 
                            if(!tmpTM.containsKey(key))
                                tmpTM.put(key, finalScore);
                            else
                                tmpTM.replace(key, tmpTM.get(key)+wtdPositive);
                        } else{
                            double wtqOriginal = ((getTFQuery(termQueryFreq) * calculateIdfQuery(cr.getCorpusSize(), getNrDocsByTerm(key))) / norm) * alpha;
                            double wtdNegative = miu * entryPosting.getTermWeight()/(norelevantdocsExplicit.size());
                            double finalScore = (wtqOriginal - wtdNegative);
                            if(!tmpTM.containsKey(key))
                                tmpTM.put(key, finalScore);
                            else
                                tmpTM.replace(key, tmpTM.get(key)-wtdNegative);
                        }
                    });
                    
                }else if(feedType.equals("implicit")){
                    tmpPostings.forEach((entryPosting) -> {
                        if(relevantDocsImplicit.contains(entryPosting.getDocId())){
                            double wtqOriginal = ((getTFQuery(termQueryFreq) * calculateIdfQuery(cr.getCorpusSize(), getNrDocsByTerm(key)))/norm)*alpha; 
                            //double wtqPositive = tmpPostings.get(entryPosting.getDocId()).getTermWeight()*betha/(relevantDocsExplicit.size());
                            double wtdPositive = betha*entryPosting.getTermWeight()/(relevantDocsImplicit.size());
                            double finalScore = (wtqOriginal+wtdPositive); //* entryPosting.getTermWeight(); // * tf doc
                            if(!tmpTM.containsKey(key))
                                tmpTM.put(key, finalScore);
                            else
                                tmpTM.replace(key, tmpTM.get(key)+wtdPositive);
                        }
                    });
                }
            }
        }   
        
        // call calculateNewWeights
        calculateNewWeights(tmpTM, tmap, queryId, feedType);
    }
    
    /**
     * Método responsável por calcular os pesos da nova query.
     * @param tmpTM
     * @param tmap
     * @param queryId
     * @param feedType
     */
    public void calculateNewWeights(TreeMap<String, Double> tmpTM, TreeMap<String, LinkedList<Posting>> tmap, int queryId, String feedType){
        
        for (Map.Entry<String, Double> entry : tmpTM.entrySet()) {
            if(tmap.containsKey(entry.getKey())){
                LinkedList<Posting> tmpPostings = tmap.get(entry.getKey());

                tmpPostings.forEach((entryPosting) -> {
                    // score = Wt,d * Wt,q = (tfdoc*1) * (tfquery*idfquery)
                    double wtd = entryPosting.getTermWeight();
                    double wtq = entry.getValue();
                    double score = wtd*wtq;
                    if (score != 0) {
                        if(feedType.equals("explicit"))
                            addToQueryIdDocIdScoreTM(entryPosting.getDocId(), queryId, score, queryIdDocIdRankExplicit);
                        else if(feedType.equals("implicit"))
                            addToQueryIdDocIdScoreTM(entryPosting.getDocId(), queryId, score, queryIdDocIdRankImplicit);
                    }
                });
            }
        }
    }
    
    
    /**
     * Método que faz a contagem do score do tipo 1.
     * @param processedList
     * @param tmap
     * @param queryId
    */
    public void calculateScoreAssign2(List processedList, TreeMap<String, LinkedList<Posting>> tmap, int queryId){
        
        for(int query_term=0; query_term<processedList.size(); query_term++){
            String key = (String) processedList.get(query_term);
            if(tmap.containsKey(key)){
                LinkedList<Posting> tmpPostings = new LinkedList<>();
                tmpPostings = tmap.get(key);

                tmpPostings.forEach((entryPosting) -> {                        
                    addToQueryIdDocIdScoreTM(entryPosting.getDocId(), queryId, 1.0, queryIdDocIdScorei);                    
                    addToQueryIdDocIdScoreTM(entryPosting.getDocId(), queryId, entryPosting.getTermWeight(), queryIdDocIdScoreii);
                });
            }
        }
    }
    
    /**
     * Método responsável pela geração do ficheiro de saída.
     * @param filename
     * @param tmap 
     * @param assignment 
     */
    public void printToFile(String filename, TreeMap<Integer, HashMap<Integer,Double>> tmap, String assignment){
       try{
            File fileTwo = new File("./output/"+filename+".txt");
            FileOutputStream fos = new FileOutputStream(fileTwo);
            PrintWriter pw = new PrintWriter(fos);
            
            pw.println("Qi  doc_id  doc_score");
            if(assignment.equals("2")) {
                tmap.entrySet().forEach((entry) -> {
                    HashMap<Integer, Double> entryHM = entry.getValue();

                    entryHM.entrySet().forEach((entryReverse) -> {
                        pw.println(entry.getKey()+"     "+entryReverse.getKey()+"        "+entryReverse.getValue());
                    });
               });
                
            } else if(assignment.equals("3") || assignment.equals("4")) {
                tmap.entrySet().forEach((entry) -> {
                    HashMap<Integer, Double> entryHM = entry.getValue();
                    TreeMap<Double,Integer> tree = new TreeMap<>(Collections.reverseOrder());
                    entryHM.entrySet().forEach((entry2) -> {
                        tree.put(entry2.getValue(),entry2.getKey());
                    });

                    tree.entrySet().forEach((entryReverse) -> {
                        pw.println(entry.getKey()+"     "+entryReverse.getValue()+"        "+entryReverse.getKey());
                    });
               });       
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
                    Posting tmp = new Posting(Integer.parseInt(tmpPosting[0]), Double.parseDouble(tmpPosting[1]));
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
     * Metodo que retorna a TreeMap queryIdDocIdRank.
     * @return queryIdDocIdRank
     */    
    public TreeMap<Integer, HashMap<Integer, Double>> getQueryIdDocIdScoreTM(){
        return queryIdDocIdRank;
    }
    
    /**
     * Metodo que retorna a TreeMap queryIdDocIdRankImplicit.
     * @return queryIdDocIdRankImplicit
     */
    public TreeMap<Integer, HashMap<Integer, Double>> getQueryIdDocIdRankImplicit(){
        return queryIdDocIdRankImplicit;
    }
    
    /**
     * Metodo que retorna a TreeMap queryIdDocIdRankExplicit.
     * @return queryIdDocIdRankExplicit
     */
    public TreeMap<Integer, HashMap<Integer, Double>> getQueryIdDocIdRankExplicit(){
        return queryIdDocIdRankExplicit;
    }
    
    /**
     * Método que retorna a TreeMap queryIdDocIdRankWord2Vec.
     * @return 
     */
    public TreeMap<Integer, HashMap<Integer, Double>> getQueryIdDocIdRankWord2Vec(){
        return queryIdDocIdRankWord2Vec;
    }
    
    /**
     * Metodo que retorna a TreeMap relevantDocsHM.
     * @return nonRelevantDocsHM
     */
    public TreeMap<Integer, HashMap<Integer, Integer>> getRelDocsAndRelevance(){
        return relevantDocsHM;
    }
    
    /**
     * Metodo que retorna a TreeMap nonRelevantDocsHM.
     * @return queryIdDocIdRank
     */
    public TreeMap<Integer, ArrayList<Integer>> getNonRelevantDocs(){
        return nonRelevantDocsHM;
    }
    
    /**
     * Metodo que retorna a TreeMap queryIdDocIdScorei.
     * @return queryIdDocIdRank
     */  
    public TreeMap<Integer, HashMap<Integer, Double>> getQueryIdDocIdScorei(){
        return queryIdDocIdScorei;
    }
    
    /**
     * Metodo que retorna a TreeMap queryIdDocIdScoreii.
     * @return queryIdDocIdRank
     */  
    public TreeMap<Integer, HashMap<Integer, Double>> getQueryIdDocIdScoreii(){
        return queryIdDocIdScoreii;
    }
    
    /**
     * Método que retorna o tamanho do corpus.
     * @return 
     */
    public int sizeCorpus(){
        return cr.getCorpusSize();
    }
}