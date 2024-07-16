package com.aluracursos.challenges.literAlura.service;

public interface IConvertirDatos {

    <T> T obtenerDatos(String json, Class<T> clase);
}