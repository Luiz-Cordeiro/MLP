/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MLP;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author luizf
 */
public class Recon_caracteres {

    /**
     * @param args the command line arguments
     */
    double pesosLidosEntrada[][] = new double[64][7];
    double pesosLidosSaida[][] = new double[8][7];
    int saida[][] = new int[7][7];
    String respostaObtida[] = new String[21];
    String respostaEsperada[] = new String[21];
    
    public void iniciarTeste(boolean gravaDados) throws FileNotFoundException, InterruptedException, IOException{
        IO_Operations iop = new IO_Operations();
        String teste = iop.getNomeTeste();
        System.out.println(teste);
        leituraArquivoPesos();
        processaEntrada(pesosLidosEntrada, pesosLidosSaida, teste, gravaDados);
        interpretaRotulosTeste(leituraSaidaEsperada(teste));
        gerarMatrizConfusao();
    }
    
    //Realiza a leitura do arquivo que contém os pesos da entrada e saída
    public void leituraArquivoPesos() throws FileNotFoundException{
        
        Scanner scannerEntrada = new Scanner(new FileReader("PesosEntradaFinais.bkp")).useDelimiter(" ");
            for(int linha = 0; linha < pesosLidosEntrada.length; linha ++){
                for(int coluna = 0; coluna < pesosLidosEntrada[0].length; coluna++){
                    pesosLidosEntrada[linha][coluna] = Double.parseDouble(scannerEntrada.next());
                }
            }
       
        Scanner scannerSaida = new Scanner(new FileReader("PesosSaidaFinais.bkp")).useDelimiter(" ");
            for(int linha = 0; linha < pesosLidosSaida.length; linha ++){
                for(int coluna = 0; coluna < pesosLidosSaida[0].length; coluna++){
                    pesosLidosSaida[linha][coluna] = Double.parseDouble(scannerSaida.next());
                }
            }
    }
    
    //Realiza a leitura do arquivo que contém os rótulos do teste
    public int[][] leituraSaidaEsperada(String teste) throws FileNotFoundException{
        int rotulosTesteLidos[][] = new int [21][7];
        Scanner scannerEntrada = new Scanner(new FileReader(teste + "/rotulosTeste.bkp")).useDelimiter(",");
            for(int linha = 0; linha < 21; linha ++){
                for(int coluna = 0; coluna < 7; coluna++){
                    rotulosTesteLidos[linha][coluna] = Integer.parseInt(scannerEntrada.next());
                }
            }
            return rotulosTesteLidos;
    }
    
