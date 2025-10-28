package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class Local {
    private String nome;
    private List<Rota> rotasDeSaida;

    public Local(String nome) {
        this.nome = nome;
        this.rotasDeSaida = new ArrayList<>();
    }

    public void adicionarRota(Local destino, double peso) {
        Rota novaRota = new Rota(this, destino, peso);
        this.rotasDeSaida.add(novaRota);
    }

    public String getNome() {
        return nome;
    }

    public List<Rota> getRotasDeSaida() {
        return rotasDeSaida;
    }
    
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
        return nome;
    }
}