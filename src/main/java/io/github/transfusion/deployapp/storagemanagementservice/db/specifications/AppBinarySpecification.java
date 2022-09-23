package io.github.transfusion.deployapp.storagemanagementservice.db.specifications;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AppBinarySpecification implements Specification<AppBinary> {

    private final SearchCriteria criteria;

    public AppBinarySpecification(SearchCriteria searchCriteria) {
        this.criteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<AppBinary> root,
                                 @NotNull CriteriaQuery<?> query,
                                 @NotNull CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase("eq")) {
            return builder.equal(
                    root.<String>get(criteria.getKey()), criteria.getValue());
        }

        if (criteria.getOperation().equalsIgnoreCase("like")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
