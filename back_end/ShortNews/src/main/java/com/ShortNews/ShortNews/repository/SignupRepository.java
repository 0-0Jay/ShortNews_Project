package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignupRepository extends JpaRepository<Member, String> {

    public Optional<Member> findByPhone(@Param("phone") String phone);

    public Optional<Member> findByNickname(@Param("nickname") String nickname);
}
