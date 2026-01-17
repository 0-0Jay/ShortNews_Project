package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.ActivityReplyInterface;
import com.ShortNews.ShortNews.dto.ReplyAlarmDto;
import com.ShortNews.ShortNews.dto.ReplyAlarmInterface;
import com.ShortNews.ShortNews.dto.ReplyInterface;
import com.ShortNews.ShortNews.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, String> {
    @Query(value = "SELECT r.*, m.nickname, NVL(a.type, 0) as type, " +
            "(SELECT COUNT(*) FROM recommend a WHERE a.reply_id = r.reply_id and type = 1) as good, " +
            "(SELECT COUNT(*) FROM recommend a WHERE a.reply_id = r.reply_id and type = -1) as bad, " +
            "(SELECT COUNT(*) FROM report b WHERE b.reply_id = r.reply_id) as report " +
            "FROM reply r " +
            "JOIN member m ON r.id = m.id " +
            "LEFT JOIN (SELECT a.type, a.reply_id FROM recommend a WHERE id = :id) a ON r.reply_id = a.reply_id " +
            "WHERE r.news_id = :news_id " +
            "START WITH r.upper_id IS NULL " +
            "CONNECT BY PRIOR r.reply_id = r.upper_id " +
            "ORDER SIBLINGS BY 1", nativeQuery = true)
    public List<ReplyInterface> selectReply(@Param("news_id") String news_id, @Param("id") String id);

    @Query(value = "SELECT n.news_id, n.title, n.imgs, " +
            "r.reply_id AS lowrid, r.id AS lowuid, (SELECT m.nickname FROM member  m WHERE m.id = r.id) AS lownick, r.content AS lowcnt, " +
            "r.upper_id AS uprid, p.id AS upuid, (SELECT m.nickname FROM member m WHERE m.id = p.id) AS upnick, p.content AS upcnt " +
            "FROM news n " +
            "JOIN reply r ON n.news_id = r.news_id " +
            "LEFT JOIN reply p ON r.upper_id = p.reply_id " +
            "WHERE r.id = :id " +
            "ORDER BY r.reply_id desc", nativeQuery = true)
    public List<ActivityReplyInterface> selectActivityReply(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO reply(reply_id, news_id, id, content, upper_id, edited) VALUES(CONCAT(to_char(sysdate, 'YYYYMMDDHH24MISS'), reply_seq.nextval), :news_id, :id, :content, :upper_id, 0)", nativeQuery = true)
    public void insertReply(@Param("news_id") String news_id, @Param("id") String id, @Param("content") String content, @Param("upper_id") String upper_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Reply r set r.content = :text, r.edited = 1 WHERE r.reply_id = :reply_id")
    public void updateReply(@Param("reply_id") String reply_id, @Param("text") String text);

    @Query(value = "SELECT m.id, r.reply_id, m.alarm " +
            "FROM reply r JOIN member m " +
            "ON r.id = m.id " +
            "WHERE r.reply_id = :reply_id", nativeQuery = true)
    public ReplyAlarmInterface selectReplyAlarm(@Param("reply_id") String reply_id);

}
