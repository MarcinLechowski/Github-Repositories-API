package org.kaer.api_git_users_repo.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kaer.api_git_users_repo.dto.GithubPaginationDto;
import org.kaer.api_git_users_repo.dto.GithubResponseDto;
import org.kaer.api_git_users_repo.mappers.GithubResponseMapper;
import org.kaer.api_git_users_repo.mappers.UrlBuilder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @InjectMocks
    private GithubRepositoryService githubRepositoryService;

    @Mock
    private GithubResponseMapper githubResponseMapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlBuilder urlBuilder;

    @Test
    void testGetRepositories() {
        // Arrange
        when(urlBuilder.buildUserReposUrl(anyString(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("foo"));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> githubRepositoryService.getRepositories("janedoe", 1, 3));
        verify(urlBuilder).buildUserReposUrl(anyString(), anyInt(), anyInt());
    }

    @Test
    void testGetRepositories2() {
        // Arrange, Act and Assert
        assertThrows(IllegalArgumentException.class, () -> githubRepositoryService.getRepositories(null, 1, 3));
    }

    @Test
    void testGetRepositories3() {
        // Arrange
        when(urlBuilder.buildUserReposUrl(anyString(), anyInt(), anyInt()))
                .thenThrow(new RestClientException("Msg"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> githubRepositoryService.getRepositories("janedoe", 1, 3));
        verify(urlBuilder).buildUserReposUrl(anyString(), anyInt(), anyInt());
    }

    @Test
    void testGetRepositories4() throws RestClientException {
        // Arrange
        ResponseEntity<List<Map<Object, Object>>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);
        when(urlBuilder.buildUserReposUrl(anyString(), anyInt(), anyInt()))
                .thenReturn("https://example.org/example");

        GithubPaginationDto pagination = new GithubPaginationDto();
        pagination.setNextPage(1);
        pagination.setNextPageLink("Next Page Link");
        pagination.setPage(1);
        pagination.setPageSize(3);
        pagination.setPreviousPage(1);
        pagination.setPreviousPageLink("Previous Page Link");
        pagination.setTotalPages("Total Pages");

        GithubResponseDto githubResponseDto = new GithubResponseDto();
        githubResponseDto.setItems(new ArrayList<>());
        githubResponseDto.setMessage("Not all who wander are lost");
        githubResponseDto.setPagination(pagination);
        githubResponseDto.setStatus(1);
        githubResponseDto.setTotal(1);
        when(githubResponseMapper.mapToResponseDto(anyString(), anyList(), anyInt(), anyInt())).thenReturn(githubResponseDto);

        // Act
        GithubResponseDto actualRepositories = githubRepositoryService.getRepositories("janedoe", 1, 3);

        // Assert
        verify(githubResponseMapper).mapToResponseDto(anyString(), anyList(), anyInt(), anyInt());
        verify(urlBuilder).buildUserReposUrl(anyString(), anyInt(), anyInt());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class));
        assertSame(githubResponseDto, actualRepositories);
    }

    @Test
    void testGetRepositories5() throws RestClientException {
        // Arrange
        ResponseEntity<List<Map<Object, Object>>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);
        when(urlBuilder.buildUserReposUrl(anyString(), anyInt(), anyInt()))
                .thenReturn("https://example.org/example");
        when(githubResponseMapper.mapToResponseDto(anyString(), anyList(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("foo"));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> githubRepositoryService.getRepositories("janedoe", 1, 3));
        verify(githubResponseMapper).mapToResponseDto(anyString(), anyList(), anyInt(), anyInt());
        verify(urlBuilder).buildUserReposUrl(anyString(), anyInt(), anyInt());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetRepositoriesExceptionHandling() throws RestClientException {
        // Arrange
        when(urlBuilder.buildUserReposUrl(anyString(), anyInt(), anyInt()))
                .thenReturn("https://example.org/example");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenThrow(new RestClientException("Error communicating with GitHub API"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> githubRepositoryService.getRepositories("janedoe", 1, 3));
        verify(urlBuilder).buildUserReposUrl(anyString(), anyInt(), anyInt());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetRepositoriesEmptyResponse() throws RestClientException {
        // Arrange
        when(urlBuilder.buildUserReposUrl(anyString(), anyInt(), anyInt()))
                .thenReturn("https://example.org/example");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        GithubResponseDto actualRepositories = githubRepositoryService.getRepositories("janedoe", 1, 3);

        // Assert
        assertNotNull(actualRepositories);
        assertEquals(0, actualRepositories.getTotal());
        assertEquals(HttpStatus.OK.value(), actualRepositories.getStatus());
        assertEquals("No repositories found for the user.", actualRepositories.getMessage());
    }
}