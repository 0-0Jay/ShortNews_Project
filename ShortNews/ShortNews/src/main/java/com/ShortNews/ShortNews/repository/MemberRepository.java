package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    public List<Member> findByNickname(@Param("nickname") String nickname);
    @Modifying
    @Transactional
    @Query(value = "update Member m set m.pw = :pw where m.id = :id")
    public void updatePassword(@Param("id") String id, @Param("pw") String pw);

    @Modifying
    @Transactional
    @Query(value = "update Member m set m.nickname = :nickname where m.id = :id")
    public void updateNickname(@Param("id") String id, @Param("nickname") String nickname);

    public List<Member> findByEmail(@Param("email") String email);
}
