package com.ShortNews.ShortNews.Token;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<Refreshtoken, String> {

    public Optional<Refreshtoken> findByRefreshtoken(String refreshtoken);

    @Modifying
    @Transactional
    @Query("update Refreshtoken r set r.refreshtoken = :token where r.id = :id")
    public void update(@Param(value = "id") String id, @Param(value = "token") String token);
}
