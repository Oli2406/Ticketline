package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCancelDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseCancelService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import jakarta.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PurchaseCancelEndpoint.BASE_PATH)
public class PurchaseCancelEndpoint {

    public static final String BASE_PATH = "/api/v1/cancelpurchase";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PurchaseCancelService purchaseCancelService;
    private final RandomStringGenerator randomStringGenerator;
    private final MerchandiseService merchandiseService;
    private final TicketService ticketService;

    public PurchaseCancelEndpoint(PurchaseCancelService purchaseCancelService,
        RandomStringGenerator randomStringGenerator,
        MerchandiseService merchandiseService, TicketService ticketService) {
        this.purchaseCancelService = purchaseCancelService;
        this.randomStringGenerator = randomStringGenerator;
        this.merchandiseService = merchandiseService;
        this.ticketService = ticketService;
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseCancelDetailDto> getCancelPurchaseById(@PathVariable Long id) {
        LOG.info("Fetching cancel Purchase by ID: {}", id);
        PurchaseCancelDetailDto purchase = purchaseCancelService.getCancelPurchaseById(id);
        LOG.info("Successfully fetched cancel Purchase: {}", purchase);
        return ResponseEntity.ok(purchase);
    }

    @PermitAll
    @GetMapping("/user/{encryptedUserId}")
    public ResponseEntity<List<PurchaseCancelDetailDto>> getCancelPurchasesByUser(
        @PathVariable String encryptedUserId) {
        LOG.info("Fetching cancel purchases for user with encrypted ID: {}", encryptedUserId);
        Long userId = randomStringGenerator.retrieveOriginalId(encryptedUserId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));
        List<PurchaseCancelDetailDto> purchases = purchaseCancelService.getCancelPurchasesByUserId(userId);
        LOG.info("Fetched {} cancelled purchases for user with ID: {}", purchases.size(), userId);
        return ResponseEntity.ok(purchases);
    }

    @PermitAll
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCancelPurchase(@PathVariable Long id,
        @RequestBody PurchaseCancelDetailDto purchaseCancelDetailDto) throws ValidationException {
        LOG.info("Received request to update cancel Purchase with ID: {}{}", id, purchaseCancelDetailDto);
        if (!id.equals(purchaseCancelDetailDto.getPurchaseId())) {
            throw new ValidationException("ID mismatch",
                List.of("URL ID does not match the body ID."));
        }
        purchaseCancelService.updateCancelPurchase(purchaseCancelDetailDto);
        LOG.info("Successfully updated Purchase: {}", purchaseCancelDetailDto);
        return ResponseEntity.noContent().build();
    }

    @PermitAll
    @GetMapping("/details/{encryptedUserId}")
    public ResponseEntity<List<PurchaseOverviewDto>> getCancelPurchaseDetailsByUser(
        @PathVariable String encryptedUserId) {
        LOG.info("Fetching detailed cancel purchases for user with encrypted ID: {}", encryptedUserId);
        Long userId = randomStringGenerator.retrieveOriginalId(encryptedUserId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));

        List<PurchaseOverviewDto> purchases = purchaseCancelService.getCancelPurchaseDetailsByUser(userId);
        LOG.info("Fetched {} detailed cancel purchases for user with ID: {}", purchases.size(), userId);
        return ResponseEntity.ok(purchases);
    }

}
