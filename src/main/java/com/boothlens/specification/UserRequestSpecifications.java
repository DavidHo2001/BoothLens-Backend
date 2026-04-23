package com.boothlens.specification;

import com.boothlens.entity.UserRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UserRequestSpecifications {

    private UserRequestSpecifications() {
    }

    public static Specification<UserRequest> withFilters(String keyword, String status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status.trim().toUpperCase(Locale.ROOT)));
            }

            if (StringUtils.hasText(keyword)) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                List<Predicate> keywordPredicates = new ArrayList<>();
                keywordPredicates.add(cb.like(cb.lower(root.get("referenceCode")), normalizedKeyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("requestInfo")), normalizedKeyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("resultInfo")), normalizedKeyword));

                try {
                    Integer parsedUserId = Integer.valueOf(keyword.trim());
                    keywordPredicates.add(cb.equal(root.get("userId"), parsedUserId));
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric keyword for userId filtering.
                }
                predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
