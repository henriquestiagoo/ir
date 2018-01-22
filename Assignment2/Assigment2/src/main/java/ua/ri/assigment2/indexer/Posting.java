/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment2.indexer;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class Posting {
    
    private final Integer docId;
    private final Integer frequency;
    
    /**
     * Constructor para a identificação de um Posting que é caracterizado
     * por identificador do documento e frequência respetiva.
     * @param docId
     * @param frequency
     */
    public Posting(Integer docId, Integer frequency) {
        this.docId = docId;
        this.frequency = frequency;
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
    public Integer getFreq(){
        return frequency;
    }
}