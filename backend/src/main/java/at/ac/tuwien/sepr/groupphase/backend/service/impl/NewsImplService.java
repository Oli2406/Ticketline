package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMPFDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final Path uploadDirectory = Path.of("");

    // Paths.get(NewsImplService.class.getClassLoader().getResource("images").toURI());

    public NewsImplService(
        NewsRepository newsRepository, NewsValidator newsValidator, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.newsValidator = newsValidator;
        this.newsMapper = newsMapper;
    }

    @Override
    public NewsCreateDto createNews(NewsCreateMPFDto newsCreateMPFDto)
        throws ValidationException, IOException, URISyntaxException {
        LOG.trace("createNews({})", newsCreateMPFDto);
        newsValidator.validateNews(newsCreateMPFDto);

        List<String> imageUrls = new ArrayList<String>();

        for (MultipartFile imageFile : newsCreateMPFDto.getImages()) {
            imageUrls.add(this.uploadImagePath(null, imageFile));
        }

        NewsCreateDto newsCreate = newsMapper.entityToCreateDtoWithIMGURL(newsCreateMPFDto,
            imageUrls);

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
            String pictureURL = "";
            if (pictureURL != null && !pictureURL.isEmpty()) {
                return pictureURL;
            }
            return null;
        }
        return uploadNewImage(image);
    }

    private String uploadNewImage(MultipartFile image) throws IOException {

        String relativePathVar =
            uploadDirectory
                .toString()
                .substring(0, uploadDirectory.toString().lastIndexOf(File.separator));
        Path relativePath = Paths.get(relativePathVar);

        String fileName = image.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDirectory + File.separator + fileName);
        Files.createDirectories(uploadPath.getParent());
        image.transferTo(new File(uploadPath.toString()));

        return relativePath.relativize(uploadPath).toString().replace("\\", "/");
    }
}
