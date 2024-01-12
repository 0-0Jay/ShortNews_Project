package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.ActivityNewsInterface;
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
    @Query(value = "SELECT n.news_id, n.cate_id, n.title, n.views, n.imgs, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1) AS good, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 0) AS bad, " +
            "(SELECT COUNT(*) FROM reply r WHERE r.news_id = n.news_id) AS reply " +
            "FROM bookmark b " +
            "JOIN news n on n.news_id = b.news_id " +
            "WHERE b.id = :id", nativeQuery = true)
    public List<ActivityNewsInterface> selectBookmark(@Param("id") String id);

    @Transactional
    @Modifying
    @Query(value = "delete from Bookmark b where b.id = :id and b.news_id = :news_id", nativeQuery = true)
    public void delete(@Param("id") String id, @Param("news_id") String news_id);
}
