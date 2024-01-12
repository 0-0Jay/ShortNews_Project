package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query(value = "insert into report values(report_seq.nextval, systimestamp, :id, :type, :content, null, :news_id)", nativeQuery = true)
    @Modifying
    @Transactional
    public void insertReport(@Param("id") String id, @Param("type") String type, @Param("content") String content, @Param("news_id") String news_id);

    @Query(value = "insert into report values(report_seq.nextval, systimestamp, :id, :type, :content, :reply_id, :news_id)", nativeQuery = true)
    @Modifying
    @Transactional
    public void insertReplyReport(@Param("id") String id, @Param("type") String type, @Param("content") String content, @Param("reply_id") String reply_id, @Param("news_id") String news_id);
}
