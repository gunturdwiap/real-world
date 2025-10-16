package com.example.realworld.repository;

import com.example.realworld.entity.Follow;
import com.example.realworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    boolean existsByFollowerAndFollowee(User follower, User followee);
    void deleteByFollowerAndFollowee(User follower, User followee);
}
