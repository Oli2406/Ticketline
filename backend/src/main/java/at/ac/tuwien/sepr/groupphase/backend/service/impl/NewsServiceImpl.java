package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.NewsValidator;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;
    private final NewsMapper newsMapper;
    private final UserRepository userRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserService userService,
        NewsValidator newsValidator, NewsMapper newsMapper, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.newsValidator = newsValidator;
        this.newsMapper = newsMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsDetailDto> getNews() {
        LOGGER.trace("getNews()");

        return newsRepository.findAll().stream().map(newsMapper::entityToDetailDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsDetailDto> getUnreadNews(String email) {
        LOGGER.trace("getUnreadNews({})", email);
        ApplicationUser user = userService.findApplicationUserByEmail(email);

        return newsRepository.findUnreadNews(user.getReadNewsIds()).stream()
            .map(newsMapper::entityToDetailDto).toList();
    }

    @Override
    public NewsCreateDto createNews(NewsCreateMpfDto mpfDto)
        throws ValidationException, IOException {
        LOGGER.trace("createNews({})", mpfDto);
        newsValidator.validateNews(mpfDto);

        List<String> imageUrls = new ArrayList<>();

        MultipartFile[] images = mpfDto.getImages();
        if (images != null) {
            for (MultipartFile imageFile : images) {
                imageUrls.add(this.uploadImagePath(null, imageFile));
            }
        }

        NewsCreateDto newsCreate = newsMapper.entityToCreateDtoWithImgUrl(mpfDto, imageUrls);
        News news = new News(newsCreate.getTitle(), newsCreate.getSummary(),
            newsCreate.getContent(), newsCreate.getDate(), newsCreate.getImages());

        var createdNews = newsRepository.save(news);

        return newsMapper.entityToCreateDto(createdNews);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDetailDto getById(long id) throws NotFoundException {
        LOGGER.trace("getById({})", id);
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("News not found with id: " + id));

        return newsMapper.entityToDetailDto(news);
    }


    private String uploadImagePath(Long id, MultipartFile image) throws IOException {
        if (id == null && image != null) {
            return saveImageToFileSystem(image);
        } else if (id == null) {
            return null;
        }

        if (image == null) {
            // Todo get img url
            String pictureUrl = "";
            return null;
        }
        return saveImageToFileSystem(image);
    }

    private String saveImageToFileSystem(MultipartFile imageFile) throws IOException {
        String imageFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        Resource resourceDir = new ClassPathResource("newsImages");
        Path uploadPath = Paths.get(resourceDir.getFile().getAbsolutePath());

        Files.createDirectories(uploadPath);

        Path imagePath = uploadPath.resolve(imageFileName);

        Files.write(imagePath, imageFile.getBytes());

        return imageFileName;
    }
}