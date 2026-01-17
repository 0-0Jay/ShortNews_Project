package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query(value = "insert into report values(systimestamp, :id, :type, :content, :reply_id, :news_id)", nativeQuery = true)
    @Modifying
    @Transactional
    public void insertReport(@Param("id") String id, @Param("type") String type, @Param("content") String content, @Param("reply_id") String reply_id, @Param("news_id") String news_id);

    @Query(value = "SELECT r.* FROM report r " +
            "WHERE r.id = :id AND NVL(r.news_id, 0) = NVL(:news_id, 0) AND NVL(r.reply_id, 0) = NVL(:reply_id, 0)", nativeQuery = true)
    public List<Report> findReport(@Param("id") String id, @Param("news_id") String news_id, @Param("reply_id") String reply_id);
}
