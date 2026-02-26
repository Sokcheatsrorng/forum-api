package com.forum.bookmark;


import com.forum.bookmark.dto.BookMarkRequest;
import com.forum.bookmark.dto.BookMarkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @GetMapping
    public ResponseEntity<BookMarkResponse> getMyBookmarks() {
        return ResponseEntity.ok(bookMarkService.getMyBookmarks());
    }

    @PostMapping("/add")
    public ResponseEntity<BookMarkResponse> addBookMark(@RequestBody BookMarkRequest request) {
        return ResponseEntity.ok(bookMarkService.addBookMark(request));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<BookMarkResponse> removeBookMark(@RequestBody BookMarkRequest request) {
        return ResponseEntity.ok(bookMarkService.removeBookMark(request));
    }
}