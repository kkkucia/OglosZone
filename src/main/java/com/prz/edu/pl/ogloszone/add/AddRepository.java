package com.prz.edu.pl.ogloszone.add;

import com.prz.edu.pl.ogloszone.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AddRepository extends MongoRepository<Add, String> {
    Page<Add> findByCategory(Category category, Pageable pageable);

    Page<Add> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Add> findByDateTimeAfter(LocalDateTime dateTime, Pageable pageable);

    Page<Add> findByCategoryAndDateTimeAfter(Category category, LocalDateTime dateTime, Pageable pageable);

    Page<Add> findByCategoryAndTitleContainingIgnoreCase(Category category, String keyword, Pageable pageable);

    Page<Add> findByDateTimeAfterAndTitleContainingIgnoreCase(LocalDateTime dateTime, String keyword, Pageable pageable);

    Page<Add> findByCategoryAndDateTimeAfterAndTitleContainingIgnoreCase(
            Category category, LocalDateTime dateTime, String keyword, Pageable pageable);

    void deleteByDateTimeBefore(LocalDateTime dateTime);
}
