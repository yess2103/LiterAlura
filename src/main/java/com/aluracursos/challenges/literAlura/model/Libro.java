package com.aluracursos.challenges.literAlura.model;

import com.aluracursos.challenges.literAlura.model.dto.DTOLibro;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private List<String> idiomas;
    private Double numeroDeDescargas;
    @ManyToOne
    private Autor autor;

    public Libro() {}

    public Libro(DTOLibro l) {
        this.titulo = l.titulo();
        this.idiomas = l.idiomas();
        this.numeroDeDescargas = l.numeroDeDescargas();
    }

    public String getTitulo() {
        return titulo;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "Libro{" +
                ", titulo='" + titulo + '\'' +
                ", idiomas='" + idiomas + '\'' +
                ", numeroDeDescargas=" + numeroDeDescargas +
                ", autor=" + autor.getNombre() +
                '}';
    }
}