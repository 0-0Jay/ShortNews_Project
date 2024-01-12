package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonRepository extends JpaRepository<Reason, Integer> {
    @Query(value = "select reason_seq.nextval from dual", nativeQuery = true)
    public Integer selectId();
}
