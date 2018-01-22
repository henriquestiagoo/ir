/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4.relevanceFeedback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/**
 * FeedBack Class.
 */
public class Feedback {
    private final Map<Integer, ArrayList<Integer>> relevantDocsImplicit;
    private final Map<Integer, HashMap<Integer, Integer>> relevantDocsExplicit;
    private final int nDocs = 10;

    public Feedback(){
        relevantDocsImplicit = new HashMap<>();
        relevantDocsExplicit = new HashMap<>();
    }

    /**
     * Método responsável por colocar em memória os documentos relevantes dependendo do método (Implicito ou explicito).
     * @param filename
     * @param method 
     */
    public void getRelevantDocs(String filename, String method){
        int queryId, docId, relevance;
        int i = 0;
        int tmpQueryId = -1;

        if(method.equals("implicit")){
            // Implict method
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                br.readLine(); // this will read the first line
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    //1 184 2
                    String[] lineSplit = sCurrentLine.split("     ");
                    queryId = Integer.parseInt(lineSplit[0]);
                    docId = Integer.parseInt(lineSplit[1]);

                    if(tmpQueryId == -1)
                        tmpQueryId = queryId;

                    if(queryId == tmpQueryId){
                        try{
                            //relevance = Integer.parseInt(lineSplit[2]);
                            if(i < nDocs){
                                relevantDocsImplicit.computeIfAbsent(queryId, k -> new ArrayList()).add(docId);
                                ++i;
                            }
                        }catch(NumberFormatException ex){}
                    }else{
                        i=0;
                        tmpQueryId=-1;
                        try{
                            //relevance = Integer.parseInt(lineSplit[2]);
                            if(i < nDocs){
                                relevantDocsImplicit.computeIfAbsent(queryId, k -> new ArrayList()).add(docId);
                                ++i;
                            }
                        }catch(NumberFormatException ex){}
                    }

                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("explicit")){
            // Explict method - goald standard
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    //1 184 2
                    String[] lineSplit = sCurrentLine.split(" ");
                    queryId = Integer.parseInt(lineSplit[0]);
                    docId = Integer.parseInt(lineSplit[1]);
                    if(tmpQueryId == -1)
                        tmpQueryId = queryId;
                    if(queryId == tmpQueryId){
                        try{
                            relevance = Integer.parseInt(lineSplit[2]);
                            if(i < nDocs){
                                //relevantDocsExplict.computeIfAbsent(queryId, k -> new ArrayList()).add(docId);
                                // para dar mais relevancia aos ficheiros mais relevantes (pois o mais relevante tem valor 1)
                                relevantDocsExplicit.computeIfAbsent(queryId, k -> new HashMap<>()).put(docId,(5 - relevance));
                                ++i;
                            }
                        }catch(NumberFormatException ex){}
                    }else{
                        i=0;
                        tmpQueryId=-1;
                        try{
                            relevance = Integer.parseInt(lineSplit[2]);
                            if(i < nDocs){
                                //relevantDocsExplict.computeIfAbsent(queryId, k -> new ArrayList()).add(docId);
                                // para dar mais relevancia aos ficheiros mais relevantes (pois o mais relevante tem valor 1)
                                relevantDocsExplicit.computeIfAbsent(queryId, k -> new HashMap<>()).put(docId,(5 - relevance));
                                ++i;
                            }
                        }catch(NumberFormatException ex){}
                    }

                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método responsável por retornar a estrutura relevantDocsImplicit.
     * @return 
     */
    public Map<Integer, ArrayList<Integer>> getImplicitRelevantDocsHM(){
        return relevantDocsImplicit;
    }

    /**
     * Método responsável por returnar a estrutura relevantDocsExplicit;
     * @return 
     */
    public Map<Integer, HashMap<Integer,Integer>> getExplicitRelevantDocsHM(){
        return relevantDocsExplicit;
    }
}