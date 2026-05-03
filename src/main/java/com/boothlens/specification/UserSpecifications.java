package com.boothlens.specification;

import com.boothlens.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> withFilters(String keyword, Integer roleId, Boolean isBlocked) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (roleId != null) {
                predicates.add(cb.equal(root.get("roleId"), roleId));
            }
            if (isBlocked != null) {
                predicates.add(cb.equal(root.get("isBlocked"), isBlocked));
            }
            if (StringUtils.hasText(keyword)) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                List<Predicate> keywordPredicates = new ArrayList<>();
                keywordPredicates.add(cb.like(cb.lower(root.get("username")), normalizedKeyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("email")), normalizedKeyword));

                try {
                    Integer parsedUserId = Integer.valueOf(keyword.trim());
                    keywordPredicates.add(cb.equal(root.get("id"), parsedUserId));
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric keyword for id filtering.
                }

                predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
