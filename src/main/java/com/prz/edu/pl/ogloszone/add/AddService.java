package com.prz.edu.pl.ogloszone.add;

import com.prz.edu.pl.ogloszone.category.Category;
import com.prz.edu.pl.ogloszone.email.EmailService;
import com.prz.edu.pl.ogloszone.exception.AddNotFoundException;
import com.prz.edu.pl.ogloszone.exception.InvalidCategoryException;
import com.prz.edu.pl.ogloszone.util.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class AddService {

    private static final Logger logger = LoggerFactory.getLogger(AddService.class);

    private final AddRepository repository;
    private final EmailService emailService;

    public AddService(AddRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Transactional
    public AddResponse createAdd(AddRequest addRequest) {
        logger.info("Creating new announcement with title: {}", addRequest.title());
        Category category = validateCategory(addRequest.category());

        Add newAdd = new Add(
                UUID.randomUUID().toString(),
                addRequest.title(),
                addRequest.content(),
                category,
                addRequest.contact(),
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
        Add savedAdd = repository.save(newAdd);
        logger.info("Successfully created announcement with ID: {}", savedAdd.id());
        emailService.sendConfirmationEmail(savedAdd);
        return new AddResponse(savedAdd);
    }

    public AddResponse getAddById(UUID id) {
        logger.info("Fetching announcement with ID: {}", id);
        Add add = repository.findById(id.toString())
                .orElseThrow(() -> {
                    logger.warn("Announcement with ID {} not found", id);
                    return new AddNotFoundException("Add with ID " + id + " not found");
                });
        logger.debug("Found announcement: {}", add);
        return new AddResponse(add);
    }

    @Transactional
    public AddResponse updateAdd(UUID id, UUID editCode, AddRequest addUpdate) {
        logger.info("Updating announcement with ID: {} and editCode: {}", id, editCode);
        Add currentAdd = getExistingAdd(id, editCode);

        Category category = validateCategory(addUpdate.category());

        Add updatedAdd = new Add(
                id.toString(),
                addUpdate.title(),
                addUpdate.content(),
                category,
                addUpdate.contact(),
                LocalDateTime.now(),
                currentAdd.editCode()
        );
        repository.save(updatedAdd);
        emailService.sendConfirmationEmail(updatedAdd);
        logger.info("Successfully updated announcement with ID: {}", id);
        return new AddResponse(updatedAdd);
    }

    @Transactional
    public void deleteAdd(UUID id, UUID editCode) {
        logger.info("Deleting announcement with ID: {} and editCode: {}", id, editCode);
        Add currentAdd = getExistingAdd(id, editCode);
        repository.deleteById(currentAdd.id());
        logger.info("Successfully deleted announcement with ID: {}", id);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanOldAnnouncements() {
        logger.info("Starting cleanup of old announcements");
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        repository.deleteByDateTimeBefore(threshold);
    }

    public PagedResponse<AddResponse> getAllAnnouncements(String category, String date, String keyword, int page, int size) {
        logger.info("Fetching all announcements with filters - category: {}, date: {}, keyword: {}, page: {}, size: {}",
                category, date, keyword, page, size);
        if (page < 0 || size <= 0) {
            logger.warn("Invalid pagination parameters: page = {}, size = {}", page, size);
            throw new IllegalArgumentException("Page number and size must be positive, with page starting at 0");
        }
        if (size > 100) {
            logger.warn("Size {} exceeds maximum allowed (100)", size);
            throw new IllegalArgumentException("Size cannot exceed 100");
        }

        LocalDateTime dateTime = null;
        if (date != null && !date.isEmpty()) {
            try {
                dateTime = LocalDateTime.parse(date);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format: {}", date, e);
                throw new IllegalArgumentException("Invalid date format. Use ISO format (e.g., 2025-10-18T14:33:00)");
            }
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<AddResponse> pageResult = fetchAnnouncements(category, dateTime, keyword, pageable);
        logger.debug("Fetched {} announcements for page {}", pageResult.getTotalElements(), page);
        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast());
    }

    private Page<AddResponse> fetchAnnouncements(String category, LocalDateTime dateTime, String keyword, Pageable pageable) {
        logger.debug("Fetching announcements with filters - category: {}, dateTime: {}, keyword: {}",
                category, dateTime, keyword);
        if (category != null && !category.isEmpty()) {
            validateCategory(category);
            if (dateTime != null) {
                if (keyword != null && !keyword.isEmpty()) {
                    return repository.findByCategoryAndDateTimeAfterAndTitleContainingIgnoreCase(
                                    Category.valueOf(category.toUpperCase()), dateTime, keyword, pageable)
                            .map(AddResponse::new);
                }
                return repository.findByCategoryAndDateTimeAfter(
                                Category.valueOf(category.toUpperCase()), dateTime, pageable)
                        .map(AddResponse::new);
            } else if (keyword != null && !keyword.isEmpty()) {
                return repository.findByCategoryAndTitleContainingIgnoreCase(
                                Category.valueOf(category.toUpperCase()), keyword, pageable)
                        .map(AddResponse::new);
            }
            return repository.findByCategory(Category.valueOf(category.toUpperCase()), pageable)
                    .map(AddResponse::new);
        } else if (dateTime != null) {
            if (keyword != null && !keyword.isEmpty()) {
                return repository.findByDateTimeAfterAndTitleContainingIgnoreCase(dateTime, keyword, pageable)
                        .map(AddResponse::new);
            }
            return repository.findByDateTimeAfter(dateTime, pageable)
                    .map(AddResponse::new);
        } else if (keyword != null && !keyword.isEmpty()) {
            return repository.findByTitleContainingIgnoreCase(keyword, pageable)
                    .map(AddResponse::new);
        }
        return repository.findAll(pageable)
                .map(AddResponse::new);
    }

    Add getExistingAdd(UUID id, UUID editCode) {
        logger.debug("Checking existence of announcement with ID: {} and editCode: {}", id, editCode);
        Optional<Add> existing = repository.findById(id.toString());
        if (existing.isEmpty()) {
            throw new AddNotFoundException("Add with ID " + id + " not found");
        }

        Add currentAdd = existing.get();
        if (!Objects.equals(editCode.toString(), currentAdd.editCode())) {
            throw new SecurityException("Invalid edit code for ID " + id);
        }
        return currentAdd;
    }

    Category validateCategory(String categoryName) {
        logger.debug("Validating category: {}", categoryName);
        if (categoryName == null || categoryName.trim().isEmpty()) {
            logger.warn("Category is null or empty");
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        String normalizedCategory = categoryName.trim().toUpperCase();
        try {
            return Category.valueOf(normalizedCategory);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid category value: {}", categoryName, e);
            throw new InvalidCategoryException(
                    String.format("Invalid category: '%s'. Must be one of: %s.",
                            categoryName, String.join(", ", Category.getNames()))
            );
        }
    }
}