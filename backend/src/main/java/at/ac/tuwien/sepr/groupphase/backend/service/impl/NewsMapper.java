package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMPFDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsMapper {

    /**
     * Maps an entity to a
     *
     * @param news entity for mapping
     * @ return the mapped create dto
     */
    public NewsCreateDto entityToCreateDto(News news) {
        return new NewsCreateDto(
            news.getTitle(),
            news.getSummary(),
            news.getContent(),
            news.getImageUrl(),
            news.getDateOfNews());
    }

    /**
     * Maps an and multipart-file-Dto to a dto which only contains the imag urls and not the
     * pictures
     *
     * @param newsCreateMPFDto dto to be mapped
     * @param imgUrls          the image urls from the pictures from the multipart-file
     * @ return the mapped dto
     */
    public NewsCreateDto entityToCreateDtoWithIMGURL(
        NewsCreateMPFDto newsCreateMPFDto, List<String> imgUrls) {
        return new NewsCreateDto(
            newsCreateMPFDto.getTitle(),
            newsCreateMPFDto.getSummary(),
            newsCreateMPFDto.getContent(),
            imgUrls,
            newsCreateMPFDto.getDateOfNews());
    }
}
