/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MLP;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author luizf
 */
public class Operations {
    //Imprime uma matriz de inteiros bidimensional
    public void imprimeMatriz(int[][] tabela){
        
        System.out.printf("%s\n%s%3s\n","Matriz", "x1", "x2");
        
        
        for(int i = 0; i < tabela.length; i++){
            for(int j = 0; j < tabela[0].length; j++){
                System.out.print(tabela[i][j] + "  ");              
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }
    //Imprime uma matriz double bidimensional
      public void imprimeMatrizDouble(double[][] tabela){
        System.out.printf("%s\n%s%3s\n","Matriz", "x1", "x2");
        for(int i = 0; i < tabela.length; i++){
            for(int j = 0; j < tabela[0].length; j++){
                System.out.print(tabela[i][j] + "  ");            
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }
    //Multiplica uma matriz int unidimensional por outra double bidimensional
    public double[] multiplicaMatriz(double[][] matriz1, int[] matriz2){
        int coluna = 0;
        int linha = 0;
        double saida[] = new double[matriz1[0].length];
        double sum = 0;
        for(coluna = 0; coluna < matriz1[0].length; coluna++){
            sum = 0;
            for(linha = 0; linha < matriz1.length; linha++){
                sum += matriz2[linha]*matriz1[linha][coluna];
            }
            saida[coluna] = sum;
        }
        return saida;
    }
        //Multiplica uma matriz double unidimensinal por outra bidimensional
        public double[] multiplicaMatrizDouble(double[][] matriz1, double[] matriz2){
        int coluna = 0;
        int linha = 0;
        double saida[] = new double[matriz1[0].length];
        double sum = 0;
        for(coluna = 0; coluna < matriz1[0].length; coluna++){
            sum = 0;
            for(linha = 0; linha < matriz1.length; linha++){
                sum += matriz2[linha]*matriz1[linha][coluna];
            }
            saida[coluna] = sum;
        }
        return saida;
    }
    //Extrai uma linha de uma matriz bidimensional
    public int[] extratorLinha(int [][] matriz, int linha){
            int[] linhaExtraida = new int [matriz[0].length];
            for(int i = 0; i < matriz[0].length; i++)
                linhaExtraida[i] = matriz[linha][i];
            return linhaExtraida;
    }
    //Aplica a função de ativação sigmóide sobre um vetor
    public double[] funcaoAtivacao(double[] campoLocalInduzido){
        double sinalSaida[] = new double[campoLocalInduzido.length];
        for(int i = 0; i < campoLocalInduzido.length; i++){
            //sinalSaida[i] = ((2/(1+Math.exp(-1*(campoLocalInduzido[i]))))-1);
            sinalSaida[i] = (1/(1+Math.exp(-1*(campoLocalInduzido[i]))));
        }
        return sinalSaida;
    }
    //Faz a junção de vetores double com o bias
    public double[] juncaoVetores(double[] sinalSaidaOculta){
        double vetorJuncao[] = new double[sinalSaidaOculta.length + 1];
        vetorJuncao[0] = 1;
        for(int i = 1; i < vetorJuncao.length; i++)
            vetorJuncao[i] = sinalSaidaOculta[i-1];
        return vetorJuncao;
    }
    //Faz a junção de vetores int com o bias
    public int[] juncaoVetoresInt(int[] sinalEntrada){
        int vetorJuncao[] = new int[sinalEntrada.length + 1];
        vetorJuncao[0] = 1;
        for(int i = 1; i < vetorJuncao.length; i++)
            vetorJuncao[i] = sinalEntrada[i-1];
        return vetorJuncao;
    }
    //Determina o erro da saída da rede neural para cada neurônio
    public double[] calculaErroSaida(int[] saidaEsperada, double[] saidaObtida){
        double[] erroSaida = new double[saidaObtida.length];
        for(int i = 0; i < saidaObtida.length; i++)
            erroSaida[i] = saidaEsperada[i] - saidaObtida[i];
        return erroSaida;        
    }
    //Aplica a derivada da função de ativação
    public double derivadaFuncaoAtivacao(double x){
        return ((Math.exp(x))/(Math.pow((Math.exp(x)+1), 2)));
    }
    //Determina o vetor que contém os gradientes de saída
    public double[] calculaGradienteSaida(double[] erroSaida, double[] campoLocalInduzidoSaida){
        double[] gradientesSaida = new double [erroSaida.length];
        for(int i = 0; i < gradientesSaida.length; i++)
            gradientesSaida[i] = erroSaida[i]*derivadaFuncaoAtivacao(campoLocalInduzidoSaida[i]);
        return gradientesSaida;        
    }
    //Aplica a correção dos pesos da camada de saída
    public double[][] corrigePesosSaida(double eta, double[][] pesosSaidaAtual, double[] erroSaida, double[] campoLocalInduzidoSaida, double[] sinalCamadaOculta){
        double pesosSaidaAjustado[][] = new double[pesosSaidaAtual.length][pesosSaidaAtual[0].length];
        int coluna = 0;
        int linha = 0;
        for(coluna = 0; coluna < pesosSaidaAtual[0].length; coluna++){
            for(linha = 0; linha < pesosSaidaAtual.length; linha++){
                if(linha == 0){
                    pesosSaidaAjustado[linha][coluna] = pesosSaidaAtual[linha][coluna] + //Ajuste do peso que conecta o bias a camada de saída
                    eta*erroSaida[coluna]*derivadaFuncaoAtivacao(campoLocalInduzidoSaida[coluna]);
                } else {
                    pesosSaidaAjustado[linha][coluna] = pesosSaidaAtual[linha][coluna]+ //Ajuste do peso dos neurônios da camada de saída
                    eta*erroSaida[coluna]*derivadaFuncaoAtivacao(campoLocalInduzidoSaida[coluna])*sinalCamadaOculta[linha-1];        
                }
            }
        }
        return pesosSaidaAjustado;
    }
    //Aplica a correção dos pesos da camada de entrada
    public double[][] corrigePesosEntrada(double eta, double[][] pesosEntradaAtual, double[] gradienteOculta, int[] sinalEntrada){
        double pesosEntradaAjustado[][] = new double[pesosEntradaAtual.length][pesosEntradaAtual[0].length];
        int coluna = 0;
        int linha = 0;
        for(coluna = 0; coluna < pesosEntradaAtual[0].length; coluna++){
            for(linha = 0; linha < pesosEntradaAtual.length; linha++){
                if(linha == 0){
                    pesosEntradaAjustado[linha][coluna] = pesosEntradaAtual[linha][coluna] + //Ajuste do peso que conecta o bias a camada de saída
                    eta*gradienteOculta[coluna];
                } else { 
                    pesosEntradaAjustado[linha][coluna] = pesosEntradaAtual[linha][coluna] + //Ajuste do peso dos neurônios da camada de entrada
                    eta*gradienteOculta[coluna]*sinalEntrada[linha-1];        
                }
            }
        }
        return pesosEntradaAjustado;
    }
    //Imprime uma matriz double unidimensional
    public void imprimeMatrizUniDouble(double[] matriz){
        for(Double e : matriz)
            System.out.print(e + " ");
        System.out.println("");
    }
    //Imprime uma matriz int unidimensional
    public void imprimeMatrizUniInt(int[] matriz){
        for(Integer e : matriz)
            System.out.print(e + " ");
        System.out.println("");
    }
    //Faz a transposição de uma matriz de pesos sem o bias
    public double[][] traspoeMatrizSemBias(double[][] matriz){
        double matrizTransposta[][] = new double[matriz[0].length][matriz.length-1];
        int coluna = 0;
        int linha = 0;
        for(coluna = 0; coluna < matriz[0].length; coluna++)
            for(linha = 1; linha < matriz.length; linha++)
                matrizTransposta[coluna][linha-1] = matriz[linha][coluna];
        return matrizTransposta;
    }
    //Aplica a derivada da função de ativação sobre um vetor
    public double[] derivadaFuncaoAtivacaoMatriz(double[] campoLocalInduzidoCamOculta){
        double matrizDerivada[] = new double[campoLocalInduzidoCamOculta.length];
        
        for(int i = 0; i < matrizDerivada.length; i++)
            matrizDerivada[i] = derivadaFuncaoAtivacao(campoLocalInduzidoCamOculta[i]);
        return matrizDerivada;
    }
    //Multiplica os itens correspondentes entre duas matrizes unidimensionais
    public double[] multiplicaMatrizDoubleUni (double[] matriz1, double[] matriz2){
        double matrizProduto[] = new double[matriz1.length];
        for(int i = 0; i < matriz1.length; i++)
            matrizProduto[i] = matriz1[i]*matriz2[i];
        return matrizProduto;
    }
    //Calcula a energia total do erro dado o vetor do erro produzido na saída de uma iteração
    public double energiaTotalErro (double[] erroSaida){
        double energiaTotalErro = 0;
        for(int i = 0; i < erroSaida.length; i++)
            energiaTotalErro += 0.5*Math.pow(erroSaida[i],2);
        return energiaTotalErro;
    }
    //Arredonda os resultados da saída da rede neural para que sejam devidamente interpretados
    public double[] CalculaSaidaRedeNeural(double[] sinalSaida){
        double[] saidaRedeNeural = new double[sinalSaida.length];
        BigDecimal bd;
        for(int i = 0; i < sinalSaida.length;i++){
            bd = new BigDecimal(sinalSaida[i]).setScale(0, RoundingMode.HALF_UP);
            saidaRedeNeural[i] = bd.doubleValue();
        }
        return saidaRedeNeural;
    }
    
    //Gera pesos aleatórios entre -0.5 e +0.5
    public double[][] geraPesosAleatorios(int linhas, int colunas){
        double pesosEntrada[][] = new double[linhas][colunas];
        
        for(int coluna = 0; coluna < pesosEntrada[0].length; coluna++){
            for(int linha = 0; linha < pesosEntrada.length; linha++){
                pesosEntrada[linha][coluna] = Math.random()*(0.5 - (-0.5))+(-0.5);
            }
        }
        return pesosEntrada;
    }
    //Interpreta o sinal da rede neural obtido sobre dados de treinamento
    public String InterpretaSaidaRedeNeural(double[] resultado){
        int soma = 0;
        for(int i = 0; i < resultado.length; i++)
            soma += resultado[i];
        //ABCDEJK
        if(resultado[0] == 1 && soma == 1)
            return "A";
        else if(resultado[1] == 1 && soma == 1)
            return "B";
        else if(resultado[2] == 1 && soma == 1)
            return "C";
        else if(resultado[3] == 1 && soma == 1)
            return "D";
        else if(resultado[4] == 1 && soma == 1)
            return "E";
        else if(resultado[5] == 1 && soma == 1)
            return "J";
        else if(resultado[6] == 1 && soma == 1)
            return "K";
       return "-";
    }
    //Interpreta os rótulos de teste para serem comparados com os resultados obtidos 
    public String InterpretaSaidaRotulosTeste(int[] resultado){
        int soma = 0;
        for(int i = 0; i < resultado.length; i++)
            soma += resultado[i];
        //ABCDEJK
        if(resultado[0] == 1)
            return "A";
        else if(resultado[1] == 1)
            return "B";
        else if(resultado[2] == 1)
            return "C";
        else if(resultado[3] == 1)
            return "D";
        else if(resultado[4] == 1)
            return "E";
        else if(resultado[5] == 1)
            return "J";
        else if(resultado[6] == 1)
            return "K";
       return "-";
    }
}