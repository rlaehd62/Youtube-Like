package kid.youtube.Repository;

import kid.youtube.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String>
{
    boolean existsCategoryByName(String name);
    @Transactional void deleteCategoryByName(String name);

    Optional<Category> findCategoryByName(String name);
}
