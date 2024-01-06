package ua.spro.tcdemo.controller;

import static java.lang.StringTemplate.STR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ua.spro.tcdemo.post.Post;
import ua.spro.tcdemo.post.PostRepository;

@Slf4j
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean PostRepository repository;

  List<Post> posts = new ArrayList<>();

  @BeforeEach
  void setUp() {
    posts =
        List.of(
            new Post(1, 1, "Hello, World!", "This is my first post.", null),
            new Post(2, 1, "Second Post", "This is my second post.", null));
  }

  @Test
  void findAll_shouldReturnList() throws Exception {
    String jsonResponse = """
        [
            {
                "id":1,
                "userId":1,
                "title":"Hello, World!",
                "body":"This is my first post.",
                "version": null
            },
            {
                "id":2,
                "userId":1,
                "title":"Second Post",
                "body":"This is my second post.",
                "version": null
            }
        ]
        """;
    when(repository.findAll()).thenReturn(posts);

    ResultActions resultActions = mockMvc.perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));
    JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
  }

  @Test
  void getById_shouldFindPost_whenIdIsValid() throws Exception {
    Post post = new Post(1,1, "Test title", "Test body", null);
    when(repository.findById(1)).thenReturn(Optional.of(post));
    String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;
    mockMvc.perform(get("/api/posts/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void save_shouldCreateNewPost() throws Exception {
    Post postRequest = new Post(null, 1, "third post", "post3 body", null);
    Post postResponse = new Post(3, 1, "third post", "post3 body", null);

    when(repository.save(postRequest)).thenReturn(postResponse);
    String jsonIn = STR."""
                {
                    "userId":\{postResponse.userId()},
                    "title":"\{postResponse.title()}",
                    "body":"\{postResponse.body()}",
                    "version": null
                }
                """;
    String jsonOut = STR."""
                {
                    "id":\{postResponse.id()},
                    "userId":\{postResponse.userId()},
                    "title":"\{postResponse.title()}",
                    "body":"\{postResponse.body()}",
                    "version": null
                }
                """;
    mockMvc.perform(
          post("/api/posts")
            .contentType("application/json")
            .content(jsonIn)
          )
        .andExpect(status().isCreated())
        .andExpect(content().json(jsonOut));
  }

  @Test
  void update_shouldUpdatePost() throws Exception {
    Post updated = new Post(1,1,"This is my brand new post", "UPDATED BODY",1);
    when(repository.findById(1)).thenReturn(Optional.of(posts.get(0)));
    when(repository.save(updated)).thenReturn(updated);
    String requestBody = STR."""
                {
                    "id":\{updated.id()},
                    "userId":\{updated.userId()},
                    "title":"\{updated.title()}",
                    "body":"\{updated.body()}",
                    "version": \{updated.version()}
                }
                """;

    mockMvc.perform(put("/api/posts/1")
            .contentType("application/json")
            .content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void update_shouldNotUpdateAndThrowNotFound_whenInvalidId() throws Exception {
    Post updated = new Post(50, 1, "This is brand new post", "updated body", 1);
    when(repository.save(updated)).thenReturn(updated);


    String requestBody = STR."""
                {
                    "id":\{updated.id()},
                    "userId":\{updated.userId()},
                    "title":"\{updated.title()}",
                    "body":"\{updated.body()}",
                    "version": \{updated.version()}
                }
                """;
    mockMvc.perform(put("/api/posts/999")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isNotFound());

  }

}
