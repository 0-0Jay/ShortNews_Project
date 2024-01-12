package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.ActivityReplyInterface;
import com.ShortNews.ShortNews.dto.ReplyInterface;
import com.ShortNews.ShortNews.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, String> {

    @Query(value = "select count(*) from Reply where news_id = :news_id")
    public Integer replyCount(@Param("news_id") String news_id);

    @Query(value = "SELECT r.*, (SELECT COUNT(*) FROM recommend a WHERE a.reply_id = r.reply_id and type = 1) as good, (SELECT COUNT(*) FROM recommend a WHERE a.reply_id = r.reply_id and type = 0) as bad " +
            "FROM REPLY r " +
            "WHERE news_id = :news_id " +
            "START WITH upper_id IS NULL " +
            "CONNECT BY PRIOR reply_id = upper_id " +
            "ORDER SIBLINGS BY 1", nativeQuery = true)
    public List<ReplyInterface> selectReply(@Param("news_id") String news_id);

    @Query(value = "SELECT n.news_id, n.title, r.content, SUBSTR(r.reply_id, 1, 8) AS time, n.imgs " +
            "FROM news n JOIN reply r " +
            "ON n.news_id = r.news_id " +
            "WHERE r.id = :id " +
            "ORDER BY r.reply_id", nativeQuery = true)
    public List<ActivityReplyInterface> selectActivityReply(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO reply(reply_id, news_id, id, content, upper_id) VALUES(CONCAT(to_char(sysdate, 'YYYYMMDDHHMISS'), reply_seq.nextval), :news_id, :id, :content, :upper_id)", nativeQuery = true)
    public void insertReply(@Param("news_id") String news_id, @Param("id") String id, @Param("content") String content, @Param("upper_id") String upper_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Reply r set r.content = :text WHERE r.reply_id = :reply_id")
    public void updateReply(@Param("reply_id") String reply_id, @Param("text") String text);

}
