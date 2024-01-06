package ua.spro.tcdemo.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PostRepositoryTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:0");

  @Autowired PostRepository postRepository;

  @Test
  void connectionEstablished() {
    assertThat(postgres.isCreated()).isTrue();
    assertThat(postgres.isRunning()).isTrue();
  }

  @BeforeEach
  void setUp() {
    List<Post> posts = List.of(
        new Post(1, 1, "Hello world!", "This is my first post", null)
    );
    postRepository.saveAll(posts);
  }

  @Test
  void shouldReturnPostByTitle(){
    Post post = postRepository.findByTitle("Hello world!");
    assertThat(post).isNotNull();
  }
}
