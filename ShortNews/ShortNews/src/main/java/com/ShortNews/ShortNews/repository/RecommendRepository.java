package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.ActivityDisLikeDto;
import com.ShortNews.ShortNews.dto.ActivityDisLikeInterface;
import com.ShortNews.ShortNews.dto.ActivityLikeInterface;
import com.ShortNews.ShortNews.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
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

//    @Query(value = "delete from Recommend r where r.news_id = :news_id and r.id = :id", nativeQuery = true)
//    @Modifying
//    @Transactional
//    public void offLike(@Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "insert into Recommend values(:rec_id, :id, null, :news_id, 1)", nativeQuery = true)
    @Modifying
    @Transactional
    public void onLike(@Param("rec_id") String rec_id, @Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "insert into Recommend values(:rec_id, :id, null, :news_id, 0)", nativeQuery = true)
    @Modifying
    @Transactional
    public void onDisLike(@Param("rec_id") String rec_id, @Param("id") String id, @Param("news_id") String news_id);

    @Query(value = "select r from Recommend r where r.id = :id and reply_id is null and type = 1")
    public List<Recommend> getLike(@Param("id") String id);

    @Query(value = "select r from Recommend r where r.id = :id and reply_id is null and type = 0")
    public List<Recommend> getDisLike(@Param("id") String id);


    @Query(value = "select a.id, a.content, b.type, substr(b.recom_id, 0, 8) as time from reply a join recommend b on a.reply_id = b.reply_id and type = 1 where b.id = :id", nativeQuery = true)
    public List<ActivityLikeInterface> getReplyLike(@Param("id") String id);

    @Query(value = "select a.id, a.content, b.type, substr(b.recom_id, 0, 8) as time from reply a join recommend b on a.reply_id = b.reply_id and type = 0 where b.id = :id", nativeQuery = true)
    public List<ActivityDisLikeInterface> getReplyDisLike(@Param("id") String id);


    @Query(value = "update recommend set type = :type where news_id = :news_id", nativeQuery = true)
    @Modifying
    @Transactional
    public void updateLike(@Param("news_id") String news_id, @Param("type") String type);

    @Query(value = "delete from recommend where news_id = :news_id and id = :id", nativeQuery = true)
    @Modifying
    @Transactional
    public void deleteLike(@Param("news_id") String news_id, @Param("id") String id);
}