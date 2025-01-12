package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

@Component
@Profile("generateData")
@DependsOn("performanceDataGenerator")
public class MerchandiseDataGenerator {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchandiseRepository merchandiseRepository;

    public MerchandiseDataGenerator(MerchandiseRepository merchandiseRepository) {
        this.merchandiseRepository = merchandiseRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        createMerchandiseIfNotExists("silly cat", "pet", new BigDecimal("420.69"),
            2000, "1.jpg", new BigDecimal("420.69").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("wacky duck", "toy", new BigDecimal("19.99"),
            150, "2.jpg", new BigDecimal("19.99").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("funky hat", "fashion", new BigDecimal("29.49"),
            80, "3.jpg", new BigDecimal("29.49").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("cosmic mug", "kitchen", new BigDecimal("15.95"),
            300, "4.jpg", new BigDecimal("15.95").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("robot sneaker", "shoes", new BigDecimal("79.99"),
            120, "5.jpg", new BigDecimal("79.99").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("dancing fish", "gadget", new BigDecimal("49.50"),
            200, "6.jpg", new BigDecimal("49.50").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("invisible book", "literature", new BigDecimal("9.99"),
            500, "7.jpg", new BigDecimal("9.99").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("galactic bottle", "accessory", new BigDecimal("12.75"),
            250, "8.jpg", new BigDecimal("12.75").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("sunglass shark", "fashion", new BigDecimal("22.10"),
            175, "9.png", new BigDecimal("22.10").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("Super mario hat", "fashion", new BigDecimal("299.99"),
            200, "9.jpg", new BigDecimal("299.99").multiply(BigDecimal.valueOf(10)).intValue());
        createMerchandiseIfNotExists("Swimsuit", "fashion", new BigDecimal("16200.66"),
            1, "10.jpg", new BigDecimal("16200.66").multiply(BigDecimal.valueOf(10)).intValue());
        LOGGER.info("All merchandise were created!");
    }

    public void createMerchandiseIfNotExists(
        String name, String category, BigDecimal price, int stock, String imagePath, int points) {
        if (!merchandiseRepository.existsByName(name)) {
            Merchandise m = new Merchandise(name, category, price, stock, imagePath, points);
            merchandiseRepository.save(m);
            LOGGER.debug("Merchandise created: {}", name);
        }
    }
}
