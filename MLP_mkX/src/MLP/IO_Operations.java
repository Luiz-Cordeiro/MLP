/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MLP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author luizf
 */
public class IO_Operations {
    
    //Leitura do arquivo com os dados de treinamento
    public int[][] leituraEntrada(String teste) throws FileNotFoundException{
        int entrada[][] = new int[33][63];
        Scanner scannerEntrada = new Scanner(new FileReader(teste + "/dadosTreinamento.bkp")).useDelimiter(",");
            for(int linha = 0; linha < entrada.length; linha ++){
                for(int coluna = 0; coluna < entrada[0].length; coluna++){
                    entrada[linha][coluna] = Integer.parseInt(scannerEntrada.next());
                }
            }       
            return entrada;
    }
    
    //Leitura do arquivo dos rótulos da entrada
    public int[][] rotulosEntrada(String teste) throws FileNotFoundException{
        int saida_esp[][] = new int[33][7];
        Scanner scannerEntrada = new Scanner(new FileReader(teste + "/rotulosTreinamento.bkp")).useDelimiter(",");
            for(int linha = 0; linha < saida_esp.length; linha ++){
                for(int coluna = 0; coluna < saida_esp[0].length; coluna++){
                    saida_esp[linha][coluna] = Integer.parseInt(scannerEntrada.next());
                }
            }          
            return saida_esp;
    }
    //Leitura do arquivo com os dados de validação
    public int[][] dadosValidacao(String teste) throws FileNotFoundException{
        int validacao[][] = new int[9][63];
        Scanner scannerEntrada = new Scanner(new FileReader(teste + "/dadosValidacao.bkp")).useDelimiter(",");
            for(int linha = 0; linha < validacao.length; linha ++){
                for(int coluna = 0; coluna < validacao[0].length; coluna++){
                    validacao[linha][coluna] = Integer.parseInt(scannerEntrada.next());
                }
            }   
            return validacao;
    }
    //Leitura do arquivos com os rótulos da validação
    public int[][] saidaEspValidacao(String teste) throws FileNotFoundException{
        int saida_espValidacao[][] = new int[9][7];
        Scanner scannerEntrada = new Scanner(new FileReader(teste + "/rotulosValidacao.bkp")).useDelimiter(",");
            for(int linha = 0; linha < saida_espValidacao.length; linha ++){
                for(int coluna = 0; coluna < saida_espValidacao[0].length; coluna++){
                    saida_espValidacao[linha][coluna] = Integer.parseInt(scannerEntrada.next());
                }
            }
            return saida_espValidacao;
    }    
    //Gravação dos pesos em extensão .bkp
    public void gravaPesosBackup(double pesos[][], String nome) throws IOException {
             
       String FILE_NAME = nome + ".bkp";
       FileWriter arq = new FileWriter(FILE_NAME);       
       PrintWriter gravarArq = new PrintWriter(arq);
           
       for(int linha = 0; linha < pesos.length; linha ++){
           for (int coluna = 0; coluna < pesos[0].length; coluna++){
               gravarArq.print(pesos[linha][coluna] + " ");
           }
       }       
       arq.close();
    }
    //Gravação dos pesos em .txt na forma de uma matriz bidimensional
    public void gravaPesosTxt(double pesos[][], String nome)throws IOException {
             
       String FILE_NAME = nome + ".txt";
       FileWriter arq = new FileWriter(FILE_NAME);       
       PrintWriter gravarArq = new PrintWriter(arq);
           
       for(int linha = 0; linha < pesos.length; linha ++){
           for (int coluna = 0; coluna < pesos[0].length; coluna++){
               gravarArq.print(pesos[linha][coluna] + "   ");
           }
           gravarArq.print("\n");
       }       
       arq.close();
    }
    //Gravação dos dados em formato .csv (para possíveis comparações)
    public void gravaDadosEntrada(int entrada[][], String nome)throws IOException {
             
       String FILE_NAME = nome + ".csv";
       FileWriter arq = new FileWriter(FILE_NAME);       
       PrintWriter gravarArq = new PrintWriter(arq);
           
       for(int linha = 0; linha < entrada.length; linha ++){
           for (int coluna = 0; coluna < entrada[0].length; coluna++){
               gravarArq.print(entrada[linha][coluna] + ",");
           }
           gravarArq.print("\n");
       }       
       arq.close();
    }
    
    //Gravação dos erros dos neurônios de saída para cada iteração dos dados aprensentados
    public void gravaErrosTxt (double[] erros, int epoca, int iteracao, boolean novoArquivo) throws IOException{
        File arquivo = new File("logErros.txt");
        
        
        if(arquivo.exists() && !novoArquivo){
            FileWriter fw = new FileWriter(arquivo.getName(), true);
            PrintWriter pw = new PrintWriter(fw);
            
            for(int i = 0; i < erros.length; i++){
                pw.printf("\n%s%d%s%d%s%d%s%f","Época ", epoca + 1, ", Iteração ", iteracao + 1,
                        ", erro N",i + 1," = ",erros[i]);
            }
            fw.close();
        }
        else{
            FileWriter fw = new FileWriter(arquivo.getName());
            PrintWriter pw = new PrintWriter(fw);
            
            for(int i = 0; i < erros.length; i++){
                pw.printf("\n%s%d%s%d%s%d%s%f","Época ", epoca + 1, ", Iteração ", iteracao + 1,
                        ", erro N",i + 1," = ",erros[i]);
            }
            fw.close();
        }
    }
    //Gravação dos parâmetros da rede neural em formato txt
    public void gravaParametrosRede(double taxaAprendizagem, int nroEpocas, int entrada[][], double pesosEntrada[][], double pesosSaida[][]) throws IOException{
        File arquivo = new File("ParametrosRede.txt");
        
        FileWriter fw = new FileWriter(arquivo.getName());
        PrintWriter pw = new PrintWriter(fw);
        
        pw.printf("%s\n\n","            Parâmetros da rede neural");
        pw.printf("%s%f\n","Taxa de aprendizagem: ", taxaAprendizagem);
        pw.printf("%s%d\n","Número de épocas: ", nroEpocas);
        pw.printf("%s%d\n","Características extraídas: ", entrada[0].length);
        pw.printf("%s%d\n","Número de neurônios ocultos: ", pesosEntrada[0].length);
        pw.printf("%s%d\n","Número de neurônios na saída: ", pesosSaida[0].length);
        fw.close();
    }
    //Gravação em .txt do nome do teste aplicado na rede neural
    public void gravaTeste(String nomeTeste) throws IOException{
        File arquivo = new File("nomeTeste.txt");
        FileWriter fw = new FileWriter(arquivo.getName());
        PrintWriter pw = new PrintWriter(fw);
        pw.printf("%s",nomeTeste);
        fw.close();
    }
    //Recuperação do nome do teste aplicado na rede neural
    public String getNomeTeste() throws FileNotFoundException{
        Scanner scannerEntrada = new Scanner(new FileReader("nomeTeste.txt")).useDelimiter(",");          
        String nomeTeste = scannerEntrada.next();
        return nomeTeste;
    }
    
    
}
