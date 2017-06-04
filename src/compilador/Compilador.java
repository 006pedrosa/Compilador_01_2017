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
    public static String path, linha, token_atual, lex, tipoId;
    //public static AnalisadorLexico analisadorLexico = new AnalisadorLexico();
    public static int erroLinha, posLinha;
    public static Map<String, String> tS = new HashMap<String, String>();
    public static Map<String, String> hashTipo = new HashMap<String, String>();
    public static Map<String, String> hashClasse = new HashMap<String, String>();
    

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
      tS.put("and", "and");
      tS.put("or", "or");
      tS.put("not", "not");
      tS.put("=", "=");
      tS.put("(", "(");
      tS.put(")", ")");
      tS.put("<", "<");
      tS.put("<=", "<=");
      tS.put(">", ">");
      tS.put(">=", ">=");
      tS.put("!=", "!=");
      tS.put("==", "==");
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

   public static void setHash(String token, String lexema, String classe){
      tS.put(token, lexema);
      hashTipo.put(token, tipoId);
      hashClasse.put(token, classe);
   }
   
   //Efetua uma busca na hashTipo pelo tipo desejado em função do token, retorna null se não encontrado
   public static String getTipo(String token){
       return hashTipo.get(token);
   }
   //Efetua uma busca na hash pelo token desejado, retorna null se não encontrado
   public static String buscaHash(String token){
      return tS.get(token);
   }

    public static void chamaTabela(){
      if(buscaHash(lex) == null){
         if(Character.isDigit(lex.charAt(0)) || lex.charAt(0) == '\''){
            setHash(lex, "const", "classe-const");
         }else{
            setHash(lex, "id", "classe-var");
         }
       }else if (buscaHash(lex) == "id" || buscaHash(lex) == "const"){
          System.out.println("identificador já declarado "+ lex);
          System.exit(0);
      }
   }

   public static String analisadorLexico(String linhaAnalisador) throws IOException{
       String token_retorno = null;
       lex = "";
       if(linha == null){
           return "EOF";
       }
       if(posLinha == linha.length()){
          linha = buffRead.readLine();
          if(linha != null){
           posLinha = 0;
           erroLinha++;
           linhaAnalisador = linha;
          } else {
            return "EOF";
          }
       }

       if(linha.length() != 0){
            token_retorno = automatoLexico(linhaAnalisador);
        }

       if(token_retorno == null){
           linha = buffRead.readLine();
           if(linha != null){
                posLinha = 0;
                erroLinha++;
                return buscaHash(analisadorLexico(linha));
           } else {
                return "EOF";
          }
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
            }else if(linha.charAt(i) == '\''){
               lex += linha.charAt(i);
               //i--;
               estado = 11;
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
                 if(i == linha.length()-1){
                    System.out.println("ERRO NA LINHA "+ erroLinha + " Token recebido: "+ token_atual);
                    System.exit(0);
                 }else{
                     estado = 8;
                 }
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
        case 11:
             if(linha.charAt(i) == '\''){
                 lex += linha.charAt(i);
                 estado = 12;
             }else{
                 if(i == linha.length()-1){
                    estado = 2;
                 }else{
                    lex += linha.charAt(i);
                    estado = 11;
                 }

             }
             break;
        case 12:
             if(linha.charAt(i) == '\''){
                 estado = 11;
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
      if(token_atual != "EOF"){
          System.out.println("ERRO NA LINHA "+ erroLinha + " Token recebido: "+ token_atual);
          System.exit(0);
      }
   }
   //Metodo casaToken
   public static void casaToken(String token_esperado) throws IOException{
       if(token_atual == token_esperado){
           token_atual = analisadorLexico(linha);
           //System.out.println(token_atual);
       }else{
            System.out.println("ERRO NA LINHA "+ erroLinha + " Token recebido: "+ token_atual + " TOKEN ESPERADO: "+token_esperado);
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
       String id_tipo = TIPO();
       casaToken("id");
       Y(id_tipo);
   }
   //Metodo DC
   public static void DC() throws IOException{
       String id_tipo = "";
       String auxLex = "";
       
       casaToken("const");
       casaToken("id");
       
       auxLex = lex;
       id_tipo = getTipo(auxLex);
       Y(id_tipo);
   }
   //Metodo TIPO
   public static String TIPO() throws IOException{
       String tipo_tipo = "";
       if(token_atual == "integer"){
           tipoId = "tipo-inteiro";
           casaToken("integer");
       }else if(token_atual == "byte"){
           tipoId = "tipo-byte";
           casaToken("byte");
       }else if(token_atual == "string"){
           tipoId = "tipo-string";
           casaToken("string");
       }else{
           tipoId = "tipo-logico";
           casaToken("boolean");
       }
       tipo_tipo = tipoId; //herda o tipo
       return tipo_tipo;
   }
   //Metodo Y
   public static void Y(String Y_tipo) throws IOException{
       String EXP_tipo = "";
       if(token_atual == "="){
           casaToken("=");
           EXP_tipo = EXP();
           
           //Acao semantica: 21
           if(EXP_tipo != Y_tipo){
               //LEMBRAR ERRO criar um metodo para imprimir os erros e parar a execucao (exit por exemplo)
               System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
           }
           
           //Acao semantica: 22
           V(Y_tipo);
       }else{
           //Acao semantica: 22
           V(Y_tipo);
       }
   }

   //Metodo V
   public static void V(String V_tipo) throws IOException{
       if(token_atual == ";"){
           casaToken(";");
       }else{
           casaToken(",");
           casaToken("id");
           //Acao semantica: 24
           Y(V_tipo);
       }
   }
   //Metodo COMANDO
   public static void COMANDO() throws IOException{
	   if(token_atual == "id"){
		   CA();
	   }else if(token_atual == "while"){
		   CR();
	   }else if(token_atual =="if"){
		   CT();
	   }else if(token_atual == ";"){
		   CN();
	   }else if(token_atual == "readln"){
		   CL();
	   }else if(token_atual == "write" || token_atual == "writeln"){
		   CE();
	   }else{
                System.out.println("ERRO NA LINHA "+ erroLinha + " Token recebido: "+ token_atual);
                System.exit(0);
           }
   }
   //Metodo CA
public static void CA() throws IOException{
            String EXP_tipo = "";
            String id_tipo = "";
            String auxLex = lex;
            
            casaToken("id");
            id_tipo = getTipo(auxLex); 
            if(id_tipo == null ){
                System.out.println("ID NÃO DECLARADO, LINHA: "+ erroLinha);
                System.exit(0);
            }
            casaToken("=");
            EXP_tipo = EXP();
            
            if(id_tipo != EXP_tipo){
                System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
            }
            casaToken(";");
   }
   //Metodo CR
   public static void CR() throws IOException{
	   String CR_tipo = "";
           casaToken("while");
	   casaToken("(");
	   
           CR_tipo = EXP();
           
           if(CR_tipo != "tipo-logico"){
               System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
           }
	   casaToken(")");
	   X();

   }
   //Metodo X
   public static void X() throws IOException{
	   if(token_atual == "begin"){
		   casaToken("begin");
		   while(token_atual != "end"){              // Verificar se esta correto
			   COMANDO();
		   }
		   casaToken("end");
	   }else{
		   COMANDO();
	   }
   }
   //Metodo CT
   public static void CT() throws IOException{
                String aux = "";
		casaToken("if");
		casaToken("(");
		aux = EXP();
		casaToken(")");
		casaToken("then");
		CT_A();
   }
   //Metodo CT_A
   public static void CT_A() throws IOException{
	   if(token_atual =="begin"){
		   casaToken("begin");
		   while(token_atual != "end"){
			   COMANDO();
		   }
		   casaToken("end");
		   if(token_atual == "else"){
			   casaToken("else");
			   casaToken("begin");
			   while(token_atual!= "end"){
				   COMANDO();
			   }
                           casaToken("end");
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
        String auxLex = "";
        String id_tipo = "";
        
        casaToken("readln");
        casaToken("(");
         
        auxLex = lex;
        casaToken("id");
        id_tipo = getTipo(lex);
        
        //ACAO SEMANTICA 30
        if(id_tipo == null){
                System.out.println("ID NÃO DECLARADO, LINHA: "+ erroLinha);
                System.exit(0);
         }
         casaToken(")");
         
         //ACAO SEMANTICA 28
         
         casaToken(";");
    }
   //Metodo CE
   public static void CE() throws IOException{
           String CE_tipo = "";
	   if(token_atual == "write"){
		   casaToken("write");
		   casaToken("(");
		   CE_tipo = EXP();
                   if(CE_tipo == "tipo-logico"){
                        System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                   }
		   while(token_atual != ")"){
			casaToken(",");
			CE_tipo = EXP();
                        if(CE_tipo == "tipo-logico"){
                            System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                        }
		   }
		   casaToken(")");
		   casaToken(";");
	   }else{
		   casaToken("writeln");
		   casaToken("(");
		   CE_tipo = EXP();
                   if(CE_tipo == "tipo-logico"){
                        System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                    }
		   while(token_atual != ")"){
                        casaToken(",");
                        CE_tipo = EXP();
                        if(CE_tipo == "tipo-logico"){
                            System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                        }
		   }
		   casaToken(")");
		   casaToken(";");
	   }
   }
   //Metodo EXP
   public static String EXP()throws IOException{
	   String EXP_tipo = ""; //recebe o tipo retornado pelo EXPS
           String EXPS1_tipo = "";
           String auxToken = "";
           EXP_tipo = EXPS();
           auxToken = token_atual;
	   if(token_atual =="<"){
		   casaToken("<");
		   EXPS1_tipo = EXPS();
	   }else if(token_atual == ">"){
		   casaToken(">");
		   EXPS1_tipo = EXPS();
	   }else if(token_atual == "<="){
		   casaToken("<=");
		   EXPS1_tipo = EXPS();
	   }else if(token_atual == ">="){
		   casaToken(">=");
		   EXPS1_tipo = EXPS();
	   }else if(token_atual == "=="){
		   casaToken("==");
		   EXPS1_tipo = EXPS();
	   }else if(token_atual =="!="){
		   casaToken("!=");
		   EXPS1_tipo = EXPS();
	   }
           
           //semantico
           if(auxToken == "=="){
               if(EXP_tipo != EXPS1_tipo){
                   if(EXP_tipo == "tipo-string" || EXP_tipo == "tipo-logico" || EXPS1_tipo == "tipo-string" || EXPS1_tipo == "tipo-logico"){
                       System.out.println("Erro: Tipos incompativeis - Linha: "+erroLinha);
                   } else {
                       EXP_tipo = "tipo-logico";
                   }
               } else {
                   EXP_tipo = "tipo-logico";
               }
           } else {
               if(EXP_tipo == "tipo-string" || EXPS1_tipo == "tipo-string"){
                   System.out.println("Erro: Tipos incompativeis - Linha: "+erroLinha);                  
               } else {
                   EXP_tipo = "tipo-logico";
               }
           }
           return EXP_tipo;
	}
   //Metodo EXPS
   public static String EXPS()throws IOException{
           String EXPS_tipo = "";
           String T1_tipo = "";
           String auxToken = "";
	   if(token_atual == "+"){
		   casaToken("+");
		   EXPS_tipo = T();
                   if(EXPS_tipo == "tipo-string" || EXPS_tipo == "tipo-logico"){
                       System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                   } else {
                       EXPS_tipo = "tipo-inteiro";
                   }
	   }else if(token_atual == "-"){
		   casaToken("-");
		   EXPS_tipo = T();
                   if(EXPS_tipo == "tipo-string" || EXPS_tipo == "tipo-logico"){
                       System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                   } else {
                       EXPS_tipo = "tipo-inteiro";
                   }
	   }else{
		   EXPS_tipo = T();
		   while(token_atual == "+" || token_atual == "-" || token_atual == "or"){
                           auxToken = token_atual;
			   if(token_atual == "+"){
				   casaToken("+");
				   T1_tipo = T();
			   }else if(token_atual == "-"){
				   casaToken("-");
				   T1_tipo = T();
			   }else if(token_atual == "or"){
				   casaToken("or");
				   T1_tipo = T();
			   }
                           if(auxToken == "or"){
                               if(EXPS_tipo != "tipo-logico" || T1_tipo != "tipo-logico"){
                                    System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                               }
                           }else if(auxToken == "+" || auxToken == "-"){
                               if(EXPS_tipo == "tipo-logico" || T1_tipo == "tipo-logico"){
                                    System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                               }else{
                                    if(auxToken == "-"){
                                        if(EXPS_tipo == "tipo-string" || T1_tipo == "tipo-logico"){
                                            System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                                        } else {
                                            EXPS_tipo = "tipo-inteiro";
                                        }
                                    } else {
                                        if(EXPS_tipo == "tipo-string" || T1_tipo == "tipo-string"){
                                            EXPS_tipo = "tipo-string";
                                        } else {
                                            EXPS_tipo = "tipo-inteiro";
                                        }
                                    }
                               }
                           }
		   }//fim while
                   
	   }
           return EXPS_tipo;
   }
   //Metodo T
   public static String T() throws IOException{
	   String T_tipo = "";
           String F1_tipo = "";
           String auxToken = "";
           T_tipo = F();
	   while(token_atual == "*" || token_atual == "/" || token_atual == "and"){
                   auxToken = token_atual;
		   if(token_atual == "*"){
                           casaToken("*");
			   F1_tipo = F();
		   }else if(token_atual == "/"){
			   casaToken("/");
			   F1_tipo = F();
		   }else if(token_atual == "and"){
			   casaToken("and");
			   F1_tipo = F();
		   }
                   //Semantico
                   if(auxToken == "and"){
                       if(T_tipo != "tipo-logico" || F1_tipo != "tipo-logico"){
                           System.out.println("Erro: Tipos incompativeis - Linha: "+erroLinha);
                       }
                   }else if(auxToken == "*" || auxToken == "/"){
                       if(T_tipo != "tipo-byte" || T_tipo != "tipo-inteiro" || F1_tipo != "tipo-byte" || F1_tipo != "tipo-inteiro"){
                           System.out.println("Erro: Tipos incompativeis - Linha: "+erroLinha);
                       } else {
                           if(auxToken == "/"){
                               T_tipo = "tipo-inteiro";
                           } else {
                               if(T_tipo != "tipo-byte" || F1_tipo != "tipo-byte"){
                                   T_tipo = "tipo-inteiro";
                               }
                           }
                       }   
                   }
	   }//fim while
           return T_tipo;
   }
   //Metodo F
   public static String F() throws IOException{
           String F_tipo = "";//Variavel que guarda o retorno dos tipos
           String F1_tipo = "";
	   String auxLex = "";
           if(token_atual == "("){
		   casaToken("(");
		   F_tipo = EXP();
		   casaToken(")");
	   }else if(token_atual == "id"){
                   auxLex = lex;
		   casaToken("id");
                   F_tipo = getTipo(auxLex);//procura na tabela de simbolos o tipo do id
	   }else if(token_atual == "const"){
                   auxLex = lex;
  		   casaToken("const");
                   F_tipo = getTipo(auxLex);
	   }else if(token_atual == "not"){
		   casaToken("not");
		   F1_tipo = F();
                   if(F1_tipo != "tipo-logico"){
                       System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                   }else {
                       F_tipo = F1_tipo;
                   }
	   }else if(token_atual == "true"){
		   casaToken("true");
                   F_tipo = "tipo-logico";
	   }else if (token_atual == "false"){
		   casaToken("false");
                   F_tipo = "tipo-logico";
	   }
           return F_tipo;
   }

   //--------------------------------------------FIM DO ANALISADOR SINTATICO


    //PROGRAMA PRINCIPAL
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
    //path = args[0];
      //path = "C:/Users/lucas/Documents/NetBeansProjects/Compilador/src/compilador/novo_teste.l";
      path = "C:/Users/Pedro/Documents/FACULDADE_PEDRO/Compiladores/BACKUP_TP_COMPILA/Compilador/src/compilador/t1.l";
      erroLinha=0;

      buffRead = new BufferedReader(new FileReader(path));
      analisadorSintatico();

      System.out.println("COMPILADO COM SUCESSO");
    }

}
