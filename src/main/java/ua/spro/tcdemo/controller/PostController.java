package ua.spro.tcdemo.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.spro.tcdemo.exception.PostNotFoundException;
import ua.spro.tcdemo.post.Post;
import ua.spro.tcdemo.post.PostRepository;

@AllArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostRepository postRepository;

  @GetMapping
  List<Post> findAll() {
    return postRepository.findAll();
  }

  @GetMapping("/{id}")
  Optional<Post> findById(@PathVariable Integer id) {
    return Optional.ofNullable(
        postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Post save(@RequestBody @Valid Post post){
    return postRepository.save(post);
  }

  @PutMapping("/{id}")
  Post update(@PathVariable Integer id, @RequestBody Post post){
    Optional<Post> existing = postRepository.findById(id);
    if(existing.isPresent()){
      Post updatedPost = new Post(
          existing.get().id(),
          existing.get().userId(),
          post.title(),
          post.body(),
          existing.get().version()
      );
      return postRepository.save(updatedPost);
    }else {
      throw new PostNotFoundException(id);
    }
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable Integer id){
    postRepository.deleteById(id);
  }
}
