package com.forum.controller;

import com.forum.dto.lostfound.*;
import com.forum.entity.ItemCategory;
import com.forum.entity.ItemLocation;
import com.forum.repository.UserRepository;
import com.forum.service.LostFoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/lost-found")
@RequiredArgsConstructor
public class LostFoundController {
    private final LostFoundService lostFoundService;
    private final UserRepository userRepository;

    @GetMapping("/categories") public List<ItemCategory> categories() { return lostFoundService.categories(); }
    @PostMapping("/categories") public ResponseEntity<ItemCategory> createCategory(@Valid @RequestBody CategoryRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(lostFoundService.createCategory(request)); }
    @GetMapping("/locations") public List<ItemLocation> locations() { return lostFoundService.locations(); }
    @PostMapping("/locations") public ResponseEntity<ItemLocation> createLocation(@RequestBody LocationRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(lostFoundService.createLocation(request)); }

    @GetMapping("/reports") public List<ItemReportResponse> reports(@RequestParam(required = false) String itemType) { return lostFoundService.listReports(itemType); }
    @GetMapping("/reports/{reportId}") public ItemReportResponse report(@PathVariable Integer reportId) { return lostFoundService.getReport(reportId); }
    @PostMapping("/reports") public ResponseEntity<ItemReportResponse> createReport(@Valid @RequestBody ItemReportRequest request, Authentication authentication) { return ResponseEntity.status(HttpStatus.CREATED).body(lostFoundService.createReport(request, userId(authentication))); }

    @PostMapping("/reports/{reportId}/claims") public ResponseEntity<ClaimResponse> claim(@PathVariable Integer reportId, @Valid @RequestBody ClaimRequest request, Authentication authentication) { return ResponseEntity.status(HttpStatus.CREATED).body(lostFoundService.claim(reportId, request, userId(authentication))); }
    @GetMapping("/reports/{reportId}/claims") public List<ClaimResponse> claims(@PathVariable Integer reportId, Authentication authentication) { return lostFoundService.claims(reportId, userId(authentication)); }
    @PatchMapping("/claims/{claimId}/approve") public ClaimResponse approveClaim(@PathVariable Integer claimId, Authentication authentication) { return lostFoundService.reviewClaim(claimId, true, userId(authentication)); }
    @PatchMapping("/claims/{claimId}/reject") public ClaimResponse rejectClaim(@PathVariable Integer claimId, Authentication authentication) { return lostFoundService.reviewClaim(claimId, false, userId(authentication)); }

    @GetMapping("/reports/{reportId}/matches") public List<MatchResponse> matches(@PathVariable Integer reportId, Authentication authentication) { return lostFoundService.matches(reportId, userId(authentication)); }
    @PatchMapping("/matches/{matchId}") public MatchResponse updateMatch(@PathVariable Integer matchId, @RequestParam String status, Authentication authentication) { return lostFoundService.updateMatch(matchId, status, userId(authentication)); }

    private Integer userId(Authentication authentication) {
        if (authentication == null) throw new IllegalStateException("Authentication is required");
        return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists")).getId();
    }
}
