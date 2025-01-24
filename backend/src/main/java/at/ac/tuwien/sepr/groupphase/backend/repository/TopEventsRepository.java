package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.TopEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopEventsRepository extends JpaRepository<TopEvents, Long> {
    void deleteByCategoryAndYearAndMonth(String category, int year, int month);

    List<TopEvents> findByCategoryAndYearAndMonth(String category, int year, int month);
}
