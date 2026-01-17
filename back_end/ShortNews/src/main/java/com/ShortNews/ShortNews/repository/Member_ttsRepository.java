package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Member_tts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface Member_ttsRepository extends JpaRepository<Member_tts, String> {
    @Modifying
    @Transactional
    @Query(value = "update member_tts set model_id = :model, speed = :speed where id = :id", nativeQuery = true)
    public void updateTts(@Param("id") String id, @Param("model") String model, @Param("speed") String speed);
}
