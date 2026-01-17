package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReasonRepository extends JpaRepository<Reason, Integer> {
    @Query(value = "INSERT INTO reason(reason_id, content) VALUES(reason_seq.nextval, :content)", nativeQuery = true)
    @Modifying
    @Transactional
    public void insertReason(@Param("content") String content);
}
