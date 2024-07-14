package com.example.raspberrymonitor;

public class User {
    private int id;
    private String nome;
    private String cognome;
    private String cellulare;
    private String provenienza;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCellulare() { return cellulare; }
    public void setCellulare(String cellulare) { this.cellulare = cellulare; }

    public String getProvenienza() { return provenienza; }
    public void setProvenienza(String provenienza) { this.provenienza = provenienza; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", cellulare='" + cellulare + '\'' +
                ", provenienza='" + provenienza + '\'' +
                '}';
    }
}
