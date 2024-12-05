package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Finds all news items that have not been read.
     *
     * @param readNewsIds A list of IDs of news items that have already been read.
     * @return A list of news items that have not been read.
     */
    @Query("SELECT n FROM News n WHERE n.newsId NOT IN :readNewsIds ORDER BY n.dateOfNews DESC")
    List<News> findUnreadNews(@Param("readNewsIds") List<Long> readNewsIds);

    /**
     * Finds all news items with {@code title} as title.
     *
     * @param title The title to find the news by.
     * @return A list of news items that have the desired title.
     */
    @Query("SELECT n FROM News n WHERE n.title = :title")
    List<News> findByTitle(String title);
}