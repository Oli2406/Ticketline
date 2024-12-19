package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(PurchaseEndpoint.BASE_PATH)
public class PurchaseEndpoint {

    public static final String BASE_PATH = "/api/v1/purchase";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PurchaseService purchaseService;
    private final RandomStringGenerator randomStringGenerator;

    public PurchaseEndpoint(PurchaseService purchaseService, RandomStringGenerator randomStringGenerator) {
        this.purchaseService = purchaseService;
        this.randomStringGenerator = randomStringGenerator;
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDetailDto> getPurchaseById(@PathVariable Long id) {
        LOG.info("Fetching Purchase by ID: {}", id);
        PurchaseDetailDto purchase = purchaseService.getPurchaseById(id);
        LOG.info("Successfully fetched Purchase: {}", purchase);
        return ResponseEntity.ok(purchase);
    }

    @PermitAll
    @GetMapping("/user/{encryptedUserId}")
    public ResponseEntity<List<PurchaseDetailDto>> getPurchasesByUser(@PathVariable String encryptedUserId) {
        LOG.info("Fetching purchases for user with encrypted ID: {}", encryptedUserId);
        Long userId = randomStringGenerator.retrieveOriginalId(encryptedUserId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));
        List<PurchaseDetailDto> purchases = purchaseService.getPurchasesByUserId(userId);
        LOG.info("Fetched {} purchases for user with ID: {}", purchases.size(), userId);
        return ResponseEntity.ok(purchases);
    }


    @PermitAll
    @PostMapping
    public ResponseEntity<PurchaseDetailDto> createPurchase(@RequestBody PurchaseCreateDto purchaseCreateDto) throws ValidationException {
        LOG.info("Received request to create or update Purchase: {}", purchaseCreateDto);
        System.out.println("hellohellohello " + purchaseCreateDto.getPurchaseDate());
        PurchaseDetailDto createdPurchase = purchaseService.createPurchase(purchaseCreateDto);
        LOG.info("Successfully created/updated Purchase: {}", createdPurchase);
        return ResponseEntity.ok(createdPurchase);
    }
}
