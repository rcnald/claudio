package com.example;

import java.util.*;
// Importações necessárias para o "Par" (Caminho, Custo)
import java.util.AbstractMap;
import java.util.Map;

public class MapaGalaxia {
    private Map<String, Local> locais;

    public MapaGalaxia() {
        this.locais = new HashMap<>();
    }

    // --- Métodos de Manutenção do Grafo ---
    public void adicionarLocal(String nome) {
        if (!locais.containsKey(nome)) {
            locais.put(nome, new Local(nome));
        }
    }

    public void adicionarRota(String nomeOrigem, String nomeDestino, double peso) {
        Local origem = getLocal(nomeOrigem);
        Local destino = getLocal(nomeDestino);
        if (origem != null && destino != null) {
            origem.adicionarRota(destino, peso);
        }
    }

    public void adicionarRotaMaoDupla(String nomeA, String nomeB, double peso) {
        adicionarRota(nomeA, nomeB, peso);
        adicionarRota(nomeB, nomeA, peso);
    }
    
    // --- Métodos de Consulta ---
    public Local getLocal(String nome) {
        return locais.get(nome);
    }
    
    public List<Local> getTodosOsLocais() {
        return new ArrayList<>(locais.values());
    }

    public List<Local> verDestinosPossiveis(String nomeLocal) {
        Local local = getLocal(nomeLocal);
        if (local == null) return Collections.emptyList();
        
        List<Local> destinos = new ArrayList<>();
        for (Rota rota : local.getRotasDeSaida()) {
            destinos.add(rota.getDestino());
        }
        return destinos;
    }

    
    // --- MÉTODO PARA OPÇÃO 3 (SCANNER) ---
    /**
     * Calcula o custo da rota mais rápida da origem para TODOS os outros locais.
     * Retorna um Map<Destino, Custo>.
     */
    public Map<Local, Double> calcularTodasRotasPossiveis(String nomeOrigem) {
        Local origem = getLocal(nomeOrigem);
        Map<Local, Double> distancias = new HashMap<>();
        PriorityQueue<Local> fila = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

        for (Local local : locais.values()) {
            distancias.put(local, Double.MAX_VALUE);
        }
        
        if (origem == null) return distancias; 
        
        distancias.put(origem, 0.0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            Local localAtual = fila.poll();
            double distanciaAtual = distancias.get(localAtual);

            if (distanciaAtual == Double.MAX_VALUE) continue;

            for (Rota rota : localAtual.getRotasDeSaida()) {
                Local vizinho = rota.getDestino();
                double novaDistancia = distanciaAtual + rota.getPeso();

                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    fila.remove(vizinho); 
                    fila.add(vizinho);
                }
            }
        }
        return distancias;
    }

    // --- NOVO MÉTODO (NA VERDADE, ANTIGO) PARA OPÇÃO 4 (PILOTO AUTOMÁTICO) ---
    /**
     * Calcula a rota mais rápida da origem para UM destino específico.
     * Retorna um "Par" (Map.Entry) contendo o Caminho (List<Local>) e o Custo (Double).
     */
    public Map.Entry<List<Local>, Double> encontrarRotaMaisRapida(String nomeOrigem, String nomeDestino) {
        Local origem = getLocal(nomeOrigem);
        Local destino = getLocal(nomeDestino);

        if (origem == null || destino == null) {
            return null;
        }

        Map<Local, Double> distancias = new HashMap<>();
        Map<Local, Local> predecessores = new HashMap<>();
        PriorityQueue<Local> fila = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

        for (Local local : locais.values()) {
            distancias.put(local, Double.MAX_VALUE);
            predecessores.put(local, null);
        }
        distancias.put(origem, 0.0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            Local localAtual = fila.poll();
            
            // Achamos o destino
            if (localAtual.equals(destino)) {
                List<Local> caminho = reconstruirCaminho(predecessores, origem, destino);
                if (caminho == null) return null; // Não deveria acontecer
                
                double custoTotal = distancias.get(destino);
                return new AbstractMap.SimpleEntry<>(caminho, custoTotal);
            }
            
            if (distancias.get(localAtual) == Double.MAX_VALUE) continue;

            for (Rota rota : localAtual.getRotasDeSaida()) {
                Local vizinho = rota.getDestino();
                double novaDistancia = distancias.get(localAtual) + rota.getPeso();

                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    predecessores.put(vizinho, localAtual);
                    fila.remove(vizinho);
                    fila.add(vizinho);
                }
            }
        }
        return null; // Não achou caminho
    }
    
    // Método auxiliar para o 'encontrarRotaMaisRapida'
    private List<Local> reconstruirCaminho(Map<Local, Local> predecessores, Local origem, Local destino) {
        List<Local> caminho = new ArrayList<>();
        Local passoAtual = destino;
        while (passoAtual != null) {
            caminho.add(passoAtual);
            passoAtual = predecessores.get(passoAtual);
        }
        Collections.reverse(caminho);
        
        // Garante que o caminho começa na origem (se houver caminho)
        if (caminho.isEmpty() || !caminho.get(0).equals(origem)) {
            return null;
        }
        return caminho;
    }
}