package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    // Beispiel: Benutzerdefinierte Methode, um Künstler nach ihrem Namen zu suchen
    boolean existsByArtistName(String artistName);
}
