/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MLP;


import java.io.IOException;


/**
 *
 * @author luizf
 */
public class Treinamento {

    /**
     * @param args the command line arguments
     */

    double pesosEntradaRedeNeural[][] = null; //Pesos entre a entrada e os neurônios ocultos
    double pesosSaidaRedeNeural[][] = null; // Pesos entre a camada oculta e a saída da rede neural
    double pesosEntradaRedeNeuralValidacao[][] = null; //Pesos entre a entrada e os neurônios ocultos na validação
    double pesosSaidaRedeNeuralValidacao[][] = null; //Pesos entre a camada oculta e a saída da rede na validação
    double menorEnergiaMediaErroQuadradoValidacao; //Armazena o menor valor registrado do erro quadrado médio na validação
    int count;
    Grafico graph = new Grafico(); // Classe necessária para exibir o gráfico do erro quadrado médio
    
    public void treinarRede(boolean paradaAntecipada, String teste, boolean gravarDados) throws InterruptedException, IOException {
        
        double eta = 0.1; //Taxa de aprendizagem
        int nroEpocas  = 100000; // Número máximo de épocas
        Operations op = new Operations();
        IO_Operations iop = new IO_Operations();

        int entrada[][] = iop.leituraEntrada(teste); //Lê o arquivo fonte da entrada dos dados de treinamento
        int saida_esp[][] = iop.rotulosEntrada(teste);//Lê o arquivo fonte da entrada dos rótulos de treinamento
        int validacao[][] = iop.dadosValidacao(teste);//Lê o arquivo fonte da entrada dos dados de validação
        int saida_espValidacao[][] = iop.saidaEspValidacao(teste);//Lê o arquivo fonte da entrada dos rótulos de validação
        
        if(gravarDados){
            iop.gravaDadosEntrada(entrada, "dadosTreinamento"); //Gravar os dados em .csv para avaliação (opcional)
            iop.gravaDadosEntrada(saida_esp, "rotulosTreinamento");
            iop.gravaDadosEntrada(validacao, "dadosValidacao");
            iop.gravaDadosEntrada(saida_espValidacao, "rotulosValidacao");
        }

        //Geração de pesos aleatórios para a entrada, entre -0.5 e +0.5
        double pesosEntrada[][] = op.geraPesosAleatorios(entrada[0].length + 1, saida_esp[0].length);
        //Gravação em arquivo .txt dos pesos iniciais
        iop.gravaPesosTxt(pesosEntrada, "PesosEntradaIniciais");
        //Geração de pesos aleatórios para a saída, entre -0.5 e +0.5
        double pesosSaida[][] = op.geraPesosAleatorios(pesosEntrada[0].length + 1, saida_esp[0].length);
        //Gravação em arquivo .txt dos pesos finais
        iop.gravaPesosTxt(pesosSaida, "PesosSaidaIniciais");
        //Gravação em arquivo .txt dos parâmetros da rede
        iop.gravaParametrosRede(eta, nroEpocas, entrada, pesosEntrada, pesosSaida);
        //Gravação do tipo de teste
        iop.gravaTeste(teste);
        //Treinamento da rede      
        treinamentoRede(entrada, saida_esp, pesosEntrada, pesosSaida, validacao, saida_espValidacao ,eta, nroEpocas, paradaAntecipada);
        //Gravação dos pesos em .txt (para visualização) e .bkp (para recuperação pela algoritmo de aplicação)
        iop.gravaPesosTxt(pesosEntradaRedeNeural, "PesosEntradaFinais");
        iop.gravaPesosTxt(pesosSaidaRedeNeural, "PesosSaidaFinais");
        iop.gravaPesosBackup(pesosEntradaRedeNeural, "PesosEntradaFinais");
        iop.gravaPesosBackup(pesosSaidaRedeNeural, "PesosSaidaFinais");     
        
        graph.criaGrafico();
    }
    
