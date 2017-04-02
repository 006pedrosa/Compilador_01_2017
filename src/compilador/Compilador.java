/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import static compilador.AnalisadorLexico.lex;
import static compilador.AnalisadorLexico.tS;
import java.io.*;
import java.lang.*;
import java.util.HashMap;
import java.util.Map;



public class Compilador {
    
    public static BufferedReader buffRead;
    public static String path, linha, token_atual, lex;
    public static AnalisadorLexico analisadorLexico = new AnalisadorLexico();
    public static int erroLinha, posLinha; 
    public static Map<String, String> tS = new HashMap<String, String>();
   
    //-----------------------------------------------ANALISADOR LEXICO----------------------
    
    public static void inicializarHash(){
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
   
    public static void chamaTabela(){
      if(buscaHash(lex) == null){
         if(Character.isDigit(lex.charAt(0))){
            setHash(lex, "const");
         }else{
            setHash(lex, "id");
         }
       }
   }
   
   public static String analisadorLexico(String linhaAnalisador) throws IOException{
       String token_retorno = null;
       lex = "";
       
       if(linha.length() != 0){
            token_retorno = automatoLexico(linhaAnalisador);  
        }
       
       if(token_retorno == null){
           linha = buffRead.readLine();
           posLinha = 0;
           erroLinha++;
           return buscaHash(analisadorLexico(linha));
       }
       return buscaHash(token_retorno);
   }
   
   public static String automatoLexico(String linha){
     int estado = 0;
     
     for(int i = posLinha; i<=linha.length();i++){
        switch(estado){
         case 0:
            if(Character.isLetter(linha.charAt(i)) || linha.charAt(i) == '_'){
               lex += linha.charAt(i);
               estado = 1;
            }else if(linha.charAt(i) == '_'){
               lex += linha.charAt(i);
               estado = 3;
            }else if(Character.isDigit(linha.charAt(i))){
               lex += linha.charAt(i);
               estado = 4;
            }else if(linha.charAt(i) == '<'){
               lex += linha.charAt(i);
               estado = 5;
            }else if(linha.charAt(i) == '>'){
               lex += linha.charAt(i);
               estado = 6;
            }else if(linha.charAt(i) == '/'){
               lex += linha.charAt(i);
               estado = 7;
            }else if(linha.charAt(i) == '='){
               lex += linha.charAt(i);
               estado = 10;
            }else if(linha.charAt(i) == '+' || linha.charAt(i) == '-' || linha.charAt(i) == ',' || linha.charAt(i) == '*' || linha.charAt(i) == ';' || linha.charAt(i) == '(' || linha.charAt(i) == ')'){
               lex += linha.charAt(i);
               //i--;
               estado = 2;
            }
            break;
         case 1:
             if(Character.isLetter(linha.charAt(i)) || linha.charAt(i) == '_' || Character.isDigit(linha.charAt(i))){
                  lex += linha.charAt(i);
                  if(i == linha.length()-1){
                      estado = 2;
                  }else{
                    estado = 1;
                  }
             }else{
                 i--;
                 estado = 2;
             }
             break;
         case 2:
             chamaTabela();
             posLinha = i;
             return lex;
             
         case 3:
             if(linha.charAt(i) == '_'){
                 lex += linha.charAt(i);
                 estado = 3;
             }else if(Character.isLetter(linha.charAt(i)) || Character.isDigit(linha.charAt(i))){
                 lex += linha.charAt(i);
                 estado = 1;
             }else{
                 estado = 666;
             }
             break;
         case 4:
             if(Character.isDigit(linha.charAt(i))){
               lex += linha.charAt(i);
               estado = 4;
            }else{
               i--;
               estado = 2;
             }
             break;
         case 5:
             if(linha.charAt(i) == '='){
                 lex += linha.charAt(i);
                 estado = 2;
             }else{
                 i--;
                 estado = 2;
             }
             break;
         case 6:
             if(linha.charAt(i) == '='){
                 lex += linha.charAt(i);
                 estado = 2;
             }else{
                 i--;
                 estado = 2;
             }
             break;
         case 7:
             if(linha.charAt(i) == '*'){
                 lex = "";
                 estado = 8;
             }else{
                 i--;
                 estado = 2;
             }
             break;
         case 8:
             if(linha.charAt(i) == '*'){
                 estado = 9;
             }else{
                 estado = 8;
             }
             break;
         case 9:
             if(linha.charAt(i) == '/'){
                 estado = 0;
                 if(i == linha.length()-1){
                     i++;
                 }
                 posLinha = i;
             }else{
                 estado = 8;
             }
             break;
        case 10:
             if(linha.charAt(i) == '='){
                 lex += linha.charAt(i);
                 estado = 2;
             }else{
                 i--;
                 estado = 2;
             }
             break;
         case 666:
               System.out.println("ERRO");
               break;
             // --------- FIM CASE 666 ----------
            default:
               System.out.println("ERRO");
             
        }
    }
     return null;
   }
    
   //-----------------------------------------------FIM DO ANALISADOR LEXICO---------------
   
   //--------------------------------------------ANALISADOR SINTATICO-----------------------
   
   //Metodo analisadorSintatico
   public static void analisadorSintatico() throws IOException{
      inicializarHash();
      linha = buffRead.readLine();
      erroLinha++;
      posLinha = 0;
      token_atual = analisadorLexico(linha);
      S();
   }
   //Metodo casaToken   
   public static void casaToken(String token_esperado) throws IOException{
       if(token_atual == token_esperado){
           token_atual = analisadorLexico(linha);
       }else{
            System.out.println("ERRO NA LINHA "+ erroLinha + " Token recebido: "+ token_atual);
            System.exit(0);
        }
   }
   //Metodo S
   public static void S() throws IOException{
       while(token_atual != "main"){
           DECLARACAO();
       }
       casaToken("main");
       while(token_atual != "end"){
           COMANDO();
       }
       casaToken("end");
   }
   //Metodo DECLARACAO
   public static void DECLARACAO() throws IOException{
       if(token_atual == "const"){
           DC();
       }else{
           DV();
       }
   }
   //Metodo DV
   public static void DV() throws IOException{
       TIPO();
       casaToken("id");
       Y();
       casaToken(";");
   }
   //Metodo DC
   public static void DC() throws IOException{
       casaToken("const");
       casaToken("id");
       Y();
       casaToken(";");
   }
   //Metodo TIPO
   public static void TIPO() throws IOException{
       if(token_atual == "integer"){
           casaToken("integer");
       }else if(token_atual == "byte"){
           casaToken("byte");
       }else if(token_atual == "string"){
           casaToken("string");
       }else{
           casaToken("boolean");
       }
   }
   //Metodo Y
   public static void Y() throws IOException{
       if(token_atual == "="){
           casaToken("=");
           EXP();
           V();
       }else{
           V();
       }
   }
   
   //Metodo V
   public static void V() throws IOException{
       if(token_atual == ";"){
           casaToken(";");
       }else{
           casaToken(",");
           casaToken("id");
           Y();
       }
   }
   //Metodo COMANDO
   public static void COMANDO(){
	   if(token_atual == "id"){
		   CA();
	   }else if(token_atual == "while"){
		   CR();
	   }else if(token_atual =="if"){
		   CA();
	   }else if(token_atual == ";"){
		   CN();
	   }else if(token_atual == "readln"){
		   CL();
	   }else if(token_atual == "white" || token_atual == "writeln"){
		   CE();
	   }
   }
   //Metodo CA
   public static void CA() throws IOException{
		casaToken("id");
		casaToken("=");
		EXP();
		casaToken(";");
   }
   //Metodo CR
   public static void CR() throws IOException{
	   casaToken("while");
	   casaToken("(");
	   EXP();
	   casaToken(")");
	   X();
	   
   }
   //Metodo X
   public static void X() throws IOException{
	   if(token_atual == "begin"){
		   casaToken("begin");
		   while(token_atual!= "end"){              // Verificar se esta correto
			   COMANDO();
		   }
		   casaToken("end");
	   }else{
		   COMANDO();
	   }
   }
   //Metodo CT
   public static void CT() throws IOException{
		casaToken("if");
		casaToken("(");
		EXP();
		casaToken(")");
		casaToken("then");
		CT_A();
   }
   //Metodo CT_A
   public static void CT_A() throws IOException{
	   if(token_atual =="begin"){
		   casaToken("begin");
		   while(token_atual=!"end"){
			   COMANDO();
		   }
		   casaToken("end");
		   if(token_atual == "else"){
			   casaToken("else");
			   casaToken("begin");
			   while(token_atual!= "end"){
				   COMANDO();
			   }
		   }
	   }else{
		   COMANDO();
		   if(token_atual =="else"){
			   casaToken("else");
			   COMANDO();
		   }
	   }
   }
   //Metodo CN
   public static void CN() throws IOException{
	   casaToken(";");
   }
   //Metodo CL
   public static void CL() throws IOException{
	   casaToken("readln");
	   casaToken("(");
	   casaToken("id");
	   casaToken(")");
	   casaToken(";");
   }
   //Metodo CE
   public static void CE() throws IOException{
	   if(token_atual == "white"){
		   casaToken("while");
		   casaToken("(");
		   EXP();
		   while(token_atual != ")"){
			   casaToken(",");
			   EXP();
		   }
		   casaToken(")");
		   casaToken(";");
	   }else{
		   casaToken("writeln");
		   casaToken("(");
		   EXP();
		   while(token_atual != ")"){
			   casaToken(",");
			   EXP();
		   }
		   casaToken(")");
		   casaToken(";");
	   }
   }
   //Metodp EXP
   public static void EXP()throws IOException{
	   EXPS();
	   if(token_atual =="<"){
		   casaToken("<");
		   EXPS();
	   }else if(token_atual==">"){
		   casaToken(">");
		   EXPS();
	   }else if(token_atual=="<="){
		   casaToken("<=");
		   EXPS();
	   }else if(token_atual==">="){
		   casaToken(">=");
		   EXPS();
	   }else if(token_atual=="=="){
		   casaToken("==");
		   EXPS();
	   }else if(token_atual=="!="){
		   casaToken("!=");
		   EXPS();
	   }
	}
   //Metodo EXPS
   public static void EXPS()throws IOException{
	   if(token_atual == "+"){
		   casaToken("+");
		   T();
	   }else if(token_atual == "-"){
		   casaToken("-");
		   T();
	   }else{
		   T();
		   while(token_atual != "+" || token_atual != "-" || token_atual != "or"){
			   if(token_atual == "+"){
				   casaToken("+");
				   T();
			   }else if(token_atual == "-"){
				   casaToken("-");
				   T();
			   }else if(token_atual == "or"){
				   casaToken("or");
				   T();
			   }
		   }//fim while
	   }
	   
   }
   //Metodo T
   public static void T() throws IOException{
	   F();
	   while(token_atual != "*" || token_atual != "/" || token_atual != "and"){
		   if(token_atual == "*"){
			   casaToken("*");
			   F();
		   }else if(token_atual == "/"){
			   casaToken("/");
			   F();
		   }else if(token_atual == "and"){
			   casaToken("and");
			   F();
		   }
	   }//fim while
   }
   //Metodo F
   public static void F() throws IOException{
	   if(token_atual == "("){
		   casaToken("(");
		   EXP();
		   casaToken(")");
	   }else if(token_atual == "id"){
		   casaToken("id");
	   }else if(token_atual == "const"){
		   casaToken("const");
	   }else if(token_atual == "not"){
		   casaToken("not");
		   F();
	   }else if(token_atual == "TRUE"){
		   casaToken("TRUE");
	   }else if (token_atual == "FALSE"){
		   casaToken("FALSE");
	   }
   }
   
   //--------------------------------------------FIM DO ANALISADOR SINTATICO
   
    
    //PROGRAMA PRINCIPAL
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
    //path = args[0];
      path = "C:/Users/Pedro/Documents/FACULDADE/Compiladores/COMPILADOR/Compilador/src/compilador/teste.txt";
      erroLinha=0;

      buffRead = new BufferedReader(new FileReader(path));
      analisadorSintatico();
      
      /*while( (linha = buffRead.readLine())!= null ){ 
         erroLinha++;
         posLinha = 0;
         //System.out.println(linha.length());
         while(posLinha < linha.length()){
            token_atual = analisadorLexico(linha);
            System.out.println(token_atual);
         }
      }*/
      //System.out.println("SUCESSO");
    }
    
}
