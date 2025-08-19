package com.juci.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // CUIDADO: pode causar problemas se títulos repetirem
    private String title;

    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Person> authors = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private List<Languages> languages = new ArrayList<>();

    private Integer downloadCount;

    public Book(BookData bookData) {
        this.title = bookData.title();
        this.languages = bookData.languages();
        this.downloadCount = bookData.downloadCount();
    }

    public Book() {
        // Inicializado para evitar NullPointerException
        this.authors = new ArrayList<>();
        this.languages = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Person> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Person> authors) {
        this.authors = authors;
        for (Person author : authors) {
            if (!author.getBooks().contains(this)) {
                author.getBooks().add(this);
            }
        }
    }

    public void addAuthor(Person person) {
        this.authors.add(person);
        if (!person.getBooks().contains(this)) {
            person.getBooks().add(this);
        }
    }

    public List<Languages> getLanguages() {
        return languages;
    }

    public String getLanguagesAsString() {
        return languages.stream()
                .map(Languages::getIdioma)
                .collect(Collectors.joining(", "));
    }

    public void setLanguages(List<Languages> languages) {
        this.languages = languages;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {
        return "Título: " + title + '\n' +
                "Autor(es): \n" + authors.stream().map(Person::toString).collect(Collectors.joining("\n")) + '\n' +
                "Idioma(s): " + getLanguagesAsString() + '\n' +
                "Downloads: " + downloadCount + "\n";
    }
}
