/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.*;
import java.lang.*;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Pedro
 */
public class Compilador {
    
    public static BufferedReader buffRead;
    public static String path, linha, token_atual;
    public static int erroLinha, posLinha;
    public static AnalisadorLexico analisadorLexico = new AnalisadorLexico();
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
      
    //path = args[0];
      path = "C:/Users/Pedro/Documents/FACULDADE/Compiladores/COMPILADOR/Compilador/src/compilador/teste.txt";
      erroLinha=0;

      buffRead = new BufferedReader(new FileReader(path));
      analisadorLexico.inicializarHash();
      while( (linha = buffRead.readLine())!= null ){ 
         erroLinha++;
         posLinha = 0;
         while(posLinha < linha.length()){
            token_atual = analisadorLexico.analisadorLexico(linha, posLinha);
            System.out.println(token_atual);
         }
      }
      //System.out.println("SUCESSO");
    }
    
}
