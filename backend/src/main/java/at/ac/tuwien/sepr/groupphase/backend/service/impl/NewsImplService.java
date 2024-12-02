package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsImplService implements NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;
    private final NewsMapper newsMapper;


    public NewsImplService(
        NewsRepository newsRepository, NewsValidator newsValidator, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.newsValidator = newsValidator;
        this.newsMapper = newsMapper;
    }

    @Override
    public NewsCreateDto createNews(NewsCreateMpfDto mpfDto)
        throws ValidationException, IOException, URISyntaxException {
        LOG.trace("createNews({})", mpfDto);
        newsValidator.validateNews(mpfDto);

        List<String> imageUrls = new ArrayList<>();

        MultipartFile[] images = mpfDto.getImages();
        if (images != null) {
            for (MultipartFile imageFile : images) {
                imageUrls.add(this.uploadImagePath(null, imageFile));
            }
        }

        NewsCreateDto newsCreate = newsMapper.entityToCreateDtoWithImgUrl(mpfDto, imageUrls);

        News news =
            new News(
                newsCreate.getTitle(),
                newsCreate.getSummary(),
                newsCreate.getContent(),
                newsCreate.getDateOfNews(),
                newsCreate.getImageUrl());
        var createdNews = newsRepository.save(news);

        return newsMapper.entityToCreateDto(createdNews);
    }


    /*Private methods handling the image upload*/

    private String uploadImagePath(Long id, MultipartFile image) throws IOException {
        if (id == null && image != null) {
            return uploadNewImage(image);
        } else if (id == null) {
            return null;
        }

        if (image == null) {
            // Todo get img url
            String pictureUrl = "";
            if (pictureUrl != null && !pictureUrl.isEmpty()) {
                return pictureUrl;
            }
            return null;
        }
        return uploadNewImage(image);
    }

    private String uploadNewImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Invalid image file: file is null or empty");
        }
        Path baseDirectory = Paths.get("src", "main", "newsImages");
        Path uploadDirectory = baseDirectory.resolve("uploads").toAbsolutePath();
        try {
            if (!Files.exists(baseDirectory)) {
                Files.createDirectories(baseDirectory);
                LOG.info("Created base resources directory: {}", baseDirectory.toAbsolutePath());
            }
            Files.createDirectories(uploadDirectory);
            LOG.info("Uploads directory verified/created: {}", uploadDirectory.toAbsolutePath());
        } catch (IOException e) {
            LOG.error("Failed to create upload directory: {}", uploadDirectory, e);
            throw new IOException("Could not create upload directory", e);
        }
        String fileName = image.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid image file: filename is null or empty");
        }
        Path uploadPath = uploadDirectory.resolve(fileName);
        try {
            image.transferTo(uploadPath.toFile());
            LOG.info("File uploaded successfully to: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            LOG.error("Error while saving file: {}", fileName, e);
            throw new IOException("Failed to save file: " + fileName, e);
        }
        return uploadDirectory.relativize(uploadPath).toString().replace("\\", "/");
    }


}
