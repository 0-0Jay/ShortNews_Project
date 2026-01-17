package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.NewsInterface;
import com.ShortNews.ShortNews.dto.NewsListInterface;
import com.ShortNews.ShortNews.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {

    @Query(value = "SELECT n.news_id, n.cate_id, n.title, n.views, n.imgs, " +
            "(SELECT COUNT(*) FROM bookmark b WHERE b.id = :id and b.news_id = n.news_id) AS bookmark, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1 AND l.reply_id is NULL) AS good, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = -1 AND l.reply_id is NULL) AS bad, " +
            "(SELECT COUNT(*) FROM report a WHERE a.news_id = n.news_id AND a.reply_id is NULL) AS report, " +
            "(SELECT COUNT(*) FROM reply r WHERE r.news_id = n.news_id) AS reply " +
            "FROM news n " +
            "WHERE n.cate_id = :category and SUBSTR(n.news_id, 1, 8) = :date " +
            "ORDER BY n.news_id desc", nativeQuery = true)
    public List<NewsListInterface> findNews(@Param("category") String category, @Param("date") String date, @Param("id") String id);

    @Query(value = "SELECT n.news_id, n.cate_id, n.content, n.title, n.views, n.imgs, NVL(a.type, 0) AS type," +
            "(SELECT COUNT(*) FROM bookmark b WHERE b.id = :id and b.news_id = n.news_id) AS bookmark, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1 AND l.reply_id is NULL) AS good, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = -1 AND l.reply_id is NULL) AS bad, " +
            "(SELECT COUNT(*) FROM reply p WHERE p.news_id = n.news_id) AS reply " +
            "FROM news n " +
            "LEFT JOIN (SELECT l.type, l.news_id, l.reply_id FROM recommend l WHERE l.id = :id and l.reply_id is null) a ON n.news_id = a.news_id " +
            "WHERE n.news_id = :news_id", nativeQuery = true)
    public Optional<NewsInterface> selectNews(@Param("news_id") String news_id, @Param("id") String id);

    @Query(value = "SELECT news_id, cate_id, title, views, imgs, bookmark, good, bad, reply " +
            "FROM (" +
            "SELECT n.news_id, n.cate_id, n.title, n.views, n.imgs, " +
            "(SELECT COUNT(*) FROM bookmark b WHERE b.id = :id and b.news_id = n.news_id) AS bookmark, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1 AND l.reply_id is NULL) AS good, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = -1 AND l.reply_id is NULL) AS bad, " +
            "(SELECT COUNT(*) FROM reply r WHERE r.news_id = n.news_id) AS reply, " +
            "(SELECT COUNT(*) FROM report a WHERE a.news_id = n.news_id AND a.reply_id is NULL) AS report, " +
            "ROW_NUMBER() over (ORDER BY n.news_id desc) AS rn " +
            "FROM news n " +
            "WHERE n.title LIKE :keyword OR n.content LIKE :keyword " +
            ") " +
            "WHERE rn BETWEEN :st AND :ed", nativeQuery = true)
    public List<NewsListInterface> selectKeyword(@Param("id") String id, @Param("keyword") String keyword, @Param("st") int st, @Param("ed") int ed);

    @Modifying
    @Transactional
    @Query(value = "update News n set n.views = n.views + 1 where news_id = :news_id")
    public void updateViews(@Param("news_id") String news_id);

    @Query(value = "SELECT n.news_id, n.content, n.title, n.views, n.imgs, n.url, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1 AND l.reply_id is NULL) AS good, " +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = -1 AND l.reply_id is NULL) AS bad, " +
            "(SELECT COUNT(*) FROM reply p WHERE p.news_id = n.news_id) AS reply " +
            "FROM news n " +
            "WHERE n.news_id = :news_id", nativeQuery = true)
    public Optional<NewsInterface> shareNews(@Param("news_id") String news_id);
}