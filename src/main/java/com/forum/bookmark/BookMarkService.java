package com.forum.bookmark;

import com.forum.bookmark.dto.BookMarkRequest;
import com.forum.bookmark.dto.BookMarkResponse;

public interface BookMarkService {

    BookMarkResponse addBookMark(BookMarkRequest bookMarkRequest);

    BookMarkResponse removeBookMark(BookMarkRequest bookMarkRequest);

    BookMarkResponse getMyBookmarks();


}
