package ua.vasylenko.library.v13.Spring.Boot.DTO;

import java.util.List;

public class BooksResponse {
    private List<BookDTOAllFields> bookDTOList;

    public BooksResponse(List<BookDTOAllFields> bookDTOList) {
        this.bookDTOList = bookDTOList;
    }

    public List<BookDTOAllFields> getBookDTOList() {
        return bookDTOList;
    }

    public void setBookDTOList(List<BookDTOAllFields> bookDTOList) {
        this.bookDTOList = bookDTOList;
    }
}
