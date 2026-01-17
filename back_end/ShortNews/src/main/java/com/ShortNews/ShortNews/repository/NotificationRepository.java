package com.ShortNews.ShortNews.repository;

import com.ShortNews.ShortNews.dto.AlarmInterface;
import com.ShortNews.ShortNews.entity.Notification;
import com.ShortNews.ShortNews.entity.NotificationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, NotificationKey> {

    @Query(value = "SELECT n.*, (SELECT nickname FROM member WHERE n.id = member.id) AS nickname " +
            "FROM notification n JOIN member m " +
            "ON n.target_id = m.id " +
            "WHERE n.target_id = :id and n.time >= systimestamp - 3 " +
            "ORDER BY 1 desc ", nativeQuery = true)
    public List<AlarmInterface> selectAlarm(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "update Notification n set n.status = 1 where n.target_id = :id and n.link = :link and " +
            "to_char(n.time, 'yyyy/mm/dd hh24:mi:ss.ff3') = to_char(to_timestamp(:time, 'yyyy/mm/dd hh24:mi:ss.ff3'), 'yyyy/mm/dd hh24:mi:ss.ff3')", nativeQuery = true)
    public void updateStatus(@Param("id") String id, @Param("time") String time, @Param("link") String link);

    @Modifying
    @Transactional
    @Query(value = "delete from Notification n where n.target_id = :id and n.link = :link and " +
            "to_char(n.time, 'yy/mm/dd hh24:mi:ss.ff3') = to_char(to_timestamp(:time, 'yy-mm-dd hh24:mi:ss.ff3'), 'yy/mm/dd hh24:mi:ss.ff3')", nativeQuery = true)
    public void deleteAlarm(@Param("id") String id, @Param("time") String time, @Param("link") String link);

    @Modifying
    @Transactional
    @Query(value = "delete from Notification n where n.target_id = :id", nativeQuery = true)
    public void allDeleteAlarm(@Param("id") String id);
}
