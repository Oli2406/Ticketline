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
            175, "9.jpg", new BigDecimal("22.10").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("super mario hat", "fashion", new BigDecimal("299.99"),
            200, "10.jpg", new BigDecimal("299.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("swimsuit", "fashion", new BigDecimal("16200.66"),
            1, "11.jpg", new BigDecimal("16200.66").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("magic lamp", "home", new BigDecimal("59.99"),
            50, "12.jpg", new BigDecimal("59.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("glowing globe", "decor", new BigDecimal("24.99"),
            120, "13.jpg", new BigDecimal("24.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("retro radio", "electronics", new BigDecimal("89.95"),
            45, "14.jpg", new BigDecimal("89.95").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("solar charger", "gadget", new BigDecimal("34.49"),
            300, "15.jpg", new BigDecimal("34.49").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("wireless keyboard", "electronics", new BigDecimal("45.99"),
            200, "16.jpg", new BigDecimal("45.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("vintage typewriter", "office", new BigDecimal("129.99"),
            25, "17.jpg", new BigDecimal("129.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("rainbow umbrella", "fashion", new BigDecimal("19.50"),
            400, "18.jpg", new BigDecimal("19.50").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("camping tent", "outdoor", new BigDecimal("249.99"),
            80, "19.jpg", new BigDecimal("249.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("digital camera", "electronics", new BigDecimal("599.99"),
            15, "20.jpg", new BigDecimal("599.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("plush unicorn", "toy", new BigDecimal("14.99"),
            300, "21.jpg", new BigDecimal("14.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("eco-friendly water bottle", "accessory", new BigDecimal("18.95"),
            350, "22.jpg", new BigDecimal("18.95").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("steel coffee tumbler", "kitchen", new BigDecimal("22.49"),
            270, "23.jpg", new BigDecimal("22.49").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("gaming mouse", "electronics", new BigDecimal("39.99"),
            150, "24.jpg", new BigDecimal("39.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("smartwatch", "gadget", new BigDecimal("199.99"),
            75, "25.jpg", new BigDecimal("199.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("noise-canceling headphones", "electronics", new BigDecimal("299.99"),
            60, "26.jpg", new BigDecimal("299.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("electric scooter", "transport", new BigDecimal("450.00"),
            30, "27.jpg", new BigDecimal("450.00").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("yoga mat", "fitness", new BigDecimal("25.50"),
            400, "28.jpg", new BigDecimal("25.50").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("mini drone", "gadget", new BigDecimal("99.99"),
            100, "29.jpg", new BigDecimal("99.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("leather wallet", "accessory", new BigDecimal("35.00"),
            200, "30.jpg", new BigDecimal("35.00").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("graphic tee", "fashion", new BigDecimal("19.99"),
            500, "31.jpg", new BigDecimal("19.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("board game", "toy", new BigDecimal("49.95"),
            250, "32.jpg", new BigDecimal("49.95").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("ceramic vase", "decor", new BigDecimal("29.99"),
            180, "33.jpg", new BigDecimal("29.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("adjustable desk", "office", new BigDecimal("199.50"),
            40, "34.jpg", new BigDecimal("199.50").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("rocking chair", "furniture", new BigDecimal("329.99"),
            15, "35.jpg", new BigDecimal("329.99").multiply(BigDecimal.valueOf(10)).intValue());

        createMerchandiseIfNotExists("metal toolbox", "tools", new BigDecimal("69.99"),
            90, "36.jpg", new BigDecimal("69.99").multiply(BigDecimal.valueOf(10)).intValue());

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
