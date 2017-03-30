/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.HashMap;
import java.util.Map;

public class AnalisadorLexico {
    
   //Hash Tabela de Simbolos

    /**
     *
     */
   protected static Map<String, String> tS = new HashMap<String, String>();
   public static String lex;
    /**
     *
     */
    public void inicializarHash(){
      tS.put("const", "const");
      tS.put("integer", "integer");
      tS.put("byte", "byte");
      tS.put("string", "string");
      tS.put("boolean", "boolean");   
      tS.put("while", "while");
      tS.put("if", "if");
      tS.put("else", "else");
      tS.put("and", "logica");
      tS.put("or", "logica");
      tS.put("not", "negacao");
      tS.put("=", "atribuicao");
      tS.put("(", "(");
      tS.put(")", ")");
      tS.put("<", "comparacao");
      tS.put("<=", "comparacao");
      tS.put(">", "comparacao");
      tS.put(">=", "comparacao");
      tS.put("!=", "comparacao");
      tS.put("==", "comparacao");
      tS.put(",", ",");
      tS.put("+", "+");
      tS.put("*", "*");
      tS.put("/", "/");
      tS.put("-", "-");
      tS.put(";", ";");
      tS.put("main", "main");
      tS.put("begin", "begin");
      tS.put("end", "end");
      tS.put("then", "then");
      tS.put("readln", "readln");
      tS.put("write", "write");
      tS.put("writeln", "writeln");
      tS.put("true", "true");
      tS.put("false", "false");
   }
    
   public static void setHash(String token, String lexema){
      tS.put(token, lexema);
   }
   //Efetua uma busca na hash pelo token desejado, retorna null se não encontrado
   public static String buscaHash(String token){
      return tS.get(token);
   }
   
   public void analisadorLexico(String linha, int posLinha){
       
   }
   
}
