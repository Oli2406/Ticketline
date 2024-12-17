package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.EncryptedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncryptedIdRepository extends JpaRepository<EncryptedId, Long> {
}