    public void treinamentoRede(int dadosEntradaRede[][], int saidaEsperadaRede[][], double pesosEntradaRede[][], 
        double pesosSaidaRede[][], int[][] dadosValidacao, int saida_espValidacao[][], double eta, int nroEpocas, boolean habilitaParadaAnt) throws InterruptedException, IOException {
        int linhaExtraidaEntrada[] = null; // Armazena a linha extraída da conjunto de dados de entrada
        int linhaExtraidaSaidaEsperada[] = null; // Armazena a linha extraída da saída esperada
        double campoLocalInduzidoEntrada[] = null; // Armazena o campo local induzido da entrada/camada oculta
        double campoLocalInduzidoSaida[] = null; // Armazena o campo local induzido da camada oculta/saída
        double sinalSaidaOculta[] = null; // Armazena o sinal gerado pela camada oculta da rede neural para cada neurônio oculto
        double sinalSaidaOcultaBias[] = null; // Armazena o sinal da camada oculta com o bias da rede neural 
        double sinalSaidaRede[] = null; // Armazena o sinal de saída obtido na última camada
        double erroSaida[] = null; // Armazena o erro da saída do neurônio
        double[][] pesosSaidaTMP = null; // Armazena os pesos de saída corrigidos temporariamente até a atribuição definitiva
        int[] entradaRede = null; // Armazena os pesos atualizados para as próximas iterações das épocas
        double gradientesSaida[] = null; // Armazena o cálculo do gradiente de saída
        double produtoGradientePesosSaida[] = null; // Armazena o produto entre as matrizes dos gradientes de saída e pesos da saída
        double derivadaSaidaOculta[] = null; // Armazena o resultado da derivada da função de ativação        
        double gradienteOculta[] = null; // Armazena o gradiente da camada oculta
        double pesosEntradaTMP[][] = null; // Armazena os pesos de entrada corrigidos temporariamente até a atribuição definitiva
        long velocidade = 0; // Determina o tempo de delay entre algumas operações
        boolean paradaAntecipada = false; // Determina a existência da validação e parada antecipada
        double somatorioEnergiaTotalErro = 0; // Indica a energia total do erro de cada iteração 
        double energiaMediaErroQuadrado = 0; // Indica a energia média do erro quadrado ao final de cada época
        
        Operations op = new Operations();
        IO_Operations iop = new IO_Operations();  
           
        //Iniciar o loop de épocas
        for(int i = 0; i < nroEpocas ; i++){
            System.out.printf("%s%d\n","Época ", i+1);
            //iniciar iteração dos dados
            for (int j = 0; j < dadosEntradaRede.length; j++){
                //Etapa 1: geração do sinal de saída da camada oculta
                                
                linhaExtraidaEntrada = op.extratorLinha(dadosEntradaRede, j); //Extrai a linha j do array entrada para a multiplicação
                linhaExtraidaSaidaEsperada = op.extratorLinha(saidaEsperadaRede, j); //Extrai a linha j do array saida_esperada para avaliação do erro
                entradaRede = op.juncaoVetoresInt(linhaExtraidaEntrada); // Atribui ao dados de entrada o bias
                campoLocalInduzidoEntrada = op.multiplicaMatriz(pesosEntradaRede, entradaRede); //Vetor equivalente ao campo local induzido da camada entrada/oculta
                sinalSaidaOculta = op.funcaoAtivacao(campoLocalInduzidoEntrada); // Retorna um vetor equivalente ao sinal de saída da camada oculta
                sinalSaidaOcultaBias = op.juncaoVetores(sinalSaidaOculta); // Retorna um vetor do sinal de saída com o bias para a próxima multiplicação de matrizes

                //Etapa 2: geração do sinal de saída da rede
                campoLocalInduzidoSaida = op.multiplicaMatrizDouble(pesosSaidaRede, sinalSaidaOcultaBias); // Retorna o campo local induzido da camada de saída
                sinalSaidaRede = op.funcaoAtivacao(campoLocalInduzidoSaida); // Aplica a função de ativação ao campo local induzido da saída
                erroSaida = op.calculaErroSaida(linhaExtraidaSaidaEsperada, sinalSaidaRede); // Retorna um vetor com o erro das saídas
                if(i == 0 && j == 0) // Se for a primeira iteração da primeira época
                    iop.gravaErrosTxt(erroSaida, i, j, true); // Cria um novo arquivo de log 
                else 
                    iop.gravaErrosTxt(erroSaida, i, j, false); // Anexa o registro a um arquivo existente
                pesosSaidaTMP = op.corrigePesosSaida(eta, pesosSaidaRede, erroSaida, campoLocalInduzidoSaida,sinalSaidaOculta); // Armazena a correção dos pesos da saída temporariamente
                gradientesSaida = op.calculaGradienteSaida(erroSaida, campoLocalInduzidoSaida); // Retorna o vetor com os gradientes da saída da rede         
                produtoGradientePesosSaida = op.multiplicaMatrizDouble(op.traspoeMatrizSemBias(pesosSaidaRede), gradientesSaida); // Retorna o produto matricial entre os vetores dos gradientes das saídas e pesos da saída
                derivadaSaidaOculta = op.derivadaFuncaoAtivacaoMatriz(campoLocalInduzidoEntrada); // Aplica a derivada da função de ativação sobre o campo local induzido da saída
                gradienteOculta = op.multiplicaMatrizDoubleUni(derivadaSaidaOculta, produtoGradientePesosSaida); // Retorna o gradiente da camada oculta
                pesosEntradaTMP = op.corrigePesosEntrada(eta, pesosEntradaRede, gradienteOculta, linhaExtraidaEntrada); // Armazena a correção dos pesos da entrada temporariamente
                pesosEntradaRede = pesosEntradaTMP; // Os pesos de entrada locais do método são atualizados
                pesosSaidaRede = pesosSaidaTMP; // Os pesos de saída locais do método são atualizados
                pesosEntradaRedeNeural = pesosEntradaTMP; // Os pesos de entrada globais são atualizados
                pesosSaidaRedeNeural = pesosSaidaTMP; // Os pesos de saída globais são atualizados
                somatorioEnergiaTotalErro += op.energiaTotalErro(erroSaida); // Os erros da saída são acumulados

            }
            energiaMediaErroQuadrado = somatorioEnergiaTotalErro/dadosEntradaRede.length; // Cálculo do energia média do erro quadrado
            graph.adicionaDados(energiaMediaErroQuadrado, "Treinamento", Integer.toString(i));
            System.out.print("Energia Média do Erro Quadrado: ");
            System.out.println(energiaMediaErroQuadrado);
            Thread.sleep(velocidade);
            
            //Teste da iteração da validação da rede neural
            if(habilitaParadaAnt){ // Se a parada antecipada estiver habilitada
                paradaAntecipada = validacaoRede(dadosValidacao, saida_espValidacao, i + 1); // Inicia o processo de validação
            
                if(paradaAntecipada == true){ // Se houver uma parada antecipada
                    pesosEntradaRedeNeural = pesosEntradaRedeNeuralValidacao; // Os pesos da entrada definitivos são atualizados 
                    pesosSaidaRedeNeural = pesosSaidaRedeNeuralValidacao; // Os pesos da saída definitivos são atualizados
                    break; // O loop é encerrado, assim como o treinamento
                }
            }
            
            if(energiaMediaErroQuadrado <= 0.0001){ // Critério de parada 
                break; // O loop é encerrado, assim como o treinamento
            }
            somatorioEnergiaTotalErro = 0; // O somatório da energia do erro é zerado para nova iteração
        }  
    }
    
