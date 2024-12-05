package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class NewsMapper {

    public NewsDetailDto entityToDetailDto(News news) {
        return new NewsDetailDto(
            news.getNewsId(),
            news.getTitle(),
            news.getSummary(),
            news.getContent(),
            news.getImageUrl(),
            news.getDateOfNews()
        );
    }

    /**
     * Maps a {@link News} entity to a {@link NewsCreateDto}.
     *
     * @param news the {@link News} entity to be mapped
     * @return the mapped {@link NewsCreateDto}
     */
    public NewsCreateDto entityToCreateDto(News news) {
        return new NewsCreateDto(
            news.getTitle(),
            news.getSummary(),
            news.getContent(),
            news.getImageUrl(),
            news.getDateOfNews()
        );
    }

    /**
     * Maps a {@link NewsCreateMpfDto} and a list of image URLs to a {@link NewsCreateDto}.
     * This method is used to map data from a multipart-file DTO to a DTO containing only
     * image URLs without the actual pictures.
     *
     * @param mpfDto  the {@link NewsCreateMpfDto} to be mapped
     * @param imgUrls the list of image URLs extracted from the multipart files
     * @return the mapped {@link NewsCreateDto}
     */
    public NewsCreateDto entityToCreateDtoWithImgUrl(
        NewsCreateMpfDto mpfDto, List<String> imgUrls) {
        return new NewsCreateDto(
            mpfDto.getTitle(),
            mpfDto.getSummary(),
            mpfDto.getContent(),
            imgUrls,
            mpfDto.getDate()
        );
    }
}
