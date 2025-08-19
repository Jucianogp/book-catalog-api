package com.juci.literalura.service;

import com.juci.literalura.model.*;
import com.juci.literalura.repository.BookRepository;
import com.juci.literalura.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetBook {
    ApiInvoker apiInvoker = new ApiInvoker();
    JsonConverter converter = new JsonConverter();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PersonRepository personRepository;

    public GetBook(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public void getDataBook(String livro) {
        String json = apiInvoker.getData(livro);
        Data data = converter.getData(json, Data.class);

        try {
            BookData dadosLivro = data.books().get(0);
            Book book = new Book(dadosLivro);

            // Evitar chamar findByTitle duas vezes
            Book existingBook = bookRepository.findByTitle(book.getTitle());
            if (existingBook != null) {
                System.out.println("Livro já pesquisado, recuperando dados do Banco de dados:\n");
                System.out.println(existingBook);
            } else {
                List<Person> authors = new ArrayList<>();
                for (PersonData authordata : dadosLivro.authors()) {
                    Person author = new Person(authordata);
                    List<Person> existingAuthors = personRepository.findByName(author.getName());
                    if (existingAuthors.isEmpty()) {
                        author = personRepository.save(author);
                    } else {
                        author = existingAuthors.get(0);
                    }
                    authors.add(author);
                }
                book.setAuthors(authors);
                bookRepository.save(book);
                System.out.println(book);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Livro não encontrado\n");
        }
    }

    public void getTop(String url) {
        String json = apiInvoker.getData(url);
        Data data = converter.getData(json, Data.class);

        for (int i = 0; i < 10; i++) {
            try {
                BookData dadosLivro = data.books().get(i);
                Book book = new Book(dadosLivro);

                Book existingBook = bookRepository.findByTitle(book.getTitle());
                if (existingBook != null) {
                    System.out.println(existingBook);
                } else {
                    List<Person> authors = new ArrayList<>();
                    for (PersonData authordata : dadosLivro.authors()) {
                        Person author = new Person(authordata);
                        List<Person> existingAuthors = personRepository.findByName(author.getName());
                        if (existingAuthors.isEmpty()) {
                            author = personRepository.save(author);
                        } else {
                            author = existingAuthors.get(0);
                        }
                        authors.add(author);
                    }
                    book.setAuthors(authors);
                    bookRepository.save(book);
                    System.out.println(book);
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Livro não encontrado\n");
            }
        }
    }

    public void getAuthor(String search) {
        String json = apiInvoker.getData("https://gutendex.com/books/?search=" + search);
        Data data = converter.getData(json, Data.class);

        try {
            BookData dadosLivro = data.books().get(0);
            Book book = new Book(dadosLivro);
            String normalizedSearch = search.toLowerCase();

            // Busca no banco pelo nome ou sobrenome ignorando case
            List<Person> matchingAuthors = personRepository.findByNameContainingIgnoreCase(normalizedSearch);
            if (!matchingAuthors.isEmpty()) {
                System.out.println("Encontrado no banco de dados:\n" + matchingAuthors.get(0));
                return;
            }

            System.out.println("Autor não encontrado no banco de dados.");

            Book existingBook = bookRepository.findByTitle(book.getTitle());
            if (existingBook != null) {
                // Filtra autores do livro que contenham o termo pesquisado
                List<Person> existingAuthors = existingBook.getAuthors().stream()
                        .filter(a -> a.getName().toLowerCase().contains(normalizedSearch))
                        .collect(Collectors.toList());

                if (!existingAuthors.isEmpty()) {
                    existingAuthors.forEach(author -> System.out.println("Autor encontrado no livro: " + author));
                    return;
                }
            }

            // Se não encontrou no banco, adiciona o autor e livro
            List<Person> authors = new ArrayList<>();
            for (PersonData authordata : dadosLivro.authors()) {
                Person author = new Person(authordata);
                List<Person> existingAuthorsList = personRepository.findByName(author.getName());
                if (existingAuthorsList.isEmpty()) {
                    author = personRepository.save(author);
                } else {
                    author = existingAuthorsList.get(0);
                }
                authors.add(author);
            }
            book.setAuthors(authors);
            bookRepository.save(book);
            System.out.println("Novo livro inserido no banco de dados:\n" + book);

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Autor não encontrado.\n");
        }
    }
}
