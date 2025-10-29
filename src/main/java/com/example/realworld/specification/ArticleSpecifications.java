package com.example.realworld.specification;

import com.example.realworld.entity.Article;
import com.example.realworld.entity.Tag;
import com.example.realworld.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecifications {

    public static Specification<Article> hasTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isBlank()) return null;

            root.fetch("tags", JoinType.LEFT); // eager load
            if (query != null) query.distinct(true);

            Join<Article, Tag> tagJoin = root.join("tags", JoinType.INNER);
            return cb.equal(tagJoin.get("name"), tag);
        };
    }

    public static Specification<Article> hasAuthor(String authorUsername) {
        return (root, query, cb) -> {
            if (authorUsername == null || authorUsername.isBlank()) return null;

            root.fetch("author", JoinType.LEFT);
            if (query != null) query.distinct(true);

            Join<Article, User> authorJoin = root.join("author", JoinType.INNER);
            return cb.equal(authorJoin.get("username"), authorUsername);
        };
    }

    public static Specification<Article> isFavoritedBy(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isBlank()) return null;

            root.fetch("favoritedBy", JoinType.LEFT);
            if (query != null) query.distinct(true);

            Join<Article, User> favJoin = root.join("favoritedBy", JoinType.INNER);
            return cb.equal(favJoin.get("username"), username);
        };
    }
}
