package ua.spro.tcdemo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.spro.tcdemo.post.Post;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class PostControllerIntTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:0");

  @Autowired TestRestTemplate restTemplate;

  @Test
  void findAll_shouldReturnList() {
    // /api/posts
    Post[] posts = restTemplate.getForObject("/api/posts", Post[].class);
    assertThat(posts.length).isEqualTo(100);
    log.info("posts.length: " + posts.length);
  }

  @Test
  void findById_shouldFindPost_whenValidId() {
    ResponseEntity<Post> response =
        restTemplate.exchange("/api/posts/1", HttpMethod.GET, null, Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    log.info(response.getBody().toString());
  }

  @Test
  void findById_shouldThrowNotFound_whenInvalidId() {
    ResponseEntity<Post> response =
        restTemplate.exchange("/api/posts/999", HttpMethod.GET, null, Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @Rollback
  void save_shouldCreateNewPost_whenIdIsValid() {
    Post post = new Post(101, 1, "101 title", "101 body", null);

    ResponseEntity<Post> response =
        restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<>(post), Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().id()).isEqualTo(101);
    assertThat(response.getBody().userId()).isEqualTo(1);
    assertThat(response.getBody().title()).isEqualTo("101 title");
    assertThat(response.getBody().body()).isEqualTo("101 body");
  }

  @Test
  void save_shouldNotCreateNewPost_whenValidationFails() {
    Post post = new Post(101, 1, "", "", null);
    ResponseEntity<Post> response =
        restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<>(post), Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @Rollback
  void update_shouldUpdatePost_whenIdIsValid() {
    ResponseEntity<Post> response =
        restTemplate.exchange("/api/posts/99", HttpMethod.GET, null, Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Post existingPost = response.getBody();
    assertThat(existingPost).isNotNull();
    Post updatedPost =
        new Post(
            existingPost.id(),
            existingPost.userId(),
            "new post title",
            "new post body",
            existingPost.version());

    ResponseEntity<Post> responseUpdate =
        restTemplate.exchange(
            "/api/posts/99", HttpMethod.PUT, new HttpEntity<>(updatedPost), Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Post updatedPostResponse = responseUpdate.getBody();
    assertThat(updatedPostResponse).isNotNull();

    assertEquals(updatedPost.id(), updatedPostResponse.id());
    assertEquals(updatedPost.userId(), updatedPostResponse.userId());
    assertEquals(updatedPost.title(), updatedPostResponse.title());
    assertEquals(updatedPost.body(), updatedPostResponse.body());
    assertNotEquals(updatedPost.version(), updatedPostResponse.version());
  }

  @Test
  @Rollback
  void delete_shouldDelete_whenIdIsValid() {
    ResponseEntity<Void> response =
        restTemplate.exchange("/api/posts/88", HttpMethod.DELETE, null, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
