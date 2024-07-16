package com.aluracursos.challenges.literAlura.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DTOLibro(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DTOAutor> autor,
        @JsonAlias("languages") List<String> idiomas,
        @JsonAlias("download_count") Double numeroDeDescargas
) {
    public DTOLibro {
        if (autor.size() == 0) {
            autor.add(new DTOAutor("NULL", 0, 0));
        }
    }
}