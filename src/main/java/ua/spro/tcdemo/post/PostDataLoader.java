package ua.spro.tcdemo.post;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class PostDataLoader implements CommandLineRunner {

  private final ObjectMapper objectMapper;
  private final PostRepository postRepository;


  @Override
  public void run(String... args)  {
    if(postRepository.count() == 0){
      String POSTS_JSON = "/data/posts.json";
      log.info("Loading posts into database from JSON: {}", POSTS_JSON);
      try(InputStream inputStream = TypeReference.class.getResourceAsStream(POSTS_JSON)){
        Posts response = objectMapper.readValue(inputStream, Posts.class);
        postRepository.saveAll(response.posts());
      }catch (IOException e){
        throw new RuntimeException("Failed to read JSON data", e);
      }
    }
  }
}
