package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.ActivityNewsInterface;
import com.ShortNews.ShortNews.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, String> {

    @Query(value = "SELECT n.news_id, n.cate_id, n.title, n.views, n.imgs," +
        "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1 AND l.reply_id is NULL) AS good," +
        "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = -1 AND l.reply_id is NULL) AS bad," +
        "(SELECT COUNT(*) FROM reply r WHERE r.news_id = n.news_id) AS reply, " +
        "(SELECT COUNT(*) FROM bookmark b WHERE b.news_id = n.news_id and b.id = l.id) AS bookmark " +
        "FROM news n " +
        "JOIN recommend l on n.news_id = l.news_id " +
        "WHERE l.id = :id and l.type = :type and l.reply_id is NULL " +
        "ORDER BY l.recom_id desc", nativeQuery = true)
    List<ActivityNewsInterface> selectLikeNews(@Param("id") String id, @Param("type") Integer type);

    @Query(value = "update recommend set type = :type where recom_id = :recom_id", nativeQuery = true)
    @Modifying
    @Transactional
    public void updateLike(@Param("recom_id") String recom_id, @Param("type") int type);

    @Query(value = "INSERT INTO recommend(recom_id, id, news_id, reply_id, type) VALUES(CONCAT(to_char(sysdate, 'YYYYMMDDHHMISS'), recommend_seq.nextval), :id, :news_id, :reply_id, :type)", nativeQuery = true)
    @Modifying
    @Transactional
    public void insertLike(@Param("id") String id, @Param("news_id") String news_id, @Param("reply_id") String reply_id, @Param("type") int type);

    @Query(value = "DELETE FROM recommend WHERE recom_id = :recom_id", nativeQuery = true)
    @Modifying
    @Transactional
    public void deleteLike(@Param("recom_id") String recom_id);

    @Query(value = "SELECT recom_id FROM recommend WHERE id = :id and news_id = :news_id and reply_id is null", nativeQuery = true)
    Optional<String> selectNewsLike(@Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "SELECT recom_id FROM recommend WHERE id = :id and news_id = :news_id and reply_id = :reply_id", nativeQuery = true)
    Optional<String> selectReplyLike(@Param("id") String id, @Param("news_id") String news_id, @Param("reply_id") String reply_id);

}