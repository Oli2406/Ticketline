package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.impl.CustomPerformanceRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class CustomPerformanceRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<PerformanceDetailDto> criteriaQuery;

    @Mock
    private Root<Event> rootEvent;

    @Mock
    private Root<Performance> rootPerformance;

    @Mock
    private Join<?, ?> joinEventPerformance;

    @Mock
    private Join<?, ?> joinArtist;

    @Mock
    private Join<?, ?> joinLocation;

    @Mock
    private Predicate predicate;

    @Mock
    private TypedQuery<PerformanceDetailDto> typedQuery;

    private CustomPerformanceRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new CustomPerformanceRepositoryImpl();
        repository.entityManager = entityManager;

        when(criteriaQuery.select(any())).thenReturn(criteriaQuery);
        when(criteriaQuery.distinct(true)).thenReturn(criteriaQuery);

        when(rootEvent.join("performanceIds", JoinType.INNER)).thenReturn((Join<Object, Object>) joinEventPerformance);
        when(rootPerformance.join("artist", JoinType.INNER)).thenReturn((Join<Object, Object>) joinArtist);
        when(rootPerformance.join("location", JoinType.INNER)).thenReturn((Join<Object, Object>) joinLocation);

        when(criteriaQuery.from(Event.class)).thenReturn(rootEvent);
        when(criteriaQuery.from(Performance.class)).thenReturn(rootPerformance);
    }

    @Test
    void findByAdvancedSearchReturnsEmptyListWhenQueryIsNullOrBlank() {
        assertTrue(repository.findByAdvancedSearch(null).isEmpty());
        assertTrue(repository.findByAdvancedSearch("   ").isEmpty());
    }

    @Test
    void findByAdvancedSearchHandlesNoMatchingPredicates() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(PerformanceDetailDto.class)).thenReturn(criteriaQuery);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        when(typedQuery.getResultList()).thenReturn(List.of());

        String query = "nonexistent";
        List<PerformanceDetailDto> results = repository.findByAdvancedSearch(query);

        assertTrue(results.isEmpty());
        verify(entityManager).createQuery(criteriaQuery);
        verify(typedQuery).getResultList();
    }

    @Test
    void findByAdvancedSearchReturnsResults() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(PerformanceDetailDto.class)).thenReturn(criteriaQuery);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        PerformanceDetailDto result = new PerformanceDetailDto(
            1L,
            "Classical Night",
            1L,
            1L,
            LocalDateTime.now(),
            BigDecimal.valueOf(50),
            100L,
            "Main Hall",
            null,
            null,
            120
        );
        when(typedQuery.getResultList()).thenReturn(List.of(result));

        String query = "Classical Night";
        List<PerformanceDetailDto> results = repository.findByAdvancedSearch(query);

        assertEquals(1, results.size());
        assertEquals("Classical Night", results.getFirst().getName());
        verify(entityManager).createQuery(criteriaQuery);
        verify(typedQuery).getResultList();
    }

    @Test
    void parseDateTimeParsesValidDateTimeFormat() {
        String validDateTime = "2025.01.19 14:30";
        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 1, 19, 14, 30);

        LocalDateTime result = repository.parseDateTime(validDateTime);

        assertNotNull(result);
        assertEquals(expectedDateTime, result);

        String validDate = "2025-01-19";
        LocalDateTime expectedDate = LocalDateTime.of(2025, 1, 19, 0, 0);

        result = repository.parseDateTime(validDate);

        assertNotNull(result);
        assertEquals(expectedDate, result);
    }

    @Test
    void parseDateTimeReturnsNullForInvalidFormat() {
        String invalidDateTime = "invalid date";
        LocalDateTime result = repository.parseDateTime(invalidDateTime);

        assertNull(result);

        String emptyDateTime = "";
        result = repository.parseDateTime(emptyDateTime);

        assertNull(result);

        String unsupportedDateTime = "01-19-2025";
        result = repository.parseDateTime(unsupportedDateTime);

        assertNull(result);
    }

    @Test
    void advancedSearchAddsDatePredicatesForValidDateTerms() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);

        Predicate mockMatchDateTime = mock(Predicate.class);
        Predicate mockGreaterThanOrEqual = mock(Predicate.class);
        Predicate mockLessThanOrEqual = mock(Predicate.class);
        Predicate mockAndPredicate = mock(Predicate.class);
        Predicate mockOrPredicate = mock(Predicate.class);

        Path mockPerformanceDatePath = mock(Path.class);
        when(rootPerformance.get("date")).thenReturn(mockPerformanceDatePath);
        when(criteriaBuilder.equal(eq(mockPerformanceDatePath), any(LocalDateTime.class))).thenReturn(mockMatchDateTime);
        when(criteriaBuilder.greaterThanOrEqualTo(eq(mockPerformanceDatePath), any(LocalDateTime.class))).thenReturn(mockGreaterThanOrEqual);
        when(criteriaBuilder.lessThanOrEqualTo(eq(mockPerformanceDatePath), any(LocalDateTime.class))).thenReturn(mockLessThanOrEqual);
        when(criteriaBuilder.and(eq(mockGreaterThanOrEqual), eq(mockLessThanOrEqual))).thenReturn(mockAndPredicate);
        when(criteriaBuilder.or(eq(mockMatchDateTime), eq(mockAndPredicate))).thenReturn(mockOrPredicate);

        String query = "2025-01-19 some-text";
        String[] terms = query.split("\\s+");

        List<Predicate> predicates = new ArrayList<>();

        for (String term : terms) {
            if (term.contains("-")) {
                LocalDateTime dateTime = repository.parseDateTime(term);
                if (dateTime != null) {
                    LocalDate dateOnly = dateTime.toLocalDate();
                    LocalDateTime startOfDay = dateOnly.atStartOfDay();
                    LocalDateTime endOfDay = dateOnly.atTime(LocalTime.MAX);

                    Predicate matchDateTime = criteriaBuilder.equal(mockPerformanceDatePath, dateTime);
                    Predicate withinDayRange = criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(mockPerformanceDatePath, startOfDay),
                        criteriaBuilder.lessThanOrEqualTo(mockPerformanceDatePath, endOfDay)
                    );

                    predicates.add(terms.length == 1 ? matchDateTime : criteriaBuilder.or(matchDateTime, withinDayRange));
                }
            }
        }

        assertEquals(1, predicates.size());
        assertNotNull(predicates.getFirst());

        verify(criteriaBuilder).equal(eq(mockPerformanceDatePath), eq(LocalDateTime.of(2025, 1, 19, 0, 0)));
        verify(criteriaBuilder).greaterThanOrEqualTo(eq(mockPerformanceDatePath), eq(LocalDateTime.of(2025, 1, 19, 0, 0)));
        verify(criteriaBuilder).lessThanOrEqualTo(eq(mockPerformanceDatePath), eq(LocalDateTime.of(2025, 1, 19, 23, 59, 59, 999999999)));
        verify(criteriaBuilder).and(eq(mockGreaterThanOrEqual), eq(mockLessThanOrEqual));
        verify(criteriaBuilder).or(eq(mockMatchDateTime), eq(mockAndPredicate));
    }
}
