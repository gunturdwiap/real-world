package com.example.realworld.repository;

import com.example.realworld.entity.Follow;
import com.example.realworld.entity.FollowId;
import com.example.realworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    boolean existsByFollowerAndFollowee(User follower, User followee);
    void deleteByFollowerAndFollowee(User follower, User followee);

    @Query("""
        SELECT f.followee.id
        FROM Follow f
        WHERE f.follower = :currentUser
          AND f.followee.id IN :authorIds
    """)
    List<UUID> findFollowedAuthorIds(User currentUser, List<UUID> authorIds);

    List<User> findFolloweesByFollower(User user);
}
