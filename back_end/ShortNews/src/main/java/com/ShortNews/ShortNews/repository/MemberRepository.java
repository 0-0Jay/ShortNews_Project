package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public List<Member> findByPhone(@Param("phone") String phone);

    @Modifying
    @Transactional
    @Query(value = "update Member m set m.alarm = :type where m.id = :id")
    public void updateAlarm(@Param("id") String id, @Param("type") Integer type);

    @Modifying
    @Transactional
    @Query(value = "UPDATE member m SET m.phone = :phone WHERE m.id = :id", nativeQuery = true)
    public void updatePhone(@Param("id") String id, @Param("phone") String phone);
}
