package com.juci.literalura.menu;

import com.juci.literalura.model.Book;
import com.juci.literalura.model.Languages;
import com.juci.literalura.repository.BookRepository;
import com.juci.literalura.repository.PersonRepository;
import com.juci.literalura.service.GetBook;
import org.springframework.stereotype.Component;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Menu {

    private final Scanner sc = new Scanner(System.in);
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;

    private static final String SPLASH = """
           ,gggg,                                        ,ggg,  
          d8" "8I        I8                             dP""8I  
          88  ,dP        I8                            dP   88  
       8888888P"    gg88888888                        dP    88  
          88        ""   I8                          ,8'    88  
          88        gg   I8   ,ggg,   ,gggggg,       d88888888  
     ,aa,_88        88   I8  i8" "8i  dP""\\"8I __   ,8"     88  
    dP" "88P        88  ,I8, I8, ,8I ,8'    8IdP"  ,8P      Y8  
    Yb,_,d88b,,_  _,88,,d88b,`YbadP',dP     Y8Yb,_,dP       `8b 
     "Y8P"  "Y88888P""Y8P""Y888P"Y888P      `Y8"Y8P"         `Y8 
    """;

    private static final String MENU_TEXT = """
            1 - Buscar livro pelo título
            2 - Listar livros registrados
            3 - Listar autores registrados
            4 - Listar autores vivos em um determinado ano
            5 - Listar livros em determinado idioma
            6 - Listar Top 10 livros mais baixados
            7 - Buscar autor
            8 - Verificar percentual de livros por idioma

            0 - Sair
        """;

    private static final String API_BASE = "https://gutendex.com/books/?search=";

    public Menu(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    public void exibirMenu() {
        System.out.println(SPLASH);
        int option = -1;

        while (option != 0) {
            System.out.println(MENU_TEXT);
            try {
                option = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida!\n");
                continue;
            }

            switch (option) {
                case 1 -> buscarLivro();
                case 2 -> listarLivros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivosAno();
                case 5 -> listarLivrosPorIdioma();
                case 6 -> listarTop10Livros();
                case 7 -> buscarAutor();
                case 8 -> listarPercentualPorIdioma();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!\n");
            }
        }
    }

    private void buscarLivro() {
        System.out.println("Digite o título do livro:");
        String searchTerm = sc.nextLine().trim().replace(" ", "%20");
        new GetBook(bookRepository, personRepository).getDataBook(API_BASE + searchTerm);
    }

    private void listarLivros() {
        bookRepository.findAll().forEach(System.out::println);
    }

    private void listarAutores() {
        personRepository.findAll().forEach(System.out::println);
    }

    private void listarAutoresVivosAno() {
        System.out.println("Digite o ano:");
        try {
            int year = Integer.parseInt(sc.nextLine().trim());
            personRepository.findLivingAuthorsInYear(year).forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido!\n");
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Digite o idioma:");
        String idiomaInput = sc.nextLine().trim().toLowerCase();
        Languages lang = Languages.fromIdioma(idiomaInput);
        if (lang == null) {
            System.out.println("Idioma não reconhecido.\n");
            return;
        }
        bookRepository.findBooksByLanguage(lang).forEach(System.out::println);
    }

    private void listarTop10Livros() {
        new GetBook(bookRepository, personRepository).getTop(API_BASE);
    }

    private void buscarAutor() {
        System.out.println("Digite nome ou sobrenome do autor:");
        String autor = sc.nextLine().trim().replace(" ", "%20");
        new GetBook(bookRepository, personRepository).getAuthor(autor);
    }

    private void listarPercentualPorIdioma() {
        List<Book> books = bookRepository.findAll();
        long total = books.size();
        if (total == 0) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        // Aqui achata a lista de idiomas e agrupa por idioma individual
        Map<Languages, DoubleSummaryStatistics> map = books.stream()
                .flatMap(book -> book.getLanguages().stream())
                .collect(Collectors.groupingBy(
                        lang -> lang,
                        Collectors.summarizingDouble(b -> 1)
                ));

        System.out.println("Percentual de livros por idioma:");
        map.forEach((lang, stats) -> {
            double pct = stats.getCount() * 100.0 / total;
            System.out.printf("%s: %.2f%%%n", lang.getIdioma(), pct);
        });
    }
}
