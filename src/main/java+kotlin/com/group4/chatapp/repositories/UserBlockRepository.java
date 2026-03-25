package com.group4.chatapp.repositories;

import com.group4.chatapp.models.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    boolean existsByBlocker_IdAndBlocked_Id(long blockerId, long blockedId);

    List<UserBlock> findByBlocker_Id(long blockerId);

    long deleteByBlocker_IdAndBlocked_Id(long blockerId, long blockedId);
}
