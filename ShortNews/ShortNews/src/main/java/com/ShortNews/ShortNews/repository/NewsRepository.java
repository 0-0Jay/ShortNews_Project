package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {

    @Query("select n from News n where SUBSTR(n.news_id, 1, 8) = :date and n.cate_id = :category")
    public List<News> selectNews(@Param("category") String category, @Param("date") String date);

    @Query("select n from News n where title like concat('%', :keyword, '%') or content like concat('%', :keyword, '%')")
    public List<News> selectKeyword(@Param("keyword") String keyword);

    @Query("select n.title from News n where news_id = :news_id")
    public String selectTitle(@Param("news_id") String news_id);

    @Modifying
    @Transactional
    @Query(value = "update News n set n.views = n.views + 1 where news_id = :news_id")
    public void updateViews(@Param("news_id") String news_id);
}