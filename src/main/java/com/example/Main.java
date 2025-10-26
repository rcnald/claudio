package com.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
// Importações necessárias para ordenar o mapa de resultados e usar o "Par"
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static MapaGalaxia mapa = new MapaGalaxia();
    private static Local localAtual = null; 
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        
        carregarDadosIniciais();
        boolean executando = true;

        while (executando) {
            limparTela();
            imprimirMenu(); 
            
            int escolha = lerEscolha();

            switch (escolha) {
                case 1:
                    if (localAtual == null) {
                        definirLocalInicial();
                    } else {
                        viajar(); // Viagem Adjacente
                    }
                    break;
                case 2:
                    verDestinosImediatos();
                    break;
                case 3:
                    // A Opção 3 agora é o "Scanner"
                    escanearRotasDeLongoAlcance();
                    break;
                case 4:
                    // --- NOVA OPÇÃO ---
                    ligarPilotoAutomatico(); // Viagem Longa
                    break;
                case 5:
                    adicionarLocal();
                    break;
                case 6:
                    adicionarRota();
                    break;
                case 7:
                    listarTodosLocais();
                    break;
                case 0:
                    executando = false;
                    System.out.println("Desligando navegador... Até a próxima viagem!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
            
            if (escolha != 0) {
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
    
    private static void limparTela() {
        try {
            String so = System.getProperty("os.name");
            if (so.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            for (int i = 0; i < 30; i++) System.out.println();
        }
    }

    private static void imprimirMenu() {
        System.out.println("=== NAVEGADOR GALÁCTICO ===");
        
        if (localAtual == null) {
            System.out.println("Localização Atual: DESCONHECIDA");
            System.out.println("---------------------------------");
            System.out.println("1. Definir Localização Inicial");
        } else {
            System.out.println("Localização Atual: " + localAtual.getNome());
            System.out.println("---------------------------------");
            System.out.println("1. Viajar (Mover para local adjacente)");
        }
        
        System.out.println("2. Ver Destinos Imediatos");
        System.out.println("3. Escanear Rotas (Planejar)"); // Opção 3 (Scanner)
        System.out.println("4. Ligar Piloto Automático (Viagem Longa)"); // NOVA Opção 4
        System.out.println("5. (Manutenção) Adicionar Novo Local");
        System.out.println("6. (Manutenção) Adicionar Nova Rota");
        System.out.println("7. Listar Todos os Locais Conhecidos");
        System.out.println("0. Sair");
        System.out.print("Escolha sua opção: ");
    }

    private static int lerEscolha() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Erro: Por favor, digite apenas números.");
            return -1;
        }
    }

    private static void definirLocalInicial() {
        System.out.println("--- Locais Conhecidos ---");
        listarTodosLocais();
        System.out.println("-------------------------");
        System.out.print("Digite o nome exato do seu local de origem: ");
        String nomeLocal = scanner.nextLine();
        
        Local loc = mapa.getLocal(nomeLocal);
        
        if (loc == null) {
            System.out.println("Erro: Local '" + nomeLocal + "' não encontrado.");
        } else {
            localAtual = loc; 
            System.out.println("Localização inicial definida para: " + localAtual.getNome());
        }
    }

    // OPÇÃO 1: Viagem de um "pulo"
    private static void viajar() {
        System.out.println("Escolha seu próximo destino:");
        List<Local> destinosPossiveis = mapa.verDestinosPossiveis(localAtual.getNome());
        
        if (destinosPossiveis.isEmpty()) {
            System.out.println("Você está preso em '" + localAtual.getNome() + "'. Não há rotas de saída.");
            return;
        }
        
        for (int i = 0; i < destinosPossiveis.size(); i++) {
            System.out.println((i + 1) + ". " + destinosPossiveis.get(i).getNome());
        }
        System.out.println("0. Cancelar Viagem");

        int escolha = -1;
        while (escolha < 0 || escolha > destinosPossiveis.size()) {
            System.out.print("Digite o número do destino: ");
            escolha = lerEscolha();
        }
        
        if (escolha == 0) {
            System.out.println("Viagem cancelada.");
            return;
        }
        
        Local novoDestino = destinosPossiveis.get(escolha - 1);
        localAtual = novoDestino; // ATUALIZA O LOCAL ATUAL
        
        System.out.println("Viajando...");
        System.out.println("Você chegou em: " + localAtual.getNome());
    }

    // OPÇÃO 2: Ver adjacentes
    private static void verDestinosImediatos() {
        if (localAtual == null) {
            System.out.println("Erro: Você precisa definir seu local inicial primeiro (Opção 1).");
            return;
        }
        
        List<Local> destinosPossiveis = mapa.verDestinosPossiveis(localAtual.getNome());
        
        if (destinosPossiveis.isEmpty()) {
            System.out.println("Não há rotas de saída diretas de '" + localAtual.getNome() + "'.");
        } else {
            System.out.println("A partir de '" + localAtual.getNome() + "', você pode pular diretamente para:");
            for (Local d : destinosPossiveis) {
                System.out.println("- " + d.getNome());
            }
        }
    }

    // OPÇÃO 3: Scanner de longo alcance
    private static void escanearRotasDeLongoAlcance() {
        if (localAtual == null) {
            System.out.println("Erro: Você precisa definir seu local inicial primeiro (Opção 1).");
            return;
        }

        Map<Local, Double> distancias = mapa.calcularTodasRotasPossiveis(localAtual.getNome());

        List<Map.Entry<Local, Double>> alcançaveis = distancias.entrySet().stream()
                .filter(entry -> entry.getValue() < Double.MAX_VALUE && entry.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        List<Local> inalcançaveis = distancias.entrySet().stream()
                .filter(entry -> entry.getValue() == Double.MAX_VALUE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Relatório de Rotas a partir de: " + localAtual.getNome());
        System.out.println("---------------------------------------------");
        
        if (alcançaveis.isEmpty()) {
            System.out.println("Nenhum outro local é alcançável a partir daqui.");
        } else {
            System.out.println("DESTINOS ALCANÇÁVEIS (do mais próximo ao mais distante):");
            for (Map.Entry<Local, Double> entry : alcançaveis) {
                System.out.printf("- %-28s (Custo: %.1f)\n", entry.getKey().getNome(), entry.getValue());
            }
        }

        System.out.println("---------------------------------------------");
        
        if (!inalcançaveis.isEmpty()) {
            System.out.println("DESTINOS INALCANÇÁVEIS (sem rota conhecida):");
            for (Local local : inalcançaveis) {
                System.out.println("- " + local.getNome());
            }
        }
    }

    // --- OPÇÃO 4 ATUALIZADA (Piloto Automático com simulação) ---
    private static void ligarPilotoAutomatico() {
        if (localAtual == null) {
            System.out.println("Erro: Você precisa definir seu local inicial primeiro (Opção 1).");
            return;
        }

        System.out.print("Digite o local de DESTINO FINAL: ");
        String rotaDestino = scanner.nextLine();
        
        if (localAtual.getNome().equalsIgnoreCase(rotaDestino)) {
            System.out.println("Você já está no destino!");
            return;
        }

        // 1. Encontra a rota MAIS RÁPIDA
        Map.Entry<List<Local>, Double> rota = mapa.encontrarRotaMaisRapida(localAtual.getNome(), rotaDestino);

        if (rota == null) {
            System.out.println("Nenhuma rota encontrada de " + localAtual.getNome() + " para " + rotaDestino);
            return;
        }

        // 2. Mostra a rota e pede confirmação
        System.out.println("Rota mais rápida encontrada:");
        System.out.println(formatarRota(rota));
        System.out.print("\nDeseja iniciar a viagem? (s/n): ");
        
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.equalsIgnoreCase("s")) {
            // 3. Executa a viagem SIMULADA
            List<Local> caminho = rota.getKey();
            
            System.out.println("\nPiloto automático ativado...");
            
            // --- INÍCIO DA SIMULAÇÃO DE VIAGEM ---
            try {
                // Começa do índice 1, porque o índice 0 é onde já estamos
                for (int i = 1; i < caminho.size(); i++) {
                    Local proximaParada = caminho.get(i);
                    
                    System.out.println("Viajando para " + proximaParada.getNome() + "...");
                    
                    // Pausa de 1.5 segundos para simular a viagem
                    Thread.sleep(1500); 
                    
                    // ATUALIZA O LOCAL ATUAL passo a passo
                    localAtual = proximaParada; 
                    
                    System.out.println("...Salto concluído. Você chegou em: " + localAtual.getNome());
                }
                
                // Pausa final
                Thread.sleep(500);
                System.out.println("\nViagem concluída! Destino final (" + localAtual.getNome() + ") alcançado.");

            } catch (InterruptedException e) {
                // Trata o erro se o 'sleep' for interrompido
                System.out.println("Alerta! O piloto automático foi interrompido!");
                Thread.currentThread().interrupt(); // Restaura o status de interrupção
            }
            // --- FIM DA SIMULAÇÃO ---

        } else {
            System.out.println("Viagem cancelada.");
        }
    }
    
    /**
     * Método auxiliar para formatar a rota
     */
    private static String formatarRota(Map.Entry<List<Local>, Double> rota) {
        double custo = rota.getValue();
        List<Local> caminho = rota.getKey();
        
        StringJoiner sj = new StringJoiner(" -> ");
        for (Local local : caminho) {
            sj.add(local.getNome());
        }
        return String.format("(Custo: %.1f) %s", custo, sj.toString());
    }

    // --- Opções de Manutenção ---

    private static void adicionarLocal() {
        System.out.print("Digite o nome do novo local: ");
        String nomeLocal = scanner.nextLine();
        mapa.adicionarLocal(nomeLocal);
        System.out.println("Local '" + nomeLocal + "' adicionado com sucesso!");
    }

    private static void adicionarRota() {
        System.out.print("Digite o local de ORIGEM: ");
        String origem = scanner.nextLine();
        System.out.print("Digite o local de DESTINO: ");
        String destino = scanner.nextLine();
        
        double peso = -1.0;
        while (peso < 0) {
            System.out.print("Digite o custo/tempo da rota (ex: 5.0): ");
            try {
                peso = Double.parseDouble(scanner.nextLine());
                if (peso < 0) System.out.println("O custo não pode ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Erro: Valor inválido. Use ponto para decimais.");
            }
        }

        System.out.print("A rota é de mão dupla? (s/n): ");
        String maoDupla = scanner.nextLine();
        
        if (maoDupla.equalsIgnoreCase("s")) {
            mapa.adicionarRotaMaoDupla(origem, destino, peso);
            System.out.println("Rota de mão dupla adicionada!");
        } else {
            mapa.adicionarRota(origem, destino, peso);
            System.out.println("Rota de mão única adicionada!");
        }
    }

    private static void listarTodosLocais() {
        List<Local> todosLocais = mapa.getTodosOsLocais();
        if (todosLocais.isEmpty()) {
            System.out.println("Nenhum local cadastrado.");
        } else {
            for (Local l : todosLocais) {
                System.out.println("- " + l.getNome());
            }
        }
    }

    private static void carregarDadosIniciais() {
        mapa.adicionarLocal("Terra");
        mapa.adicionarLocal("Lua");
        mapa.adicionarLocal("Estação Orbital Terra");
        mapa.adicionarLocal("Marte");
        mapa.adicionarLocal("Venus");
        mapa.adicionarLocal("Mercurio");
        mapa.adicionarLocal("Cinturão de Asteroides");
        mapa.adicionarLocal("Jupiter");
        mapa.adicionarLocal("Saturno");
        mapa.adicionarLocal("Ponto de Salto (Hiperespaço)");
        mapa.adicionarLocal("Alpha Centauri");
        mapa.adicionarLocal("Sirius");
        mapa.adicionarLocal("Betelgeuse");
        mapa.adicionarLocal("Nebulosa do Caranguejo");
        mapa.adicionarLocal("Sistema Trappist-1"); // Isolado

        mapa.adicionarRotaMaoDupla("Terra", "Estação Orbital Terra", 0.5);
        mapa.adicionarRotaMaoDupla("Terra", "Lua", 1.0);
        mapa.adicionarRotaMaoDupla("Terra", "Marte", 4.0);
        mapa.adicionarRotaMaoDupla("Terra", "Venus", 3.0);
        mapa.adicionarRotaMaoDupla("Venus", "Mercurio", 2.0);
        mapa.adicionarRotaMaoDupla("Marte", "Cinturão de Asteroides", 2.0);
        mapa.adicionarRotaMaoDupla("Marte", "Ponto de Salto (Hiperespaço)", 10.0);
        mapa.adicionarRotaMaoDupla("Cinturão de Asteroides", "Jupiter", 8.0);
        mapa.adicionarRotaMaoDupla("Jupiter", "Saturno", 10.0);
        mapa.adicionarRotaMaoDupla("Ponto de Salto (Hiperespaço)", "Alpha Centauri", 5.0);
        mapa.adicionarRotaMaoDupla("Ponto de Salto (Hiperespaço)", "Sirius", 8.0);
        mapa.adicionarRota("Alpha Centauri", "Betelgeuse", 10.0);
        mapa.adicionarRota("Sirius", "Betelgeuse", 3.0);
        mapa.adicionarRota("Betelgeuse", "Nebulosa do Caranguejo", 20.0);
        mapa.adicionarRota("Nebulosa do Caranguejo", "Sirius", 15.0);
        mapa.adicionarRotaMaoDupla("Terra", "Ponto de Salto (Hiperespaço)", 50.0);
    }
}