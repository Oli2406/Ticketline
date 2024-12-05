package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MerchandiseRepositoryTest {

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Test
    void testSaveAndFindById() {
        Merchandise merchandise = new Merchandise();
        merchandise.setName("T-Shirt");
        merchandise.setPrice(BigDecimal.valueOf(20.00));
        merchandise.setCategory("Clothing");
        merchandise.setStock(10);
        merchandise.setPoints(200);
        merchandise.setImageUrl("http://example.com/tshirt.jpg");

        Merchandise savedMerchandise = merchandiseRepository.save(merchandise);

        Optional<Merchandise> foundMerchandise = merchandiseRepository.findById(savedMerchandise.getId());
        assertTrue(foundMerchandise.isPresent());
        assertEquals("T-Shirt", foundMerchandise.get().getName());
    }

    @Test
    void testExistsByName_ReturnsTrueIfNameExists() {
        Merchandise merchandise = new Merchandise();
        merchandise.setName("Mug");
        merchandise.setPrice(BigDecimal.valueOf(10.00));
        merchandise.setCategory("Accessories");
        merchandise.setStock(15);
        merchandise.setPoints(100);
        merchandise.setImageUrl("http://example.com/mug.jpg");

        merchandiseRepository.save(merchandise);

        boolean exists = merchandiseRepository.existsByName("Mug");
        assertTrue(exists);
    }

    @Test
    void testExistsByName_ReturnsFalseIfNameDoesNotExist() {
        boolean exists = merchandiseRepository.existsByName("NonExistentItem");
        assertFalse(exists);
    }

    @Test
    void testDeleteById() {
        Merchandise merchandise = new Merchandise();
        merchandise.setName("Hat");
        merchandise.setPrice(BigDecimal.valueOf(15.00));
        merchandise.setCategory("Clothing");
        merchandise.setStock(5);
        merchandise.setPoints(150);
        merchandise.setImageUrl("http://example.com/hat.jpg");

        Merchandise savedMerchandise = merchandiseRepository.save(merchandise);
        Long savedId = savedMerchandise.getId();

        merchandiseRepository.deleteById(savedId);

        Optional<Merchandise> deletedMerchandise = merchandiseRepository.findById(savedId);
        assertFalse(deletedMerchandise.isPresent());
    }
}
