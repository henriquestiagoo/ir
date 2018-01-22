/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4.queries;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class DocWeight {
    
    private final Integer docId;
    private final double weightScore;
    
    /**
     * Constructor para a identificação de um DocScore que é caracterizado
     * por identificador do documento e o score respetivo.
     * @param docId
     * @param weight
     */
    public DocWeight(Integer docId, double weight) {
        this.docId = docId;
        this.weightScore = weight;
    }
    
    /**
     * Método que retorna o identificador do documento.
     * @return docId
     */
    public Integer getDocId(){
        return docId;
    }
    
    /**
     * Método que retorna o score nesse documento.
     * @return frequency
     */
    public double getWeightScore(){
        return weightScore;
    }
}