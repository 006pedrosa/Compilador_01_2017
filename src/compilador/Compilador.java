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
    public static BufferedWriter buffWriteDSEG, buffWriteCSEG;
    public static String path, pathDSEG, pathCSEG, pathFINAL, linha, token_atual, lex, tipoId, aux_lex;
    //public static AnalisadorLexico analisadorLexico = new AnalisadorLexico();
    public static int erroLinha, posLinha, declaracao, memoria, memoria_tmp, contRot;
    public static Map<String, String> tS = new HashMap<String, String>();
    public static Map<String, String> hashTipo = new HashMap<String, String>();
    public static Map<String, String> hashClasse = new HashMap<String, String>();
    public static Map<String, Integer> hashEndereco = new HashMap<String, Integer>();
    
    //enderecos
    public static int F_end, V_end, Y_end, T_end, EXPS_end, EXP_end, Const_end, temp_end, id_end, DC_end, CA_end, CT_end;
    

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
      hashEndereco.put(token, memoria);
      
      if(tipoId == "tipo-string"){
          memoria+=256;
      }else if(tipoId == "tipo-byte" || tipoId == "tipo-logico"){
          memoria+=1;
      }else if(tipoId == "tipo-inteiro"){
          memoria+=2;
      }
   }
   
   public static void setTipo(String token, String tipo){
      hashTipo.put(token, tipo);
   }
   
   //Efetua uma busca na hashTipo pelo tipo desejado em função do token, retorna null se não encontrado
   public static String getTipo(String token){
       return hashTipo.get(token);
   }
   //Efetua uma busca na hash pelo token desejado, retorna null se não encontrado
   public static String buscaHash(String token){
      return tS.get(token);
   }
   
   //Efetua uma busca na hash pelo endereco armazenado na memoria
   public static int getEnd(String token){
      return hashEndereco.get(token);
   }
   
   //Cria um novo temporario para armazenar na memoria
   public static int novoTemp(int soma){
       int resp = memoria_tmp;
       memoria_tmp+=soma;
       return resp;
   }

    public static void chamaTabela(){
      if(buscaHash(lex) == null && declaracao == 1){
         if(tipoId == "const"){
            setHash(lex, "id", "classe-const");
         }else{
            setHash(lex, "id", "classe-var");
         }
       }else if (buscaHash(lex) == "id" && declaracao == 1){
          System.out.println("IDENTIFICADOR '" + lex + "' JÁ DECLARADO");
          System.exit(0);
      }
   }

   public static String analisadorLexico(String linhaAnalisador) throws IOException{
       String token_retorno = null;
       String retornaToken = "";
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
       retornaToken = buscaHash(token_retorno);
       if(retornaToken == null){
           if((Character.isDigit(lex.charAt(0)) || lex.charAt(0) == '\'') || lex == "true" || lex == "false"){
               retornaToken = "const";
           }
       }
       return retornaToken;
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
            }else if(linha.charAt(i) == ' ' || linha.charAt(i) == '\t'){
               if(i >= linha.length()-1){
                   return null;
               }
            }else{
                System.out.println("ERRO NA LINHA "+ erroLinha + " CARACACTERE: " + linha.charAt(i) + " NAO ESPERADO");
                System.exit(0);
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
               if(i == linha.length()-1){
                   estado = 2;
               }else{
                    estado = 4;
               }
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
                    System.out.println("ERRO NA LINHA "+ erroLinha + " COMENTARIO NAO FINALIZADO");
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
       memoria_tmp = 0;
       memoria = 4000;
       contRot = 0;
       //Geracao de codigo - Acao 34
       try{
        buffWriteDSEG.write("dseg SEGMENT PUBLIC");
        buffWriteDSEG.newLine();
        buffWriteDSEG.write("\tbyte 4000h DUP(?)");
        buffWriteDSEG.newLine();
        
        buffWriteCSEG.write("cseg SEGMENT PUBLIC");
        buffWriteCSEG.newLine();
        buffWriteCSEG.write("ASSUME CS:cseg, DS,dseg");
        buffWriteCSEG.newLine();
        buffWriteCSEG.write("strt:");
        buffWriteCSEG.newLine();
        buffWriteCSEG.write("\tmov AX, dseg");
        buffWriteCSEG.newLine();
        buffWriteCSEG.write("\tmov DS, AX");
        buffWriteCSEG.newLine();
       } catch (IOException E){}
       
       while(token_atual != "main"){
           DECLARACAO();
       }
       //Geracao de codigo - Acao 35
       casaToken("main");
       while(token_atual != "end"){
           COMANDO();
       }
       casaToken("end");
       
       try{
        buffWriteDSEG.write("dseg ENDS");
        buffWriteDSEG.newLine();
       }catch (IOException E){}
       
       //Geracao de codigo acao 36
       try{
        buffWriteCSEG.write("cseg ENDS");
        buffWriteCSEG.newLine();
        buffWriteCSEG.write("END strt");
        buffWriteCSEG.newLine();
       }catch (IOException E){}
   }
   //Metodo DECLARACAO
   public static void DECLARACAO() throws IOException{
       if(token_atual == "const"){
           DC();
       }else if(token_atual == "integer" || token_atual == "byte" || token_atual == "string" || token_atual == "boolean"){
           DV();
       }else{
           System.out.println("MAIN ESPERADO, ERRO LINHA: "+ erroLinha);
           System.exit(0);
       }
   }
   //Metodo DV
   public static void DV() throws IOException{
       String id_tipo = TIPO();
       String masm = "";
       
       casaToken("id");
       
       //Geracao de codigo acao 5
       if(id_tipo == "tipo-byte" || id_tipo == "tipo-logico"){
          masm = "\tbyte ?";
       } else if(id_tipo == "tipo-inteiro"){
          masm = "\tsword ?";
       } else if(id_tipo == "tipo-string"){
          masm = "\tbyte 256 DUP(?)";
       }
        try{
            buffWriteDSEG.write(masm);
            buffWriteDSEG.newLine();
           }catch (IOException E){}
       Y(id_tipo);
   }
   //Metodo DC
   public static void DC() throws IOException{
       String id_tipo = "";
       String idLex = "";
       String idConst = "";
       String masm = "";
       
       tipoId = "const";
       declaracao = 1;
       casaToken("const");
       declaracao = 0;
       
       idLex = lex;
       DC_end = memoria;
       casaToken("id");
      
       casaToken("=");
       
       idConst = lex;

       id_tipo = VALOR();

       setTipo(idLex, id_tipo);
       
        //Geracao de codigo acao 5
       if(id_tipo == "tipo-inteiro"){
          masm = "\tsword "+aux_lex+"";
       }else{
          masm = "\tbyte "+aux_lex+"";
       }
       buffWriteDSEG.write(masm);
       buffWriteDSEG.newLine();
       
       casaToken(";");
       
   }
   
   public static String VALOR() throws IOException{
       String VALOR_tipo = "";
       
       if(token_atual == "const"){
          VALOR_tipo = verTipoConst(lex);
          aux_lex = lex;
          casaToken("const");
       }else if(token_atual == "true"){
           VALOR_tipo = "tipo-logico";
           aux_lex = "FFh";
           casaToken("true");
       }else{
           VALOR_tipo = "tipo-logico";
           aux_lex = "0h";
           casaToken("false");
       }
       
       return VALOR_tipo;
   }
   
   //Metodo TIPO
   public static String TIPO() throws IOException{
       String tipo_tipo = "";
       declaracao = 1;
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
       declaracao = 0;
       return tipo_tipo;
   }
   //Metodo Y
   public static void Y(String Y_tipo) throws IOException{
       String EXP_tipo = "";
       int tmp = 0;
       
       if(token_atual == "="){
           casaToken("=");
           EXP_tipo = EXP();
           Y_end = EXP_end;
           
           //Acao semantica: 21
           if(EXP_tipo != Y_tipo){
               if(EXP_tipo == "tipo-string" || EXP_tipo == "tipo-logico" || Y_tipo == "tipo-string" || Y_tipo == "tipo-logico"){
                    System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                    System.exit(0);
               }else{
                    buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov DS:["+Y_end+"], AX");
                    buffWriteCSEG.newLine();
               }
           }else{
                if(EXP_tipo == "tipo-string"){
                    buffWriteCSEG.write("\tmov DI, "+Y_end+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov SI, "+EXP_end+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("R"+contRot+":");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov AX, DS:[DI]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov SI, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tadd DI, 1");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tadd SI, 1");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tcmp AX, $");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tje R"+(contRot+1)+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tjmp R"+contRot+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("R"+(contRot+1)+":");
                    buffWriteCSEG.newLine();
                    contRot+=2;
                    
                }else{
                    buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov DS:["+Y_end+"], AX");
                    buffWriteCSEG.newLine();
                }
           }
           
           
           //Acao semantica: 22
           V_end = Y_end;
           V(Y_tipo);
       }else{
           //Acao semantica: 22
           V_end = Y_end;
           V(Y_tipo);
       }
   }

   //Metodo V
   public static void V(String V_tipo) throws IOException{
       String masm = "";
       if(token_atual == ";"){
           casaToken(";");
       }else if (token_atual == ","){
           
           declaracao = 1;
           casaToken(",");
           declaracao = 0;
           
           id_end = getEnd(lex);
            
           casaToken("id");
           
           //Geracao de codigo acao 5
           if(V_tipo == "tipo-byte" || V_tipo == "tipo-logico"){
                   masm = "\tbyte ?";
           } else if(V_tipo == "tipo-inteiro"){
                   masm = "\tsword ?";
           } else if(V_tipo == "tipo-string"){
               masm = "\tbyte 256 DUP(?)";
           }
           buffWriteDSEG.write(masm);
           buffWriteDSEG.newLine();
           
           Y_end = memoria;
           //Acao semantica: 24
           Y(V_tipo);
       }else{
           casaToken(";");
       }
   }
   //Metodo COMANDO
   public static void COMANDO() throws IOException{
	   if(token_atual == "id" || token_atual == null){
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
            
            if(buscaHash(lex) == null){
                System.out.println("ID " + lex + " NÃO DECLARADO, LINHA: "+ erroLinha);
                System.exit(0);
            }
            
            casaToken("id");
            id_tipo = getTipo(auxLex);
            id_end = getEnd(auxLex);
            
            casaToken("=");
            EXP_tipo = EXP();
            
            if(id_tipo != EXP_tipo){
                if(id_tipo == "tipo-string" || id_tipo == "tipo-logico" || EXP_tipo == "tipo-string" || EXP_tipo == "tipo-logico"){
                        System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha+ " tipos comparados: " + id_tipo + " e " + EXP_tipo);
                        System.exit(0);
                }else{
                    buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov DS:["+id_end+"], AX");
                    buffWriteCSEG.newLine();   
                }
            }else{
                    if(EXP_tipo == "tipo-string"){
                    buffWriteCSEG.write("\tmov DI, "+id_end+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov SI, "+EXP_end+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("R"+contRot+":");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov AX, DS:[DI]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov SI, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tadd DI, 1");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tadd SI, 1");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tcmp AX, $");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tje R"+(contRot+1)+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tjmp R"+contRot+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("R"+(contRot+1)+":");
                    buffWriteCSEG.newLine();
                    contRot+=2;
                    
                }else{
                    buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov DS:["+id_end+"], AX");
                    buffWriteCSEG.newLine();
                }
            }
            casaToken(";");
   }
   //Metodo CR
   public static void CR() throws IOException{
	   String CR_tipo = "";
           int auxCont = contRot;
           int exp_aux_end;
           
           contRot+=1;
           
           buffWriteCSEG.write("R"+auxCont+":");
           buffWriteCSEG.newLine();
           
           casaToken("while");
	   casaToken("(");
	   
           CR_tipo = EXP();
           exp_aux_end = EXP_end;
           
           if(CR_tipo != "tipo-logico"){
               System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha+ " tipos esperado:  tipo-logico - tipo encontrado : " + CR_tipo);
               System.exit(0);
           }
	   casaToken(")");
	   X();
           
           buffWriteCSEG.write("\tmov AX, DS:["+exp_aux_end+"]");
           buffWriteCSEG.newLine();
           buffWriteCSEG.write("\tcmp AX, FFh");
           buffWriteCSEG.newLine();
           buffWriteCSEG.write("\tjmp R"+auxCont+"");
           buffWriteCSEG.newLine();
           
           

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
                String CT_tipo = "";
		casaToken("if");
		casaToken("(");
		CT_tipo = EXP();
                
                if(CT_tipo != "tipo-logico"){
                    System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                }else{
                    buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tcmp AX, FFh");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tjne R"+contRot+"");
                    buffWriteCSEG.newLine();                             
                }
		casaToken(")");
		casaToken("then");
		CT_A();
   }
   //Metodo CT_A
   public static void CT_A() throws IOException{
           int auxCont = contRot;
           contRot+=2;
	   if(token_atual =="begin"){
		   casaToken("begin");
		   while(token_atual != "end"){
			   COMANDO();
		   }
                   
                   buffWriteCSEG.write("\tjmp R"+(auxCont+1)+"");
                   buffWriteCSEG.newLine();
                   buffWriteCSEG.write("R"+auxCont+":");
                   buffWriteCSEG.newLine();
                   
		   casaToken("end");
		   if(token_atual == "else"){
			   casaToken("else");
			   casaToken("begin");
			   while(token_atual!= "end"){
				   COMANDO();
			   }
                           casaToken("end");
                           buffWriteCSEG.write("R"+(auxCont+1)+":");
                           buffWriteCSEG.newLine();
		   }
	   }else{
		   COMANDO();
                   
                   buffWriteCSEG.write("\tjmp R"+(auxCont+1)+"");
                   buffWriteCSEG.newLine();
                   buffWriteCSEG.write("R"+auxCont+":");
                   buffWriteCSEG.newLine();
                   
		   if(token_atual =="else"){
			   casaToken("else");
			   COMANDO();
		   }
                   buffWriteCSEG.write("R"+(auxCont+1)+":");
                   buffWriteCSEG.newLine();
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
        id_tipo = getTipo(auxLex);
        
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
                        System.exit(0);
                   }
		   while(token_atual != ")"){
			casaToken(",");
			CE_tipo = EXP();
                        if(CE_tipo == "tipo-logico"){
                            System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                            System.exit(0);
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
                        System.exit(0);
                    }
		   while(token_atual != ")"){
                        casaToken(",");
                        CE_tipo = EXP();
                        if(CE_tipo == "tipo-logico"){
                            System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                            System.exit(0);
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
           String auxString = "";
           EXP_tipo = EXPS();
           EXP_end = EXPS_end;
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
                       System.exit(0);
                   } else {
                       
                       EXP_tipo = "tipo-logico";
                       buffWriteDSEG.write("\tbyte ?");
                       buffWriteDSEG.newLine();
                       temp_end = memoria;
                       memoria += 2;
                       buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov BX, DS:["+EXPS_end+"]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tcmp AX, BX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tje R"+contRot+"");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov CX, 0h");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tjmp R"+(contRot+1)+"");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("R"+(contRot)+":");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov CX, FFh");
                       buffWriteCSEG.newLine();
                       contRot+=1;
                       
                       buffWriteCSEG.write("R"+(contRot)+":");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov DS:["+ temp_end +"], CX");
                       buffWriteCSEG.newLine();
                       
                       EXP_end = temp_end;
                       
                   }
               } else {
                   if(EXP_tipo == "tipo-string"){
                       buffWriteDSEG.write("\tbyte ?");
                       buffWriteDSEG.newLine();
                       temp_end = memoria;
                       memoria += 1;
                       buffWriteCSEG.write("\tmov DI, DS:["+EXP_end+"]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov SI, DS:["+EXPS_end+"]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("R"+contRot+":");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov AX, DS:[SI]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov BX, DS:[DI]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tcmp AX, BX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tjne R"+(contRot+1)+"");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tcmp AX, $");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tje R"+contRot+"");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov CX, FFh");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov DS:["+temp_end+"], CX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tjmp R"+(contRot+2)+"");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("R"+(contRot+1)+":");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov CX, 0h");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov DS:["+temp_end+"], CX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("R"+(contRot+2)+":");
                       buffWriteCSEG.newLine();
                       
                       EXP_end = temp_end;
                       contRot+=3;
                   }
                   EXP_tipo = "tipo-logico";
               }
           }else if(auxToken == "<" || auxToken == ">" || auxToken == "<=" || auxToken == ">=" || auxToken == "!="){
               if(EXP_tipo == "tipo-string" || EXPS1_tipo == "tipo-string"){
                   System.out.println("Erro: Tipos incompativeis - Linha: "+erroLinha);  
                   System.exit(0);
               } else {
                   if(auxToken == "<"){
                       auxString = "\tjl R"+contRot+"";
                   }else if(auxToken == "<="){
                       auxString = "\tjle R"+contRot+"";
                   }else if(auxToken == ">"){
                       auxString = "\tjg R"+contRot+"";
                   }else if(auxToken == ">="){
                       auxString = "\tjge R"+contRot+"";
                   }else if(auxToken == "!="){
                       auxString = "\tjne R"+contRot+"";
                   }
                   
                    buffWriteDSEG.write("\tbyte?");
                    buffWriteDSEG.newLine();
                    temp_end = memoria;
                    memoria += 1;
                    buffWriteCSEG.write("\tmov AX, DS:["+EXP_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov BX, DS:["+EXPS_end+"]");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tcmp AX, BX");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write(auxString);
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov CX, 0h");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tjmp R"+(contRot+1)+"");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("R"+(contRot)+":");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov CX, FFh");
                    buffWriteCSEG.newLine();
                    contRot+=1;
                       
                    buffWriteCSEG.write("R"+(contRot)+":");
                    buffWriteCSEG.newLine();
                    buffWriteCSEG.write("\tmov DS:["+ temp_end +"], CX");
                    buffWriteCSEG.newLine();
                       
                    EXP_end = temp_end;
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
                   auxToken = token_atual;
		   casaToken("+");
		   EXPS_tipo = T();
                   if(EXPS_tipo == "tipo-string" || EXPS_tipo == "tipo-logico"){
                       System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                       System.exit(0);
                   } else {
                       EXPS_tipo = "tipo-inteiro";
                   }
	   }else if(token_atual == "-"){
                   auxToken = token_atual;
		   casaToken("-");
		   EXPS_tipo = T();
                   if(EXPS_tipo == "tipo-string" || EXPS_tipo == "tipo-logico"){
                       System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                       System.exit(0);
                   } else {
                       EXPS_tipo = "tipo-inteiro";
                   }
	   }else{
		   EXPS_tipo = T();
                   EXPS_end = T_end;
                   if(auxToken == "-"){
                       buffWriteCSEG.write("\tmov AX, DS:["+EXPS_end+"]");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov BX, AX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tsub AX, BX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tsub AX, BX");
                       buffWriteCSEG.newLine();
                       buffWriteCSEG.write("\tmov DS:["+EXPS_end+"], AX");
                       buffWriteCSEG.newLine();
                   }
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
                                    System.exit(0);
                               }else{
                                    buffWriteCSEG.write("\tmov AL, DS:["+EXPS_end+"]");
                                    buffWriteCSEG.newLine();
                                    buffWriteCSEG.write("\tmov BL, DS:["+T_end+"]");
                                    buffWriteCSEG.newLine();
                                    buffWriteCSEG.write("\tor AL, BL");
                                    buffWriteCSEG.newLine();
                                    buffWriteCSEG.write("\tmov DS:["+EXPS_end+"], AL");
                                    buffWriteCSEG.newLine();
                               }
                           }else if(auxToken == "+" || auxToken == "-"){
                               if(EXPS_tipo == "tipo-logico" || T1_tipo == "tipo-logico"){
                                    System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                                    System.exit(0);
                               }else{
                                    if(auxToken == "-"){
                                        if(EXPS_tipo == "tipo-string" || T1_tipo == "tipo-logico"){
                                            System.out.println("Erro: Tipos incompativeis - Linhas: "+erroLinha);
                                            System.exit(0);
                                        } else {
                                            EXPS_tipo = "tipo-inteiro";
                                            buffWriteCSEG.write("\tmov AX, DS:["+EXPS_end+"]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tmov BX, DS:["+T_end+"]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tsub AX, BX");
                                            buffWriteCSEG.newLine(); 
                                            buffWriteCSEG.write("\tmov DS:["+EXPS_end+"], AX");
                                            buffWriteCSEG.newLine();                                            
                                        }
                                    } else {
                                        if(EXPS_tipo == "tipo-string" || T1_tipo == "tipo-string"){
                                            EXPS_tipo = "tipo-string";
                                            
                                            buffWriteCSEG.write("\tmov DI, "+EXPS_end+"");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tmov SI, "+T_end+"");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("R"+contRot+":");
                                            buffWriteCSEG.write("\tmov AX, DS[DI]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tcmp AX, $");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tje R"+(contRot+1)+"");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tadd DI, 1");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tjmp R" + contRot +"");
                                            buffWriteCSEG.newLine();
                                            
                                            contRot+=1;
                                            
                                            buffWriteCSEG.write("R"+contRot+":");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tmov BX, DS[SI]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tmov DS[DI], BX");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tadd DI, 1");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tadd SI, 1");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tcmp BX, $");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tjne R"+ contRot +"");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tBX, DS[SI]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tDS[DI], BX");
                                            buffWriteCSEG.newLine();
                                            
                                            contRot+=1;
                                            
                                        } else {
                                            EXPS_tipo = "tipo-inteiro";
                                            buffWriteCSEG.write("\tmov AX, DS:["+EXPS_end+"]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tmov BX, DS:["+T_end+"]");
                                            buffWriteCSEG.newLine();
                                            buffWriteCSEG.write("\tadd AX, BX");
                                            buffWriteCSEG.newLine(); 
                                            buffWriteCSEG.write("\tmov DS:["+EXPS_end+"], AX");
                                            buffWriteCSEG.newLine();
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
           T_end = F_end;
           
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
                           System.exit(0);
                       } else {
                               buffWriteCSEG.write("\tmov AL, DS:["+T_end+"]");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmov BL, DS:["+F_end+"]");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tand AL, BL");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmov DS:["+T_end+"], AL");
                               buffWriteCSEG.newLine();
                       }
                   }else if(auxToken == "*" || auxToken == "/"){
                       if(T_tipo != "tipo-byte" || T_tipo != "tipo-inteiro" || F1_tipo != "tipo-byte" || F1_tipo != "tipo-inteiro"){
                           System.out.println("Erro: Tipos incompativeis - Linha: "+erroLinha);
                           System.exit(0);
                       } else {
                           if(auxToken == "/"){
                               T_tipo = "tipo-inteiro";
                               //Geracao de Codigo Acao 13
                               buffWriteCSEG.write("\tmov AX, DS:["+T_end+"]");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmov BX, DS:["+F_end+"]");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tdiv BX");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmov DS:["+T_end+"], BX");
                               buffWriteCSEG.newLine();
                           } else {
                               if(T_tipo != "tipo-byte" || F1_tipo != "tipo-byte"){
                                   T_tipo = "tipo-inteiro";
                               } 
                               //Geracao de Codigo Acao 13
                               buffWriteCSEG.write("\tmov AX, DS:["+T_end+"]");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmov BX, DS:["+F_end+"]");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmul BX");
                               buffWriteCSEG.newLine();
                               buffWriteCSEG.write("\tmov DS:["+T_end+"], BX");
                               buffWriteCSEG.newLine();
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
                   F_end = EXP_end;
		   casaToken(")");
	   }else if(token_atual == "id"){
                   auxLex = lex;
		   casaToken("id");
                   
                   F_end = getEnd(auxLex);
                   F_tipo = getTipo(auxLex);//procura na tabela de simbolos o tipo do id
	   }else if(token_atual == "const"){
                   auxLex = lex;
  		   F_tipo = verTipoConst(lex);
                   
                   Const_end = memoria;
                   
                   //Geracao Codigo Acao 7
                   if(F_tipo == "tipo-logico"){
                       if(auxLex == "true"){
                        try{
                         buffWriteDSEG.write("\tbyte FFh");
                         buffWriteDSEG.newLine();
                        }catch (IOException E){}
                       }else if(auxLex == "false"){
                        try{
                         buffWriteDSEG.write("\tbyte 0h");
                         buffWriteDSEG.newLine();
                        }catch (IOException E){}
                       }
                       memoria+= 1;
                   }else if(F_tipo == "tipo-byte" ){
                       buffWriteDSEG.write("\tbyte "+convertHexa(auxLex));
                       buffWriteDSEG.newLine();
                       memoria+= 1;
                   }else if(F_tipo == "tipo-inteiro"){
                       buffWriteDSEG.write("\tbyte "+auxLex);
                       buffWriteDSEG.newLine();
                       memoria+= 2;
                   }else if(F_tipo == "tipo-string"){
                       buffWriteDSEG.write("\tbyte "+auxLex+"$");
                       buffWriteDSEG.newLine();
                       memoria+= auxLex.length()+1;
                   }
                   
                   F_end = Const_end;
                   casaToken("const");
	   }else if(token_atual == "not"){
		   casaToken("not");
                   
		   F1_tipo = F();
                   if(F1_tipo != "tipo-logico"){
                       System.out.println("Erro: Tipo incompativel - Linha: "+erroLinha);
                       System.exit(0);
                   }else {
                       F_tipo = F1_tipo;
                       buffWriteCSEG.write("\tmov AX, DS:["+F_end+"]");
                       buffWriteDSEG.newLine();
                       buffWriteCSEG.write("\tneg AX");
                       buffWriteDSEG.newLine();
                       buffWriteCSEG.write("\tmov DS:["+F_end+"], AX");
                       buffWriteDSEG.newLine();
                   }
	   }else if(token_atual == "true"){
                   F_end = memoria-1;
		   casaToken("true");
                   F_tipo = "tipo-logico";
	   }else if (token_atual == "false"){
                   F_end = memoria-1;
		   casaToken("false");
                   F_tipo = "tipo-logico";
	   }
           return F_tipo;
   }

   //--------------------------------------------FIM DO ANALISADOR SINTATICO

   
   //-------------------------------------------VERIFICAR TIPO CONST
   
   public static boolean verInt(String constante){ 
      boolean resp = false;
      int val = 0;
      
      if(constante.charAt(0) == '-'){
         if(constante.length() <= 6){
            for(int x = 1; x < constante.length(); x++){
               if(Character.isDigit(constante.charAt(x))){
                  resp = true;
               }else{
                  resp = false;
               }
            }
         }
      } else {
         if(constante.length() <= 5){
            for(int x = 0; x < constante.length(); x++){
               if(Character.isDigit(constante.charAt(x))){
                  resp = true;
               }else{
                  resp = false;
               }
            }
         }
      }
      if(resp){
         val = Integer.parseInt(constante);
         
         if(val >= -32768 && val <= 32767){
            resp = true;
         } 
      }
         
         
      return resp;
   }


   public static boolean verByte(String constante){
      boolean resp = false;
      int val = 0;
      
      if(constante.length() >=1 && constante.length() <= 3){
         for(int x = 0; x < constante.length(); x++){
            if(Character.isDigit(constante.charAt(x))){
               resp = true;
            }else{
               resp = false;
               x = constante.length();
            }
         }
      }
      if(resp){
         val = Integer.parseInt(constante);
         
         if(val >= 0 && val <= 255){
            resp = true;
         }else{
            resp = false;
         }
      }
      return resp;   
   }


   //verifica se e' hexadecimal
   public static boolean verHexa(String constante){
      boolean resp = false;
      
      if(constante.length() == 4){
         if(constante.charAt(0) == '0' && constante.charAt(1) == 'h'){
            if(Character.isDigit(constante.charAt(2))|| (constante.charAt(2) >= 'A'  && constante.charAt(2) <= 'F' )){
               if(Character.isDigit(constante.charAt(3))|| (constante.charAt(3) >= 'A'  && constante.charAt(3) <= 'F' )){
                  resp = true;
               }else{
                  resp = false;
               } 
            }
         }
      }
      
      return resp;
   }


   public static String verTipoConst(String constante){
      String tipoConstante = "";
      
      if(constante.length() > 254){
         System.out.println("Erro: Limite de memoria atingido");
      } else {
         if(constante.charAt(0) == '\'' && constante.charAt(constante.length()-1) == '\''){
            tipoConstante = "tipo-string";
         } else if(constante == "true" || constante == "false"){
            tipoConstante = "tipo-logico";
         } else if(verHexa(constante)){
            tipoConstante = "tipo-byte";
         } else if(verByte(constante)){
            tipoConstante = "tipo-byte";
         } else if(verInt(constante)){
            tipoConstante = "tipo-inteiro";
         }
      } 
      
      return tipoConstante;
   }
   
   //Funcao que converte o modelo do hexa da Linguagem L para MASM
   public static String convertHexa(String hexa){
       String result = hexa;
       
       if(hexa.length()== 4 && hexa.charAt(0) == '0'){
           result = hexa.charAt(2) + hexa.charAt(3) + "h";
       }
       
       return result;
   }

   
   //-------------------------------------------FIM DO VERIF TIPO CONST
   
   
   

    //PROGRAMA PRINCIPAL
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
       //path = args[0];
      //path = "C:/Users/lucas/Documents/NetBeansProjects/Compilador/src/compilador/novo_teste.l";
      path = "C:/Users/Pedro/Documents/FACULDADE_PEDRO/Compiladores/BACKUP_TP_COMPILA/Compilador/src/compilador/t1.l";
      pathDSEG = "C:/Users/Pedro/Documents/FACULDADE_PEDRO/Compiladores/BACKUP_TP_COMPILA/Compilador/src/compilador/DSEG.txt";
      pathCSEG = "C:/Users/Pedro/Documents/FACULDADE_PEDRO/Compiladores/BACKUP_TP_COMPILA/Compilador/src/compilador/CSEG.txt";
      
      //path = "C:/wamp64/www/Compilador_01_2017/src/compilador/t1.l";
      //pathDSEG = "C:/wamp64/www/Compilador_01_2017/src/compilador/DSEG.txt";
      //pathCSEG = "C:/wamp64/www/Compilador_01_2017/src/compilador/CSEG.txt";
      erroLinha=0;

      buffRead = new BufferedReader(new FileReader(path));
      buffWriteDSEG = new BufferedWriter(new FileWriter(pathDSEG));
      buffWriteCSEG = new BufferedWriter(new FileWriter(pathCSEG));
      
      analisadorSintatico();
      
      buffWriteDSEG.close();
      buffWriteCSEG.close();
      buffRead.close();
      
      System.out.println("COMPILADO COM SUCESSO");
    }

}
