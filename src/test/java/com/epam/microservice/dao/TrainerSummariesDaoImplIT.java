package com.epam.microservice.dao;

import com.epam.microservice.common.EntityNotFoundException;
import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.domain.TrainerSummary;
import com.epam.microservice.domain.YearlyWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TrainerSummariesDaoImplIT {
    private MonthlyWorkload monthlyWorkload;
    private YearlyWorkload yearlyWorkload;
    private TrainerSummary summary;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private TrainerSummariesDaoImpl dao;

    @BeforeEach
    void setUp() {
        monthlyWorkload = MonthlyWorkload.builder()
                .month(Month.DECEMBER)
                .workingHours(60)
                .build();
        yearlyWorkload = YearlyWorkload.builder()
                .year(2024)
                .list(new ArrayList<>(List.of(monthlyWorkload)))
                .build();
        summary = TrainerSummary.builder()
                .username("Jane.Doe")
                .firstName("Jane")
                .lastName("Doe")
                .status(true)
                .workload(new ArrayList<>(List.of(yearlyWorkload)))
                .build();
    }

    void cleanUp() {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(summary.getUsername()));
        template.remove(query, TrainerSummary.class);
    }

    @Test
    void getTrainerSummaryShouldReturnSummary() {
        dao.updateOrNewDocument(summary);
        assertEquals(summary, dao.getTrainerSummary(summary.getUsername()));
        cleanUp();
    }

    @Test
    void getTrainerSummaryShouldThrowEntityNotFoundExceptionWhenSummaryNotFound() {
        RuntimeException e = assertThrows(EntityNotFoundException.class, () -> dao.getTrainerSummary("Nonexistent"));
        assertEquals("Trainer summary not found for username: " + "Nonexistent", e.getMessage());
    }

    @Test
    void existsShouldReturnWhetherDocumentExistsInDB() {
        assertFalse(dao.exists("Nonexistent"));
        dao.updateOrNewDocument(summary);
        assertTrue(dao.exists(summary.getUsername()));
        cleanUp();
    }

    @Test
    void updateOrNewDocumentShouldSaveDocumentWhenNonexistent() {
        dao.updateOrNewDocument(summary);
        assertTrue(dao.exists(summary.getUsername()));
        cleanUp();
    }

    @Test
    void updateOrNewDocumentShouldUpdateDocumentWhenExistsWithSameMonth() {
        dao.updateOrNewDocument(summary);
        dao.updateOrNewDocument(summary);
        summary.getWorkload().get(0).getList().get(0).setWorkingHours(monthlyWorkload.getWorkingHours());
        assertEquals(summary, dao.getTrainerSummary(summary.getUsername()));
        cleanUp();
    }

    @Test
    void updateOrNewDocumentShouldUpdateDocumentWhenExistsWithDifferentMonth() {
        dao.updateOrNewDocument(summary);
        summary.getWorkload().get(0).getList().get(0).setMonth(Month.NOVEMBER);
        dao.updateOrNewDocument(summary);
        MonthlyWorkload m1 = MonthlyWorkload.builder()
                .workingHours(60)
                .month(Month.DECEMBER)
                .build();
        MonthlyWorkload m2 = MonthlyWorkload.builder()
                .workingHours(60)
                .month(Month.NOVEMBER)
                .build();
        YearlyWorkload y = YearlyWorkload.builder()
                .year(yearlyWorkload.getYear())
                .list(List.of(m1, m2))
                .build();
        assertEquals(List.of(y), dao.getTrainerSummary(summary.getUsername()).getWorkload());
        cleanUp();
    }

    @Test
    void updateOrNewDocumentShouldUpdateDocumentWhenExistsWithDifferentYear() {
        dao.updateOrNewDocument(summary);
        summary.getWorkload().get(0).setYear(2025);
        dao.updateOrNewDocument(summary);
        YearlyWorkload y = YearlyWorkload.builder()
                .year(2024)
                .build();
        y.add(yearlyWorkload.getList().toArray(new MonthlyWorkload[0]));
        assertEquals(List.of(y, yearlyWorkload), dao.getTrainerSummary(summary.getUsername()).getWorkload());
        cleanUp();
    }
}
