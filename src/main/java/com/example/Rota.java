package com.example;

public class Rota {
    private Local origem;
    private Local destino;
    private double peso;

    public Rota(Local origem, Local destino, double peso) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

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