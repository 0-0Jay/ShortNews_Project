package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Preference;
import com.ShortNews.ShortNews.entity.PreferenceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, PreferenceKey> {

    @Query(value = "select * from Preference where id = :id", nativeQuery = true)
    public List<Preference> selectCate(@Param("id") String id);
}
