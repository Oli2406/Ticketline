package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    // Beispiel: Benutzerdefinierte Methode, um Performances nach einer Halle zu suchen
    boolean existsByHall(String hall);

    boolean existsByNameAndLocationIdAndDate(String name, Long locationId, LocalDate date);
}
