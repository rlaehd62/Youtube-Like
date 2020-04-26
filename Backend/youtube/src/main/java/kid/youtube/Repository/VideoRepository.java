package kid.youtube.Repository;

import kid.youtube.Entity.Category;
import kid.youtube.Entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, String>
{
    List<Video> findAllByCategory(Category category);

    @Transactional
    void deleteVideoByUuid(String uuid);
}
