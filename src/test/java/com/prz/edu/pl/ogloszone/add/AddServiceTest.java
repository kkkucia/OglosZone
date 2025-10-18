package com.prz.edu.pl.ogloszone.add;

import com.prz.edu.pl.ogloszone.category.Category;
import com.prz.edu.pl.ogloszone.email.EmailService;
import com.prz.edu.pl.ogloszone.exception.AddNotFoundException;
import com.prz.edu.pl.ogloszone.exception.InvalidCategoryException;
import com.prz.edu.pl.ogloszone.util.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddServiceTest {

    @Mock
    private AddRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AddService addService;

    private Add add;
    private AddRequest addRequest;
    private UUID addId;
    private UUID editCode;

    @BeforeEach
    void setUp() {
        addId = UUID.randomUUID();
        editCode = UUID.randomUUID();
        add = new Add(addId.toString(), "Test Title", "Test Content", Category.JOB, new Contact("test@example.com", "123456789"), LocalDateTime.now(), editCode.toString());
        addRequest = new AddRequest("Test Title", "Test Content", Category.JOB.name(), new Contact("test@example.com", "123456789"));
    }

    @Test
    void createAdd_SuccessfulCreation() {
        when(repository.save(any(Add.class))).thenReturn(add);

        AddResponse response = addService.createAdd(addRequest);

        assertNotNull(response);
        assertEquals(addId.toString(), response.id().toString());
        verify(repository, times(1)).save(any(Add.class));
        verify(emailService, times(1)).sendConfirmationEmail(add);
    }

    @Test
    void getAddById_SuccessfulRetrieval() {
        when(repository.findById(addId.toString())).thenReturn(Optional.of(add));

        AddResponse response = addService.getAddById(addId);

        assertNotNull(response);
        assertEquals(addId.toString(), response.id().toString());
        verify(repository, times(1)).findById(addId.toString());
    }

    @Test
    void getAddById_AddNotFound() {
        when(repository.findById(addId.toString())).thenReturn(Optional.empty());

        assertThrows(AddNotFoundException.class, () -> addService.getAddById(addId));
        verify(repository, times(1)).findById(addId.toString());
    }

    @Test
    void updateAdd_SuccessfulUpdate() {
        when(repository.findById(addId.toString())).thenReturn(Optional.of(add));
        when(repository.save(any(Add.class))).thenReturn(add);

        AddResponse response = addService.updateAdd(addId, editCode, addRequest);

        assertNotNull(response);
        assertEquals(addId.toString(), response.id().toString());
        verify(repository, times(1)).save(any(Add.class));
        verify(emailService, times(1)).sendConfirmationEmail(any(Add.class));
    }

    @Test
    void updateAdd_AddNotFound() {
        when(repository.findById(addId.toString())).thenReturn(Optional.empty());

        assertThrows(AddNotFoundException.class, () -> addService.updateAdd(addId, editCode, addRequest));
        verify(repository, times(1)).findById(addId.toString());
    }

    @Test
    void deleteAdd_SuccessfulDeletion() {
        when(repository.findById(addId.toString())).thenReturn(Optional.of(add));

        addService.deleteAdd(addId, editCode);

        verify(repository, times(1)).deleteById(addId.toString());
    }

    @Test
    void deleteAdd_AddNotFound() {
        when(repository.findById(addId.toString())).thenReturn(Optional.empty());

        assertThrows(AddNotFoundException.class, () -> addService.deleteAdd(addId, editCode));
        verify(repository, times(1)).findById(addId.toString());
    }

    @Test
    void cleanOldAnnouncements_SuccessfulCleanup() {
        addService.cleanOldAnnouncements();

        verify(repository, times(1)).deleteByDateTimeBefore(any(LocalDateTime.class));
    }

    @Test
    void getAllAnnouncements_SuccessfulRetrieval() {
        Page<Add> page = new PageImpl<>(Collections.singletonList(add));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        PagedResponse<AddResponse> response = addService.getAllAnnouncements(null, null, null, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.content().size());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllAnnouncements_InvalidPage() {
        assertThrows(IllegalArgumentException.class, () -> addService.getAllAnnouncements(null, null, null, -1, 10));
    }

    @Test
    void getAllAnnouncements_InvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> addService.getAllAnnouncements(null, null, null, 0, 0));
    }

    @Test
    void getAllAnnouncements_ExcessiveSize() {
        assertThrows(IllegalArgumentException.class, () -> addService.getAllAnnouncements(null, null, null, 0, 101));
    }

    @Test
    void getAllAnnouncements_InvalidDateFormat() {
        assertThrows(IllegalArgumentException.class, () -> addService.getAllAnnouncements(null, "invalid-date", null, 0, 10));
    }

    @Test
    void validateCategory_ValidCategory() {
        Category result = addService.validateCategory(Category.JOB.name());

        assertEquals(Category.JOB, result);
    }

    @Test
    void validateCategory_InvalidCategory() {
        assertThrows(InvalidCategoryException.class, () -> addService.validateCategory("INVALID_CATEGORY"));
    }

    @Test
    void validateCategory_NullCategory() {
        assertThrows(IllegalArgumentException.class, () -> addService.validateCategory(null));
    }

    @Test
    void getExistingAdd_SuccessfulRetrieval() {
        when(repository.findById(addId.toString())).thenReturn(Optional.of(add));

        Add result = addService.getExistingAdd(addId, editCode);

        assertNotNull(result);
        assertEquals(addId.toString(), result.id());
    }

    @Test
    void getExistingAdd_AddNotFound() {
        when(repository.findById(addId.toString())).thenReturn(Optional.empty());

        assertThrows(AddNotFoundException.class, () -> addService.getExistingAdd(addId, editCode));
    }

    @Test
    void getExistingAdd_InvalidEditCode() {
        when(repository.findById(addId.toString())).thenReturn(Optional.of(add));
        UUID wrongEditCode = UUID.randomUUID();

        assertThrows(SecurityException.class, () -> addService.getExistingAdd(addId, wrongEditCode));
    }
}