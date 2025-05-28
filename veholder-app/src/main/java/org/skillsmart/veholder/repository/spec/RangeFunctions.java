package org.skillsmart.veholder.repository.spec;

import com.vladmihalcea.hibernate.type.range.Range;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import java.time.ZonedDateTime;

public class RangeFunctions {

    /**
     * Получает нижнюю границу диапазона для ZonedDateTime
     */
    public static Expression<ZonedDateTime> lower(
            CriteriaBuilder cb,
            Expression<Range<ZonedDateTime>> rangeExpression) {
        return cb.function(
                "lower",
                ZonedDateTime.class,
                rangeExpression
        );
    }

    /**
     * Получает верхнюю границу диапазона для ZonedDateTime
     */
    public static Expression<ZonedDateTime> upper(
            CriteriaBuilder cb,
            Expression<Range<ZonedDateTime>> rangeExpression) {
        return cb.function(
                "upper",
                ZonedDateTime.class,
                rangeExpression
        );
    }
}