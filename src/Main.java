import static java.lang.String.format;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {
  public static void main(String[] args) {
    List<Processo> processos = new ArrayList<>();
    // Leitura do arquivo e criação da lista de processos
    try (BufferedReader buffRead =
             new BufferedReader(new FileReader("C:\\Users\\leand\\OneDrive\\Área de Trabalho\\entrada.txt"))) {
      String linha;
      while ((linha = buffRead.readLine()) != null) {
        String[] separador = linha.split(" ");
        processos.add(new Processo(Integer.parseInt(separador[0]),
                                   Integer.parseInt(separador[1])));
      }
    } catch (FileNotFoundException e) {
      System.err.println("Arquivo não encontrado.");
    } catch (IOException e) {
      System.err.println("Erro de leitura do arquivo.");
    }

    // Chamada as funçõoes
    Main main = new Main();
    System.out.printf("FCFS %.1f %.1f %.1f\n",  main.fcfsRetorno(processos), main.fcfs(processos), main.fcfsEspera(processos));
    main.sjf(processos);
    main.rr(processos);
    }

  // IMPLEMENTAÇÃO FCFS
  public Double fcfs(List<Processo> processos) {
    double media = 0;
    double tempoAtual = 0; // Representa o tempo atual no sistema

    for (Processo processo : processos) {
      //tempo em que o processo atual começara a ser executado
      //garante que o processo so vai ser executadoo após o tempo atual do sistema
      double tempoInicio = Math.max(tempoAtual, processo.getChegada());

      //armazena para cada p quando ele executou - quando chegou no sistema
      media += tempoInicio - processo.getChegada();



      // Atualiza o tempo atual com o tempo q ele iniciou execucção ate o fim do pico dele
      tempoAtual = tempoInicio + processo.getPico();

    }
    //retorna a média de Resposta
    return media/processos.size();
  }


  public Double fcfsRetorno(List<Processo> processos) {
    double media = 0;
    double tempoAtual = 0;
    //itera sobre a lista de processos
    for (Processo processo : processos) {
      //confere se o tempo atual é menor do que o de chegada
      if (tempoAtual < processo.getChegada()) {
        // se o tempo atual for menor q o de chegada do p, ajusta para o tempo de chegada
        tempoAtual = processo.getChegada();
      }
      //atualiza o tempo com o pico do p atual
      tempoAtual += processo.getPico();
      //adiciona a variavel media o tempo atual - chegada do p, ou seja quanto tempo ele demorou pra terminar o p
      media += tempoAtual - processo.getChegada();
    }

    //retorna a média de Retono do fcfs o valor de cada tempo de p / tamanho da lista
    return media/processos.size();

  }


  public Double fcfsEspera(List<Processo> processos) {
    double media = 0;
    double tempoAtual = 0;
    //itera sobre os processoos
    for (Processo processo : processos) {
        //confere se o tempo atual é menor do que o de chegada
      if (tempoAtual < processo.getChegada()) {
        // se o tempo atual for menor q o de chegada do p, ajusta para o tempo de chegada
        tempoAtual = processo.getChegada();
      }
      //adiciona a variavel media o tempo atual - chegada do p, ou seja quanto tempo ele demorou na fila
      media += tempoAtual - processo.getChegada();
      //atualiza o tempo com o pico do p atual
      tempoAtual += processo.getPico();

    }
    //retorna a média de espera do fcfs o valor de cada tempo de p / tamanho da lista
    return media/processos.size();

  }

  public void sjf(List<Processo> processos) {
    List<Processo> copiaProcessos = new ArrayList<>(processos);
    List<Processo> listaProntos = new ArrayList<>();
    int tempoAtual = 0, tempoRetorno = 0, tempoResposta = 0, tempoEspera = 0;
    int totalProcessos = copiaProcessos.size();

    // Ordena a cópia da lista de processos por tempo de chegada
    copiaProcessos.sort(
        (p1, p2) -> Integer.compare(p1.getChegada(), p2.getChegada()));

    while (!copiaProcessos.isEmpty() || !listaProntos.isEmpty()) {
      // Adiciona todos os processos que chegaram ao tempo atual à lista de
      // prontos
      while (!copiaProcessos.isEmpty() &&
             copiaProcessos.get(0).getChegada() <= tempoAtual) {
        listaProntos.add(copiaProcessos.remove(0));
      }

      // Se não há nenhum processo pronto, avança o tempo para a chegada do
      // próximo processo
      if (listaProntos.isEmpty()) {
        tempoAtual = copiaProcessos.get(0).getChegada();
        continue;
      }

      // Seleciona o processo com o menor tempo de execução (SJF)
      listaProntos.sort(
          (p1, p2) -> Integer.compare(p1.getPico(), p2.getPico()));
      Processo p = listaProntos.remove(
          0); // Obtém o processo com o menor tempo de execução

      tempoAtual += p.getPico();
      tempoRetorno += (tempoAtual - p.getChegada());
      tempoEspera += (tempoAtual - p.getChegada() - p.getPico());
      tempoResposta = tempoEspera; // Para o SJF, o tempo de resposta é igual ao
                                   // tempo de espera
    }

    // Calcula os tempos médios
    float tempoRetornoMedio = (float)tempoRetorno / totalProcessos;
    float tempoRespostaMedio = (float)tempoResposta / totalProcessos;
    float tempoEsperaMedio = (float)tempoEspera / totalProcessos;

    System.out.println(String.format("SJF %.1f %.1f %.1f", tempoRetornoMedio,
                                     tempoRespostaMedio, tempoEsperaMedio));
  }

  public void rr(List<Processo> processos) {
    List<Processo> copiaProcessos = new ArrayList<>(processos);
    int quantum = 2;
    Queue<Processo> listaProntos = new LinkedList<>();
    int tempoAtual = 0, tempoRetorno = 0, tempoResposta = 0, tempoEspera = 0;
    int totalProcessos = copiaProcessos.size();
    int[] tempoRestante =
        new int[totalProcessos]; // Array para rastrear o tempo restante de
                                 // cada processo
    boolean[] primeiraExecucao =
        new boolean[totalProcessos]; // Array para rastrear se o processo já
                                     // foi executado

    // Inicializa o tempo restante e o flag de primeira execução de cada
    // processo
    for (int i = 0; i < totalProcessos; i++) {
      tempoRestante[i] = copiaProcessos.get(i).getPico();
      primeiraExecucao[i] =
          true; // Inicialmente, todos os processos ainda não foram executados
    }

    // Índice para manter o controle de processos já adicionados à fila de
    // prontos
    int indiceProcesso = 0;

    while (indiceProcesso < totalProcessos || !listaProntos.isEmpty()) {
      // Adiciona processos à lista de prontos conforme chegam
      while (indiceProcesso < totalProcessos &&
             copiaProcessos.get(indiceProcesso).getChegada() <= tempoAtual) {
        listaProntos.add(copiaProcessos.get(indiceProcesso));
        indiceProcesso++;
      }

      // Se a lista de prontos estiver vazia, avança o tempo para a chegada do
      // próximo processo
      if (listaProntos.isEmpty()) {
        tempoAtual = copiaProcessos.get(indiceProcesso).getChegada();
        continue;
      }

      // Pega o próximo processo na fila
      Processo processoAtual = listaProntos.poll();
      int idProcesso = copiaProcessos.indexOf(
          processoAtual); // Obtém o índice do processo na lista original

      // Se o processo está sendo executado pela primeira vez, calcula o tempo
      // de resposta
      if (primeiraExecucao[idProcesso]) {
        tempoResposta += (tempoAtual - processoAtual.getChegada());
        primeiraExecucao[idProcesso] =
            false; // Marca que o processo já foi executado pela primeira vez
      }

      // Calcula o tempo que o processo vai executar neste ciclo
      int tempoExecucao = Math.min(quantum, tempoRestante[idProcesso]);
      tempoAtual += tempoExecucao;
      tempoRestante[idProcesso] -= tempoExecucao;

      // Adiciona processos que chegam durante a execução do processo atual
      while (indiceProcesso < totalProcessos &&
             processos.get(indiceProcesso).getChegada() <= tempoAtual) {
        listaProntos.add(processos.get(indiceProcesso));
        indiceProcesso++;
      }

      // Se o processo terminou sua execução
      if (tempoRestante[idProcesso] == 0) {
        tempoRetorno += (tempoAtual - processoAtual.getChegada());
        tempoEspera +=
            (tempoAtual - processoAtual.getChegada() - processoAtual.getPico());
      } else {
        // Se o processo não terminou, reenvia para o final da fila
        listaProntos.add(processoAtual);
      }

      // Corrige o avanço do tempo caso não haja mais processos na fila
      if (listaProntos.isEmpty() && indiceProcesso < totalProcessos) {
        tempoAtual = Math.max(tempoAtual,
                              copiaProcessos.get(indiceProcesso).getChegada());
      }
    }

    // Calcula os tempos médios
    float tempoRetornoMedio = (float)tempoRetorno / totalProcessos;
    float tempoRespostaMedio = (float)tempoResposta / totalProcessos;
    float tempoEsperaMedio = (float)tempoEspera / totalProcessos;

    System.out.println(String.format("RR %.1f %.1f %.1f", tempoRetornoMedio, tempoRespostaMedio, tempoEsperaMedio));
  }
}
