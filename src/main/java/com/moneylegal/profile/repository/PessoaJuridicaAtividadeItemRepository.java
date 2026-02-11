package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.PessoaJuridicaAtividadeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PessoaJuridicaAtividadeItemRepository extends JpaRepository<PessoaJuridicaAtividadeItem, String> {
    List<PessoaJuridicaAtividadeItem> findAllByIsActiveTrueAndCategoryIdOrderBySortOrderAscLabelAsc(String categoryId);
}
