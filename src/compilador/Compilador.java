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
    public static String path, linha;
    public static int erroLinha, posLinha;
    public static AnalisadorLexico analisadorLexico = new AnalisadorLexico();
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
      path = args[0];
      erroLinha=0;

      buffRead = new BufferedReader(new FileReader(path));
      analisadorLexico.inicializarHash();
      while( (linha = buffRead.readLine())!= null ){ 
         erroLinha++;
         posLinha = 0;
         analisadorLexico.analisadorLexico(linha, posLinha);
      }
      System.out.println("SUCESSO");
    }
    
}
