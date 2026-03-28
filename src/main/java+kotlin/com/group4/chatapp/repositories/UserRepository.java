package com.group4.chatapp.repositories;

import com.group4.chatapp.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Stream<User> findByUsernameContaining(String keyword, PageRequest pageable);

    @Query("""
        select u
        from User u
        where u.id <> :excludeUserId and (
            lower(u.username) like lower(concat('%', :keyword, '%')) or
            lower(coalesce(u.displayName, '')) like lower(concat('%', :keyword, '%'))
        )
        order by
            case when lower(u.username) = lower(:keyword) then 0 else 1 end,
            u.username asc
    """)
    List<User> searchByKeyword(
        @Param("keyword") String keyword,
        @Param("excludeUserId") long excludeUserId,
        Pageable pageable
    );

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
