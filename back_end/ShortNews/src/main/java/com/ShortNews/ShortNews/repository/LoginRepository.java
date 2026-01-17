package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Member, String> {

    public Optional<Member> findByPhone(@Param("phone") String phone);

    @Modifying
    @Transactional
    @Query(value = "update Member m set m.pw = :pw where m.id = :id")
    public void updatePw(@Param("id") String id, @Param("pw") String pw);

}