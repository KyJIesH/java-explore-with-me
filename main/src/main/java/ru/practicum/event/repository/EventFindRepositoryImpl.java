package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSearch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventFindRepositoryImpl implements EventFindRepository {

    private EntityManager em;
    private CriteriaBuilder cb;

    public EventFindRepositoryImpl(EntityManager em) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
    }

    @Override
    public Page<Event> findAllByParam(EventSearch eventSearch, Pageable pageable) {
        CriteriaQuery<Event> criteriaQuery = cb.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);
        Predicate predicate = getPredicate(eventSearch, eventRoot);
        criteriaQuery.where(predicate);

        if (pageable.getSort().isUnsorted()) {
            criteriaQuery.orderBy(cb.desc(eventRoot.get("createdOn")));
        }

        TypedQuery<Event> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Event> events = typedQuery.getResultList();

        return new PageImpl<>(events);
    }

    private Predicate getPredicate(EventSearch eventSearch, Root<Event> eventRoot) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate annotationPredicate = null;
        if (Objects.nonNull(eventSearch.getText())) {
            annotationPredicate = cb.like(cb.lower(eventRoot.get("annotation")),
                    "%" + eventSearch.getText().toLowerCase() + "%");
        }
        if (Objects.nonNull(eventSearch.getText()) && annotationPredicate == null) {
            predicates.add(cb.like(cb.lower(eventRoot.get("description")),
                    "%" + eventSearch.getText().toLowerCase() + "%"));
        } else if (Objects.nonNull(eventSearch.getText())) {
            Predicate descriptionPredicate = cb.like(cb.lower(eventRoot.get("description")),
                    "%" + eventSearch.getText().toLowerCase() + "%");
            predicates.add(cb.or(annotationPredicate, descriptionPredicate));
        }

        if (eventSearch.getCategories() != null && !eventSearch.getCategories().isEmpty()) {
            Join<Event, Category> categoryJoin = eventRoot.join("category");
            predicates.add(categoryJoin.get("id").in(eventSearch.getCategories()));
        }
        if (eventSearch.getPaid() != null && eventSearch.getPaid().equals(Boolean.TRUE)) {
            predicates.add(cb.equal(eventRoot.get("paid"), eventSearch.getPaid()));
        }
        if (eventSearch.getRangeStart() != null || eventSearch.getRangeEnd() != null) {
            LocalDateTime rangeStart = eventSearch.getRangeStart() != null
                    ? eventSearch.getRangeStart()
                    : LocalDateTime.MIN;
            LocalDateTime rangeEnd = eventSearch.getRangeEnd() != null
                    ? eventSearch.getRangeEnd()
                    : LocalDateTime.MAX;
            predicates.add(cb.between(eventRoot.get("eventDate"), rangeStart, rangeEnd));
        } else {
            predicates.add(cb.between(eventRoot.get("eventDate"), LocalDateTime.now(), LocalDateTime.now().plusYears(10)));
        }

        if (eventSearch.getOnlyAvailable() != null && eventSearch.getOnlyAvailable()) {
            predicates.add(cb.or(
                    cb.isNull(eventRoot.get("participantLimit")),
                    cb.greaterThan(
                            cb.diff(eventRoot.get("participantLimit"), eventRoot.get("confirmedRequests")), 0L
                    )
            ));
        }
        return cb.and(predicates.toArray(predicates.toArray(new Predicate[0])));
    }
}
