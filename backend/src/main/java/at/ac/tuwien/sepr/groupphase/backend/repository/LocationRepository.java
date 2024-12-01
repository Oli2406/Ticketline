package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Beispiel: Benutzerdefinierte Methode, um Orte nach Namen zu suchen
    boolean existsByName(String name);

    boolean existsByNameAndCity(String name, String city);
}