    public boolean validacaoRede(int dadosValidacao[][], int saida_espValidacao[][], int epoca) throws InterruptedException{
        
        int linhaExtraidaEntrada[] = null; //Array que armazena a linha extraída da conjunto de dados de entrada
        int linhaExtraidaSaidaEsperada[] = null; //Array que armazena a linha extraída da conjunto da saída esperada
        int entradaRede[] = null; // Armazena os sinais de entrada(neurônios sensores) com o bias
        double campoLocalInduzidoEntrada[] = null; // Armazena o campo local induzido da entrada/camada oculta
        double sinalSaidaOculta[] = null; //Armazena o resultado da função de ativação sobre o campo local induzido da camada oculta
        double sinalSaidaOcultaBias[] = null; //Armazena o resultado da camada oculta com o bias
        double campoLocalInduzidoSaida[] = null; // Armazena o campo local induzido da camada de saída
        double sinalSaidaRede[] = null; //Armazena os valores calculados pelos neurônios de saída
        double erroSaida[] = null;//Armazena os erros gerados pelos neurônios de saída
        double somatorioEnergiaTotalErro = 0;
        double energiaMediaErroQuadradoValidacao = 0;
        
        for (int j = 0; j < dadosValidacao.length; j++){
                //Etapa 1: geração do sinal de saída da camada oculta
                Operations op = new Operations();
                
                linhaExtraidaEntrada = op.extratorLinha(dadosValidacao, j); //Extrai a linha j do array entrada para a multiplicação
                linhaExtraidaSaidaEsperada = op.extratorLinha(saida_espValidacao, j); //Extrai a linha j do array saida_esperada para avaliação do erro
                entradaRede = op.juncaoVetoresInt(linhaExtraidaEntrada); //Atribui o bias a entrada da rede neural
                campoLocalInduzidoEntrada = op.multiplicaMatriz(pesosEntradaRedeNeural, entradaRede); //Vetor equivalente ao campo local induzido da camada entrada/oculta
                sinalSaidaOculta = op.funcaoAtivacao(campoLocalInduzidoEntrada); // Retorna um vetor equivalente ao sinal de saída da camada oculta
                sinalSaidaOcultaBias = op.juncaoVetores(sinalSaidaOculta); // Retorna um vetor do sinal de saída com o bias para a próxima multiplicação de matrizes

                //Etapa 2: geração do sinal de saída da rede
                campoLocalInduzidoSaida = op.multiplicaMatrizDouble(pesosSaidaRedeNeural, sinalSaidaOcultaBias);//Retorna o campo local induzido da saída
                sinalSaidaRede = op.funcaoAtivacao(campoLocalInduzidoSaida); //Retorna o valor calculado de cada um dos neurônios de saída
                erroSaida = op.calculaErroSaida(linhaExtraidaSaidaEsperada, sinalSaidaRede); //Calcula o erro cometido por cada neurônio na saída
                
                somatorioEnergiaTotalErro += op.energiaTotalErro(erroSaida); //Somatório necessário para o erro quadrado médio 
            }
        
        energiaMediaErroQuadradoValidacao = somatorioEnergiaTotalErro/dadosValidacao.length; //Cálculo do erro quadrado médio da época apresentada 
        //Adiciona o erro quadrado da validação ao gráfico
        graph.adicionaDados(energiaMediaErroQuadradoValidacao, "Validação", Integer.toString(epoca)); //Atribui um ponto no gráfico a ser gerado com base no erro quadrado médio 
        System.out.println("Erro quadrado médio da validação: " + energiaMediaErroQuadradoValidacao);
                
        if(epoca == 1){
            menorEnergiaMediaErroQuadradoValidacao = energiaMediaErroQuadradoValidacao; //Armazena o primeiro erro quadrado médio da validação
        }
        else{
            if(energiaMediaErroQuadradoValidacao <= menorEnergiaMediaErroQuadradoValidacao){
                count = 0; //Se o valor do erro quadrado médio diminuir, o contador é zerado
                menorEnergiaMediaErroQuadradoValidacao = energiaMediaErroQuadradoValidacao; //O valor obtido é atribuído como menor erro encontrado na validação
                pesosEntradaRedeNeuralValidacao = pesosEntradaRedeNeural; //Os pesos atuais para o erro são armazenados
                pesosSaidaRedeNeuralValidacao = pesosSaidaRedeNeural;
            }
            else{
                count++; // Contador para verificar a quantidade de sucessões no acréscimo do erro
            }
        }       
        if(count == 500) //Paciência: quantidade de sucessões nas quais o erro quadrado médio da validação aumenta
            return true; //Retorna verdadeiro para indicar a parada antecipada.
 
        return false;
    }
    
}

        
        


    

