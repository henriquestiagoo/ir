/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4.indexer;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class Posting {
    
    private final Integer docId;
    private final double termWeight;
    
    /**
     * Constructor para a identificação de um Posting que é caracterizado
     * por identificador do documento e frequência respetiva.
     * @param docId
     * @param termWeight
     */
    public Posting(Integer docId, double termWeight) {
        this.docId = docId;
        this.termWeight = termWeight;
    }
    
    /**
     * Método que retorna o identificador do documento.
     * @return docId
     */
    public Integer getDocId(){
        return docId;
    }
    
    /**
     * Método que retorna a frequência de repetição da palavra nesse documento.
     * @return frequency
     */
    public double getTermWeight(){
        return termWeight;
    }
}