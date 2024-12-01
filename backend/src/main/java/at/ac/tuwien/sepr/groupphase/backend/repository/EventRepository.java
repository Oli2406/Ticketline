package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Beispiel: Benutzerdefinierte Methode, um Events nach Titel zu suchen
    boolean existsByTitle(String title);

    boolean existsByTitleAndDateOfEvent(String title, LocalDate dateOfEvent);
}
