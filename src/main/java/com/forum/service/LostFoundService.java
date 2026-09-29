package com.forum.service;

import com.forum.dto.lostfound.*;
import com.forum.entity.*;
import com.forum.exception.ResourceAlreadyExistsException;
import com.forum.exception.ResourceNotFoundException;
import com.forum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LostFoundService {
    private final ItemCategoryRepository categoryRepository;
    private final ItemLocationRepository locationRepository;
    private final ItemReportRepository reportRepository;
    private final ItemMatchRepository matchRepository;
    private final ItemClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ItemCategory createCategory(CategoryRequest request) {
        if (categoryRepository.findByNameIgnoreCase(request.name().trim()).isPresent()) throw new ResourceAlreadyExistsException("Category already exists");
        ItemCategory category = new ItemCategory(); category.setName(request.name().trim()); return categoryRepository.save(category);
    }
    public List<ItemCategory> categories() { return categoryRepository.findAll(); }
    public ItemLocation createLocation(LocationRequest request) {
        ItemLocation location = new ItemLocation(); location.setBuilding(request.building()); location.setFloor(request.floor()); location.setRoom(request.room()); return locationRepository.save(location);
    }
    public List<ItemLocation> locations() { return locationRepository.findAll(); }

    public ItemReportResponse createReport(ItemReportRequest request, Integer userId) {
        validateRequest(request);
        ItemReport report = new ItemReport();
        report.setUser(user(userId)); report.setItemType(request.itemType().toLowerCase()); report.setTitle(request.title());
        report.setCategory(request.categoryId() == null ? null : category(request.categoryId())); report.setDescription(request.description());
        report.setItemDate(request.itemDate()); report.setScope(request.scope().toLowerCase());
        report.setLocation(request.locationId() == null ? null : location(request.locationId())); report.setMapLat(request.mapLat()); report.setMapLng(request.mapLng());
        report.setFreeTextLocation(request.freeTextLocation()); report.setPhotoUrl(request.photoUrl());
        // A found item's hidden detail is intentionally not included by toResponse.
        report.setHiddenDetail(request.itemType().equalsIgnoreCase("found") ? request.hiddenDetail() : null);
        report.setStatus("open"); report.setModerationStatus("pending"); report.setExpiryDate(LocalDate.now().plusDays(30));
        ItemReport saved = reportRepository.save(report);
        createSuggestions(saved);
        return toResponse(saved);
    }

    public ItemReportResponse updateReport(Integer reportId, ItemReportRequest request, Integer currentUserId) {
        ItemReport report = report(reportId);
        requireOwner(report, currentUserId);
        if (!"open".equals(report.getStatus())) throw new IllegalStateException("Only open reports can be updated");
        validateRequest(request);
        report.setItemType(request.itemType().toLowerCase()); report.setTitle(request.title());
        report.setCategory(request.categoryId() == null ? null : category(request.categoryId())); report.setDescription(request.description());
        report.setItemDate(request.itemDate()); report.setScope(request.scope().toLowerCase());
        report.setLocation(request.locationId() == null ? null : location(request.locationId())); report.setMapLat(request.mapLat()); report.setMapLng(request.mapLng());
        report.setFreeTextLocation(request.freeTextLocation()); report.setPhotoUrl(request.photoUrl());
        report.setHiddenDetail(request.itemType().equalsIgnoreCase("found") ? request.hiddenDetail() : null);
        report.setModerationStatus("pending");
        ItemReport saved = reportRepository.save(report);
        return toResponse(saved);
    }

    public void deleteReport(Integer reportId, Integer currentUserId) {
        ItemReport report = report(reportId);
        requireOwner(report, currentUserId);
        if (claimRepository.existsByItemReportIdAndStatus(reportId, "approved")) throw new IllegalStateException("Cannot delete a report with an approved claim");
        matchRepository.deleteByLostItemIdOrFoundItemId(reportId, reportId);
        claimRepository.deleteByItemReportId(reportId);
        reportRepository.delete(report);
    }

    @Transactional(readOnly = true)
    public List<ItemReportResponse> listReports(String itemType) {
        List<ItemReport> reports = itemType == null ? reportRepository.findByStatusOrderByCreatedAtDesc("open") :
                reportRepository.findByItemTypeAndStatusOrderByCreatedAtDesc(itemType.toLowerCase(), "open");
        return reports.stream().map(this::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public ItemReportResponse getReport(Integer id) { return toResponse(report(id)); }

    public ClaimResponse claim(Integer reportId, ClaimRequest request, Integer claimantId) {
        ItemReport report = report(reportId);
        if (!"found".equals(report.getItemType()) || !"open".equals(report.getStatus())) throw new IllegalStateException("Only open found reports can be claimed");
        if (report.getUser().getId().equals(claimantId)) throw new IllegalStateException("You cannot claim your own report");
        ItemClaim claim = new ItemClaim(); claim.setItemReport(report); claim.setClaimant(user(claimantId)); claim.setDescribedHiddenDetail(request.describedHiddenDetail());
        ItemClaim savedClaim = claimRepository.save(claim);
        notificationService.create(
                report.getUser(), savedClaim.getClaimant(), NotificationType.LOST_FOUND_CLAIM_SUBMITTED,
                "New claim for your found item",
                savedClaim.getClaimant().getDisplayName() + " submitted a claim for \"" + report.getTitle() + "\".",
                "/lost-found/reports/" + report.getId() + "/claims");
        return toClaimResponse(savedClaim);
    }
    public List<ClaimResponse> claims(Integer reportId, Integer currentUserId) {
        ItemReport report = report(reportId); requireOwner(report, currentUserId);
        return claimRepository.findByItemReportIdOrderByCreatedAtDesc(reportId).stream().map(this::toClaimResponse).toList();
    }
    public ClaimResponse reviewClaim(Integer claimId, boolean approve, Integer currentUserId) {
        ItemClaim claim = claimRepository.findById(claimId).orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        requireOwner(claim.getItemReport(), currentUserId);
        claim.setStatus(approve ? "approved" : "rejected"); claim.setReviewedAt(LocalDateTime.now());
        if (approve) claim.getItemReport().setStatus("claimed"); else claim.setFailedAttemptCount(claim.getFailedAttemptCount() + 1);
        notificationService.create(
                claim.getClaimant(), claim.getItemReport().getUser(),
                approve ? NotificationType.LOST_FOUND_CLAIM_APPROVED : NotificationType.LOST_FOUND_CLAIM_REJECTED,
                approve ? "Your claim was approved" : "Your claim was not approved",
                approve
                        ? "Your claim for \"" + claim.getItemReport().getTitle() + "\" was approved."
                        : "Your claim for \"" + claim.getItemReport().getTitle() + "\" was not approved.",
                "/lost-found/reports/" + claim.getItemReport().getId());
        return toClaimResponse(claim);
    }
    public List<MatchResponse> matches(Integer reportId, Integer currentUserId) {
        ItemReport report = report(reportId); requireOwner(report, currentUserId);
        return matchRepository.findByLostItemIdOrFoundItemIdOrderByTotalScoreDesc(reportId, reportId).stream().map(this::toMatchResponse).toList();
    }
    public MatchResponse updateMatch(Integer matchId, String status, Integer currentUserId) {
        if (!List.of("dismissed", "confirmed").contains(status)) throw new IllegalArgumentException("Match status must be dismissed or confirmed");
        ItemMatch match = matchRepository.findById(matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        if (!match.getLostItem().getUser().getId().equals(currentUserId) && !match.getFoundItem().getUser().getId().equals(currentUserId)) throw new IllegalStateException("You do not own this match");
        match.setStatus(status); return toMatchResponse(match);
    }

    private void createSuggestions(ItemReport report) {
        String opposite = report.getItemType().equals("lost") ? "found" : "lost";
        if (report.getCategory() == null) return;
        reportRepository.findByCategoryIdAndItemTypeAndStatus(report.getCategory().getId(), opposite, "open").forEach(other -> {
            ItemReport lost = report.getItemType().equals("lost") ? report : other;
            ItemReport found = report.getItemType().equals("found") ? report : other;
            if (!matchRepository.existsByLostItemIdAndFoundItemId(lost.getId(), found.getId())) {
                ItemMatch match = new ItemMatch(); match.setLostItem(lost); match.setFoundItem(found);
                BigDecimal date = BigDecimal.valueOf(Math.max(0, 1 - Math.min(30, Math.abs(lost.getItemDate().toEpochDay() - found.getItemDate().toEpochDay())) / 30.0));
                BigDecimal location = lost.getLocation() != null && found.getLocation() != null && lost.getLocation().getId().equals(found.getLocation().getId()) ? BigDecimal.ONE : BigDecimal.ZERO;
                match.setCategoryScore(BigDecimal.ONE); match.setDateScore(date); match.setLocationScore(location); match.setKeywordScore(BigDecimal.ZERO);
                match.setTotalScore(BigDecimal.ONE.add(date).add(location).divide(BigDecimal.valueOf(3), 3, RoundingMode.HALF_UP)); matchRepository.save(match);
                notifyMatch(lost, found);
            }
        });
    }
    private void notifyMatch(ItemReport lost, ItemReport found) {
        notificationService.createSystem(
                lost.getUser(), NotificationType.LOST_FOUND_MATCH, "Potential match found",
                "A found-item report \"" + found.getTitle() + "\" may match your lost item \"" + lost.getTitle() + "\".",
                "/lost-found/reports/" + lost.getId() + "/matches");
        if (!found.getUser().getId().equals(lost.getUser().getId())) {
            notificationService.createSystem(
                    found.getUser(), NotificationType.LOST_FOUND_MATCH, "Potential match found",
                    "A lost-item report \"" + lost.getTitle() + "\" may match your found item \"" + found.getTitle() + "\".",
                    "/lost-found/reports/" + found.getId() + "/matches");
        }
    }
    private void validateRequest(ItemReportRequest r) {
        if (!List.of("lost", "found").contains(r.itemType().toLowerCase())) throw new IllegalArgumentException("itemType must be lost or found");
        if (!List.of("istad", "public").contains(r.scope().toLowerCase())) throw new IllegalArgumentException("scope must be istad or public");
        if (r.scope().equalsIgnoreCase("istad") && r.locationId() == null) throw new IllegalArgumentException("ISTAD reports require locationId");
        if (r.scope().equalsIgnoreCase("public") && r.freeTextLocation() == null && (r.mapLat() == null || r.mapLng() == null)) throw new IllegalArgumentException("Public reports require a map location or freeTextLocation");
    }
    private User user(Integer id) { return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")); }
    private ItemCategory category(Integer id) { return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found")); }
    private ItemLocation location(Integer id) { return locationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location not found")); }
    private ItemReport report(Integer id) { return reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item report not found")); }
    private void requireOwner(ItemReport report, Integer userId) { if (!report.getUser().getId().equals(userId)) throw new IllegalStateException("You do not own this report"); }
    private ItemReportResponse toResponse(ItemReport r) { ItemLocation l = r.getLocation(); return new ItemReportResponse(r.getId(), r.getUser().getId(), r.getItemType(), r.getTitle(), r.getCategory() == null ? null : r.getCategory().getId(), r.getCategory() == null ? null : r.getCategory().getName(), r.getDescription(), r.getItemDate(), r.getScope(), l == null ? null : l.getId(), l == null ? null : String.join(" / ", java.util.stream.Stream.of(l.getBuilding(), l.getFloor(), l.getRoom()).filter(java.util.Objects::nonNull).toList()), r.getMapLat(), r.getMapLng(), r.getFreeTextLocation(), r.getPhotoUrl(), r.getStatus(), r.getModerationStatus(), r.getExpiryDate(), r.getCreatedAt()); }
    // UPDATED: now includes describedHiddenDetail (the claimant's answer) so the report owner can verify the claim.
    private ClaimResponse toClaimResponse(ItemClaim c) { return new ClaimResponse(c.getId(), c.getItemReport().getId(), c.getClaimant().getId(), c.getStatus(), c.isConfirmedByFinder(), c.isConfirmedByClaimant(), c.getCreatedAt(), c.getDescribedHiddenDetail()); }
    private MatchResponse toMatchResponse(ItemMatch m) { return new MatchResponse(m.getId(), m.getLostItem().getId(), m.getFoundItem().getId(), m.getTotalScore(), m.getStatus()); }
}