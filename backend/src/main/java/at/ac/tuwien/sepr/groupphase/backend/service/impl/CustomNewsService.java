package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomNewsService implements NewsService {

    private final Path imageDir = Paths.get("./newsImages").toAbsolutePath().normalize();
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;
    private final NewsMapper newsMapper;

    @Autowired
    public CustomNewsService(NewsRepository newsRepository, UserService userService, NewsValidator newsValidator, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.newsValidator = newsValidator;
        this.newsMapper = newsMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsDetailDto> getUnreadNews(String email) {
        LOG.trace("getUnreadNews({})", email);
        ApplicationUser user = userService.findApplicationUserByEmail(email);

        return newsRepository.findUnreadNews(user.getReadNewsIds())
            .stream()
            .map(newsMapper::entityToDetailDto)
            .collect(Collectors.toList());
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
                newsCreate.getDate(),
                newsCreate.getImages());

        System.out.println("NEWS BEING SAVED: " + news.getImageUrl().size());
        var createdNews = newsRepository.save(news);

        return newsMapper.entityToCreateDto(createdNews);
    }


    /*Private methods handling the image upload*/

    private String uploadImagePath(Long id, MultipartFile image) throws IOException {
        if (id == null && image != null) {
            return saveImageLocally(image);
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
        return saveImageLocally(image);
    }

    private String saveImageLocally(MultipartFile image) throws IOException {
        LOG.trace("saveImageLocally({})", image.getOriginalFilename());

        if (!Files.exists(imageDir)) {
            Files.createDirectories(imageDir);
        }

        String imageName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path imagePath = imageDir.resolve(imageName);
        Files.copy(image.getInputStream(), imagePath);

        return imageName;
    }

    private void deleteImageLocally(String imageName) throws IOException {
        LOG.trace("deleteImageLocally({})", imageName);
        Files.delete(imageDir.resolve(imageName));
    }
}
