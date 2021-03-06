package com.github.gpluscb.ggjava.entity.object.response.enums;

import com.github.gpluscb.ggjava.entity.EntityType;
import com.github.gpluscb.ggjava.entity.enums.TournamentPaginationSort;

import javax.annotation.Nonnull;

public class TournamentPaginationSortResponse extends EnumResponse<TournamentPaginationSort> {
	public TournamentPaginationSortResponse() {
		super(EntityType.TOURNAMENT_PAGINATION_SORT);
	}

	public TournamentPaginationSortResponse(@Nonnull TournamentPaginationSort value) {
		super(EntityType.TOURNAMENT_PAGINATION_SORT, value);
	}
}
