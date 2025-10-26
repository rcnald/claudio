package com.example;

// Nossa "Aresta" Ponderada
public class Rota {
    private Local origem;
    private Local destino;
    private double peso; // Custo (tempo, combustível, etc.)

    public Rota(Local origem, Local destino, double peso) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    // Getters
    public Local getOrigem() {
        return origem;
    }

    public Local getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }
}