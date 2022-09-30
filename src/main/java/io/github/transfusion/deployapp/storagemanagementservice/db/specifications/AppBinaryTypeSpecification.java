package io.github.transfusion.deployapp.storagemanagementservice.db.specifications;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinary;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AppBinaryTypeSpecification implements Specification<AppBinary> {

    private final Class clazz;

    public AppBinaryTypeSpecification(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Predicate toPredicate(Root<AppBinary> root, @NotNull CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.type(), criteriaBuilder.literal(clazz));
    }
}
