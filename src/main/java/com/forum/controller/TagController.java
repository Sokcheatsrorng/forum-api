package com.forum.controller;

import com.forum.dto.TagRequest;
import com.forum.dto.TagResponse;
import com.forum.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tags")
@AllArgsConstructor
@Tag(name = "Tags", description = "Tag management endpoints")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create tag", description = "Create a new tag (authenticated users only)")
    @ApiResponse(responseCode = "201", description = "Tag created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Tag already exists")
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest tagDTO) {
        TagResponse createdTag = tagService.createTag(tagDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @GetMapping("/{tagId}")
    @Operation(summary = "Get tag by ID", description = "Retrieve tag information by tag ID")
    @ApiResponse(responseCode = "200", description = "Tag found")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Integer tagId) {
        TagResponse tag = tagService.getTagById(tagId);
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/{tagId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update tag", description = "Update tag information (authenticated users only)")
    @ApiResponse(responseCode = "200", description = "Tag updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Integer tagId,
            @Valid @RequestBody TagRequest tagDTO) {
        TagResponse updatedTag = tagService.updateTag(tagId, tagDTO);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/{tagId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete tag", description = "Delete a tag (authenticated users only)")
    @ApiResponse(responseCode = "204", description = "Tag deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    public ResponseEntity<Void> deleteTag(@PathVariable Integer tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all tags", description = "Retrieve all available tags")
    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular tags", description = "Retrieve most popular tags by usage count")
    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully")
    public ResponseEntity<List<TagResponse>> getPopularTags() {
        List<TagResponse> tags = tagService.getPopularTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/top/{limit}")
    @Operation(summary = "Get top tags", description = "Retrieve top N popular tags")
    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully")
    public ResponseEntity<List<TagResponse>> getTopTags(@PathVariable Integer limit) {
        List<TagResponse> tags = tagService.getTopTags(limit);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tags", description = "Search tags by name")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<List<TagResponse>> searchTags(@RequestParam String query) {
        List<TagResponse> tags = tagService.searchTags(query);
        return ResponseEntity.ok(tags);
    }
}
