package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.User_log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface User_logRepository extends JpaRepository<User_log, Integer> {
    @Modifying
    @Transactional
    @Query(value = "insert into user_log values(:news_id, :cate_id, :id, localtimestamp)", nativeQuery = true)
    public void insertLog(@Param("id") String id, @Param("news_id") String news_id, @Param("cate_id") String cate_id);
}
