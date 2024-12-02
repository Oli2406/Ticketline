package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

@Component
public class MerchandiseDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String LOCALHOST_IMAGE_PATH = "http://localhost:8080/images/";
    private final MerchandiseRepository merchandiseRepository;

    public MerchandiseDataGenerator(MerchandiseRepository merchandiseRepository) {
        this.merchandiseRepository = merchandiseRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        createMerchandiseIfNotExists("silly cat", "pet", new BigDecimal("420.69"), 2000, "1.jpg");
        createMerchandiseIfNotExists("wacky duck", "toy", new BigDecimal("19.99"), 150, "2.jpg");
        createMerchandiseIfNotExists("funky hat", "fashion", new BigDecimal("29.49"), 80, "3.jpg");
        createMerchandiseIfNotExists("cosmic mug", "kitchen", new BigDecimal("15.95"), 300, "4.jpg");
        createMerchandiseIfNotExists("robot sneaker", "shoes", new BigDecimal("79.99"), 120, "5.jpg");
        createMerchandiseIfNotExists("dancing fish", "gadget", new BigDecimal("49.50"), 200, "6.jpg");
        createMerchandiseIfNotExists("invisible book", "literature", new BigDecimal("9.99"), 500, "7.jpg");
        createMerchandiseIfNotExists("galactic bottle", "accessory", new BigDecimal("12.75"), 250, "8.jpg");
        createMerchandiseIfNotExists("sunglass shark", "fashion", new BigDecimal("22.10"), 175, "9.png");
    }

    public void createMerchandiseIfNotExists(
        String name, String category, BigDecimal price, int stock, String imagePath) {
        if (!merchandiseRepository.existsByName(name)) {
            Merchandise m = new Merchandise(name, category, price, stock, imagePath);
            merchandiseRepository.save(m);
            LOGGER.debug("Merchandise created: {}", name);
        }
    }
}
