/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment1.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/*
 * Create a simple tokenizer that splits on whitespace, lowercases tokens, removes all nonalphabetic 
 * characters, and keeps only terms with 3 or more characters
*/
public class SimpleTokenizer {   

    /**
     * Construtor do Simple Tokenizer.
    */
    public SimpleTokenizer(){
    }
    
    
    /**
     * Método que converte uma palavra para minúsculas.
     * @param s
     * @return
     */
    public String toLowerCase(String s){
        return s.toLowerCase();
    }
    
    /**
     * Método que verifica se a palavra tem 3 ou mais caracteres.
     * @param s
     * @return
     */
    public boolean minTerm(String s){
        return s.length() >= 3;
    } 
    
    /**
     * Método que faz o processamento do texto proveniente de um documento.
     * @param text
     * @return list
     */
    public List textProcessor(String text) {
        // Remove all non-alphabetic characters and pontuation marks
        List<String> list = new ArrayList<>(Arrays.asList(text.split("[^A-Za-z]")));
       
        list = list.stream()
                .filter(s-> minTerm(s))
                .map(s-> s.toLowerCase())
                .collect(Collectors.toList());
        return list;
    }
}