package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.BookmarkInterface;
import com.ShortNews.ShortNews.entity.Bookmark;
import com.ShortNews.ShortNews.entity.BookmarkKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkKey> {
    @Query(value = "SELECT b.id, n.cate_id, n.news_id, n.title " +
            "FROM news n JOIN bookmark b " +
            "ON n.news_id = b.news_id " +
            "WHERE b.id = :id " +
            "ORDER BY b.time", nativeQuery = true)
    public List<BookmarkInterface> selectBookmark(@Param("id") String id);

    @Transactional
    @Modifying
    @Query(value = "delete from Bookmark b where b.id = :id and b.news_id = :news_id", nativeQuery = true)
    public void delete(@Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "select count(*) from bookmark where news_id = :news_id and id = :id", nativeQuery = true)
    Integer findByNewsId(@Param("news_id") String news_id, @Param("id") String id);
}