    //Processa a entrada dos dados de teste (Propagação)
    public void processaEntrada(double pesosEntrada[][], double pesosSaida[][], String teste, boolean gravaDados) throws InterruptedException, FileNotFoundException, IOException{
        int linhaExtraidaEntrada[] = null; //Array que armazena a linha extraída da conjunto de dados de entrada
        int entradaRede[] = null;
        double campoLocalInduzidoEntrada[] = null; // Armazena o campo local induzido da entrada/camada oculta
        double sinalSaidaOculta[] = null;
        double sinalSaidaOcultaBias[] = null;
        double campoLocalInduzidoSaida[] = null; // Armazena o campo local induzido da camada oculta/saída
        double sinalSaidaRede[] = null;
        double saidaRedeNeural[] = null;
        IO_Operations iop = new IO_Operations();
        
        int entrada[][] = new int[21][63];
        
        //Realiza a leitura do arquivo de dados para teste
        Scanner scannerEntrada = new Scanner(new FileReader(teste + "/dadosTeste.bkp")).useDelimiter(","); 
            for(int linha = 0; linha < entrada.length; linha ++){
                for(int coluna = 0; coluna < entrada[0].length; coluna++){
                    entrada[linha][coluna] = Integer.parseInt(scannerEntrada.next());
                }
            }
        
        if(gravaDados)    
            iop.gravaDadosEntrada(entrada, "dadosTeste"); //Grava os dados de teste que foram usados em .csv
            
        Operations op = new Operations();
        
        for(int i = 0; i < entrada.length; i++){  
            linhaExtraidaEntrada = op.extratorLinha(entrada, i); // Extrai uma linha da matriz de entrada
            entradaRede = op.juncaoVetoresInt(linhaExtraidaEntrada); // Faz a junção do vetor da entrada com o bias
            campoLocalInduzidoEntrada = op.multiplicaMatriz(pesosEntrada, entradaRede); //Vetor equivalente ao campo local induzido da camada entrada/oculta
            sinalSaidaOculta = op.funcaoAtivacao(campoLocalInduzidoEntrada); // Retorna um vetor equivalente ao sinal de saída da camada oculta
            sinalSaidaOcultaBias = op.juncaoVetores(sinalSaidaOculta); // Retorna um vetor do sinal de saída com o bias para a próxima multiplicação de matrizes
            campoLocalInduzidoSaida = op.multiplicaMatrizDouble(pesosSaida, sinalSaidaOcultaBias);
            sinalSaidaRede = op.funcaoAtivacao(campoLocalInduzidoSaida); // Aplica a função de ativação sobre o campo local induzido
            saidaRedeNeural = op.CalculaSaidaRedeNeural(sinalSaidaRede); // Arredonda o resultado para a interpretação
            System.out.print("Resultado da rede: ");
            op.imprimeMatrizUniDouble(saidaRedeNeural);
            respostaObtida[i] = op.InterpretaSaidaRedeNeural(saidaRedeNeural); // A saída é interpretada em termos de caracteres
            System.out.println(respostaObtida[i]);
        } 
   }
    //Retorna um vetor com os rótulos de teste interpretados
    public void interpretaRotulosTeste(int rotulosTeste[][]){
        int linhaExtraidaRotulos[] = null;
        Operations op = new Operations();
        
        for(int i = 0; i < rotulosTeste.length; i++){
            linhaExtraidaRotulos = op.extratorLinha(rotulosTeste, i); //Extrai a linha do rótulo de teste
            respostaEsperada[i] = op.InterpretaSaidaRotulosTeste(linhaExtraidaRotulos); // Interpreta o código em termos de caracteres
        }
    }
    
    //Cria a matriz de confusão do teste aplicado
    public void gerarMatrizConfusao(){
        int matrizConfusao[][] = new int [7][8];
        int charIntObtido = 0;
        int charIntEsperado = 0;
        for(int i = 0; i < respostaObtida.length; i++){
            
            if(respostaObtida[i].charAt(0)-65 > 6) // Verifica se o caractere corresponde a J ou K
                charIntObtido = respostaObtida[i].charAt(0)-69; //Se for J ou K, subtrai o código ASCII por 69
            else
                charIntObtido = respostaObtida[i].charAt(0)-65; // Se for A, B, C, D ou E, subtrai o código ASCII por 65
            
            if(respostaEsperada[i].charAt(0)-65 > 6)
                charIntEsperado = respostaEsperada[i].charAt(0)-69;
            else 
                charIntEsperado = respostaEsperada[i].charAt(0)-65;
  
            if (charIntObtido == -20)
                matrizConfusao[charIntEsperado][7]++; //Se não houver reconhecimento, a oitava coluna (na linha do caractere esperado), é incrementada.
            else
                matrizConfusao[charIntEsperado][charIntObtido]++; //A coluna do caractere obtido referente a linha do caractere esperado é incrementada
                
        }
        String respostaEsperada[] = {"A","B","C","D","E","J","K"};
        System.out.println("   A B C D E J K -");
        for(int linha = 0; linha < matrizConfusao.length; linha++){
            System.out.print(respostaEsperada[linha] + "  ");
            for(int coluna = 0; coluna < matrizConfusao[0].length; coluna++){
                System.out.print(matrizConfusao[linha][coluna] + " ");
            }
            System.out.println(" ");
        }
            
    }
}
