package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchandise")
public class MerchandiseEndpoint {

    public MerchandiseService merchandiseService;

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String IMAGE_UPLOAD_DIR = "merchandise/";
    private static final String LOCALHOST_IMAGE_PATH = "http://localhost:8080/images/";

    public MerchandiseEndpoint(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }


    @PermitAll
    @PostMapping("/create")
    public ResponseEntity<?> createMerchandise(
        @RequestPart("merchandise") MerchandiseCreateDto merchandiseCreateDto,
        @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            LOGGER.info("POST " + "api/v1/merchandise/create");
            String imagePathStr = image != null ? saveImageToFileSystem(image) : null;
            if (image != null) {
                imagePathStr = LOCALHOST_IMAGE_PATH + imagePathStr;
            } else {
                imagePathStr = "No image provided";
            }
            MerchandiseCreateDto merchandiseCreateDtoWithImage = new MerchandiseCreateDto();
            merchandiseCreateDtoWithImage.setName(merchandiseCreateDto.getName());
            merchandiseCreateDtoWithImage.setImageUrl(imagePathStr);
            merchandiseCreateDtoWithImage.setCategory(merchandiseCreateDto.getCategory());
            merchandiseCreateDtoWithImage.setPrice(merchandiseCreateDto.getPrice());
            merchandiseCreateDtoWithImage.setStock(merchandiseCreateDto.getStock());
            merchandiseService.saveMerchandise(merchandiseCreateDtoWithImage);
            LOGGER.info("Merchandise successfully created: {}", merchandiseCreateDto);
            return new ResponseEntity<>(merchandiseCreateDto, HttpStatus.CREATED);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @PermitAll
    @GetMapping
    public String getRoot() {
        return "works";
    }

    private String saveImageToFileSystem(MultipartFile imageFile) throws IOException {
        String imageFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path imagePath = Paths.get(IMAGE_UPLOAD_DIR, imageFileName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageFile.getBytes());
        return imageFileName;
    }



}
