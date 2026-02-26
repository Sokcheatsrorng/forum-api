package com.forum.service;

import com.forum.dto.TagRequest;
import com.forum.dto.TagResponse;
import com.forum.entity.Tag;
import com.forum.exception.ResourceNotFoundException;
import com.forum.exception.ResourceAlreadyExistsException;
import com.forum.mapper.TagMapper;
import com.forum.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagResponse createTag(TagRequest tagDTO) {
        if (tagRepository.existsByTagName(tagDTO.getTagName())) {
            throw new ResourceAlreadyExistsException("Tag already exists: " + tagDTO.getTagName());
        }

        Tag tag = new Tag();
        tag.setTagName(tagDTO.getTagName());
        tag.setCount(0);

        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toResponse(savedTag);
    }

    public TagResponse getTagById(Integer tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));
        return mapToDTO(tag);
    }

    public Tag getTagEntityById(Integer tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));
    }

    public TagResponse updateTag(Integer tagId, TagRequest tagDTO) {
        Tag tag = getTagEntityById(tagId);

        if (tagDTO.getTagName() != null && !tag.getTagName().equals(tagDTO.getTagName())) {
            if (tagRepository.existsByTagName(tagDTO.getTagName())) {
                throw new ResourceAlreadyExistsException("Tag already exists: " + tagDTO.getTagName());
            }
            tag.setTagName(tagDTO.getTagName());
        }

        Tag updatedTag = tagRepository.save(tag);
        return mapToDTO(updatedTag);
    }

    public void deleteTag(Integer tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag not found with id: " + tagId);
        }
        tagRepository.deleteById(tagId);
    }

    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TagResponse> getPopularTags() {
        return tagRepository.findPopularTags().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TagResponse> getTopTags(Integer limit) {
        return tagRepository.findTopTags(limit).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TagResponse> searchTags(String searchTerm) {
        return tagRepository.searchTags(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void incrementTagCount(Integer tagId) {
        Tag tag = getTagEntityById(tagId);
        tag.setCount(tag.getCount() + 1);
        tagRepository.save(tag);
    }

    public void decrementTagCount(Integer tagId) {
        Tag tag = getTagEntityById(tagId);
        if (tag.getCount() > 0) {
            tag.setCount(tag.getCount() - 1);
            tagRepository.save(tag);
        }
    }

    private TagResponse mapToDTO(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getTagName(),
                tag.getCount(),
                tag.getExcerptPostId(),
                tag.getWikiPostId()
        );
    }
}
