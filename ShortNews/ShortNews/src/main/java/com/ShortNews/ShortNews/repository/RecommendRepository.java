package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.ActivityLikeInterface;
import com.ShortNews.ShortNews.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, String> {

    @Query(value = "select r from Recommend r where id = :id and news_id = :news_id")
    public List<Recommend> likeCheck(@Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "select r from Recommend r where r.news_id = :news_id and reply_id is null and type = 1")
    public List<Recommend> likeCount(@Param("news_id") String news_id);

    @Query(value = "select r from Recommend r where r.news_id = :news_id and reply_id is null and type = 0")
    public List<Recommend> dislikeCount(@Param("news_id") String news_id);

    @Query(value = "delete from Recommend r where r.news_id = :news_id and r.id = :id", nativeQuery = true)
    @Modifying
    @Transactional
    public void offLike(@Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "insert into Recommend values(:rec_id, :id, null, :news_id, 1)", nativeQuery = true)
    @Modifying
    @Transactional
    public void onLike(@Param("rec_id") String rec_id, @Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "insert into Recommend values(:rec_id, :id, null, :news_id, 0)", nativeQuery = true)
    @Modifying
    @Transactional
    public void onDisLike(@Param("rec_id") String rec_id, @Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "SELECT n.news_id, n.cate_id, n.title, n.views, n.imgs," +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1) AS good," +
            "(SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 0) AS bad," +
            "(SELECT COUNT(*) FROM reply r WHERE r.news_id = n.news_id) AS reply " +
            "(SELECT COUNT(*) FROM bookmark b WHERE b.news_id = n.news_id and b.id = l.id) AS bookmark " +
            "FROM news n " +
            "JOIN recommend l on n.news_id = l.news_id " +
            "WHERE l.id = :id and l.type = :type", nativeQuery = true)
    List<ActivityLikeInterface> selectLikeNews(@Param("id") String id, @Param("type") Integer type);
}