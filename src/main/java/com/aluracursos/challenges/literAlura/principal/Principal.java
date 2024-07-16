package com.aluracursos.challenges.literAlura.principal;

import com.aluracursos.challenges.literAlura.model.*;
import com.aluracursos.challenges.literAlura.model.dto.DTOAutor;
import com.aluracursos.challenges.literAlura.model.dto.DTOLibro;
import com.aluracursos.challenges.literAlura.model.dto.DTOResultado;
import com.aluracursos.challenges.literAlura.repository.*;
import com.aluracursos.challenges.literAlura.service.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvertirDatos conversor = new ConvertirDatos();
    private final String URL_BASE = "https://gutendex.com/books/";
    private AutorRepository autorRepository;
    private LibroRepository libroRepository;
    private String nombreLibro;
    String menu = """
                    
                    Escribe el número de la opción que deseas escoger:
                    
                    1 - Buscar libro por titulo.
                    2 - Listar libros registrados.
                    3 - Listar autores registrados.
                    4 - Listar autores vivos en un determinado año.
                    5 - Listar libros por idioma.
                    
                    
                    0- Salir.
                    """;

    private int opcion;
    public Principal(AutorRepository repositorioAutores, LibroRepository libroRepository) {
        this.autorRepository = repositorioAutores;
        this.libroRepository = libroRepository;
    }


    public void muestraMenu() {
        var opcion = -1;
        Scanner teclado = new Scanner(System.in);
        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } catch (Exception e) {
                System.out.println("Opcion invalida. Intente nuevamente.");
                muestraMenu();
            }

            switch (opcion) {
                case 1:
                    buscarLibroApi();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    registroDeAutores();
                    break;
                case 4:
                    registroAutoresVivos();
                    break;
                case 5:
                    registroLibrosIdioma();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Escribe una de las opciones del menu.");
                    break;
            }

        }
    }
    private void buscarLibroApi() {
        DTOResultado DTOResultado = obtenerDatosLibro();
        DTOLibro DTOLibro = DTOResultado.resultados().get(0);
        Libro libro = new Libro(DTOLibro);
        if (libro.getTitulo().toLowerCase().contains(nombreLibro)) {
            DTOAutor DTOAutor = DTOLibro.autor().get(0);
            Autor autor = new Autor(DTOAutor);
            var datosTitulos = libroRepository.comprobacionDeExitenciaLibro();
            var datosNombreAutor = autorRepository.comprobacionDeExistenciaAutor();

            if (datosNombreAutor.contains(autor.getNombre())) {
                if (datosTitulos.contains(libro.getTitulo())) {
                    System.out.println("\nNo se puede registrar el mismo libro más de una vez.\n");
                } else {
                    var id = autorRepository.obtenerIdAutor(autor.getNombre()).get(0);
                    Autor autor1 = autorRepository.findById(id).orElse(null);
                    libro.setAutor(autor1);
                    libroRepository.save(libro);
                    var libroRegistrado = libroRepository.obtenerDatosLibro(nombreLibro);
                    libroRegistrado.forEach(l -> System.out.printf("""
                           \n--------LIBRO---------
                           Titulo: %s
                           Autor: %s
                           idioma: %s
                           Numero de descargas: %s
                           ----------------------\n
                           """, l.getTitulo(), l.getAutor().getNombre(), l.getIdiomas().get(0), l.getNumeroDeDescargas()));
                }
            } else {
                autorRepository.save(autor);
                libro.setAutor(autor);
                libroRepository.save(libro);
                var libroRegistrado = libroRepository.obtenerDatosLibro(nombreLibro);
                libroRegistrado.forEach(l -> System.out.printf("""
                           \n--------LIBRO---------
                           Titulo: %s
                           Autor: %s
                           idioma: %s
                           Numero de descargas: %s
                           ----------------------\n
                           """, l.getTitulo(), l.getAutor().getNombre(), l.getIdiomas().get(0), l.getNumeroDeDescargas()));
            }
        } else {
            System.out.println("No existe ningún libro con este titulo.");
            buscarLibroApi();
        }
    }

    private void listarLibros() {
        var registro = libroRepository.obtenerTituloLibro();
        registro.forEach(l -> System.out.printf("""
                           \n--------LIBRO---------
                           Titulo: %s
                           Autor: %s
                           idioma: %s
                           Numero de descargas: %s
                           ----------------------\n
                           """, l.getTitulo(), l.getAutor().getNombre(), l.getIdiomas().get(0), l.getNumeroDeDescargas()));


    }

    private void registroDeAutores() {
        var registro = autorRepository.registroAutores();
        var libroAutores = libroRepository.obtenerTituloLibro(registro);
        Map<Autor, List<String>> librosPorAutor = libroAutores.stream()
                .collect(Collectors.groupingBy(
                        Libro::getAutor,
                        Collectors.mapping(Libro::getTitulo, Collectors.toList())
                ));

        librosPorAutor.forEach((autor, titulos) -> System.out.printf("""
                   \nAutor: %s
                   Fecha de nacimiento: %s
                   Fecha de fallecimiento: %s
                   Libros: [%s]\n
                   """, autor.getNombre(), autor.getFechaNacimiento(), autor.getFechaFallecimiento(), String.join(", ", titulos)));
    }

    private void registroAutoresVivos() {
        int ano = 0;
        Scanner teclado = new Scanner(System.in);
        int anoActual = LocalDate.now().getYear();
        System.out.println("Escribe el año que deseas investigar.");
        try {
            ano = teclado.nextInt();
            teclado.nextLine();
        } catch (Exception e) {
            System.out.println("Ingresa un valor numérico.");
            registroAutoresVivos();
        }
        if (ano <= anoActual) {
            var nombreAutoresVivos = autorRepository.nombreAutoresVivos(ano);
            if (nombreAutoresVivos.isEmpty()) {
                System.out.println("\nNo se encontraron autores vivos en el periodo solicitado.\n");
            } else {
                var libroAutores = libroRepository.obtenerTituloLibro(nombreAutoresVivos);
                Map<Autor, List<String>> librosPorAutor = libroAutores.stream()
                        .collect(Collectors.groupingBy(
                                Libro::getAutor,
                                Collectors.mapping(Libro::getTitulo, Collectors.toList())
                        ));

                librosPorAutor.forEach((autor, titulos) -> System.out.printf("""
                           \nAutor: %s
                           Fecha de Nacimiento: %s
                           Fecha de Fallecimiento: %s
                           Libros: [%s]\n
                           """, autor.getNombre(), autor.getFechaNacimiento(), autor.getFechaFallecimiento(), String.join(", ", titulos)));
            }
        } else {
            System.out.println("Año inválido, intente nuevamente.");
        }

    }

    private void registroLibrosIdioma() {
        System.out.println("""
                   Ingrese el idioma para buscar los libros:
                   (es) - Español
                   (en) - Inglés
                   (fr) - Francés
                   (pt) - Portugués
                   """);

        var opcion = teclado.nextLine().toLowerCase();
        if (opcion.equals("es")) {
            buscarLibrosIdioma(opcion);
        } else if (opcion.equals("en")){
            buscarLibrosIdioma(opcion);
        } else if (opcion.equals("fr")) {
            buscarLibrosIdioma(opcion);
        } else if (opcion.equals("pt")) {
            buscarLibrosIdioma(opcion);
        } else {
            System.out.println("Ingresa una opcion valida.");
            registroLibrosIdioma();
        }

    }

    private void buscarLibrosIdioma(String idioma) {
        List<String> idiomas = new ArrayList<>();
        idiomas.add(idioma);
        var librosIdioma = libroRepository.obtenerLibroIdioma(idiomas);
        librosIdioma.forEach(l -> System.out.printf("""
                           \n--------LIBRO---------
                           Título: %s
                           Autor: %s
                           Idioma: %s
                           Número de Descargas: %s
                           ----------------------\n
                           """, l.getTitulo(), l.getAutor().getNombre(), l.getIdiomas().get(0), l.getNumeroDeDescargas()));
    }

    private DTOResultado obtenerDatosLibro() {
        System.out.println("Ingrese el libro que desea buscar.");
        nombreLibro = teclado.nextLine().toLowerCase();
        var json = consumoApi.obtenerJson(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        if (json.contains("title")) {
            DTOResultado datos = conversor.obtenerDatos(json, DTOResultado.class);
            return datos;
        } else {
            System.out.println("Este libro no exite, intente nuevamente.");
            DTOResultado datos = obtenerDatosLibro();
            return datos;
        }


    }
}
