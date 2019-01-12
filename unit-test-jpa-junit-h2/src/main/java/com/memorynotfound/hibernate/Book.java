package com.memorynotfound.hibernate;

import javax.persistence.*;

@Entity
@NamedQueries(value = {
    @NamedQuery(name = "Book.getAll", query = "SELECT b FROM Book b")
})
public class Book {

    @Id
    private Integer id;
    private String title;

    public Book() {
    }

    public Book(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
