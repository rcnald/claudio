package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Nosso "Vértice"
public class Local {
    private String nome;
    // Armazena as rotas que SAEM deste local
    private List<Rota> rotasDeSaida;

    public Local(String nome) {
        this.nome = nome;
        this.rotasDeSaida = new ArrayList<>();
    }

    // Adiciona uma rota de "pulo" deste local para outro
    public void adicionarRota(Local destino, double peso) {
        Rota novaRota = new Rota(this, destino, peso);
        this.rotasDeSaida.add(novaRota);
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public List<Rota> getRotasDeSaida() {
        return rotasDeSaida;
    }
    
    // Sobrescrever equals e hashCode é crucial para usar em Maps e Sets
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Local local = (Local) obj;
        return nome.equals(local.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        // Facilita na hora de imprimir
        return nome;
    }
}