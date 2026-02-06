package com.teacompliance.repository;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 茶葉ロットリポジトリのカスタム実装
 */
public class TeaLotRepositoryImpl extends SimpleJpaRepository<TeaLot, Long> implements TeaLotRepositoryCustom {
    
    private final EntityManager entityManager;
    
    public TeaLotRepositoryImpl(EntityManager entityManager) {
        super(TeaLot.class, entityManager);
        this.entityManager = entityManager;
    }
    
    @Override
    public Page<TeaLot> searchByCriteria(TeaLotSearchCriteria criteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TeaLot> query = cb.createQuery(TeaLot.class);
        Root<TeaLot> root = query.from(TeaLot.class);
        
        List<Predicate> predicates = buildPredicates(criteria, cb, root);
        query.where(predicates.toArray(new Predicate[0]));
        
        // ソート処理
        applySorting(cb, query, root, criteria);
        
        TypedQuery<TeaLot> typedQuery = entityManager.createQuery(query);
        
        // ページング設定
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<TeaLot> results = typedQuery.getResultList();
        return new PageImpl<>(results, pageable, totalRows);
    }
    
    @Override
    public List<TeaLot> searchByCriteria(TeaLotSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TeaLot> query = cb.createQuery(TeaLot.class);
        Root<TeaLot> root = query.from(TeaLot.class);
        
        List<Predicate> predicates = buildPredicates(criteria, cb, root);
        query.where(predicates.toArray(new Predicate[0]));
        
        // ソート処理
        applySorting(cb, query, root, criteria);
        
        return entityManager.createQuery(query).getResultList();
    }
    
    /**
     * 検索条件のPredicateを構築
     */
    private List<Predicate> buildPredicates(TeaLotSearchCriteria criteria, CriteriaBuilder cb, Root<TeaLot> root) {
        List<Predicate> predicates = new ArrayList<>();
        
        // 産地
        if (criteria.getOrigin() != null && !criteria.getOrigin().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("origin"), criteria.getOrigin()));
        }
        
        // 品種
        if (criteria.getVariety() != null && !criteria.getVariety().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("variety"), criteria.getVariety()));
        }
        
        // ロットコード（部分一致）
        if (criteria.getLotCode() != null && !criteria.getLotCode().trim().isEmpty()) {
            predicates.add(cb.like(root.get("lotCode"), "%" + criteria.getLotCode() + "%"));
        }
        
        // 水分量範囲
        if (criteria.getMinMoisture() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("moisture"), criteria.getMinMoisture()));
        }
        if (criteria.getMaxMoisture() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("moisture"), criteria.getMaxMoisture()));
        }
        
        // 農薬レベル上限
        if (criteria.getMaxPesticideLevel() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("pesticideLevel"), criteria.getMaxPesticideLevel()));
        }
        
        // 生産日範囲
        if (criteria.getProducedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("producedAt"), criteria.getProducedAfter()));
        }
        if (criteria.getProducedBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("producedAt"), criteria.getProducedBefore()));
        }
        
        return predicates;
    }
    
    /**
     * ソート条件を適用
     */
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<TeaLot> query, Root<TeaLot> root, TeaLotSearchCriteria criteria) {
        String sortBy = criteria.getSortBy();
        String sortDirection = criteria.getSortDirection();
        
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "producedAt";
        }
        
        if ("asc".equalsIgnoreCase(sortDirection)) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }
    }
}
