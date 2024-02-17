package org.kaer.api_git_users_repo.mappers;

import org.kaer.api_git_users_repo.dto.GithubPaginationDto;


    public class GithubPaginationHandler {

        public static GithubPaginationDto handlePagination(int totalItems, int page, int pageSize, String username, UrlBuilder urlBuilder) {
            GithubPaginationDto paginationDto = new GithubPaginationDto();
            paginationDto.setTotalPages(String.valueOf(calculateTotalPages(totalItems, pageSize)));
            paginationDto.setPage(page);
            paginationDto.setPageSize(pageSize);

            int nextPage = page + 1;
            if (nextPage <= calculateTotalPages(totalItems, pageSize)) {
                paginationDto.setNextPage(nextPage);

                String nextPageLink = urlBuilder.buildPageLink(username, nextPage, pageSize);
                paginationDto.setNextPageLink(nextPageLink);
            }

            int previousPage = page - 1;
            if (previousPage >= 1) {
                paginationDto.setPreviousPage(previousPage);

                String previousPageLink = urlBuilder.buildPageLink(username, previousPage, pageSize);
                paginationDto.setPreviousPageLink(previousPageLink);
            }
            return paginationDto;
        }
        public static int calculateTotalPages(int totalItems, int pageSize) {
            if (pageSize <= 0) {
                throw new IllegalArgumentException("PageSize should be greater than zero");
            }
            return (int) Math.ceil((double) totalItems / pageSize);
        }
    }
