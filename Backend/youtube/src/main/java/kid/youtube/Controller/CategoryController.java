package kid.youtube.Controller;

import kid.youtube.Entity.Category;
import kid.youtube.Repository.CategoryRepository;
import kid.youtube.Repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController
{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> makeCategory(@RequestParam String category)
    {
        boolean EXISTS  = categoryRepository.existsCategoryByName(category);
        if(!EXISTS)
        {
            Category cate = new Category(category);
            categoryRepository.save(cate);
            return ResponseEntity.ok("Accepted!");
        }

        throw new RuntimeException("Category Already Exists!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCategory(@RequestParam String category)
    {
        boolean VALID  = categoryRepository.existsCategoryByName(category) &&
                videoRepository.findAllByCategory_Name(category).isEmpty();

        if(VALID)
        {
            categoryRepository.deleteCategoryByName(category);
            return ResponseEntity.ok("Accepted!");
        }

        throw new RuntimeException("Sorry, Couldn't Remove The Category.");
    }

    @GetMapping("/list")
    public List<Category> getCategory()
    {
        return categoryRepository.findAll();
    }
}
