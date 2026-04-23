package com.boothlens.specification;

import com.boothlens.entity.UserCredits;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class UserCreditsSpecifications {

    private UserCreditsSpecifications() {
    }

    public static Specification<UserCredits> withFilters(String keyword, Integer userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }

            if (StringUtils.hasText(keyword)) {
                try {
                    Integer parsedUserId = Integer.valueOf(keyword.trim());
                    predicates.add(cb.equal(root.get("userId"), parsedUserId));
                } catch (NumberFormatException ignored) {
                    // user_credits only has userId as searchable field in current requirement.
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
